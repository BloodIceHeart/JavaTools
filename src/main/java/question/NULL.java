package question;

import java.math.BigDecimal;

public class NULL {
    public static void haha(){
        System.out.println("haha");
    }
    public static void main(String[] args) {
        ((NULL)null).haha();
        System.out.println(new BigDecimal(Math.pow(-2, 31)).toPlainString());
        System.out.println(Integer.MAX_VALUE);
        System.out.println(Integer.toBinaryString(Integer.MAX_VALUE));
        System.out.println(Integer.toBinaryString(Integer.MIN_VALUE));
        System.out.println(Integer.MIN_VALUE*2);
        System.out.println(Integer.MIN_VALUE<<1);
    }
}
