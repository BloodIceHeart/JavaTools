import java.util.Scanner;

/**
 * n节台阶 只能1 2 步数 多少种走法
 */
public class Fibonacci {
    static int getLevelNumber(int n) {
        if (n < 1) {
            return 0;
        }
        if (n == 1) {
            return 0;
        }
        if (n == 2) {
            return 1;
        }
        if (n == 3) {
            return 2;
        }
        int temp = 0;
        int a = 1;
        int b = 2;
        for (int i = 3; i < n; i++) {
            temp = a + b;
            a = b;
            b = temp;
        }
        return temp;
    }

    static int getLevelNumber2(int n) {
        if (n <= 1) {
            return 0;
        }
        if (n == 2) {
            return 1;
        }
        if (n == 3) {
            return 2;
        }
        return getLevelNumber2(n - 1) + getLevelNumber2(n - 2);
    }

    static int getLevelNumber3(int all) {
        if (all <= 1) {
            return 0;
        }
        if (all == 2) {
            return 1;
        }
        if (all == 3) {
            return 2;
        }
        int n = all - 1;
        int sum = 1;
        for (int m = 1; m * 2 <= all - 1; m++) {
            if (n > 0) {
                n = (all - 1) - 2 * m;
                sum += jiecheng(n+m)/(jiecheng(n)*jiecheng(m));
            } else {
                sum += 1;
            }
        }
        return sum;
    }
    static int jiecheng(int n) {
        if (n <= 1) {
            return 1;
        } else {
            return n * jiecheng(n - 1);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int b = sc.nextInt();
        int a[] = new int[b];
        for (int i = 0; i < b; i++) {
            a[i] = sc.nextInt();
        }
        for (int i = 0; i < b; i++) {
            System.out.println(String.format("2：%s, 3：%s", getLevelNumber2(a[i]),getLevelNumber3(a[i])));
        }
    }
}
