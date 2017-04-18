package nju.quadra.quantra.pool;

import nju.quadra.quantra.data.StockData;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by RaUkonn on 2017/4/3.
 */
public class CybPool extends AbstractPool {
    public CybPool() {
        super("创业板");
    }

    @Override
    protected Set<Integer> loadStockPool() {
        return StockData.getPtrList().stream()
                .mapToInt(i -> i.get().getCode())
                .filter(i -> (300000 <= i && i < 301000))
                .boxed()
                .collect(Collectors.toSet());
    }
}
