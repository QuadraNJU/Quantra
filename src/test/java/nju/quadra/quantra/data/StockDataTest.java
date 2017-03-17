package nju.quadra.quantra.data;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by adn55 on 2017/3/3.
 */
public class StockDataTest {

    @Test
    public void testGetPtrList() {
        long time = System.nanoTime();
        List<StockInfoPtr> list = StockData.getPtrList();
        Assert.assertNotNull(list);
        Assert.assertTrue(list.size() > 0);
        System.out.println("Read " + list.size() + " lines from file, took " + (System.nanoTime() - time) + " ns");
    }

    @Test
    public void testGetIndex() {
        long time = System.nanoTime();
        StockData.loadProtobuf(null);
        List<StockInfoPtr> list = StockData.getIndex();
        Assert.assertNotNull(list);
        Assert.assertTrue(list.size() > 0);
        System.out.println("Get " + list.size() + " indices, took " + (System.nanoTime() - time) + " ns");
    }

    @Test
    public void testGetByDate() {
        long time = System.nanoTime();
        List<StockInfoPtr> list = StockData.getByDate("4/29/14");
        Assert.assertNotNull(list);
        Assert.assertTrue(list.size() > 0);
        System.out.println("getByDate took " + (System.nanoTime() - time) + " ns");
    }

    @Test
    public void testGetByDate2() {
        List<StockInfoPtr> list = StockData.getByDate("1/31/14");
        Assert.assertNotNull(list);
        Assert.assertEquals(0, list.size());
    }

    @Test
    public void testGetByCode() {
        long time = System.nanoTime();
        List<StockInfoPtr> list = StockData.getByCode(1);
        Assert.assertNotNull(list);
        Assert.assertTrue(list.size() > 0);
        System.out.println("getByCode took " + (System.nanoTime() - time) + " ns");
    }

    @Test
    public void testGetByCode2() {
        List<StockInfoPtr> list = StockData.getByCode(333333);
        Assert.assertNotNull(list);
        Assert.assertEquals(0, list.size());
    }

}
