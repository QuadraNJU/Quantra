package nju.quadra.quantra.ui.controller;/**
 * Created by RaUkonn on 2017/3/7.
 */

import com.jfoenix.controls.JFXTextField;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOError;
import java.io.IOException;

public class HeaderView extends Stage {
    @FXML
    JFXTextField fieldSearch;

    public HeaderView() throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("nju/quadra/quantra/ui/assets/market.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        this.initStyle(StageStyle.TRANSPARENT);
        this.setScene(scene);
        this.setResizable(false);
    }
}
