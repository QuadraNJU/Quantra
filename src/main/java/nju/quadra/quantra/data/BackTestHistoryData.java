package nju.quadra.quantra.data;

import com.alibaba.fastjson.*;

import java.io.*;
import java.util.*;

/**
 * Created by adn55 on 2017/4/5.
 */
public class BackTestHistoryData {

    private static final String HISTORY_FILE = "data/backtest_history.json";
    private static List<BackTestHistory> backTestHistories = new ArrayList<>();

    public static List<BackTestHistory> getBackTestHistories() {
        loadFromFile();
        return Collections.unmodifiableList(backTestHistories);
    }

    public static void pushBackTestInfo(BackTestHistory info) {
        loadFromFile();
        while (backTestHistories.size() >= 20) {
            backTestHistories.remove(0);
        }
        backTestHistories.add(info);
        saveToFile();
    }

    private static void loadFromFile() {
        try {
            JSONReader reader = new JSONReader(new InputStreamReader(new FileInputStream(HISTORY_FILE), "UTF-8"));
            backTestHistories = reader.readObject(new TypeReference<List<BackTestHistory>>() {});
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveToFile() {
        try {
            JSONWriter writer = new JSONWriter(new OutputStreamWriter(new FileOutputStream(HISTORY_FILE), "UTF-8"));
            writer.writeObject(backTestHistories);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
