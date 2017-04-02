package nju.quadra.quantra.data;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created by RaUkonn on 2017/4/2.
 */
public class stockPoolDataTest {
    @Test
    public void stockPoolCustomTest() {
        Set<Integer> pool = StockPoolData.getStockPool("custom_pool");
        Assert.assertEquals(pool.size(), 0);
        StockPoolData.addStock(1, "custom_pool");
        StockPoolData.addStockList(new LinkedList<>(Arrays.asList(1, 1, 1, 10, 11, 12, 13, 14, 15)), "custom_pool");
        pool = StockPoolData.getStockPool("custom_pool");
        Assert.assertEquals(pool.size(), 7);
        StockPoolData.removeStockList(new LinkedList<>(Arrays.asList(1, 1, 1, 10, 11, 12, 13, 14, 15)), "custom_pool");
        pool = StockPoolData.getStockPool("custom_pool");
        Assert.assertEquals(pool.size(), 0);
    }
}
