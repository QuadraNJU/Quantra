package nju.quadra.quantra.data;

import nju.quadra.quantra.pool.CybPool;
import nju.quadra.quantra.pool.HS300Pool;
import nju.quadra.quantra.pool.ZxbPool;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by RaUkonn on 2017/4/2.
 */
public class StockPoolDataTest {

    @Test
    public void getHS300ListTest() {
        StockData.loadJSON(null);
        long start = System.currentTimeMillis();
        HS300Pool hs300 = new HS300Pool();
        long mid = System.currentTimeMillis();
        System.out.println("Finish loading hs300 list, using " + (mid - start) + "ms");
        System.out.println(hs300.getStockPool());
        System.out.println(hs300.getStockPool().size());
        long end = System.currentTimeMillis();
        System.out.println("Finish printing hs300 list, using " + (end - mid) + "ms");
        List<StockInfoPtr> list = StockData.getPtrList();
        System.out.println(list.stream().mapToInt(i -> i.get().getCode()).filter(i -> (0 <= i && i < 2000) || (600000 <= i && i < 602000)).boxed().collect(Collectors.toSet()).size());
    }

    @Test
    public void getAllPlates() {
        StockData.loadJSON(null);
        long start = System.currentTimeMillis();
        HS300Pool hs300Pool = new HS300Pool();
        ZxbPool zxbPool = new ZxbPool();
        CybPool cybPool = new CybPool();
        List<StockInfoPtr> list = StockData.getPtrList();
        Assert.assertEquals(hs300Pool.getStockPool().size() + zxbPool.getStockPool().size() + cybPool.getStockPool().size(),
                list.stream().mapToInt(i -> i.get().getCode()).boxed().collect(Collectors.toSet()).size());
        System.out.println(System.currentTimeMillis() - start);
        System.out.println(hs300Pool.getStockPool().size() + zxbPool.getStockPool().size() + cybPool.getStockPool().size());
    }

    @Test
    public void setTest() {
        Set<Integer> set = new HashSet<>();
        set.add(1);
        System.out.println(set.add(1));
        System.out.println(set.addAll(Arrays.asList(1, 2, 3)));
        System.out.println(set.addAll(Arrays.asList(5, 5, 5, 5, 6)));
        System.out.println(set.addAll(Arrays.asList(2, 5)));
        System.out.println(set.addAll(Arrays.asList(10, 11, 12)));
    }
}
