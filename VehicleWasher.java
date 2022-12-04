import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class VehicleWasher extends Thread {
    private Queue<Vehicle> waitingList;
    private boolean[] inProgress;
    private int number_Of_Station = 0;
    private int number_To_Wash = 0;
    private double avgT_Car_arrive = 0.0;
    private double avgT_Car_Wash = 0.0;
    public int counter;
    Random rnd = new Random();


    public VehicleWasher(int num_station, int num_Wash, double avgT_arrive, double avgT_Wash) {
        this.waitingList = new LinkedList();
        this.inProgress = new boolean[number_Of_Station];
        this.number_Of_Station = num_station;
        this.number_To_Wash = num_Wash;
        this.avgT_Car_arrive = avgT_arrive;
        this.avgT_Car_Wash = avgT_Wash;
        this.counter = 0;
    }

    private void wash(int index) throws InterruptedException {
        String type = waitingList.remove().getType();
        inProgress[index] = true;
        wait((long) (-Math.log(rnd.nextDouble()) / avgT_Car_Wash));
        inProgress[index] = false;
        counter++;
        System.out.println(type + " has been washed");
    }

    private void addCarToQueue() throws InterruptedException {
        wait((long) (-Math.log(rnd.nextDouble()) / avgT_Car_arrive));
        int type = rnd.nextInt(4);
        Vehicle v;
        switch (type) {
            case 0:
                v = new Car();
                break;
            case 1:
                v = new MiniBus();
                break;
            case 2:
                v = new SUV();
                break;
            default:
                v = new Truck();
                break;
        }
        System.out.println(v.getType() + " added to the queue");
        waitingList.add(v);

    }

    private synchronized void startProgress() throws InterruptedException {
        while (counter <= number_To_Wash) {
            addCarToQueue();

            for (int i = 0; i < number_Of_Station; i++) {
                if (!inProgress[i])
                    synchronized (waitingList.peek()) {
                        wash(i);
                    }
            }
        }
    }

    @Override
    public void run() {
        try {
            startProgress();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
