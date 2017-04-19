package nju.quadra.quantra.pool;

import nju.quadra.quantra.data.StockPoolData;

import java.util.Collections;
import java.util.Set;

/**
 * Created by RaUkonn on 2017/4/3.
 */
public abstract class AbstractPool {
    public String name;

    public AbstractPool(String name) {
        this.name = name;
    }

    public Set<Integer> getStockPool() {
        if (!StockPoolData.getCache().containsKey(name)) {
            StockPoolData.getCache().put(name, loadStockPool());
        }
        return Collections.unmodifiableSet(StockPoolData.getCache().get(name));
    }

    protected abstract Set<Integer> loadStockPool();

    @Override
    public String toString() {
        return name;
    }
}
