package nju.quadra.quantra.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXListView;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import nju.quadra.quantra.data.StockData;
import nju.quadra.quantra.data.StockInfoPtr;
import nju.quadra.quantra.data.StockPoolData;
import nju.quadra.quantra.pool.*;
import nju.quadra.quantra.utils.DateUtil;
import nju.quadra.quantra.utils.FXUtil;
import nju.quadra.quantra.utils.StockStatisticUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static nju.quadra.quantra.utils.StockTableUtil.*;

/**
 * Created by RaUkonn on 2017/4/4.
 */
public class PoolListVC extends BorderPane {
    @FXML
    private JFXListView<Label> listSystem, listUser;
    @FXML
    private Label labelName;
    @FXML
    private JFXDatePicker datePicker;
    @FXML
    private JFXButton btnModify, btnDelete;
    @FXML
    private TableView<StockInfoPtr> table;

    private ObservableList<StockInfoPtr> infos = FXCollections.observableArrayList();
    private FilteredList<StockInfoPtr> filteredInfos = new FilteredList<>(infos);
    private CustomPool selectedPool;

    public PoolListVC() throws IOException {
        FXUtil.loadFXML(this, getClass().getResource("assets/poolList.fxml"));
        listSystem.setOnMouseClicked(event -> {
            Label label = listSystem.getSelectionModel().getSelectedItem();
            if (label != null) {
                loadPool((AbstractPool) label.getUserData());
            }
        });
        listUser.setOnMouseClicked(event -> {
            Label label = listUser.getSelectionModel().getSelectedItem();
            if (label != null) {
                loadPool((AbstractPool) label.getUserData());
            }
        });

        addColumn(table, "代码", param -> new ReadOnlyObjectWrapper<>(String.format("%06d", param.getValue().get().getCode())));
        addColumn(table, "名称", param -> new ReadOnlyObjectWrapper<>(param.getValue().get().getName()));
        addColumn(table, "涨幅(%)", param -> new ReadOnlyObjectWrapper<>(Math.floor(StockStatisticUtil.RATE(param.getValue()) * 10000) / 100.0));
        addColumn(table, "交易量", param -> new ReadOnlyObjectWrapper<>(param.getValue().get().getVolume()));
        addColumn(table, "今收", param -> new ReadOnlyObjectWrapper<>(param.getValue().get().getClose()));
        addColumn(table, "今开", param -> new ReadOnlyObjectWrapper<>(param.getValue().get().getOpen()));
        addColumn(table, "昨收", param -> new ReadOnlyObjectWrapper<>(param.getValue().prev() != null ? param.getValue().prev().get().getClose() : ""));
        addColors(table.getColumns().get(2));
        addDblClick(table);

        datePicker.valueProperty().addListener(observable -> {
            String date = DateUtil.localDateToString(datePicker.getValue());
            List<StockInfoPtr> ptrs = StockData.getByDate(date);
            if (ptrs.isEmpty()) {
                datePicker.setValue(DateUtil.parseLocalDate(DateUtil.currentDate));
                UIContainer.alert("错误", "当天无股票数据，请选择其它日期");
            } else {
                infos.setAll(ptrs);
                DateUtil.currentDate = date;
            }
        });
        datePicker.setDayCellFactory(DateUtil.dayCellFactory);
        datePicker.setValue(DateUtil.parseLocalDate(DateUtil.currentDate));

        updatePoolList();
        table.setItems(filteredInfos);
        loadPool(new HS300Pool());
    }

    private void updatePoolList() {
        listSystem.getItems().clear();
        listUser.getItems().clear();
        for (AbstractPool pool : Arrays.asList(new HS300Pool(), new ZxbPool(), new CybPool())) {
            addToListView(listSystem, pool);
        }
        for (AbstractPool pool : StockPoolData.getPoolMap().values()) {
            addToListView(listUser, pool);
        }
    }

    private void loadPool(AbstractPool pool) {
        labelName.setText(pool.name);
        if (pool instanceof CustomPool) {
            selectedPool = (CustomPool) pool;
            btnModify.setVisible(true);
            btnDelete.setVisible(true);
        } else {
            btnModify.setVisible(false);
            btnDelete.setVisible(false);
        }
        filteredInfos.setPredicate(info -> pool.getStockPool().contains(info.get().getCode()));
    }

    private void addToListView(JFXListView<Label> target, AbstractPool pool) {
        Label label = new Label(pool.name);
        label.setUserData(pool);
        target.getItems().add(label);
    }

    @FXML
    private void onModifyAction() {
        new Thread(() -> {
            try {
                UIContainer.showLoading();
                UIContainer.loadContent(new PoolEditVC(selectedPool));
                UIContainer.hideLoading();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void onDeleteAction() {
        UIContainer.confirm("确认", "真的要删除这个股票池吗？", event -> {
            StockPoolData.removePool(selectedPool);
            updatePoolList();
            loadPool(new HS300Pool());
        });
    }

    @FXML
    private void onAddAction() throws IOException {
        selectedPool = null;
        onModifyAction();
    }

    }
