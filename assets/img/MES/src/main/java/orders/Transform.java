package orders;

public class Transform extends Order{

	private int From=0;
	private int To=0;
	private int Quantity=0;
	private int Quantity1=0;
	private int Quantity2=0;
	private int Quantity2_L=0;
	private int Quantity2_R=0;
	private int Quantity3=0;
	private int Time=0;
	private int Time1=0;
	private int MaxDelay=0;
	private int Start=0;
	private int End=0;
	private int Penalty=0;
	private int PenaltyIncurred=0;
	private int fromAUX=0;
	private int quantityAux=0;

	public void setFromAUX(int fromAUX) {
		this.fromAUX = fromAUX;
	}

	public int getFromAUX() {
		return fromAUX;
	}

	public int getQuantityAux() {
		return quantityAux;
	}

	public void setFrom(int from) {
		this.From = from;
	}

	public void setFrom(String from) {

		switch (from) {
			case "P1" -> From = 1;
			case "P2" -> From = 2;
			case "P3" -> From = 3;
			case "P4" -> From = 4;
			case "P5" -> From = 5;
			case "P6" -> From = 6;
			case "P7" -> From = 7;
			case "P8" -> From = 8;
			case "P9" -> From = 9;
		}
	}

	public void setTo(int to){
		this.To = to;
	}

	public void setTo(String to) {

		switch (to) {
			case "P1" -> To = 1;
			case "P2" -> To = 2;
			case "P3" -> To = 3;
			case "P4" -> To = 4;
			case "P5" -> To = 5;
			case "P6" -> To = 6;
			case "P7" -> To = 7;
			case "P8" -> To = 8;
			case "P9" -> To = 9;
		}
	}

	public void setTime(int time) {
		this.Time = time;
	}

	public void setMaxDelay(int maxDelay) {
		this.MaxDelay = maxDelay;
	}

	public void setPenalty(int penalty) {
		this.Penalty = penalty;
	}

	public void setStart(int start) {
		this.Start = start;
	}

	public void setEnd(int end) {
		this.End = end;
	}

	public void setPenaltyIncurred(int penaltyIncurred) {
		this.PenaltyIncurred = penaltyIncurred;
	}

	public void setQuantity(int quantity) {
		this.Quantity = quantity;
	}

	public void setQuantity1(int quantity) {
		this.Quantity1 = quantity;
	}

	public void setQuantity2(int quantity) {
		this.Quantity2 = quantity;
	}

	public void setQuantity2_L(int quantity2_L) {
		Quantity2_L = quantity2_L;
	}

	public void setQuantity2_R(int quantity2_R) {
		Quantity2_R = quantity2_R;
	}

	public void setQuantity3(int quantity) {
		this.Quantity3 = quantity;
	}

	public void setQuantityAux(int quantityAux) {
		this.quantityAux = quantityAux;
	}

	public void setTime1(int time1) {
		this.Time1 = time1;
	}

	public int getFrom() {
		return From;
	}

	public int getEnd() {
		return End;
	}

	public int getMaxDelay() {
		return MaxDelay;
	}

	public int getPenalty() {
		return Penalty;
	}

	public int getPenaltyIncurred() {
		return PenaltyIncurred;
	}

	public int getQuantity() {
		return Quantity;
	}

	public int getQuantity1() {
		return Quantity1;
	}

	public int getQuantity2() {
		return Quantity2;
	}

	public int getQuantity2_L() {
		return Quantity2_L;
	}

	public int getQuantity2_R() {
		return Quantity2_R;
	}

	public int getQuantity3() {
		return Quantity3;
	}

	public int getStart() {
		return Start;
	}

	public int getTime() {
		return Time;
	}

	public int getTime1() {
		return Time1;
	}

	public int getTo() {
		return To;
	}
}
