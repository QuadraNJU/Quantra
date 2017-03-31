package nju.quadra.quantra.data;

import nju.quadra.quantra.strategy.AbstractStrategy;
import nju.quadra.quantra.strategy.StubStrategy;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by RaUkonn on 2017/3/31.
 */
public class StrategyDataTest {

    @Test
    public void test() {
        StrategyData.getStrategyList();
        StrategyData.addStrategy(new StubStrategy("Stub"));
        List<AbstractStrategy> list = StrategyData.getStrategyList();
        Assert.assertEquals(list.get(0).name, "Stub");
        StrategyData.removeStrategy(list.get(0));
    }

}
