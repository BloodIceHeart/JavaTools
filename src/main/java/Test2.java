import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Test2 {
    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 200; i++) {
            Integer integer1 = i;
            Integer integer2 = i;
            System.out.println(integer1 == integer2);
        }
    }
    public static int getDayMinus(Date startDate, int intStartHour, Date endDate, int intEndHour)
            throws Exception {
        
        int intDay = 0;
        java.util.Date startDateJava = new java.util.Date(
                startDate.getYear(), startDate.getMonth(),
                startDate.getDate(), intStartHour, 0, 0);
        java.util.Date endDateJava = new java.util.Date(endDate.getYear(),
                endDate.getMonth(), endDate.getDate(),
                intEndHour, 0, 0);
        intDay = (int)((endDateJava.getTime() - startDateJava.getTime()) / 86400000l);
        return intDay;
    }
}
