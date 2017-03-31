package nju.quadra.quantra.strategy;

/**
 * Created by RaUkonn on 2017/3/31.
 */
public class PeriodStrategy extends AbstractStrategy{
    public int period;

    public PeriodStrategy(String name, String type, int freq, long time, int period) {
        super(name, type, freq, time);
        this.period = period;
    }

    @Override
    public String getCode() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }
}
