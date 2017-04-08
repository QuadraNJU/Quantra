package nju.quadra.quantra.utils;

import nju.quadra.quantra.strategy.AbstractStrategy;
import nju.quadra.quantra.strategy.PeriodStrategy;
import org.junit.Test;

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
        PPAP.extractEngine("arena", "data/python");
        new PeriodStrategy("test", "momentum", 10, 0, 20).extract("data/python");
        PPAP ppap = new PPAP("python arena.py", "data/python");
        ppap.sendInput("[{\"start_date\":\"1/1/13\",\"end_date\":\"12/31/13\",\"universe\":[300001],\"frequency\":10,\"strategy\":\"momentum\",\"params\":{\"period\":20}},"
                + "{\"start_date\":\"1/1/12\",\"end_date\":\"12/31/12\",\"universe\":[300001],\"frequency\":10,\"strategy\":\"momentum\",\"params\":{\"period\":20}},"
                + "{\"start_date\":\"1/1/12\",\"end_date\":\"12/31/12\",\"universe\":[300001],\"frequency\":10,\"strategy\":\"momentum\",\"params\":{\"period\":20}},"
                + "{\"start_date\":\"1/1/12\",\"end_date\":\"12/31/12\",\"universe\":[300001],\"frequency\":10,\"strategy\":\"momentum\",\"params\":{\"period\":20}},"
                + "{\"start_date\":\"1/1/12\",\"end_date\":\"12/31/12\",\"universe\":[300001],\"frequency\":10,\"strategy\":\"momentum\",\"params\":{\"period\":20}},"
                + "{\"start_date\":\"1/1/12\",\"end_date\":\"12/31/12\",\"universe\":[300001],\"frequency\":10,\"strategy\":\"momentum\",\"params\":{\"period\":20}},"
                + "{\"start_date\":\"1/1/12\",\"end_date\":\"12/31/12\",\"universe\":[300001],\"frequency\":10,\"strategy\":\"momentum\",\"params\":{\"period\":20}},"
                + "{\"start_date\":\"1/1/12\",\"end_date\":\"12/31/12\",\"universe\":[300001],\"frequency\":10,\"strategy\":\"momentum\",\"params\":{\"period\":20}}]");
        ppap.setOutputHandler(System.out::println);
        ppap.setErrorHandler(System.err::println);
        ppap.waitEnd();
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
