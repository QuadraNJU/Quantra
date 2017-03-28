# coding=utf-8
import pandas


def handle(account):
    history = account.get_history('adjclose', 20)
    momentum = {'symbol': [], 'c_ret': []}
    for stk in history:
        momentum['symbol'].append(stk)
        momentum['c_ret'].append(history[stk][-1] / history[stk][0])
    # 按照过去20日收益率排序，并且选择前20%的股票作为买入候选
    momentum = pandas.DataFrame(momentum).sort_values(by='c_ret')
    momentum = momentum[len(momentum) * 4 / 5:len(momentum)]  # 选择
    buylist = momentum['symbol'].tolist()
    for stk in account.sec_pos:
        if stk not in buylist:
            account.trade(stk, 0)
    # 等权重买入所选股票
    for stk in buylist:
        if stk not in account.sec_pos:
            account.trade(stk, int(account.cash / account.ref_price[stk] / len(buylist) / 100.0) * 100)
