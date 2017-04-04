package nju.quadra.quantra.pool;

import nju.quadra.quantra.pool.index.BaseIndex;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by adn55 on 2017/4/4.
 */
public class BaseIndexTest {

    @Test
    public void testHS300() {
        Assert.assertTrue(BaseIndex.HS300.getDataObject().size() > 0);
    }

    @Test
    public void testZXBZ() {
        Assert.assertTrue(BaseIndex.ZXBZ.getDataObject().size() > 0);
    }

    @Test
    public void testCYBZ() {
        Assert.assertTrue(BaseIndex.CYBZ.getDataObject().size() > 0);
    }

}
