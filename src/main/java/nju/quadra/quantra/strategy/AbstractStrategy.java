package nju.quadra.quantra.strategy;

/**
 * Created by MECHREVO on 2017/3/30.
 */
public abstract class AbstractStrategy {
    public String name, type;
    public int freq;
    public long time;

    public AbstractStrategy(String name, String type, int freq, long time) {
        this.name = name;
        this.type = type;
        this.freq = freq;
        this.time = time;
    }

    public abstract String getCode();

    public abstract String getDescription();
}
