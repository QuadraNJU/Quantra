package nju.quadra.quantra.data;

import nju.quadra.quantra.strategy.AbstractStrategy;
import nju.quadra.quantra.strategy.StubStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by MECHREVO on 2017/3/30.
 */
public class StrategyData {
    private List<AbstractStrategy> strategyList;

    public static List<AbstractStrategy> getStrategyList(){
        return Arrays.asList(new StubStrategy("test","stub",1));
    }

    public void addStrategy(AbstractStrategy strategy){

    }

    public void removeStrategy(AbstractStrategy strategy){

    }
}
