package nju.quadra.quantra.pool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.io.IOException;
import java.util.*;

/**
 * Created by adn55 on 2017/4/20.
 */
public class IndustryPool extends AbstractPool {
    private Set<Integer> pool;

    public IndustryPool(String name, Set<Integer> pool) {
        super(name);
        this.pool = pool;
    }

    @Override
    protected Set<Integer> loadStockPool() {
        return pool;
    }

    public static List<IndustryPool> getPools() {
        try {
            Map<String, Set<Integer>> datas = JSON.parseObject(
                    IndustryPool.class.getResourceAsStream("industries.json"),
                    new TypeReference<Map<String, Set<Integer>>>() {}.getType()
            );
            ArrayList<IndustryPool> pools = new ArrayList<>();
            for (Map.Entry<String, Set<Integer>> entry : datas.entrySet()) {
                pools.add(new IndustryPool(entry.getKey(), entry.getValue()));
            }
            return pools;
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }
}
