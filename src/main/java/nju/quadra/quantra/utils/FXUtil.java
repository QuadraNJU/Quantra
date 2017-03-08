package nju.quadra.quantra.utils;

import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.net.URL;

/**
 * Created by adn55 on 2017/3/9.
 */
public class FXUtil {

    public static void loadFXML(Object root, URL location) throws IOException {
        FXMLLoader loader = new FXMLLoader(location);
        loader.setRoot(root);
        loader.setController(root);
        loader.load();
    }

    public static void loadFXMLAsync(Object root, URL location, Then then) {
        new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    loadFXML(root, location);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                then.then();
                return null;
            }
        }).start();
    }

    public interface Then {
        void then();
    }

}
