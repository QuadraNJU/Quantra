package nju.quadra.quantra.data;

import nju.quadra.quantra.utils.DateUtil;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;

/**
 * Created by RaUkonn on 2017/3/9.
 */
public class DateUtilTest {
    @Test
    public void testReadProtobuf() {
        String date1 = "5/2/14";
        String date2 = "5/2/14";
        Assert.assertEquals(0, DateUtil.compare(date1, date2));
    }

    @Test
    public void testParseLocalDate() {
        String date1 = "5/2/14";
        Assert.assertEquals(LocalDate.of(2014, 5, 2), DateUtil.parseLocalDate(date1));
    }

    @Test
    public void testLocalDateToString() {
        LocalDate date = LocalDate.of(2014, 5, 2);
        Assert.assertEquals("5/2/14", DateUtil.localDateToString(date));
    }
}
