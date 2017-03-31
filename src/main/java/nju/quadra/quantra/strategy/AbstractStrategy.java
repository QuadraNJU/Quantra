package nju.quadra.quantra.strategy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by MECHREVO on 2017/3/30.
 */
public abstract class AbstractStrategy {

    public String name, type;
    public int freq;
    public long time;

    public AbstractStrategy(String name, String type, int freq, long time) {
        this.name = name;
        this.type = type;
        this.freq = freq;
        this.time = time;
    }

    public abstract String getCode();

    public abstract String getDescription();

    public void extract(String path) throws IOException {
        File filePath = new File(path);
        filePath.mkdirs();
        FileOutputStream os = new FileOutputStream(path + "/strategy.py");
        os.write(getCode().getBytes("UTF-8"));
        os.flush();
        os.close();
    }

}
