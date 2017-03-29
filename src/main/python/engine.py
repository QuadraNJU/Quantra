# coding=utf-8
import imp
import json
import time

import os
import pandas
import sys


def get_column_index(key):
    indices = ['', 'date', 'open', 'high', 'low', 'close', 'volume', 'adjclose', 'code', 'name', 'market']
    try:
        return indices.index(key)
    except:
        return -1


def get_universe(prefix):
    times = 6 - len(prefix)
    begin = int(prefix) * (10 ** times)
    end = (int(prefix) + 1) * (10 ** times)
    s = stock_data[get_column_index('code')]
    return s[s.isin(range(begin, end))].drop_duplicates()


class Account:
    def __init__(self, universe, capital):
        self.universe = universe
        self.cash = capital
        self.portfolio = capital
        self.date_index = 0
        self.stocks = {}
        self.sec_pos = {}
        self.ref_price = {}

    def set_date_index(self, date_index):
        self.date_index = date_index
        code_index = get_column_index('code')
        self.stocks = stock_data[stock_data[code_index].isin(self.universe)
                                 & (stock_data[get_column_index('date')] == trade_days[self.date_index])]
        for index, info in self.stocks.iterrows():
            self.ref_price[info[code_index]] = info[get_column_index('close')]  # 今日收盘价
        for stk in self.sec_pos.keys():
            if self.sec_pos[stk] <= 0:
                del self.sec_pos[stk]

    def get_history(self, attr, days):
        column = get_column_index(attr)
        if column == -1:
            return None
        code_column = get_column_index('code')
        result = {}
        for index, info in self.stocks.iterrows():
            if stock_data.loc[index + days - 1][code_column] == info[code_column]:
                result[info[code_column]] = stock_data[column][index:index + days].tolist()
        return result

    def trade(self, stock, target):
        if target < 0 or not self.ref_price[stock]:
            return
        if stock in self.sec_pos:
            curr_amount = self.sec_pos[stock]
        else:
            curr_amount = 0
        diff_amount = target - curr_amount  # 大于0买入，小于0卖出
        new_cash = self.cash - diff_amount * self.ref_price[stock]
        if new_cash < 0:  # 拒绝交易
            return
        self.cash = new_cash
        self.sec_pos[stock] = target

    def sell_all(self):
        for stock in self.sec_pos:
            self.trade(stock, 0)


if __name__ == '__main__':
    args = raw_input()
    args = json.loads(args)
    start_date = args['start_date']
    end_date = args['end_date']  # TODO: auto scale dates
    universe = args['universe']
    frequency = args['frequency']
    strategy = args['strategy']
    capital = 100000000

    os.chdir(os.getcwd())
    stock_data = pandas.read_csv('../../../stock_data.json', sep=',', header=None)
    trade_days = stock_data[1].drop_duplicates()

    # main logic
    start_date_index = trade_days[trade_days == start_date].index[0]
    end_date_index = trade_days[trade_days == end_date].index[0]
    handler = imp.load_source(strategy, 'strategy/' + strategy + '.py')
    account = Account(universe, capital)
    daily_earnings_rate = []
    for i in range(start_date_index, end_date_index, -frequency):
        account.set_date_index(i)
        handler.handle(account)
        new_portfolio = account.cash
        for stk in account.sec_pos:
            new_portfolio += account.sec_pos[stk] * account.ref_price[stk]
        account.portfolio = new_portfolio
        earn_rate = (new_portfolio - capital) / capital
        daily_earnings_rate.append(earn_rate)

        progress = int((start_date_index - i) * 100.0 / (start_date_index - end_date_index))
        info = {'progress': progress, 'date': trade_days[i], 'cash': account.cash, 'earn_rate': earn_rate}
        print info
        sys.stdout.flush()

    print json.dumps({'success': True, 'progress': 100, 'daily_earnings_rate': daily_earnings_rate})
    # print daily_earnings_rate
    # print daily_earnings_rate[-1]
