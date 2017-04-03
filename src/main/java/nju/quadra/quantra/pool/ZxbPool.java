package nju.quadra.quantra.pool;

import nju.quadra.quantra.data.StockData;
import nju.quadra.quantra.data.StockInfoPtr;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by RaUkonn on 2017/4/3.
 */
public class ZxbPool extends AbstractPool {
    public ZxbPool() {
        this.name = "ZXB";
        List<StockInfoPtr> list = StockData.getPtrList();
        this.stockPool = list.stream()
                .mapToInt(i -> i.get().getCode())
                .filter(i -> (2000 <= i && i < 3000))
                .boxed().collect(Collectors.toSet());
    }

    @Override
    public List<Integer> getStockPool() {
        return new ArrayList<>(stockPool);
    }
}
