package nju.quadra.quantra.pool;

import java.util.Collections;
import java.util.Set;

/**
 * Created by RaUkonn on 2017/4/3.
 */
public abstract class AbstractPool {
    public String name;
    public Set<Integer> stockPool;

    public AbstractPool(String name) {
        this.name = name;
    }

    public Set<Integer> getStockPool() {
        return Collections.unmodifiableSet(stockPool);
    }

    @Override
    public String toString() {
        return name;
    }
}
