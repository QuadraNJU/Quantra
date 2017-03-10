package nju.quadra.quantra.utils;

import java.time.LocalDate;

/**
 * Created by RaUkonn on 2017/3/9.
 */
public class DateUtil {
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
