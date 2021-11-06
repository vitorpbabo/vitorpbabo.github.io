package mes;

import db.Database;
import static mes.MES.database;

public class Warehouse {

	private int[] nP;

	public Warehouse(Database db) {
		nP = db.getWhDB();
	}

	public synchronized void setnP(int[] np) {
		this.nP = np;
	}

	public synchronized void setnP(int n, int piece) {
		this.nP[piece] = n;
	}

	public synchronized int getnP(int piece) {
		return nP[piece];
	}

	public synchronized void takeWH(int piece) {
		setnP(getnP(piece)-1,piece);
		database.updateWh(this);
	}

	public synchronized void sendWH(int piece) {
		setnP(getnP(piece)+1,piece);
		database.updateWh(this);
	}

}
