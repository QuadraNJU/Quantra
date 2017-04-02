package nju.quadra.quantra.data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONWriter;

import java.io.*;
import java.util.*;

/**
 * Created by RaUkonn on 2017/4/2.
 */
public class StockPoolData {
    private static final String STOCK_POOL_DIR = "data/pools/";
    private static Set<Integer> stockPool = new HashSet<>();

    public static Set<Integer> getStockPool(String poolName) {
        loadFromFile(poolName);
        return stockPool;
    }

    public static void removeStock(int stock, String poolName) {
        loadFromFile(poolName);
        Iterator<Integer> itr = stockPool.iterator();
        while (itr.hasNext()) {
            if (itr.next() == stock) {
                itr.remove();
                break;
            }
        }
        saveToFile(poolName);
    }

    public static void removeStockList(List<Integer> stockList, String poolName) {
        loadFromFile(poolName);
        for (int i = stockList.size() - 1; i >= 0; i--) {
            if (stockPool.contains(stockList.get(i))) {
                stockPool.remove(stockList.get(i));
                stockList.remove(i);
            }
        }
        saveToFile(poolName);
    }

    public static void addStock(int stock, String poolName) {
        loadFromFile(poolName);
        stockPool.add(stock);
        saveToFile(poolName);
    }

    public static void addStockList(List<Integer> stockList, String poolName) {
        loadFromFile(poolName);
        for (Integer i : stockList) {
            stockPool.add(i);
        }
        saveToFile(poolName);
    }

    private static void loadFromFile(String poolName) {
        InputStream is = null;
        try {
            is = new FileInputStream(STOCK_POOL_DIR + poolName + ".json");
            byte[] poolsBytes = new byte[is.available()];
            is.read(poolsBytes);
            is.close();
            JSONArray stocks = JSON.parseArray(new String(poolsBytes, "utf-8"));
            stockPool.clear();

            for (int i = stocks.size() - 1; i >= 0; i--) {
                stockPool.add(stocks.getInteger(i));
            }
        } catch (IOException e) {
            saveToFile(poolName);
        }
    }


    private static void saveToFile(String poolName) {
        try {
            File file = new File(STOCK_POOL_DIR + poolName + ".json");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }
            JSONWriter writer = new JSONWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            writer.startArray();
            for (Integer i : stockPool) {
                writer.writeObject(i);
            }
            writer.endArray();
            writer.flush();
            writer.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
