package nju.quadra.quantra.strategy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
        try {
            InputStream is = getClass().getResourceAsStream("../python/" + type + ".py");
            byte[] buf = new byte[is.available()];
            is.read(buf);
            is.close();
            return new String(buf, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public Map<String, Object> getParams() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("period", period);
        return map;
    }

    @Override
    public String getDescription() {
        switch (type) {
            case "momentum":
                return "动量策略（持有期 " + freq + " 天，形成期 " + period + " 天）";
            case "mean_reversion":
                return "均值回归策略（持有期 " + freq + " 天，" + period + " 日均值）";
            default:
                return "假策略";
        }
    }
}
