package nju.quadra.quantra.pool;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by RaUkonn on 2017/4/3.
 */
public class CustomPool extends AbstractPool {
    public CustomPool(String name) {
        super(name);
        this.stockPool = new HashSet<>();
    }

    public CustomPool(String name, Set<Integer> list) {
        super(name);
        this.stockPool = list;
    }
}
