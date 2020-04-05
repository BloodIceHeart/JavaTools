import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimerTastCus extends TimerTask {
    private int count;

    public TimerTastCus(int count) {
        this.count = count;
    }
    
    @Override
    public void run() {
        count = (count+1)%2;
        System.out.println("start");
        new Timer().schedule(new TimerTastCus(count), 2000+2000*count);
    }

    public static void main(String[] args) {
        Timer timer = new Timer();
        timer.schedule(new TimerTastCus(1), 4000);
        while (true) {
            System.out.println(new Date().getSeconds());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto generated catch block
                e.printStackTrace();
            }
        }
    }
}
