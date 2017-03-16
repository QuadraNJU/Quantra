package nju.quadra.quantra.data;

/**
 * Created by adn55 on 2017/3/15.
 */
public class StockInfoPtr {

    private StockBaseProtos.StockBase.StockInfo today;
    private StockInfoPtr prev;

    StockInfoPtr(StockBaseProtos.StockBase.StockInfo today) {
        this.today = today;
    }

    StockInfoPtr(StockBaseProtos.StockBase.StockInfo today, StockInfoPtr tomorrow) {
        this.today = today;
        tomorrow.prev = this;
    }

    public StockBaseProtos.StockBase.StockInfo get() {
        return today;
    }

    public StockInfoPtr prev() {
        return prev;
    }

}
