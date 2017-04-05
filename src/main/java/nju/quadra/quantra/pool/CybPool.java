package nju.quadra.quantra.pool;

import nju.quadra.quantra.data.StockData;
import nju.quadra.quantra.data.StockInfoPtr;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by RaUkonn on 2017/4/3.
 */
public class CybPool extends AbstractPool {
    public CybPool() {
        super("创业板");
        List<StockInfoPtr> list = StockData.getPtrList();
        this.stockPool = list.stream()
                .mapToInt(i -> i.get().getCode())
                .filter(i -> (300000 <= i && i < 301000))
                .boxed()
                .collect(Collectors.toSet());
    }
}
