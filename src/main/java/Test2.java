import bsh.Interpreter;
import bsh.NameSpace;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import elasticsearch.UserDto;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Test2 {

    public static void main(String[] args) {
        System.out.println(1>>3);
    }

    public static void getWebBody(String nowHtml) {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setActiveXNative(false);// 不启用ActiveX
        webClient.getOptions().setCssEnabled(true);// 是否启用CSS，因为不需要展现页面，所以不需要启用
        webClient.getOptions().setUseInsecureSSL(true); // 设置为true，客户机将接受与任何主机的连接，而不管它们是否有有效证书
        webClient.getOptions().setJavaScriptEnabled(true); // 很重要，启用JS
        webClient.getOptions().setDownloadImages(true);// 不下载图片
        webClient.getOptions().setThrowExceptionOnScriptError(false);// 当JS执行出错的时候是否抛出异常，这里选择不需要
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);// 当HTTP的状态非200时是否抛出异常，这里选择不需要
        webClient.getOptions().setTimeout(15 * 1000); // 等待15s
        webClient.getOptions().setConnectionTimeToLive(15 * 1000);

        HtmlPage page = null;
        try {
            page = webClient.getPage(nowHtml);// 加载网页
            webClient.waitForBackgroundJavaScript(20 * 1000);// 异步JS执行需要耗时，所以这里线程要阻塞30秒，等待异步JS执行结束
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            webClient.close();
        }
        String htmlStr = page.getBody().asXml();
        System.out.println(htmlStr);
    }

    public static boolean checkRule(String str) {
        //System.out.println(Test2.checkRule("if (user.getAge() > 10) { check = true; }"));
        Interpreter interpreter = new Interpreter();
        try {
            UserDto userDto = new UserDto();
            userDto.setAge(9);
            interpreter.set("user", userDto);
            interpreter.eval("Boolean check = false;");
            NameSpace ns = interpreter.getNameSpace();
            ns.importPackage("java.math.BigDecimal");
            ns.importPackage("java.util");
            ns.importPackage("elasticsearch.UserDto");
            interpreter.eval(str);
            Object check = interpreter.get("check");
            if (check != null) {
                return Boolean.valueOf(check.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Date parseYearMothDay(Date date, int hour){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //取得当天的起始时间
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    static class ItemKind {
        private BigDecimal premium;

        public BigDecimal getPremium() {
            return premium;
        }

        public void setPremium(BigDecimal premium) {
            this.premium = premium;
        }
    }
    public static void reduce() throws Exception {
        List<ItemKind> mergeItemList = new ArrayList<>();
        mergeItemList.add(new ItemKind());
        mergeItemList.add(new ItemKind());
        mergeItemList.add(new ItemKind());
        mergeItemList.get(0).setPremium(new BigDecimal(5));
        mergeItemList.get(1).setPremium(new BigDecimal(5));
        mergeItemList.get(2).setPremium(new BigDecimal(5));
        System.out.println(mergeItemList.stream().map(ItemKind::getPremium).reduce(BigDecimal::add).orElse(BigDecimal.ZERO));
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
