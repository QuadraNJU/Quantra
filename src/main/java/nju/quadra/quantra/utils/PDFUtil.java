package nju.quadra.quantra.utils;

import com.alibaba.fastjson.JSONObject;
import nju.quadra.quantra.data.BackTestHistory;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by adn55 on 2017/4/19.
 */
public class PDFUtil {

    private static String template = "";

    public static void createPage(BackTestHistory history, String xmlFile) throws Exception {
        InputStream is = PDFUtil.class.getResourceAsStream("pdf_template.html");
        byte[] buf = new byte[is.available()];
        is.read(buf);
        is.close();
        template = new String(buf, "UTF-8");

        setArg("now", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
        setArg("strategy", history.strategyDescription);
        setArg("date_range", history.dateStart + " - " + history.dateEnd);
        setArg("pool", history.poolName);

        JSONObject resultObject = history.resultObject;
        DecimalFormat df = new DecimalFormat("#.##");
        setArg("annual", df.format(resultObject.getFloat("annualized") * 100) + "%");
        setArg("base_annual", df.format(resultObject.getFloat("base_annualized") * 100) + "%");
        setArg("win_rate", df.format(resultObject.getFloat("win_rate") * 100) + "%");
        setArg("alpha", df.format(resultObject.getFloat("alpha")));
        setArg("beta", df.format(resultObject.getFloat("beta")));
        setArg("sharp", df.format(resultObject.getFloat("sharp")));
        setArg("max_drawdown", df.format(resultObject.getFloat("max_drawdown")));

        setArg("details", IntStream.range(0, history.dates.size())
                .mapToObj(i -> "<tr><td>" + history.dates.get(i) + "</td><td>" + history.cashs.get(i)+ "</td><td>"
                        + history.earnRates.get(i) + "</td><td>" + history.baseEarnRates.get(i) + "</td></tr>")
                .collect(Collectors.joining())
        );

        FileOutputStream os = new FileOutputStream(xmlFile);
        os.write(template.getBytes("UTF-8"));
        os.close();
    }

    private static void setArg(String name, String value) {
        template = template.replace("__" + name + "__", value);
    }

}
