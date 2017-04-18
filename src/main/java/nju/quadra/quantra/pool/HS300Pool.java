package nju.quadra.quantra.pool;

import nju.quadra.quantra.data.StockData;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by RaUkonn on 2017/4/3.
 */
public class HS300Pool extends AbstractPool {
    public HS300Pool() {
        super("沪深300");
    }

    @Override
    protected Set<Integer> loadStockPool() {
        return StockData.getPtrList().stream()
                .mapToInt(i -> i.get().getCode())
                .filter(i -> (0 <= i && i < 2000) || (600000 <= i && i < 602000))
                .boxed()
                .collect(Collectors.toSet());
    }
}
