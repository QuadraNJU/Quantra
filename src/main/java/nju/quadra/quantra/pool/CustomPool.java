package nju.quadra.quantra.pool;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by RaUkonn on 2017/4/3.
 */
public class CustomPool extends AbstractPool {
    private Set<Integer> stockPool;

    public CustomPool(String name) {
        super(name);
        stockPool = new HashSet<>();
    }

    public CustomPool(String name, Set<Integer> list) {
        super(name);
        stockPool = list;
    }

    @Override
    protected Set<Integer> loadStockPool() {
        return stockPool;
    }
}
