package nju.quadra.quantra.utils;

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

}
