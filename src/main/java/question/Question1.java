package question;

import java.util.HashSet;
import java.util.Set;

/**
 *  用 1, 2 , 2 ,3, 4 ,5 这 6个数字 , 用 Java 写一个 main 函数 , 打印出所有不同的排列 , 
 *  如 : 512234, 412345等 , 要求 : “4”不能在第三位 , “3”与 ”5”不能相连 。
 *
 * @author: wangpeng
 * @date: 2020/1/8 14:22
 */
public class Question1 {

    private Set<String> sets = new HashSet<>();

    public static void main(String[] args) {
        Question1 question1 = new Question1();
        String[] lists = {"1", "2", "2", "3", "4", "5"};
        question1.getNextString("", lists);
        System.out.println(question1.getSets().size());
        System.out.println(question1.getSets());
    }

    private void getNextString(String finalValue, String[] lists) {
        if (lists.length == 1) {
            String key = finalValue + lists[0];
            if (key.substring(2, 3).equals("4") || key.indexOf("35") > -1 || key.indexOf("53") > -1) {
                return;
            }
            sets.add(key);
        } else {
            String[] copys = new String[lists.length-1];
            String value = finalValue;
            for (int i = 0; i < lists.length; i++) {
                value = finalValue + (lists[i]);
                if (i > 0) {
                    System.arraycopy(lists, 0, copys, 0, i);
                }
                if (i < lists.length) {
                    System.arraycopy(lists, i+1, copys, i, lists.length-i-1);
                }
                getNextString(value, copys);
            }
        }
    }

    public Set<String> getSets() {
        return sets;
    }

    public void setSets(Set<String> sets) {
        this.sets = sets;
    }

}
