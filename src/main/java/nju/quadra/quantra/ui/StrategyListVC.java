package nju.quadra.quantra.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import nju.quadra.quantra.data.StrategyData;
import nju.quadra.quantra.strategy.AbstractStrategy;
import nju.quadra.quantra.utils.FXUtil;

import java.io.IOException;

/**
 * Created by MECHREVO on 2017/3/30.
 */
public class StrategyListVC extends Pane {
    @FXML
    private VBox vboxStrategy;

    public StrategyListVC() throws IOException {
        FXUtil.loadFXML(this, getClass().getResource("assets/strategyList.fxml"));
        updateStrategy();
    }

    public void updateStrategy() {
        for (AbstractStrategy strategy : StrategyData.getStrategyList()) {
            try {
                vboxStrategy.getChildren().add(new StrategyItemVC(strategy));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onAddAction() throws IOException {
        new Thread(() -> {
            try {
                UIContainer.showLoading();
                UIContainer.loadContent(new StrategyEditVC(null));
                UIContainer.hideLoading();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void onArenaAction() {
        new Thread(() -> {
            try {
                UIContainer.showLoading();
                UIContainer.loadContent(new StrategyArenaVC());
                UIContainer.hideLoading();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    class StrategyItemVC extends HBox {
        @FXML
        private Label labelTitle;
        @FXML
        private Label labelSubTitle;

        private AbstractStrategy strategy;

        public StrategyItemVC(AbstractStrategy strategy) throws IOException {
            FXUtil.loadFXML(this, getClass().getResource("assets/strategy.fxml"));
            this.strategy = strategy;
            labelTitle.setText(strategy.name);
            labelSubTitle.setText(strategy.getDescription());
        }

        @FXML
        private void onBackTestAction() throws IOException {
            UIContainer.loadContent(new BackTestVC(strategy));
        }

        @FXML
        private void onModifyAction() throws IOException {
            UIContainer.loadContent(new StrategyEditVC(strategy));
        }

        @FXML
        private void onDeleteAction() throws IOException {
            UIContainer.confirm("确认", "确认删除这条策略吗？", t -> {
                StrategyData.removeStrategy(strategy);
                try {
                    UIContainer.loadContent(new StrategyListVC());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }


    }
}
