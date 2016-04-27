
public class Packet {
	
	private double arriveTime; 	     //到达时间
	private int arrivalQueueLength;  //到达时队长

	
	private double departureTime;   	//离去时间
	private int departureQueueLength;   //离去时队长
	private int Arrivenumb;
	private int Departurenumb;

	
	public double getArriveTime() {
		return arriveTime;
	}
	public void setArriveTime(double arriveTime) {
		this.arriveTime = arriveTime;
	}
	public void setArrivenumb(int  Arrivenumb) {
		this.Arrivenumb = Arrivenumb;
	}
	public void setDeparturenumb(int  departurenumb) {
		this.Departurenumb = departurenumb;
	}
	public int getArrivalQueueLength() {
		return arrivalQueueLength;
	}
	public void setArrivalQueueLength(int arrivalQueueLength) {
		this.arrivalQueueLength = arrivalQueueLength;
	}

	public double getDepartureTime() {
		return departureTime;
	}
	public void setDepartureTime(double departureTime) {
		this.departureTime = departureTime;
	}
	public int getDepartureQueueLength() {
		return departureQueueLength;
	}
	public void setDepartureQueueLength(int departureQueueLength) {
		this.departureQueueLength = departureQueueLength;
	}
}
