package nju.quadra.quantra.strategy;

/**
 * Created by adn55 on 2017/4/3.
 */
public class CustomStrategy extends AbstractStrategy {

    public String code;

    public CustomStrategy(String name, int freq, long time, String code) {
        super(name, "custom", freq, time);
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return "自定义策略（持有期 " + freq + " 天）";
    }

}
