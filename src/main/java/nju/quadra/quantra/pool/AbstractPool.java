package nju.quadra.quantra.pool;

import java.util.List;
import java.util.Set;

/**
 * Created by RaUkonn on 2017/4/3.
 */
public abstract class AbstractPool {
    public String name;
    public Set<Integer> stockPool;

    public abstract List<Integer> getStockPool();

    @Override
    public String toString() {
        return name;
    }
}
