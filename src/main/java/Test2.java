import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Test2 {
    public static void main(String[] args) throws Exception {
        caculte();
        /*for (int i = 0; i < 200; i++) {
            Integer integer1 = i;
            Integer integer2 = i;
            System.out.println(integer1 == integer2);
        }*/
    }

    public static void caculte() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDateJava = sdf.parse("2019-12-19");
        Date endDateJava = sdf.parse("2020-05-19");
        int intDay = (int)((endDateJava.getTime() - startDateJava.getTime()) / 86400000l);
        Double d1 = intDay * 1.6 * 5;
        System.out.println("本金5万六期利息+48：" + (d1 + 48));
        endDateJava = sdf.parse("2020-06-23");
        intDay = (int)((endDateJava.getTime() - startDateJava.getTime()) / 86400000l);
        Double d2 = intDay * 1.6 * 4.5;
        System.out.println("本金4.5万截止今天利息：" + d2);
        System.out.println("本金9.5万总利息：" + (d1 + d2 + 48));
        System.out.println("本金4.5万截止今天本息合：" + (d1 + d2 + 48 + 45000));
        System.out.println("本金4.5万截止今天抵扣合计：" + (d1 + d2 + 48 + 45000 - (1800 + 1800)));
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
