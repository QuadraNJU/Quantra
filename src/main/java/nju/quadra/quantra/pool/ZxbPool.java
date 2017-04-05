package nju.quadra.quantra.pool;

import nju.quadra.quantra.data.StockData;

import java.util.stream.Collectors;

/**
 * Created by RaUkonn on 2017/4/3.
 */
public class ZxbPool extends AbstractPool {
    public ZxbPool() {
        super("中小板");
        this.stockPool = StockData.getPtrList().stream()
                .mapToInt(i -> i.get().getCode())
                .filter(i -> (2000 <= i && i < 3000))
                .boxed()
                .collect(Collectors.toSet());
    }
}
