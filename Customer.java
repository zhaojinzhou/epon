import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Customer implements Runnable {
	
	private static final int Tmax = 10000000;
	private int identifier;
    private final int SLEEP = 0, DOZE = 1;
    private int currentState = 0; //闁跨喐鏋婚幏宄邦潗闁跨喐鏋婚幏椋庢蒋闁跨喐鏋婚幏锟�

    private double systemTime = 0;
    private double lastSystemTime = 0; //闁跨喕绶濋棃鈺傚缁崵绮洪弮鍫曟晸閺傘倖瀚�
    private double nextArrivalTime = exponential(Simulation.ARRIVALRATE); //闁跨喖鎽▎锛勵暜閹风兘鏁撻弬銈嗗閺冨爼鏁撻弬銈嗗

    //缂佺喖鏁撻弬銈嗗delay
    private double totalDelay = 0;
    private double totalvar = 0;
    private int totalPacket = 0;
    private int m = 0;
    //缂佺喖鏁撻悪锟犳交閹风兘鏁撻弬銈嗗閻樿埖锟戒焦妞傞柨鐔告灮閹凤拷
    private double activeTime = 0, sleepTime = 0, dozeTime = 0;
    int[]  vacation= new int[1000];
    private int wakeUpTimes;

    private Queue<Packet> buffer = new LinkedList<Packet>();
    public Customer(int identifier) {this.identifier = identifier;}

    @Override
    public void run() {
    	
    
    	while (true) {
			
		    try {
		    	
		    	Simulation.customer_lock[identifier].acquire();
		        if(Simulation.grant[identifier] < -0.5) break;
		        totalvar+=Math.pow((systemTime-lastSystemTime-(32e-6/(1-Simulation.OFFERLOAD))), 2);
		        lastSystemTime = systemTime;
		      
		     
		        double grant = Simulation.grant[identifier] + Simulation.RTT / 2; //grant閺冨爼鏁撻弬銈嗗
		        
		        if (grant < systemTime) Simulation.report[identifier] = 0;
		        else {           
		            switch(currentState) {
		                case SLEEP: //閻繝鏁撻弬銈嗗
		                {
		                	boolean isWakeUp = false;
		                	double wakeUpPoint = 0;
		                    if (buffer.size() >= Simulation.WAKEUPTHRESHOLD) currentState = DOZE; //鐠囨挳鏁撻弬銈嗗闁跨喐鏋婚幏铚傜闁跨喐鏋婚幏绌弐ant闁跨喐鏋婚幏绋篴keUp闁跨喕濡涵閿嬪
		                    
		                    while (systemTime < grant ) { //闁跨喓瀚涢弬銈嗗缁崵绮洪弮鍫曟晸閺傘倖瀚�
		                        if (nextArrivalTime < grant) {
		                            addPacket(); 
		                            if (buffer.size() == Simulation.WAKEUPTHRESHOLD) { //闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归柨鐔告灮閹峰嘲锟斤拷
		                            	isWakeUp = true;
		                            	wakeUpPoint = systemTime;
		                            	wakeUpTimes ++;
		                                if (grant > (systemTime + Simulation.WAKEUPTIME)) currentState = DOZE; //grant閺冨爼鏁撻弬銈嗗闁跨喓鐛ら幘鍛闁跨喐鏋婚幏鐑芥晸閺傘倖瀚笵OZE
		                                
		                                else { //grant閺冨爼鏁撻弫娆掓彧閹风兘鏁撻弬銈嗗WAKEUP
		                                    grant = systemTime + Simulation.WAKEUPTIME; //闁跨喓瀚涢弬銈嗗闁跨喐鏋婚幏绋篈KEUP闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归弮鍫曟晸閺傘倖瀚�
		                                    currentState = SLEEP;
		                                }
		                            }
		                        } else { //闁跨喖鎽潏鐐arrival閺冨爼鏁撻弫娆戭劜閹风兘鏁撻弬銈嗗grant                         	
		                        	if (isWakeUp) {                      		
		                        		sleepTime += wakeUpPoint - lastSystemTime;
		                        		if (grant > (wakeUpPoint + Simulation.WAKEUPTIME)) dozeTime += grant - (wakeUpPoint + Simulation.WAKEUPTIME);
		                        	}
		                        	else { //濞岋繝鏁撻弬銈嗗闁跨喐鏋婚幏鐑芥晸閺傘倖瀚�
		                        		if (currentState == SLEEP) sleepTime += grant - lastSystemTime;
		                        		if (currentState == DOZE) dozeTime += grant - lastSystemTime;
		                        	}                          	
		                            systemTime = grant;
		                            if (currentState == DOZE) Simulation.report[identifier] = buffer.size();      
		                            if (currentState == SLEEP) Simulation.report[identifier] = 0;
		                            break;
		                        }  
		                    }
		                    break;
		                }
		                case DOZE: //闁跨喎澹欓敓锟�
		                {
		                    while (systemTime < grant) {
		                        if (nextArrivalTime < grant) {
		                            addPacket();
		                        } else {
		                            systemTime = grant;
		                            break;
		                        } //闁跨喓瀚涢弬銈嗗闁跨喐鏋婚幏绌弐ant,闁跨喐鏋婚幏宄邦潗闁跨喐鏋婚幏宄板箵
		                    }
		                    dozeTime += grant - lastSystemTime;
		                    
							//System.out.println("A: " + (grant - lastSystemTime));
  	                       //  vacation[(int)((grant - lastSystemTime)/(1e-7))]++;
		                    
		                    for (int i = 0; i < Simulation.report[identifier]; i++) { //闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归柨鐔荤窛濞嗏剝鍞婚幏鐑芥晸閺傘倖瀚归柨鐔告灮閹风兘鏁撻敓锟�
		                        Packet tmp = buffer.remove();
		                        tmp.setDepartureTime(systemTime + (i+1) * Simulation.PACKETBIT / Simulation.RATE);
		                        totalDelay += tmp.getDepartureTime() - tmp.getArriveTime();
		                        totalPacket++;
		                    }
		                    grant += Simulation.report[identifier] * Simulation.PACKETBIT / Simulation.RATE;
		                    activeTime += Simulation.report[identifier] * Simulation.PACKETBIT / Simulation.RATE;
		                    
		                    while (systemTime < grant) {//闁跨喓瀚涢弬銈嗗闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归柨鐔告灮閹风兘鏁撻弬銈嗗闁跨喐鏋婚幏鐑芥晸閻ㄥ棛灏ㄩ幏锟�
		                        if (nextArrivalTime < grant) addPacket();
		                        else {
		                            systemTime = grant;
		                            break;
		                        }
		                    }
		                    if (buffer.size() >= Simulation.SLEEPTHRESHOLD) { 
		                    	if (buffer.size()<Tmax){Simulation.report[identifier] = buffer.size();
		                    	 
		                    	}
		                    	else {Simulation.report[identifier] = Tmax;
		                    	//System.out.println("/闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归柨鐔告灮閹峰嘲锟斤拷,闁跨喖鎽▎" );
		                    	}//闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归柨鐔告灮閹峰嘲锟斤拷,闁跨喖鎽▎锛勩�嬮幏鐑芥晸閺傘倖瀚归柨鐔告灮閹风兘鏁撻弬銈嗗
		                    	
		                        currentState = DOZE;
		                    } else {
		                        currentState = SLEEP; //闁跨喐鏋婚幏鐑芥晸閺傘倖瀚归惈锟犳晸閺傘倖瀚�
		                        Simulation.report[identifier] = 0;
		                    }
		                    break;
		                }
		            }
		        }
		    }catch (InterruptedException e){
		        e.printStackTrace();
		    }
		    Simulation.server_lock.release();
		}   
    	 Simulation.totalpacket[identifier]=totalPacket;
		if (identifier == 0){
		   //System.out.println("OfferLoad:" + offerLoad);
		   // Simulation.out.println("Ad\t"+(totalDelay / totalPacket)+"\tpp\t"+((sleepTime*0.1484 + activeTime + (dozeTime + wakeUpTimes * Simulation.WAKEUPTIME)*0.76207)/systemTime)+"\tsp\t" +(sleepTime /systemTime)+"\tW-T\t" +(Simulation.WAKEUPTIME)+"\tdp\t" +((dozeTime)/systemTime)+"\tap\t" +((activeTime)/systemTime )+"\tsc\t" +((sleepTime + (wakeUpTimes) * Simulation.WAKEUPTIME)/wakeUpTimes)+"\tdc\t" +(dozeTime/wakeUpTimes )+"\ttc\t" +(systemTime/wakeUpTimes)+"\n");
           //System.out.print("Average delay:");
		    System.out.println(totalDelay / totalPacket);
		   // System.out.println(totalPacket);
		   		   //System.out.println(totalDelay-(totalPacket*(512/1e9)) / totalPacket);
			//System.out.println( systemTime/Simulation.CYCLENUMBER+"\tpp\t"+totalvar/Simulation.CYCLENUMBER);
			//System.out.println( systemTime/Simulation.CYCLENUMBER);
			/* System.out.println("Total system time:" + systemTime);
		    System.out.println("Sleep time:" + sleepTime);
		    System.out.println("Active time:" + activeTime);
		    System.out.println("wakeUpTimes:" + wakeUpTimes);
		    System.out.println("WakeUp time:" + wakeUpTimes * Simulation.WAKEUPTIME);
		    System.out.println("Doze time:" + dozeTime);
		    System.out.println("SleepTime + ActiveTime + DozeTime + WakeUpTimes: " + (sleepTime + activeTime + dozeTime + (wakeUpTimes) * Simulation.WAKEUPTIME)); 
		    System.out.println();*/
		  //  for (  m = 0;m<1000;m++){System.out.println(vacation[m]);}
		}
        
    }
    
    private double exponential(double mean) {return (-(1 / mean) * Math.log(Math.random()));}
    private double pareto(double mean) { 
    	Random rng ;
    	double alpha;
    	double arrival_time = 0;
    	alpha = 2.5;
    	rng = new Random();
    	
    	double xm = (alpha - 1) / alpha / mean;
    	double rand = rng.nextDouble();
    	arrival_time += xm / Math.pow(rand, 1 / alpha);	
    	return arrival_time;
    	}
    
    private void addPacket(){
    	systemTime = nextArrivalTime;
        Packet tmp = new Packet();
        tmp.setArriveTime(systemTime);
        tmp.setArrivenumb(totalPacket+buffer.size());
        buffer.add(tmp);
        nextArrivalTime = systemTime + exponential(Simulation.ARRIVALRATE);
        
    }
}
