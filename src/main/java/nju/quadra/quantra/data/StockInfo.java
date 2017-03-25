package nju.quadra.quantra.data;

/**
 * Created by adn55 on 17/3/25.
 */
public class StockInfo {

    private int serial;
    private String date;
    private float open, high, low, close;
    private int volume;
    private float adjClose;
    private int code;
    private String name, market, pinyin;

    public StockInfo(int serial, String date, float open, float high, float low, float close, int volume, float adjClose, int code, String name, String market, String pinyin) {
        this.serial = serial;
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.adjClose = adjClose;
        this.code = code;
        this.name = name;
        this.market = market;
        this.pinyin = pinyin;
    }

    public int getSerial() {
        return serial;
    }

    public String getDate() {
        return date;
    }

    public float getOpen() {
        return open;
    }

    public float getHigh() {
        return high;
    }

    public float getLow() {
        return low;
    }

    public float getClose() {
        return close;
    }

    public int getVolume() {
        return volume;
    }

    public float getAdjClose() {
        return adjClose;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getMarket() {
        return market;
    }

    public String getPinyin() {
        return pinyin;
    }

}
