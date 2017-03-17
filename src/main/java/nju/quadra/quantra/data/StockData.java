package nju.quadra.quantra.data;

import com.github.stuxuhai.jpinyin.PinyinHelper;
import javafx.application.Platform;
import javafx.scene.control.Label;
import nju.quadra.quantra.data.StockBaseProtos.StockBase;
import nju.quadra.quantra.data.StockBaseProtos.StockBase.StockInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by adn55 on 2017/3/3.
 */
public class StockData {

    public static final String DATA_FILE = "stock_data.protobuf";
    public static final String CSV_FILE = "stock_data.csv";
    private static StockBase base;
    private static List<StockInfoPtr> ptrList;
    private static List<StockInfoPtr> index;
    public static int size;
    public static String latest;

    public static void loadProtobuf(Label status) {
        try {
            FileInputStream is = new FileInputStream(DATA_FILE);
            base = StockBaseProtos.StockBase.parseFrom(is);
            is.close();
        } catch (FileNotFoundException e) {
            loadCSV(status);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (base != null) {
            size = base.getInfoList().size();
            if (size > 0) {
                latest = base.getInfoList().get(0).getDate();
            }
        }
    }

    private static void loadCSV(Label status) {
        try {
            File file = new File(CSV_FILE);
            long fileSize = file.length(), readedSize = 0, progress = 0;
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StockBase.Builder builder = StockBase.newBuilder();
            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                String[] items = line.split("\\t");
                try {
                    int volume = Integer.parseInt(items[6]);
                    if (volume > 0) {
                        StockBase.StockInfo.Builder infoBuilder = StockBase.StockInfo.newBuilder();
                        String name = items[9].replace("Ａ", "A").replace("S ", "S/").replace(" ", "");
                        infoBuilder.setSerial(Integer.parseInt(items[0]))
                                .setDate(items[1])
                                .setOpen(Float.parseFloat(items[2]))
                                .setHigh(Float.parseFloat(items[3]))
                                .setLow(Float.parseFloat(items[4]))
                                .setClose(Float.parseFloat(items[5]))
                                .setVolume(volume)
                                .setAdjClose(Float.parseFloat(items[7]))
                                .setCode(Integer.parseInt(items[8]))
                                .setName(name)
                                .setMarket(items[10])
                                .setPinyin(PinyinHelper.getShortPinyin(name.replace("S/", "").replace("*", "")).toLowerCase());
                        builder.addInfo(infoBuilder.build());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                readedSize += line.length() + 2;
                long newProgress = readedSize * 100 / fileSize;
                if (status != null && newProgress > progress) {
                    Platform.runLater(() -> status.setText("正在转换数据（" + newProgress + "%）"));
                    progress = newProgress;
                }
            }
            FileOutputStream os = new FileOutputStream(DATA_FILE);
            StockBase buildBase = builder.build();
            buildBase.writeTo(os);
            os.close();
            reader.close();
            base = buildBase;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<StockInfo> getList() {
        if (base == null) {
            loadProtobuf(null);
        }
        if (base != null) {
            return base.getInfoList();
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
