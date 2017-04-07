package nju.quadra.quantra.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import javafx.scene.paint.Color;
import nju.quadra.quantra.strategy.AbstractStrategy;
import nju.quadra.quantra.strategy.PeriodStrategy;
import nju.quadra.quantra.ui.chart.QuantraLineChart;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by adn55 on 2017/3/29.
 */
public class PPAPTest {

    private void test(AbstractStrategy strategy) throws Exception {
        PPAP.extractEngine("data/python");
        strategy.extract("data/python");
        PPAP ppap = new PPAP("python engine.py", "data/python");
        ppap.sendInput("{\"start_date\":\"1/1/13\",\"end_date\":\"12/31/13\",\"universe\":[300001],\"frequency\":10,\"strategy\":\"momentum\",\"params\":{\"period\":20}}");
        ppap.setOutputHandler(System.out::println);
        ppap.setErrorHandler(System.err::println);
        ppap.waitEnd();
    }

    @Test
    public void testArena() throws Exception {
        PPAP.extractEngine("data/python");
        PPAP.extractEngine("engine_for_arena", "data/python");
        new PeriodStrategy("test", "momentum", 10, 0, 20).extract("data/python");
        PPAP ppap = new PPAP("python engine_for_arena.py", "data/python");
        ppap.sendInput("[{\"start_date\":\"1/1/13\",\"end_date\":\"12/31/13\",\"universe\":[300001],\"frequency\":10,\"strategy\":\"momentum\",\"params\":{\"period\":20}},"
                + "{\"start_date\":\"1/1/12\",\"end_date\":\"12/31/12\",\"universe\":[300001],\"frequency\":10,\"strategy\":\"momentum\",\"params\":{\"period\":20}},"
                + "{\"start_date\":\"1/1/12\",\"end_date\":\"12/31/12\",\"universe\":[300001],\"frequency\":10,\"strategy\":\"momentum\",\"params\":{\"period\":20}},"
                + "{\"start_date\":\"1/1/12\",\"end_date\":\"12/31/12\",\"universe\":[300001],\"frequency\":10,\"strategy\":\"momentum\",\"params\":{\"period\":20}},"
                + "{\"start_date\":\"1/1/12\",\"end_date\":\"12/31/12\",\"universe\":[300001],\"frequency\":10,\"strategy\":\"momentum\",\"params\":{\"period\":20}},"
                + "{\"start_date\":\"1/1/12\",\"end_date\":\"12/31/12\",\"universe\":[300001],\"frequency\":10,\"strategy\":\"momentum\",\"params\":{\"period\":20}},"
                + "{\"start_date\":\"1/1/12\",\"end_date\":\"12/31/12\",\"universe\":[300001],\"frequency\":10,\"strategy\":\"momentum\",\"params\":{\"period\":20}},"
                + "{\"start_date\":\"1/1/12\",\"end_date\":\"12/31/12\",\"universe\":[300001],\"frequency\":10,\"strategy\":\"momentum\",\"params\":{\"period\":20}}]");
        List<Number> abnormalReturnList = new ArrayList<>();
        List<Number> winRateList = new ArrayList<>();
        ppap.setOutputHandler(out -> {
            System.out.println(out);
        });
        ppap.waitEnd();
        System.out.println(abnormalReturnList);
        System.out.println(winRateList);
        ppap.setErrorHandler(System.err::println);

    }

    @Test
    public void testMomentum() throws Exception {
        test(new PeriodStrategy("test", "momentum", 10, 0, 20));
    }

    @Test
    public void testMeanReversion() throws Exception {
        test(new PeriodStrategy("test", "mean_reversion", 10, 0, 20));
    }

}
