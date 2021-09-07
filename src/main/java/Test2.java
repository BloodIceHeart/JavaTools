import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Test2 {
    public static void main(String[] args) throws Exception {
        
    }

    /**
     * 测试类
     * 
     * @throws Exception
     */
    public static void compareClauseTest() throws Exception {
        String b = "投保人与车主的关系是：*****。";
        String o = "投保人与车主的关系是：我。";
        System.out.println(Test2.compareClause(b, o));
        o = "1投保人与车主的关系是：我。";
        System.out.println(Test2.compareClause(b, o));
        o = "投保人与车2主的关系是：我。";
        System.out.println(Test2.compareClause(b, o));
        o = "投保人与车主的关系是：3我。";
        System.out.println(Test2.compareClause(b, o));
        o = "投保人与车主的关系是：我。4";
        System.out.println(Test2.compareClause(b, o));
    }
    
    /**
     * 判断时候字符串是否满足b格式
     * 
     * @param b 格式 *****A*****B*****
     * @param o 字符串
     * @return 格式是否符合
     * @throws Exception
     */
    public static boolean compareClause(String b, String o) throws Exception {
        String bs = b.replaceAll("\\*\\*\\*\\*\\*", "*");
        byte[] be = bs.getBytes();
        byte[] oe = o.getBytes();
        int j = 0;
        int last = 0;
        for (int i = 0; i < oe.length; i++) {
            // be下标走到最后
            if (j == be.length) {
                // be最后不是* 且 oe比be长，则oe与be不匹配
                if (be[j - 1] != 42) {
                    j = -1;
                }
                break;
            } else if (be[j] == 42) {
                // be下标j字符为*
                j++;
                last = j;
            } else if (oe[i] == be[j]) {
                // oe下标i字符与be下标j字符相等 则累加
                j++;
            } else if (last == 0) {
                // 第一次匹配 且 be下标j字符不为* 则oe与be不匹配
                break;
            } else {
                // 重置匹配下标
                j = last;
            }
        }
        return j == be.length;
    }
    
    public static int getInt() {
        int re = 5;
        try {
            return re;
        } finally {
            re = 6;
        }
    }
    
    public static Map<String, String> get() {
        Map<String, String> re = new HashMap<>();
        try {
            return re;
        } finally {
            re.put("aa", "dd");
        }
    }
    
    public static void lists() {
        List<String> list1 = new ArrayList<>();
        int i = 0;
        list1.add(i++ + "");
        list1.add(i++ + "");
        list1.add(i++ + "");
        list1.add(i++ + "");
        list1.add(i++ + "");
        List<String> list2 = list1.stream().filter(a -> Integer.valueOf(a) > 6).collect(Collectors.toList());
        System.out.println(list2.size());
    }
    
    public static void test() {
        for (int i = 0; i < 200; i++) {
            Integer integer1 = i;
            Integer integer2 = i;
            System.out.println(integer1 == integer2);
        }
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
