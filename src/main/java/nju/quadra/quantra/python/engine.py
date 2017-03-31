# coding=utf-8
import imp
import json
import sys
import time

import numpy
import os
import pandas


def alpha(_annualized_earn_rate, _base_earn_rate, _beta, _risk_free_interest_rate=0.0175):
    return (_annualized_earn_rate - _risk_free_interest_rate) \
           - _beta * numpy.floor(numpy.mean(_base_earn_rate) - _risk_free_interest_rate)


def beta(_daily_earn_rate, _base_earn_rate):
    n = len(_base_earn_rate)
    if n == 1:
        return 0
    cov = 0
    mean_strategy = numpy.mean(_daily_earn_rate)
    mean_base = numpy.mean(_base_earn_rate)
    for j in range(0, n):
        cov += (_daily_earn_rate[j] - mean_strategy) * (_base_earn_rate[j] - mean_base)
    cov = 1.0 / (n - 1) * cov
    return cov / numpy.var(_base_earn_rate)


def get_date(str):
    return time.strptime(str, '%m/%d/%y')


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
        self.close_price = {}

    def set_date_index(self, date_index):
        self.date_index = date_index
        code_index = get_column_index('code')
        self.stocks = stock_data[stock_data[code_index].isin(self.universe)
                                 & (stock_data[get_column_index('date')] == trade_days[self.date_index])]
        for index, info in self.stocks.iterrows():
            self.ref_price[info[code_index]] = info[get_column_index('open')]  # 今日开盘价
            self.close_price[info[code_index]] = info[get_column_index('close')]  # 今日收盘价
        for stk in self.sec_pos.keys():
            if self.sec_pos[stk] <= 0:
                del self.sec_pos[stk]

    def get_history(self, attr, days):
        column = get_column_index(attr)
        if column == -1:
            return None
        code_column = get_column_index('code')
        result = {}
        for index, _info in self.stocks.iterrows():
            if stock_data.loc[index + days - 1][code_column] == _info[code_column]:
                result[_info[code_column]] = stock_data[column][index:index + days].tolist()
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
    start_date = get_date(args['start_date'])
    end_date = get_date(args['end_date'])
    universe = args['universe']
    frequency = args['frequency']
    capital = 100000000

    os.chdir(os.getcwd())
    stock_data = pandas.read_csv('../../stock_data.json', sep=',', header=None)
    trade_days = stock_data[1].drop_duplicates()

    # main logic
    start_date_index = -1
    end_date_index = -1
    for i in range(0, len(trade_days)):
        if end_date_index == -1 and get_date(trade_days[i]) <= end_date:
            end_date_index = i
        if start_date_index == -1 and get_date(trade_days[i]) <= start_date:
            start_date_index = i
    handler = imp.load_source('strategy', 'strategy.py')
    account = Account(universe, capital)
    daily_earn_rate = []
    base_earn_rate = []
    for i in range(start_date_index, end_date_index, -frequency):
        account.set_date_index(i)
        handler.handle(account)
        # portofolio
        new_portfolio = account.cash
        for stk in account.sec_pos:
            new_portfolio += account.sec_pos[stk] * account.close_price[stk]
        account.portfolio = new_portfolio
        # earning rate
        earn_rate = (new_portfolio - capital) / capital
        daily_earn_rate.append(earn_rate)
        # base earning rate
        if i == start_date_index:
            base_stock_price = numpy.mean(account.ref_price.values())
        today_base_earn_rate = (numpy.mean(account.close_price.values()) - base_stock_price) / base_stock_price
        base_earn_rate.append(today_base_earn_rate)
        # update progress
        progress = int((start_date_index - i) * 100.0 / (start_date_index - end_date_index))
        info = {'progress': progress, 'date': trade_days[i], 'earn_rate': earn_rate,
                'base_earn_rate': today_base_earn_rate}
        print info
        sys.stdout.flush()

    annualized = daily_earn_rate[-1] / (start_date_index - end_date_index + 1) * 250
    base_annualized = base_earn_rate[-1] / (start_date_index - end_date_index + 1) * 250
    sharp = (annualized - 0.0175) / numpy.std(daily_earn_rate)
    beta = beta(daily_earn_rate, base_earn_rate)
    alpha = alpha(annualized, base_earn_rate, beta)

    print json.dumps({'success': True, 'progress': 100, 'annualized': annualized, 'base_annualized': base_annualized,
                      'sharp': sharp, 'beta': beta, 'alpha': alpha})
