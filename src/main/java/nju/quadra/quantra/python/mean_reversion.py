# coding=utf-8
import pandas as pd
import numpy as np


def handle(account):
    hist = account.get_history('adjclose', __PERIOD__)
    lis = {'symbol': [], 'bias': []}
    for stk in hist:
        # 计算股票过去20天收盘平均值
        ma20 = np.mean(hist[stk])
        bias = (ma20 - hist[stk][0]) / ma20
        lis['symbol'].append(stk)
        lis['bias'].append(bias)

    lis = pd.DataFrame(lis).sort_values(by='bias', ascending=False)[:10]
    buylist = lis['symbol'].tolist()
    for stk in account.sec_pos:
        if stk not in buylist:
            account.trade(stk, 0)
    # 等权重买入所选股票
    for stk in buylist:
        if stk not in account.sec_pos:
            account.trade(stk, int(account.cash / account.ref_price[stk] / len(buylist) / 100.0) * 100)
