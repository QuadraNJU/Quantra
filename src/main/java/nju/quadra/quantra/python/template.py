# coding=utf-8
import pandas as pd
import numpy as np


# 每过一个持有期, 此方法会被调用一次
def handle(account):
    """
    account 相关 API 参考：
        account.universe     回测股票池中所有股票代码的 List
        account.cash         账户内的现金
        account.sec_pos      持仓的股票 Dict, key 为股票代码, value 为持有股数
        account.ref_price    当天所有股票的交易价格 Dict, key 为股票代码, value 为交易价格
        account.close_price  当天所有股票的收盘价格 Dict, key 为股票代码, value 为收盘价格(不复权)
        account.get_history(attr, days)
            获取一段时间内的股票历史数据, 若无足够天数的历史数据则返回 None
            attr: 属性, 可选值为 'open', 'high', 'low', 'close', 'volume', 'adjclose'
            days: 天数
            返回: 相应历史数据的 Dict, key 为股票代码, value 为历史数据组成的 List
        account.trade(stock, target)
            对某个股票发起买入或卖出交易, 注意: 若账户中的现金不足, 交易将被拒绝
            stock: 要交易的股票代码
            target: 目标持有量, 即交易后该股票的持有股数
    """
    # 在此处编写每个持有期的处理逻辑
    pass
