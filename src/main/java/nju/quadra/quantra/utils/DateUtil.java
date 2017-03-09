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
        int year = new Integer("20" + list[2]);
        int month = new Integer(list[0]);
        int day = new Integer(list[1]);
        return LocalDate.of(year, month, day);
    }

    public static int compare(String date1, String date2) {
        if(date1.compareTo(date2) == 0) return 0;
        String[] list1 = date1.split("/");
        String[] list2 = date2.split("/");
        if(Integer.valueOf(list1[2]) != Integer.valueOf(list2[2])) {
            return Integer.valueOf(list1[2]).compareTo(Integer.valueOf(list2[2]));
        } else if(Integer.valueOf(list1[0]) != Integer.valueOf(list2[0])) {
            return Integer.valueOf(list1[0]).compareTo(Integer.valueOf(list2[0]));
        }
        return 0;
    }
}
