package nju.quadra.quantra.data;

import com.alibaba.fastjson.*;
import nju.quadra.quantra.strategy.*;

import java.io.*;
import java.util.*;

/**
 * Created by MECHREVO on 2017/3/30.
 */
public class StrategyData {

    private static final String STRATEGY_FILE = "data/strategy.json";
    private static LinkedList<AbstractStrategy> strategyList = new LinkedList<>();

    public static List<AbstractStrategy> getStrategyList() {
        loadFromFile();
        return Collections.unmodifiableList(strategyList);
    }

    public static void addStrategy(AbstractStrategy strategy) {
        strategy.time = System.currentTimeMillis();
        strategyList.addFirst(strategy);
        saveToFile();
    }

    public static void removeStrategy(AbstractStrategy strategy) {
        ListIterator<AbstractStrategy> itr = strategyList.listIterator();
        while (itr.hasNext()) {
            if (itr.next().time == strategy.time) {
                itr.remove();
                break;
            }
        }
        saveToFile();
    }

    private static void loadFromFile() {
        try {
            InputStream is = new FileInputStream(STRATEGY_FILE);
            byte[] buf = new byte[is.available()];
            is.read(buf);
            is.close();
            JSONArray jsonArray = JSON.parseArray(new String(buf));
            strategyList.clear();
            for(int i = jsonArray.size() - 1; i >= 0; i--) {
                JSONObject o = jsonArray.getJSONObject(i);
                String name = o.getString("name");
                String type = o.getString("type");
                long time = o.getLongValue("time");
                int freq = o.getIntValue("freq");
                switch (type) {
                    case "momentum":
                    case "mean_reversion":
                        strategyList.add(new PeriodStrategy(name, type, freq, time, o.getIntValue("period")));
                        break;
                    case "stub":
                        strategyList.add(new StubStrategy(name));
                        break;
                    case "custom":
                        //todo: custom strategy
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveToFile() {
        try {
            File file = new File(STRATEGY_FILE);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }
            JSONWriter writer = new JSONWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            writer.startArray();
            for (AbstractStrategy as : strategyList) {
                JSONObject o = new JSONObject();
                o.put("name", as.name);
                o.put("type", as.type);
                o.put("time", as.time);
                o.put("freq", as.freq);
                switch (as.type) {
                    case "momentum":
                    case "mean_reversion":
                        o.put("period", ((PeriodStrategy) as).period);
                        break;
                    case "custom":
                        //todo: custom strategy
                        break;
                }
                writer.writeObject(o);
            }
            writer.endArray();
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
