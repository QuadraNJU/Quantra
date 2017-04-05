package nju.quadra.quantra.data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONWriter;
import nju.quadra.quantra.pool.CustomPool;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by adn55 on 2017/4/5.
 */
public class StockPoolData {

    private static final String POOL_FILE = "data/pool.json";
    private static HashMap<String, CustomPool> poolMap = new HashMap<>();

    public static Map<String, CustomPool> getPoolMap() {
        loadFromFile();
        return Collections.unmodifiableMap(poolMap);
    }

    public static void addPool(CustomPool pool) {
        loadFromFile();
        if (poolMap.containsKey(pool.name)) {
            removePool(pool);
        }
        poolMap.put(pool.name, pool);
        saveToFile();
    }

    public static void removePool(CustomPool pool) {
        loadFromFile();
        poolMap.remove(pool.name);
        saveToFile();
    }

    private static void loadFromFile() {
        try {
            InputStream is = new FileInputStream(POOL_FILE);
            byte[] buf = new byte[is.available()];
            is.read(buf);
            is.close();
            JSONObject jsonObject = JSON.parseObject(new String(buf, "UTF-8"));
            poolMap.clear();
            for (String key : jsonObject.keySet()) {
                poolMap.put(key, new CustomPool(key, jsonObject.getJSONArray(key).stream().map(x -> (Integer) x).collect(Collectors.toSet())));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveToFile() {
        try {
            File file = new File(POOL_FILE);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }
            JSONWriter writer = new JSONWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            writer.startObject();
            for (String key : poolMap.keySet()) {
                writer.writeKey(key);
                writer.writeValue(poolMap.get(key).getStockPool());
            }
            writer.endObject();
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
