package nju.quadra.quantra.data;

import nju.quadra.quantra.pool.CustomPool;
import nju.quadra.quantra.pool.CybPool;
import nju.quadra.quantra.pool.HS300Pool;
import nju.quadra.quantra.pool.ZxbPool;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
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
    public void setMultiPools() {
        CustomPool cp1 = new CustomPool("自选板块1", Arrays.asList(1, 2, 3, 4, 5));
        CustomPool cp2 = new CustomPool("自选板块2", Arrays.asList(100, 101, 102));
        CustomPool cp3 = new CustomPool("自选板块3", Arrays.asList(11, 12, 13, 14, 15));

        CustomPool cpC1 = CustomPool.createPoolFromFile("自选板块1");
        Assert.assertEquals(cp1.getStockPool().size(), 5);

    }
}
