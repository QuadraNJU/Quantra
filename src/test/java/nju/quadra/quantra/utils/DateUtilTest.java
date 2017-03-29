package nju.quadra.quantra.utils;

import nju.quadra.quantra.utils.DateUtil;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;

/**
 * Created by RaUkonn on 2017/3/9.
 */
public class DateUtilTest {
    @Test
    public void testCompare() {
        Assert.assertEquals(0, DateUtil.compare("5/2/14", "5/2/14"));
        Assert.assertEquals(-1, DateUtil.compare("5/1/14", "5/2/14"));
        Assert.assertEquals(1, DateUtil.compare("5/3/14", "5/2/14"));
    }

    @Test
    public void testParseLocalDate() {
        Assert.assertEquals(LocalDate.of(2014, 5, 2), DateUtil.parseLocalDate("5/2/14"));
    }

    @Test
    public void testLocalDateToString() {
        Assert.assertEquals("5/2/14", DateUtil.localDateToString(LocalDate.of(2014, 5, 2)));
    }
}
