package orders;

public class Unload extends Order {
	private int type 		= 0;
	private int destination = 0;
	private int quantity 	= 0;
	private int quantity1 	= 0;
	private int quantity2 	= 0;
	private int quantity3 	= 0;

	public void setType(String type) {
		switch (type) {
			case "P1" -> this.type = 1;
			case "P2" -> this.type = 2;
			case "P3" -> this.type = 3;
			case "P4" -> this.type = 4;
			case "P5" -> this.type = 5;
			case "P6" -> this.type = 6;
			case "P7" -> this.type = 7;
			case "P8" -> this.type = 8;
			case "P9" -> this.type = 9;
		}
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setDestination(String destination) {

		switch (destination) {
			case "D1" -> this.destination = 1;
			case "D2" -> this.destination = 2;
			case "D3" -> this.destination = 3;
		}
	}

	public void setDestination(int destination) {
		this.destination = destination;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public void setQuantity1(int quantity1) {
		this.quantity1 = quantity1;
	}

	public void setQuantity2(int quantity2) {
		this.quantity2 = quantity2;
	}

	public void setQuantity3(int quantity3) {
		this.quantity3 = quantity3;
	}

	public int getType() {
		return this.type;
	}

	public int getDestination() {
		return this.destination;
	}

	public int getQuantity() {
		return this.quantity;
	}

	public int getQuantity1() {
		return this.quantity1;
	}

	public int getQuantity2() {
		return this.quantity2;
	}

	public int getQuantity3() {
		return this.quantity3;
	}
}
