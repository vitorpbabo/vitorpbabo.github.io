package orders;

public class Order{

	private Transform transform;
	private Unload unload;
	private int number				= 0;
	private int type				= 0;
	private int estimation			= 0;
	private int estimation_curr		= 0;
	private int statusR 			= 0;
	private int statusL 			= 0;
	private int priority 			= 0;

	public void setNumber(int number) {
		this.number = number;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setTransform(Transform transform) {
		this.transform = transform;
	}

	public void setUnload(Unload unload) {
		this.unload = unload;
	}

	public void setEstimation(int estimation) { this.estimation = estimation; }

	public void setEstimation_curr(int estimation_curr) {
		this.estimation_curr = estimation_curr;
	}

	public void setStatusR(int statusR) {
		this.statusR = statusR;
	}

	public void setStatusL(int statusL) {
		this.statusL = statusL;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getType() {
		return type;
	}

	public int getNumber() {
		return number;
	}

	public int getEstimation() {
		return estimation;
	}

	public int getStatusR() {
		return statusR;
	}

	public int getStatusL() {
		return statusL;
	}

	public int getPriority() {
		return priority;
	}

	public int getEstimation_curr() {
		return estimation_curr;
	}

	public Transform getTransform() {
		return transform;
	}

	public Unload getUnload() {
		return unload;
	}

}
