package nju.quadra.quantra.utils;

import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.util.Callback;
import nju.quadra.quantra.data.StockData;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Created by RaUkonn on 2017/3/9.
 */
public class DateUtil {

    public static String currentDate;

    public static final Callback<DatePicker, DateCell> dayCellFactory =
            new Callback<DatePicker, DateCell>() {
                @Override
                public DateCell call(final DatePicker datePicker) {
                    return new DateCell() {
                        @Override
                        public void updateItem(LocalDate item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item.isAfter(DateUtil.parseLocalDate(StockData.latest))
                                    || item.getDayOfWeek() == DayOfWeek.SUNDAY
                                    || item.getDayOfWeek() == DayOfWeek.SATURDAY) {
                                setDisable(true);
                                setStyle("-fx-background-color: #ffc0cb; -fx-background-radius: 50%;");
                            }
                        }
                    };
                }
            };

    public static String localDateToString(LocalDate date) {
        return String.valueOf(date.getMonthValue())
                + '/' + String.valueOf(date.getDayOfMonth())
                + '/' + String.valueOf(date.getYear()).substring(2, 4);
    }

    public static LocalDate parseLocalDate(String string) {
        String[] list = string.split("/");
        int year = Integer.parseInt(list[2]) + 2000;
        int month = Integer.parseInt(list[0]);
        int day = Integer.parseInt(list[1]);
        return LocalDate.of(year, month, day);
    }

    public static int compare(String date1, String date2) {
        if (date1 == date2 || date1.equals(date2)) return 0;
        return parseLocalDate(date1).compareTo(parseLocalDate(date2));
    }
}
