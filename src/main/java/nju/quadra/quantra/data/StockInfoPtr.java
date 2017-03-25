package nju.quadra.quantra.data;

/**
 * Created by adn55 on 2017/3/15.
 */
public class StockInfoPtr {

    private StockInfo today;
    private StockInfoPtr prev;

    StockInfoPtr(StockInfo today) {
        this.today = today;
    }

    StockInfoPtr(StockInfo today, StockInfoPtr tomorrow) {
        this.today = today;
        tomorrow.prev = this;
    }

    public StockInfo get() {
        return today;
    }

    public StockInfoPtr prev() {
        return prev;
    }

}
