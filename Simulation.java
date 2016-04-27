
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.concurrent.Semaphore;

public class Simulation {
    public static  int ONUNUMBER = 32;
    public static final int CYCLENUMBER = 10000;
    public static final int WAKEUPTHRESHOLD = -1;
    public static final int SLEEPTHRESHOLD = -1;
    
     
    public static final double RTT = 0;
    public static final double GUARDTIME = 1e-6;
    public static final double WAKEUPTIME = 20e-4;
    public static final double PACKETBIT = 64 * 8;
    public static final double RATE = 1e9;
    
    public static double OFFERLOAD;
    public static int totalpackets=0;
    public static double ARRIVALRATE;

    public static int[] report;

    public static double[] grant; //grant time
	public static int[] totalpacket; 

    public static Semaphore server_lock;
    public static Semaphore[] customer_lock = new Semaphore[ONUNUMBER];
    
    public static PrintWriter out;

    
    public static void main(String[] args) {
    	
    	try {
			out = new PrintWriter(new File("EPON_out.txt"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	Thread[] ONUThread;
    	Thread OLTThread;
    
    	for (OFFERLOAD = 0.1; OFFERLOAD < 0.71; OFFERLOAD += 0.1){
    		totalpackets=0;
    		
            grant = new double[ONUNUMBER];
            report = new int[ONUNUMBER];
            totalpacket= new int[ONUNUMBER];
       

            ARRIVALRATE = OFFERLOAD * RATE / (PACKETBIT * ONUNUMBER);

            for(int i = 0; i < ONUNUMBER; i++){
                customer_lock[i] = new Semaphore(1);
                try {
                    customer_lock[i].acquire();
                }catch (InterruptedException e){}
            }
            server_lock = new Semaphore(1);

            ONUThread = new Thread[ONUNUMBER];
            OLTThread = new Thread(new Server(ONUNUMBER,CYCLENUMBER));
            OLTThread.start();

            for(int i = 0; i < ONUNUMBER; i++){
                ONUThread[i] = new Thread(new Customer(i));
                ONUThread[i].start();
            }

            try {
                OLTThread.join();
            }catch (InterruptedException e){}

			for(int i = 0; i < ONUNUMBER; i++){
				try {
					ONUThread[i].join();
				}catch (InterruptedException e){}
			}
		
			//System.out.println(totalpacket[0]);
			for(int jin = 0; jin < ONUNUMBER; jin++){
				totalpackets+=totalpacket[jin];
			}
			//System.out.println(totalpacket[0]);
			//System.out.println(totalpackets);
			System.out.println((Server.systime-totalpackets*512/(1e9))*1e6/Server.systime);
			}
    	
    	//out.close();
    }

    public static void set_grant(int i,double t){grant[i] = t;}    
    public static void set_totalpacket(int i,int t){totalpacket[i] =  t;}   //Server
    public static double read_grant(int i){return grant[i];}                    //Customer
}
