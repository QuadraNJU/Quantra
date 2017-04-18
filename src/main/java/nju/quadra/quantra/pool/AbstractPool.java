package nju.quadra.quantra.pool;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by RaUkonn on 2017/4/3.
 */
public abstract class AbstractPool {
    public String name;
    private static Map<String, Set<Integer>> stockPoolMap = new HashMap<>();

    public AbstractPool(String name) {
        this.name = name;
    }

    public Set<Integer> getStockPool() {
        if (!stockPoolMap.containsKey(name)) {
            stockPoolMap.put(name, loadStockPool());
        }
        return Collections.unmodifiableSet(stockPoolMap.get(name));
    }

    protected abstract Set<Integer> loadStockPool();

    @Override
    public String toString() {
        return name;
    }
}
