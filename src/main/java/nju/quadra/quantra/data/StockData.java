package nju.quadra.quantra.data;

import com.alibaba.fastjson.JSONReader;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import javafx.application.Platform;
import javafx.scene.control.Label;
import nju.quadra.quantra.utils.DateUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by adn55 on 2017/3/3.
 */
public class StockData {

    public static final String JSON_FILE = "stock_data.json";
    public static final String CSV_FILE = "stock_data.csv";
    private static List<StockInfo> infoList;
    private static List<StockInfoPtr> ptrList;
    private static List<StockInfoPtr> index;
    public static int size;
    public static String latest;

    public static void loadJSON(Label status) {
        try {
            JSONReader jsonReader = new JSONReader(new InputStreamReader(new FileInputStream(JSON_FILE), "UTF-8"));
            infoList = new ArrayList<>();
            jsonReader.startArray();
            while (jsonReader.hasNext()) {
                jsonReader.startArray();
                infoList.add(new StockInfo(
                        jsonReader.readInteger(),
                        jsonReader.readString(),
                        jsonReader.readObject(float.class),
                        jsonReader.readObject(float.class),
                        jsonReader.readObject(float.class),
                        jsonReader.readObject(float.class),
                        jsonReader.readInteger(),
                        jsonReader.readObject(float.class),
                        jsonReader.readInteger(),
                        jsonReader.readString(),
                        jsonReader.readString(),
                        jsonReader.readString()
                ));
                jsonReader.endArray();
            }
            jsonReader.endArray();
            jsonReader.close();
            if (infoList != null) {
                size = infoList.size();
                if (size > 0) {
                    latest = infoList.get(0).getDate();
                    DateUtil.currentDate = latest;
                }
            }
        } catch (Exception e) {
            loadCSV(status);
        }
    }

    private static void loadCSV(Label status) {
        try {
            File file = new File(CSV_FILE);
            long fileSize = file.length(), readedSize = 0, progress = 0;
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            FileOutputStream os = new FileOutputStream(JSON_FILE);
            boolean firstLine = true;
            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                String[] items = line.split("\\t");
                try {
                    int volume = Integer.parseInt(items[6]);
                    if (volume > 0) {
                        if (firstLine) {
                            os.write('[');
                            firstLine = false;
                        } else {
                            os.write(',');
                            os.write('\n');
                        }
                        String name = items[9].replace("Ａ", "A").replace("S ", "S/").replace(" ", "");
                        String item = "[" + items[0] + ",\"" + items[1] + "\"," + items[2] + "," + items[3] + "," + items[4] + "," + items[5] + "," + items[6] + "," + items[7] + "," + items[8]
                                + ",\"" + name + "\",\"" + items[10] + "\",\"" + PinyinHelper.getShortPinyin(name.replace("S/", "").replace("*", "")).toLowerCase() + "\"]";
                        os.write(item.getBytes("UTF-8"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                readedSize += line.length() + 4;
                long newProgress = readedSize * 100 / fileSize;
                if (status != null && newProgress > progress) {
                    Platform.runLater(() -> status.setText("正在转换数据（" + newProgress + "%）"));
                    progress = newProgress;
                }
            }
            os.write(']');
            os.flush();
            os.close();
            loadJSON(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<StockInfo> getList() {
        if (infoList != null) {
            return infoList;
        } else {
            return Collections.emptyList();
        }
    }

    public static List<StockInfoPtr> getPtrList() {
        if (ptrList == null) {
            List<StockInfo> infoList = getList();
            ptrList = new ArrayList<>();
            StockInfoPtr ptr = null;
            for (StockInfo info : infoList) {
                if (ptr != null && ptr.get().getCode() == info.getCode()) {
                    ptr = new StockInfoPtr(info, ptr);
                } else {
                    ptr = new StockInfoPtr(info);
                }
                ptrList.add(ptr);
            }
            getIndex();
        }
        return ptrList;
    }

    public static List<StockInfoPtr> getIndex() {
        if (index == null) {
            index = getByDate(latest);
        }
        return index;
    }

    public static List<StockInfoPtr> getByDate(String date) {
        return getPtrList().stream().filter(ptr -> ptr.get().getDate().equals(date)).collect(Collectors.toList());
    }

    public static List<StockInfoPtr> getByCode(int code) {
        return getPtrList().stream().filter(ptr -> ptr.get().getCode() == code).collect(Collectors.toList());
    }

}
