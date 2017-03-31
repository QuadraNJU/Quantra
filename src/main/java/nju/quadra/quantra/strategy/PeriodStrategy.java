package nju.quadra.quantra.strategy;

import java.io.IOException;
import java.io.InputStream;

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
            return new String(buf, "UTF-8").replace("__PERIOD__", String.valueOf(period));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

    }

    @Override
    public String getDescription() {
        return null;
    }
}
