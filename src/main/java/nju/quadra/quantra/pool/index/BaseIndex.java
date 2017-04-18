package nju.quadra.quantra.pool.index;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by adn55 on 2017/4/4.
 */
public enum BaseIndex {
    NONE("不显示参考指数", ""),
    HS300("沪深300指数", "hs300.json"),
    ZXBZ("中小板指", "zxbz.json"),
    CYBZ("创业板指", "cybz.json");

    public String name, dataFile;

    private JSONObject jsonObject = new JSONObject();

    BaseIndex(String name, String dataFile) {
        this.name = name;
        this.dataFile = dataFile;
    }

    @Override
    public String toString() {
        return name;
    }

    public JSONObject getDataObject() {
        if (jsonObject.isEmpty() && !dataFile.isEmpty()) {
            try {
                JSONReader reader = new JSONReader(new InputStreamReader(getClass().getResourceAsStream(dataFile), "UTF-8"));
                jsonObject = reader.readObject(JSONObject.class);
                reader.close();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }
}
