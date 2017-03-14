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
    public static int size;
    public static String latest;

    public static List<StockInfo> getList() {
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
            ptrList = new ArrayList<>(size);
            StockInfo next = infoList.get(0);
            for (int i = 1; i < size; i++) {
                StockInfo curr = next;
                next = infoList.get(i);
                if (curr.getCode() == next.getCode()) {
                    ptrList.add(new StockInfoPtr(curr, next));
                } else {
                    ptrList.add(new StockInfoPtr(curr));
                }
            }
            ptrList.add(new StockInfoPtr(infoList.get(size - 1)));
        }
        return ptrList;
    }

    public static List<StockInfoPtr> getByDate(String date) {
        return getPtrList().stream().filter(ptr -> ptr.getToday().getDate().equals(date)).collect(Collectors.toList());
    }

    public static List<StockInfoPtr> getPtrByCode(int code) {
        return getPtrList().stream().filter(ptr -> ptr.getToday().getCode() == code).collect(Collectors.toList());
    }

    @Deprecated
    public static List<StockInfo> getByCode(int code) {
        List<StockInfo> result = new ArrayList<>();
        for (StockInfo i : getList()) {
            if (i.getCode() == code) result.add(i);
        }
        return result;
    }

}
