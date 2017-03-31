package nju.quadra.quantra.utils;

import org.junit.Test;

/**
 * Created by adn55 on 2017/3/29.
 */
public class PPAPTest {

    @Test
    public void test() throws Exception {
        PPAP ppap = new PPAP("python engine.py", "python");
        ppap.sendInput("{\"start_date\":\"1/1/13\",\"end_date\":\"12/31/13\",\"universe\":[300001],\"frequency\":10,\"strategy\":\"momentum\"}");
        ppap.setOutputHandler(System.out::println);
        ppap.waitEnd();
    }

}
