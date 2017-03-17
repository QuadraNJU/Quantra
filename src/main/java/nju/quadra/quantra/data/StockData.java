package nju.quadra.quantra.data;

import nju.quadra.quantra.data.StockBaseProtos.StockBase;
import nju.quadra.quantra.data.StockBaseProtos.StockBase.StockInfo;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by adn55 on 2017/3/3.
 */
public class StockData {

    private static final String DATA_FILE = "stock_data.protobuf";
    private static StockBase base;
    private static List<StockInfoPtr> ptrList;
    private static List<StockInfoPtr> index;
    public static int size;
    public static String latest;

    private static List<StockInfo> getList() {
        if (base == null) {
            try {
                FileInputStream is = new FileInputStream(DATA_FILE);
                base = StockBaseProtos.StockBase.parseFrom(is);
                is.close();
                size = base.getInfoList().size();
                latest = base.getInfoList().get(0).getDate();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
