package question;

/**
 * 设计 Java程序，假设有 50瓶饮料，喝完三个空瓶可以换一瓶饮料，依次类推，请问总共喝了多少饮料。  
 * @author: wangpeng
 * @date: 2020/1/8 14:22
 */
public class Question2 {

    public static void main(String[] args) {

        Question2 question2 = new Question2();
        int n = question2.getNextNum(50, 3);
        System.out.println ("共喝了"+(50 + n) + "瓶");
        n = question2.getCount(50, 3);
        System.out.println ("共喝了"+(n) + "瓶");
    }
    /**
     * @description: 方法 1
     */
    private int getNextNum(int sum, int count) {
        int i = sum/count;
        int j = (sum - (count -1)*i);
        if (j < count) {
            return i;
        } else {
            return i + getNextNum(sum - (count -1)*i, count);
        }
    }

    /**
     * @description: 方法 2
     */
    private int getCount(int sum, int count) {
        int n = sum; // 初始饮料总数
        int i = 0; // 兑换次数
        while (true) {
            n -= count; //喝 3 瓶
            n++; // 兑换 1 瓶
            i++; // 兑换次数 +1
            if (n < count) {
                return 50 + i;
            }
        }
    }
}
