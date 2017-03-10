package nju.quadra.quantra.data;

import nju.quadra.quantra.data.StockBaseProtos.StockBase;
import nju.quadra.quantra.data.StockBaseProtos.StockBase.StockInfo;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adn55 on 2017/3/3.
 */
public class StockData {

    private static final String DATA_FILE = "stock_data.protobuf";
    private static StockBase base;
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
        return base.getInfoList();
    }

    public static List<StockInfo> getByCode(int code) {
        List<StockInfo> result = new ArrayList<>();
        for (StockInfo i : getList()) {
            if (i.getCode() == code) result.add(i);
        }
        return result;
    }

}
