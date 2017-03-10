package nju.quadra.quantra.data;

import nju.quadra.quantra.data.StockBaseProtos.StockBase.StockInfo;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by adn55 on 2017/3/3.
 */
public class ReadDataTest {

    @Test
    public void testReadProtobuf() {
        long time = System.nanoTime();
        List<StockInfo> list = StockData.getList();
        if (list == null || list.size() == 0) {
            Assert.fail();
        } else {
            System.out.println("Read " + list.size() + " lines from file, took " + (System.nanoTime() - time) + " ns");
        }
    }

    @Test
    public void testGetByCode() {
        long time = System.nanoTime();
        List<StockInfo> list = StockData.getByCode(300012);
        Assert.assertEquals(1129, list.size());
        System.out.print("Using " + (System.nanoTime() - time) / 1000000.0 + " ms");
    }
}
