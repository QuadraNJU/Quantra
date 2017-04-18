package nju.quadra.quantra.data;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Created by RaUkonn on 2017/4/18.
 */
public class BackTestHistory {

    public long timestamp;
    public long strategyKey;
    public String strategyDescription;
    public String poolName;
    public LocalDate dateStart, dateEnd;

    public JSONObject resultObject;
    public ArrayList<String> dates = new ArrayList<>();
    public ArrayList<Number> cashs = new ArrayList<>();
    public ArrayList<Number> earnRates = new ArrayList<>();
    public ArrayList<Number> baseEarnRates = new ArrayList<>();

    public BackTestHistory() {
    }

    public BackTestHistory(long timestamp, long strategyKey, String strategyDescription, String poolName, LocalDate dateStart, LocalDate dateEnd, JSONObject resultObject, ArrayList<String> dates, ArrayList<Number> cashs, ArrayList<Number> earnRates, ArrayList<Number> baseEarnRates) {
        this.timestamp = timestamp;
        this.strategyKey = strategyKey;
        this.strategyDescription = strategyDescription;
        this.poolName = poolName;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.resultObject = resultObject;
        this.dates = dates;
        this.cashs = cashs;
        this.earnRates = earnRates;
        this.baseEarnRates = baseEarnRates;
    }

    public String getStrategyDescription() {
        return strategyDescription;
    }

    public String getPoolName() {
        return poolName;
    }

    @JSONField(serialize = false)
    public String getTime() {
        DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return sdf.format(timestamp);
    }

    @JSONField(serialize = false)
    public float getRelativeRate() {
        return (resultObject.getFloat("annualized") - resultObject.getFloat("base_annualized")) * 100;
    }

}
