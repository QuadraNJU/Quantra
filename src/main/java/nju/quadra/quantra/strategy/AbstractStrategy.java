package nju.quadra.quantra.strategy;

/**
 * Created by MECHREVO on 2017/3/30.
 */
public abstract class AbstractStrategy {
    public String name, type;
    public int freq;
    public long id;

    public AbstractStrategy(String name, String type, int freq) {
        this.name = name;
        this.type = type;
        this.freq = freq;
    }

    public abstract String getCode();

    public abstract String getDescription();
}
