package nju.quadra.quantra.ui;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import nju.quadra.quantra.data.StockData;
import nju.quadra.quantra.data.StockInfoPtr;
import nju.quadra.quantra.data.StockPoolData;
import nju.quadra.quantra.pool.AbstractPool;
import nju.quadra.quantra.pool.CustomPool;
import nju.quadra.quantra.utils.FXUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by RaUkonn on 2017/4/4.
 */
public class PoolEditVC extends BorderPane {

    @FXML
    private TableView<SimpleStockInfo> table;
    @FXML
    private Label labelTitle;
    @FXML
    private JFXTextField fieldName, searchBox;

    private static final int MIN_SIZE = 100;
    private ObservableList<SimpleStockInfo> infos = FXCollections.observableArrayList();
    private FilteredList<SimpleStockInfo> filteredInfos = new FilteredList<>(infos);
    private CustomPool pool;

    public PoolEditVC(CustomPool pool) throws IOException {
        this.pool = pool;
        FXUtil.loadFXML(this, getClass().getResource("assets/poolEdit.fxml"));
        addColumns();
        if (pool != null) {
            labelTitle.setText("修改股票池");
            fieldName.setText(pool.name);
            loadPool(pool);
        } else {
            labelTitle.setText("新增股票池");
            loadStocks();
        }
    }

    private void addColumns() {
        TableColumn<SimpleStockInfo, JFXCheckBox> columnSelected = new TableColumn<>("选择");
        TableColumn<SimpleStockInfo, String> columnCode = new TableColumn<>("代码");
        TableColumn<SimpleStockInfo, String> columnName = new TableColumn<>("名称");
        TableColumn<SimpleStockInfo, String> columnMarket = new TableColumn<>("市场");
        table.getColumns().addAll(Arrays.asList(columnSelected, columnCode, columnName, columnMarket));
        columnSelected.setCellValueFactory(new StockSelectedValueFactory());
        columnName.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().name));
        columnCode.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(String.format("%06d", param.getValue().code)));
        columnMarket.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().market));
    }

    private void loadStocks() {
        infos.setAll(StockData.getIndex().stream().map(SimpleStockInfo::new).collect(Collectors.toList()));
        table.setItems(filteredInfos);
        searchBox.textProperty().addListener(o -> filteredInfos.setPredicate(ssi -> {
            String cond = searchBox.getText().trim().toLowerCase();
            if (cond.isEmpty()) {
                return true;
            }
            char first = cond.charAt(0);
            if (first >= '0' && first <= '9') {
                int input = Integer.parseInt(cond);
                int lower = (int) (input * Math.pow(10, 6 - cond.length()));
                int upper = (int) ((input + 1) * Math.pow(10, 6 - cond.length()) - 1);
                return ssi.code >= lower && ssi.code <= upper;
            } else if (first >= 'a' && first <= 'z') {
                return ssi.pinyin.startsWith(cond);
            } else {
                return ssi.name.contains(cond);
            }
        }));
    }

    private void loadPool(AbstractPool pool) {
        loadStocks();
        for (SimpleStockInfo ssi : infos) {
            if (pool.getStockPool().contains(ssi.code)) {
                ssi.isSelected = true;
            }
        }
    }

    @FXML
    private void onRandomAction() {
        int size = table.getItems().size();
        int count = 0;
        for (int i = 0; i < size; i++) {
            double remainRatio = 1.0 * (MIN_SIZE - count) / (size - i);
            table.getItems().get(i).isSelected = Math.random() < remainRatio;
            if (table.getItems().get(i).isSelected) count++;
        }
        table.refresh();
    }

    @FXML
    private void onConfirmAction() {
        Set<Integer> set = infos.stream()
                .filter(u -> u.isSelected).mapToInt(u -> u.code).boxed().collect(Collectors.toSet());
        if (set.size() < MIN_SIZE) {
            UIContainer.alert("错误", "自定义股池的最小股票数为 " + MIN_SIZE + "，已选的股票数为 " + set.size());
        } else {
            if (fieldName.getText().trim().isEmpty()) {
                UIContainer.alert("错误", "请输入股池名称");
            } else {
                if (pool != null) {
                    StockPoolData.removePool(pool);
                }
                pool = new CustomPool(fieldName.getText(), set);
                StockPoolData.addPool(pool);
                onCancelAction();
            }
        }
    }

    @FXML
    private void onCancelAction() {
        new Thread(() -> {
            try {
                UIContainer.showLoading();
                UIContainer.loadContent(new PoolListVC());
                UIContainer.hideLoading();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private class SimpleStockInfo implements Comparable<SimpleStockInfo> {
        private String name;
        private int code;
        private String market;
        private String pinyin;
        private Boolean isSelected;

        private SimpleStockInfo(StockInfoPtr ptr, Boolean isSelected) {
            this.name = ptr.get().getName();
            this.code = ptr.get().getCode();
            this.market = ptr.get().getMarket();
            this.pinyin = ptr.get().getPinyin();
            this.isSelected = isSelected;
        }

        private SimpleStockInfo(StockInfoPtr ptr) {
            this(ptr, false);
        }

        @Override
        public int compareTo(SimpleStockInfo o) {
            return code - o.code;
        }
    }

    class StockSelectedValueFactory implements Callback<TableColumn.CellDataFeatures<SimpleStockInfo, JFXCheckBox>, ObservableValue<JFXCheckBox>> {
        @Override
        public ObservableValue<JFXCheckBox> call(TableColumn.CellDataFeatures<SimpleStockInfo, JFXCheckBox> param) {
            SimpleStockInfo stock = param.getValue();
            JFXCheckBox checkBox = new JFXCheckBox();
            checkBox.selectedProperty().setValue(stock.isSelected);
            checkBox.selectedProperty().addListener((ov, old_val, new_val) -> {
                stock.isSelected = new_val;
            });
            return new SimpleObjectProperty<>(checkBox);
        }
    }
}
