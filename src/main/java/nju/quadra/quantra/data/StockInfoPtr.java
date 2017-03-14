package nju.quadra.quantra.data;

/**
 * Created by adn55 on 2017/3/15.
 */
public class StockInfoPtr {

    private StockBaseProtos.StockBase.StockInfo today;
    private StockBaseProtos.StockBase.StockInfo yesterday;

    StockInfoPtr(StockBaseProtos.StockBase.StockInfo today) {
        this.today = today;
    }

    StockInfoPtr(StockBaseProtos.StockBase.StockInfo today, StockBaseProtos.StockBase.StockInfo yesterday) {
        this.today = today;
        this.yesterday = yesterday;
    }

    public StockBaseProtos.StockBase.StockInfo getToday() {
        return today;
    }

    public StockBaseProtos.StockBase.StockInfo getYesterday() {
        return yesterday;
    }

}
