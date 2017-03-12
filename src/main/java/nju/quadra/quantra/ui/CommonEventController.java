package nju.quadra.quantra.ui;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;


/**
 * Created by RaUkonn on 2017/3/11.
 */
public class CommonEventController {
    public static void onPlusClickedEvent(MouseEvent t, int code) {
        MaterialDesignIconView icon = (MaterialDesignIconView)t.getSource();
        Tooltip tp = new Tooltip("比较队列已满，无法再添加股票");
        tp.setAutoHide(true);
        if(!StockCompareVC.chosenStocks.contains(code)) {
            if (StockCompareVC.addToList(code) != -1) {
                icon.setFill(Color.RED);
                tp.setText("已加入比较队列");
            }
        } else {
            icon.setFill(Color.valueOf("#eceff1"));
            tp.setText("已移出比较队列");
            StockCompareVC.removeFromList(code);
        }
        tp.show(icon, t.getScreenX(), t.getScreenY());
    }

}
