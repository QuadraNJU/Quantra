package nju.quadra.quantra.pool;

import nju.quadra.quantra.data.StockData;
import nju.quadra.quantra.data.StockInfo;
import nju.quadra.quantra.data.StockInfoPtr;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by RaUkonn on 2017/4/3.
 */
public class HS300Pool extends AbstractPool {
    public HS300Pool() {
        super("沪深300");
        List<StockInfoPtr> list = StockData.getPtrList();
        this.stockPool = list.stream()
                .mapToInt(i -> i.get().getCode())
                .filter(i -> (0 <= i && i < 2000) || (600000 <= i && i < 602000))
                .boxed()
                .collect(Collectors.toSet());
    }
}
