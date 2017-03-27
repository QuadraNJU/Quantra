# coding=utf-8
import time
import imp
import os
import pandas


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
        self.portfolio = capital
        self.date_index = 0
        self.sec_pos = {}
        self.ref_price = {}

    def set_date_index(self, date_index):
        self.date_index = date_index
        self.stocks = stock_data[stock_data[get_column_index('code')].isin(self.universe)
                                 & (stock_data[get_column_index('date')] == trade_days[self.date_index])]
        for index, info in self.stocks.iterrows():
            self.ref_price[info[get_column_index('code')]] = info[get_column_index('adjclose')]
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
        new_portfolio = self.portfolio - diff_amount * self.ref_price[stock]
        if new_portfolio < 0:  # 拒绝交易
            return
        self.portfolio = new_portfolio
        self.sec_pos[stock] = target
        print 'Trade', diff_amount, stock, ', portfolio =', new_portfolio

    def sell_all(self):
        for stock in self.sec_pos:
            self.trade(stock, 0)


if __name__ == '__main__':
    os.chdir(os.getcwd())
    stock_data = pandas.read_csv('../../../stock_data.json', sep=',', header=None)
    print 'Data loaded'
    start = time.time()
    trade_days = stock_data[1].drop_duplicates()
    print 'Trade dates loaded'

    # arguments
    start_date = '1/4/13'
    end_date = '12/31/13'  # TODO: auto scale dates
    universe = get_universe('300')
    capital = 100000000
    frequency = 10
    strategy = 'momentum'

    # main logic
    start_date_index = trade_days[trade_days == start_date].index[0]
    end_date_index = trade_days[trade_days == end_date].index[0]
    handler = imp.load_source(strategy, 'strategy/' + strategy + '.py')
    account = Account(universe, capital)
    for i in range(start_date_index, end_date_index, -frequency):
        print 'Current date:', trade_days[i]
        account.set_date_index(i)
        handler.handle(account)
    account.sell_all()
