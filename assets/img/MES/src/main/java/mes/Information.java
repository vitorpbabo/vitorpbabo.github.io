package mes;

import comms.OpcUaConnection;

public class Information extends Thread {

	private int stateWhOutR;
	private int stateWhOutL;

	private boolean stateWhInR;
	private boolean stateWhInL;

	private boolean requestPieceR;
	private boolean requestPieceL;

	private int storePieceR;
	private int storePieceOrderR;

	private int storePieceL;
	private int storePieceOrderL;

	private boolean stateZD1;
	private boolean stateZD2;
	private boolean stateZD3;

	private int unloadPieceD1;
	private int unloadOrderD1;
	private int unloadPieceD2;
	private int unloadOrderD2;
	private int unloadPieceD3;
	private int unloadOrderD3;

	private final String cellName = "GVL";

	// Obter Máquinas
	public synchronized int[] getMaqTools(String side) {

		int[] maq_R = new int[4];
		int[] maq_L = new int[4];
		String varName;

		if(side.equals("R")) {
			//obter tool tips das máquinas direita
			for (int i = 0; i < 4; i++) {
				varName = "tool_typeM" + (i + 1) + "_R";
				maq_R[i] = (OpcUaConnection.getValueINT(cellName, varName));
			}
			return maq_R;
		}
		else if(side.equals("L")) {
			//obter tool tips das máquinas esquerda
			for (int i = 0; i < 4; i++) {
				varName = "tool_typeM" + (i + 1) + "_L";
				maq_L[i] = (OpcUaConnection.getValueINT(cellName, varName));
			}
			return maq_L;
		}
		return null;
	}

	// stateWhOut
	public synchronized void setStateWhOutL(int stateL) {
		this.stateWhOutL = stateL;
	}

	public synchronized void setStateWhOutR(int stateR) {
		this.stateWhOutR = stateR;
	}

	public synchronized int getStateWhOut(String side) {

		if(side.equals("R")) {
			return this.stateWhOutR;
		}
		else if(side.equals("L")) {
			return this.stateWhOutL;
		}
		return 0;
	}

	// stateWhIn
	public synchronized void setStateWhInL(boolean stateL) {
		this.stateWhInL = stateL;
	}

	public synchronized void setStateWhInR(boolean stateR) {
		this.stateWhInR = stateR;
	}

	public synchronized boolean isWaitingWhIn(String side) {

		if(side.equals("R")) {
			return this.stateWhInR;
		}
		else if(side.equals("L")) {
			return this.stateWhInL;
		}
		return false;
	}

	// Unload Zone

	public synchronized void setStateZD1(boolean stateZD1) {
		this.stateZD1 = stateZD1;
	}

	public synchronized void setStateZD2(boolean stateZD2) {
		this.stateZD2 = stateZD2;
	}

	public synchronized void setStateZD3(boolean stateZD3) {
		this.stateZD3 = stateZD3;
	}

	public synchronized boolean isWaitingZD(int destination) {
		switch (destination) {
			case 1:
				return stateZD1;
			case 2:
				return stateZD2;
			case 3:
				return stateZD3;
			default:
				break;
		}
		return false;
	}

	// SPAWN - requestPiece
	public synchronized void setRequestPieceR(boolean requestPieceR) {
		this.requestPieceR = requestPieceR;
	}

	public synchronized void setRequestPieceL(boolean requestPieceL) {
		this.requestPieceL = requestPieceL;
	}

	public synchronized boolean getRequestPiece(String side) {

		if(side.equals("R")) {
			return this.requestPieceR;
		}
		else if (side.equals("L")) {
			return this.requestPieceL;
		}
		return false;
	}

	// STORE - storePiece
	public synchronized void setStorePieceL(int storePieceL, int storePieceOrderL) {
		this.storePieceL = storePieceL;
		this.storePieceOrderL = storePieceOrderL;
	}

	public synchronized void setStorePieceR(int storePieceR, int storePieceOrderR) {
		this.storePieceR = storePieceR;
		this.storePieceOrderR = storePieceOrderR;
	}

	public synchronized int getStorePiece(String side){
		if(side.equals("R")){
			return this.storePieceR;
		}
		else if(side.equals("L")){
			return this.storePieceL;
		}
		return 0;
	}

	public synchronized int getStorePieceOrder(String side){
		if(side.equals("R")){
			return this.storePieceOrderR;
		}
		else if(side.equals("L")){
			return this.storePieceOrderL;
		}
		return 0;
	}

	// UNLOAD - unloadPiece

	public synchronized int getUnloadPiece(int destination) {
		switch (destination) {
			case 1:
				return unloadPieceD1;
			case 2:
				return unloadPieceD2;
			case 3:
				return unloadPieceD3;
			default:
				break;
		}
		return 0;
	}

	public synchronized int getUnloadOrder(int destination) {
		switch (destination) {
			case 1:
				return unloadOrderD1;
			case 2:
				return unloadOrderD2;
			case 3:
				return unloadOrderD3;
			default:
				break;
		}
		return 0;
	}

	public synchronized void setUnloadD1(int unloadPieceD1, int unloadOrderD1) {
		this.unloadPieceD1 = unloadPieceD1;
		this.unloadOrderD1 = unloadOrderD1;
	}

