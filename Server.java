public class Server implements Runnable {
	
    private int ONUNUMBER;
    private int CYCLENUMBER;
    private int counter = 0;
    public static double systime = 0;
    private double[] idle_time;
    private double tmp;

    public Server(int ONUNUMBER, int CYCLENUMBER){
        this.ONUNUMBER = ONUNUMBER;
        this.CYCLENUMBER = CYCLENUMBER;
        idle_time = new double[ONUNUMBER];
    }

    public void run(){
        while(counter < CYCLENUMBER){
            for(int i = 0; i < ONUNUMBER; i++) {
                try {
                	Simulation.server_lock.acquire();
                    if(counter!=0) Simulation.set_grant((i-1+ONUNUMBER)%ONUNUMBER, tmp);
                    systime = Simulation.grant[i];
                    tmp = calculate_next_grant(Simulation.report, i);
                    Simulation.customer_lock[i].release();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            counter++;
        }
        for(int i = 0; i < ONUNUMBER; i++) {
            Simulation.set_grant(i, -1);
            Simulation.customer_lock[i].release();
        }
    }

    public double calculate_next_grant(int [] report, int i){
        double grant;
        double factor = Simulation.PACKETBIT/Simulation.RATE;

        if((systime + report[i]*factor + Simulation.RTT)>(Simulation.grant[(i-1+ONUNUMBER)%ONUNUMBER]+report[(i-1+ONUNUMBER)%ONUNUMBER]*factor+Simulation.GUARDTIME)){
            grant = systime + report[i]*factor + Simulation.RTT;
            //idle_time[i] += (systime + report[i]*factor + Simulation.RTT - (Simulation.grant[(i-1+ONUNUMBER)%ONUNUMBER]+report[(i-1+ONUNUMBER)%ONUNUMBER]*factor+Simulation.guard_time));
        }
        else {
            grant = Simulation.grant[(i-1+ONUNUMBER)%ONUNUMBER]+report[(i-1+ONUNUMBER)%ONUNUMBER]*factor+Simulation.GUARDTIME;
        }
        return grant;
    }
}