	public synchronized void setUnloadD2(int unloadPieceD2, int unloadOrderD2) {
		this.unloadPieceD2 = unloadPieceD2;
		this.unloadOrderD2 = unloadOrderD2;
	}

	public synchronized void setUnloadD3(int unloadPieceD3, int unloadOrderD3) {
		this.unloadPieceD3 = unloadPieceD3;
		this.unloadOrderD3 = unloadOrderD3;
	}

	// Envia a confirmação do spawn
	public synchronized void spawnPiece(String side) {

		if(side.equals("R")){
			OpcUaConnection.setValue(cellName,"spawnPieceR", true);
		}
		else if(side.equals("L")){
			OpcUaConnection.setValue(cellName,"spawnPieceL", true);
		}
	}

	// Envia a confirmação do store
	public synchronized void storePiece(String side) {
		if(side.equals("R")){
			OpcUaConnection.setValue(cellName,"pieceStoredR", true);
		}
		else if(side.equals("L")){
			OpcUaConnection.setValue(cellName,"pieceStoredL", true);
		}
	}

	// Envia a confirmação do unload
	public synchronized void unloadPiece(int destination) {

		if(destination == 1){
			OpcUaConnection.setValue(cellName,"pieceUnloadD1", true);
		}
		else if(destination == 2){
			OpcUaConnection.setValue(cellName,"pieceUnloadD2", true);
		}
		else if(destination == 3){
			OpcUaConnection.setValue(cellName,"pieceUnloadD3", true);
		}
		System.out.println("UNLOAD ZD:" + destination);
	}

	public void run() {
		int sPR, sOR, sPL, sOL, uPZ1, uPZ2, uPZ3, uOZ1, uOZ2, uOZ3;
		//noinspection InfiniteLoopStatement
		while(true) {

			//receber peça para o armazem do lado drt
			sPR = OpcUaConnection.getValueINT(cellName, "storePieceR");
			sOR = OpcUaConnection.getValueINT(cellName, "storeOrderR");
			if (sPR > 0 && sPR < 10 && sOR > 0) {
				setStorePieceR(sPR, sOR);
			} else if ( (sPR == 1  || sPR == 2) && sOR == 0){
				setStorePieceR(sPR, 0);
			}else {
				setStorePieceR(0, 0);
			}

			//receber peça para o armazem do lado esq
			sPL = OpcUaConnection.getValueINT(cellName, "storePieceL");
			sOL = OpcUaConnection.getValueINT(cellName, "storeOrderL");
			if (sPL > 0 && sPL < 10 && sOL > 0) {
				setStorePieceL(sPL, sOL);
			} else {
				setStorePieceL(0, 0);
			}

			//descarregar peça para na zona 1
			uPZ1 = OpcUaConnection.getValueINT(cellName, "unloadPieceD1");
			uOZ1 = OpcUaConnection.getValueINT(cellName, "unloadOrderD1");
			if (uPZ1 > 0 && uPZ1 < 10 && uOZ1 > 0) {
				setUnloadD1(uPZ1, uOZ1);
			} else {
				setUnloadD1(0, 0);
			}

			//descarregar peça para na zona 2
			uPZ2 = OpcUaConnection.getValueINT(cellName, "unloadPieceD2");
			uOZ2 = OpcUaConnection.getValueINT(cellName, "unloadOrderD2");
			if (uPZ2 > 0 && uPZ2 < 10 && uOZ2 > 0) {
				setUnloadD2(uPZ2, uOZ2);
			} else {
				setUnloadD2(0, 0);
			}

			//descarregar peça para na zona 3
			uPZ3 = OpcUaConnection.getValueINT(cellName, "unloadPieceD3");
			uOZ3 = OpcUaConnection.getValueINT(cellName, "unloadOrderD3");
			if (uPZ3 > 0 && uPZ3 < 10 && uOZ3 > 0) {
				setUnloadD3(uPZ3, uOZ3);
			} else {
				setUnloadD3(0, 0);
			}

			//verificar se o armazem de saida lado drt ta ocupado
			setStateWhOutR(OpcUaConnection.getValueINT(cellName, "stateWhOutR"));

			//verificar se o armazem de saida lado esq ta ocupado
			setStateWhOutL(OpcUaConnection.getValueINT(cellName, "stateWhOutL"));

			//verificar se existe pedido de spawn do lado direito
			setRequestPieceR(OpcUaConnection.getValueBOOL(cellName, "requestPieceR"));

			//verificar se existe pedido de spawn do lado esquerdo
			setRequestPieceL(OpcUaConnection.getValueBOOL(cellName, "requestPieceL"));

			//verificar se o armazem de entrada lado drt ta ocupado
			setStateWhInR(OpcUaConnection.getValueBOOL(cellName, "stateWhInR"));

			//verificar se o armazem de entrada lado esq ta ocupado
			setStateWhInL(OpcUaConnection.getValueBOOL(cellName, "stateWhInL"));

			//verificar se a zona de descarga 1 esta ocupada
			setStateZD1(OpcUaConnection.getValueBOOL(cellName, "stateZD1"));

			//verificar se a zona de descarga 2 esta ocupada
			setStateZD2(OpcUaConnection.getValueBOOL(cellName, "stateZD2"));

			//verificar se a zona de descarga 3 esta ocupada
			setStateZD3(OpcUaConnection.getValueBOOL(cellName, "stateZD3"));

		}
	}
}
