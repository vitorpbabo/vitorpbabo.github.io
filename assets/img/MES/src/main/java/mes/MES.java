package mes;

import db.Database;
import comms.*;
import gui.GUI;
import orders.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MES {

	static Database database 		= new Database();
	static Warehouse wh 			= new Warehouse(database);
	static Information info  		= new Information();
	private static Order order_L 	= new Order();
	private static Order order_R 	= new Order();
	private static boolean mesOn;

	public synchronized boolean isON() {
		return mesOn;
	}

	public synchronized void setMesOn(boolean mesOn) {
		MES.mesOn = mesOn;
	}

	private static final MES mes = new MES( );

	private MES() { }

	/* Static 'instance' method */
	public static MES getInstance( ) {
		return mes;
	}

	public synchronized Order getOrder(String side) {
		if(side.equals("R")){
			return order_R;
		}else if (side.equals("L")){
			return order_L;
		}
		return null;
	}

	//define program star time
	static long pStart;

	public static void resetDB (){
		//estabelecer ligação com a DB
		database.clearOrders(); //limpar ordens
		//valores iniciais
		database.initWh();
		wh.setnP(database.getWhDB());
	}

	public static void main(String[] args) throws IOException, InterruptedException {

		// INICIALIZAR TEMPO DO PROGRAMA
		pStart = System.currentTimeMillis();

		// INICIA GUI
		new GUI();

		// TESTAR CONEXAO COM A DATABASE
		while (!database.connectionTest()){
			// TENTA CONEXAO A CADA 2S
			GUI.setDbStatus(false);
			System.out.println("ERRO DE CONEXAO COM A BASE DE DADOS");
			TimeUnit.MILLISECONDS.sleep(2000);
		}
		GUI.setDbStatus(true);

		//ESTABLECER CONEXAO COM A FABRICA/PLC
		OpcUaConnection opcConn;
		String pcName = "localhost";
		String Client = "opc.tcp://" + pcName + ":4840";
		opcConn = new OpcUaConnection(Client);

		while (!opcConn.makeConnection()){
			// TENTA CONEXAO A CADA 2S
			GUI.setOpcStatus(false);
			System.out.println("ERRO DE CONEXAO OPC-UA COM PLC");
			TimeUnit.MILLISECONDS.sleep(2000);
		}
		GUI.setOpcStatus(true);

		Gateway gateway = new Gateway();

		Statistics stats = new Statistics();

		OrderXMLParser read = new OrderXMLParser();

		UDPReceive listen = new UDPReceive();
		listen.start();

		info.start();

		Runnable WhOutM_R = new WhOutManager(wh, info, gateway, "R");
		Runnable WhOutM_L = new WhOutManager(wh, info, gateway, "L");

		Runnable WhInM_R = new WhInManager(wh, info, gateway, "R", "L");
		Runnable WhInM_L = new WhInManager(wh, info, gateway, "L", "R");

		Runnable UnlM_1 = new UnloadManager(wh, info, gateway, 1);
		Runnable UnlM_2 = new UnloadManager(wh, info, gateway, 2);
		Runnable UnlM_3 = new UnloadManager(wh, info, gateway, 3);

		new Thread(WhOutM_R).start();
		new Thread(WhOutM_L).start();

		new Thread(WhInM_R).start();
		new Thread(WhInM_L).start();

		new Thread(UnlM_1).start();
		new Thread(UnlM_2).start();
		new Thread(UnlM_3).start();

		//Iniciar escrita de lista de ordens
        ListOrder listOrder = new ListOrder();

        //Iniciar escrita de storage
        CurrentStore currentStore = new CurrentStore();

		boolean existPiecesR, existPiecesL;
		Order nextOrder, interruptedOrder;

		// OBTEM AS ORDENS ATIVAS (SE EXISTIREM) ANTES DE INICIAR
		order_R = database.getOrder("R", 1);
		order_L = database.getOrder("L", 1);

		if (order_R.getNumber() == order_L.getNumber()){
			order_L = order_R;
		}

		System.out.println("[R] ACTIVE ORDER::"+order_R.getNumber());
		System.out.println("[L] ACTIVE ORDER::"+order_L.getNumber());
		//noinspection InfiniteLoopStatement
		while (true) {

			if(GUI.isResetDB()){
				resetDB();
				System.out.println("DATABASE RESETED");
				try {
					TimeUnit.MILLISECONDS.sleep(1000);
				} catch (InterruptedException interruptedException) {
					interruptedException.printStackTrace();
				}
				order_R = new Order();
				order_L = new Order();
				GUI.setResetDB(false);
				GUI.setDbResetLabel(false);
			}
			// VERIFICA SE O MES ESTA ATIVO NO BOTAO
			mes.setMesOn(GUI.isRunMES());

			if (mes.isON()) {

				//caso detete algo no UDP
				if (listen.getUDPflag()) {

					listen.setUDPflag(false);

					read.execute();

					if (read.isRequestOrderFlag()) {
						System.out.println("REQUEST ORDER");
						listOrder.newDoc(database.getOrderList());
						listen.sendListUDP();
						read.setRequestOrderFlag(false);
					}

					if (read.isRequestStoreFlag()) {
						System.out.println("REQUEST STORE");
						currentStore.newDoc(wh);
						listen.sendStorageUDP();
						read.setRequestStoreFlag(false);
					}
				}

				//verifica se ja existem pecas para ordens em espera
				List<Order> waitingOrders;
				waitingOrders = database.getWaitingOrders();

				if (waitingOrders.size() > 0) {
					for (Order aux : waitingOrders) {
						if (aux.getTransform() != null) {
							if ((aux.getTransform().getFromAUX() > 0 && wh.getnP(aux.getTransform().getFromAUX()) > aux.getTransform().getQuantity3()/2) ||
									(wh.getnP(aux.getTransform().getFrom()) > aux.getTransform().getQuantity3()/2 && aux.getTransform().getFromAUX() == 0)) {
								aux.setStatusR(0);
								aux.setStatusL(0);
								database.updateOrder(aux);
							}
						}
						else if (aux.getUnload() != null) {
							if (wh.getnP(aux.getUnload().getType()) > 0) {
								aux.setStatusR(0);
								aux.setStatusL(0);
								database.updateOrder(aux);
							}
						}
					}
				}
				System.out.println("\n----------- WAREHOUSE STORES -----------\n" +
						"P1: " +wh.getnP(1)+
						" | P2: " +wh.getnP(2)+
						" | P3: " +wh.getnP(3)+
						" | P4: " +wh.getnP(4)+
						" | P5: " +wh.getnP(5)+
						" | P6: " +wh.getnP(6)+
						" | P7: " +wh.getnP(7)+
						" | P8: " +wh.getnP(8)+
						" | P9: " +wh.getnP(9) );
				System.out.println("----------------------------------------");
				//------------------------------------------------------------------------------------------------------
				//--------------------------- CONTROLO DE ORDENS DO LADO DIREITO (R) -----------------------------------
				//------------------------------------------------------------------------------------------------------
				System.out.println("----------- ORDERS [R] -----------");
				nextOrder  			= database.getNextOrder("R");
				interruptedOrder 	= database.getOrder("R",5);
				existPiecesR 		= database.existPieces("R");
				if (existPiecesR)
					System.out.println("[R] HA PECAS A TRANSFORMAR NA CELULA ");
				System.out.println("[R] NEXT ORDER::"+nextOrder.getNumber()+"::status::"+nextOrder.getStatusR());
				// SE NAO EXISTEM ORDENS A PROCESSAR NO LADO DIREITO
				if (order_R.getStatusR() == 0 || order_R.getStatusR() == 2 || order_R.getStatusR() == 3 ){
					// VER PRIMEIRO ORDENS INTERROMPIDAS
					if (interruptedOrder.getNumber() > 0 && interruptedOrder.getType() == 1){
						if (checkTools(interruptedOrder, "R")) {
							System.out.println("[R] INTERRUPTED ORDER::" + interruptedOrder.getNumber());
							// VER SE NO LADO ESQUERDO ESTA A SER EXECUTADA ESSA ORDEM
							if (order_L.getNumber() == interruptedOrder.getNumber())
								order_R = order_L;
							else
								order_R = database.getOrder("R", 5);
						}
						// SE NAO ESPERA QUE TODAS AS PECAS SAIAM DA CELULA
						else if (!existPiecesR){
							order_R = database.getOrder("R", 5);
							order_R.setStatusR(1);
							System.out.println("[R] NAO HA PECAS NA CELULA NEXT: "+order_R.getNumber());
							database.updateOrder(order_R);
						}
					}
					// VERIFICAR SE EXISTE UMA ORDEM DE GRANDE QUANTIDADE DO LADO ESQUERDO PARA DISTRIBUIR
					else if (order_L.getTransform()!=null){
						// SE A ORDEM DA ESQUERDA FOR DE "GRANDE" QUANTIDADE E A ORDEM SEGUINTE DE MENOR PRIORIDADE
						if ( 		(order_L.getTransform().getQuantity3()>=8 && nextOrder.getPriority() > order_L.getPriority())
								||	(order_L.getTransform().getQuantity3()>=8 && nextOrder.getNumber() == 0) ){
							// FAZ A MESMA ORDEM DO LADO ESQUERDO
							order_R = order_L;
						}
						// SENAO FAZ A PROXIMA ORDEM SE TIVER AS MAQUINAS
						else if (nextOrder.getType()==1){
							if (checkTools(nextOrder, "R")) {
								order_R = database.getNextOrder("R");
							}
							// SE NAO ESPERA QUE TODAS AS PECAS SAIAM DA CELULA
							else if (!existPiecesR){
								order_R = database.getNextOrder("R");
								System.out.println("[R] NAO HA PECAS A TRANSFORMAR NA CELULA - NEXT ORDER: "+order_R.getNumber());
							}
						}
						else if (nextOrder.getType()==2){
							order_R = database.getNextOrder("R");
							System.out.println("[R] UNLOAD NEXT: "+order_R.getNumber());
						}
					}
					// SENAO FAZ A PROXIMA ORDEM SE TIVER AS MAQUINAS
					else if (nextOrder.getType()==1){
						if (checkTools(nextOrder, "R")) {
							order_R = database.getNextOrder("R");
						}
						// SE NAO ESPERA QUE TODAS AS PECAS SAIAM DA CELULA
						else if (!existPiecesR){
							order_R = database.getNextOrder("R");
							System.out.println("[R] NAO HA PECAS A TRANSFORMAR NA CELULA - NEXT ORDER: "+order_R.getNumber());
						}
					}
					else if (nextOrder.getType()==2){
						order_R = database.getNextOrder("R");
						System.out.println("[R] UNLOAD NEXT: "+order_R.getNumber());
					}

					if(order_R.getNumber()> 0){
						// INICIA START TIME SE FOR A PRIMEIRA VEZ
						if(order_R.getType() == 1 && order_R.getStatusR() == 0)
							order_R.getTransform().setStart( currentTimeSecs() );

						order_R.setStatusR(1);
						database.updateOrder(order_R);
					}
					System.out.println("[R] CKECK ORDER::" + order_R.getNumber() + "::status::" +  order_R.getStatusR());
				}
				// SE A PROXIMA ORDEM FOR DE DESCARGA E A ATUAL TRANSFORMACAO
				else if (order_R.getStatusR() == 1 && order_R.getType() == 1 && nextOrder.getType() == 2 ){
					// SE HOUVER PECAS DESSE TIPO
					if (wh.getnP(nextOrder.getUnload().getType()) > 0 ) {
						// INTERROMPE A ORDEM ATUAL
						order_R.setStatusR(5);
						database.updateOrder(order_R);
						// EXECUTA A ORDEM DE "MAIOR PRIORIDADE" -> UNLOAD
						order_R = database.getNextOrder("R");
						order_R.setStatusR(1);
						database.updateOrder(order_R);
					}
				}
				// SE SURGIR UMA ORDEM DE TRANSFORMACAO DE ALTA PRIORIDADE
				else if (order_R.getStatusR() == 1 && order_R.getType() == 1 && nextOrder.getType() == 1 ){
					if (		nextOrder.getTransform().getQuantity3() <= 4
							&& 	nextOrder.getPriority() < order_R.getPriority()
							&& 	Math.abs(nextOrder.getPriority() -  order_R.getPriority())>1000){
						// VERIFICAR FERRAMENTAS
						if (checkTools(nextOrder, "R")) {
							// INTERROMPE A ORDEM ATUAL
							order_R.setStatusR(5);
							database.updateOrder(order_R);
							// EXECUTA A ORDEM DE MAIOR PRIORIDADE
							order_R = database.getNextOrder("R");
							order_R.setStatusR(1);
							database.updateOrder(order_R);
						}
					}
				}
				// SE A ORDEM ESTIVER A SER FINALIZADA
				else if (order_R.getStatusR() == 4){
					// SE AS MAQUINAS TIVEREM AS TOOLS NECESSARIAS AVANCA PARA A PROXIMA ORDEM DE TRANSFORMACAO
					if (interruptedOrder.getNumber() > 0 && interruptedOrder.getType() == 1){
						// VER SE NO LADO ESQUERDO ESTA A SER EXECUTADA ESSA ORDEM
						if (checkTools(interruptedOrder, "R")) {
							if (order_L.getNumber() == interruptedOrder.getNumber())
								order_R = order_L;
							else
								order_R = database.getOrder("R",5);
							// ATUALIZA O ESTADO PARA ATIVO
							order_R.setStatusR(1);
							database.updateOrder(order_R);
						}
						// SE NAO ESPERA QUE TODAS AS PECAS SAIAM DA CELULA
						else if (!existPiecesR){
							order_R = database.getOrder("R", 5);
							order_R.setStatusR(1);
							System.out.println("[R] NAO HA PECAS NA CELULA NEXT: "+order_R.getNumber());
							database.updateOrder(order_R);
						}
					}
					else if (nextOrder.getType() == 1) {
						if (checkTools(nextOrder, "R")) {
							order_R = database.getNextOrder("R");
							order_R.setStatusR(1);
							database.updateOrder(order_R);
						}
						// SE NAO ESPERA QUE TODAS AS PECAS SAIAM DA CELULA
						else if (!existPiecesR){
							order_R = database.getNextOrder("R");
							order_R.setStatusR(1);
							System.out.println("[R] NAO HA PECAS NA CELULA NEXT: "+order_R.getNumber());
							database.updateOrder(order_R);
						}
					}
					// SE FOR UNLOAD
					else if (nextOrder.getType() == 2) {
						order_R = database.getNextOrder("R");
						order_R.setStatusR(1);
						database.updateOrder(order_R);
					}
				}
				// FINALIZAR ORDENS
				if (order_R.getStatusR() == 1){
					if (order_R.getType() == 1){
						if (order_R.getTransform().getQuantity1() == order_R.getTransform().getQuantity() ){
							order_R.setStatusR(2);
							database.updateOrder(order_R);
						}
						else if (order_R.getTransform().getQuantity3()==0 ){
							order_R.setStatusR(4);
							database.updateOrder(order_R);
						}
					}
					else if (order_R.getType() == 2){
						if (order_R.getUnload().getQuantity1() == order_R.getUnload().getQuantity() ){
							order_R.setStatusR(2);
							database.updateOrder(order_R);
						}
						else if (order_R.getUnload().getQuantity3()==0 ){
							order_R.setStatusR(4);
							database.updateOrder(order_R);
						}
					}
				}
				//--------------------------------------- ORDEM ATIVA R ------------------------------------------------
				System.out.println("[R] ACTIVE ORDER::" + order_R.getNumber() + "::status::" + order_R.getStatusR());
				//------------------------------------------------------------------------------------------------------

				//------------------------------------------------------------------------------------------------------
				//--------------------------- CONTROLO DE ORDENS DO LADO ESQUERDO (L) ----------------------------------
				//------------------------------------------------------------------------------------------------------
				System.out.println("----------- ORDERS [L] -----------");
				nextOrder 			= database.getNextOrder("L");
				interruptedOrder 	= database.getOrder("L", 5);
				existPiecesL 		= database.existPieces("L");
				if (existPiecesL)
					System.out.println("[L] HA PECAS A TRANSFORMAR NA CELULA ");
				System.out.println("[L] NEXT ORDER::"+nextOrder.getNumber()+"::status::"+nextOrder.getStatusL());
				// SE NAO EXISTEM ORDENS A PROCESSAR NO LADO ESQUERDO
				if (order_L.getStatusL() == 0 || order_L.getStatusL() == 2 || order_L.getStatusL() == 3 ) {
					// VER PRIMEIRO ORDENS INTERROMPIDAS
					if (interruptedOrder.getNumber() > 0){
						System.out.println("[L] INTERRUPTED ORDER::"+interruptedOrder.getNumber());
						// VER SE NO LADO DIREITO ESTA A SER EXECUTADA ESSA ORDEM
						if(checkTools(interruptedOrder, "L")){
							if(order_R.getNumber() == interruptedOrder.getNumber())
								order_L = order_R;
							else
								order_L = database.getOrder("L", 5);
						}
					}
					// VERIFICAR SE EXISTE UMA ORDEM DE GRANDE QUANTIDADE NO LADO DIREITO PARA DISTRIBUIR
					else if (order_R.getTransform()!=null){
						// SE A ORDEM DA DIREITA FOR DE "GRANDE" QUANTIDADE E A ORDEM SEGUINTE DE MENOR PRIORIDADE
						if ( 		(order_R.getType()==1 && order_R.getTransform().getQuantity3()>=10 && nextOrder.getPriority() > order_R.getPriority() )
								||  (order_R.getType()==1 && order_R.getTransform().getQuantity3()>=8 && nextOrder.getNumber()==0 ) ){
							// FAZ A ORDEM DA DIREITA
							order_L = order_R;
						}
						// SENAO FAZ A PROXIMA ORDEM SE TIVER AS MAQUINAS
						else if (nextOrder.getType()==1){
							if (checkTools(nextOrder, "L")) {
								order_L = database.getNextOrder("L");
							}
							// SE NAO ESPERA QUE TODAS AS PECAS SAIAM DA CELULA
							else if (!existPiecesL){
								order_L = database.getNextOrder("L");
								System.out.println("[L] NAO HA PECAS A TRANSFORMAR NA CELULA - NEXT ORDER: "+order_L.getNumber());
							}
						}
					}
					// SENAO FAZ A PROXIMA ORDEM SE TIVER AS MAQUINAS
					else if (nextOrder.getType()==1){
						if (checkTools(nextOrder, "L")) {
							order_L = database.getNextOrder("L");
						}
						// SE NAO ESPERA QUE TODAS AS PECAS SAIAM DA CELULA
						else if (!existPiecesL){
							order_L = database.getNextOrder("L");
							System.out.println("[L] NAO HA PECAS A TRANSFORMAR NA CELULA - NEXT ORDER: "+order_L.getNumber());
						}
					}

					if(order_L.getNumber()>0){
						// INICIA A ORDEM E START TIME
						if (order_L.getStatusL()==0)
							order_L.getTransform().setStart( currentTimeSecs() );
						order_L.setStatusL(1);
						database.updateOrder(order_L);
					}
					System.out.println("[L] CKECK ORDER::" + order_L.getNumber() + "::status::" +  order_L.getStatusL());
				}
				// SE SURGIR UMA ORDEM DE TRANSFORMACAO DE ALTA PRIORIDADE
				else if (order_L.getStatusL() == 1 && order_L.getType() == 1 && nextOrder.getType() == 1 ){
					if (	nextOrder.getTransform().getQuantity3() <= 4 	&&
							nextOrder.getPriority() < order_L.getPriority() &&
							Math.abs(nextOrder.getPriority() - order_L.getPriority())>1000){
						// SE TIVER AS MESMAS FERRAMENTAS
						if (checkTools(nextOrder, "L")) {
							// INTERROMPE A ORDEM ATUAL
							order_L.setStatusL(5);
							database.updateOrder(order_L);
							// EXECUTA A ORDEM DE MAIOR PRIORIDADE
							order_L = database.getNextOrder("L");
							order_L.setStatusL(1);
							database.updateOrder(order_L);
						}
					}
				}
				// SE A ORDEM ESTIVER A SER FINALIZADA
				else if (order_L.getStatusL() == 4){
					// VER INTERROMPIDAS
					if (interruptedOrder.getNumber() > 0 && interruptedOrder.getType() == 1){
						System.out.println("[L] INTERRUPTED ORDER::"+interruptedOrder.getNumber());
						if(checkTools(interruptedOrder, "L")) {
							// VER SE NO LADO DIREITO ESTA A SER EXECUTADA ESSA ORDEM
							if (order_R.getNumber() == interruptedOrder.getNumber())
								order_L = order_R;
							else
								order_L = database.getOrder("L", 5);
							// ATUALIZA O ESTADO PARA ATIVO
							order_L.setStatusL(1);
							database.updateOrder(order_L);
						}
						// SENAO ESPERA QUE TODAS AS PECAS SAIAM DA CELULA
						else if (!existPiecesL) {
							order_L = database.getOrder("L", 5);
							order_L.setStatusL(1);
							System.out.println("[L] NAO HA PECAS NA CELULA NEXT: "+order_L.getNumber());
							database.updateOrder(order_L);
						}
					}
					// SE AS MAQUINAS TIVEREM AS TOOLS NECESSARIAS AVANCA PARA A PROXIMA
					else if (nextOrder.getType() == 1 && nextOrder.getNumber() > 0) {
						if (checkTools(nextOrder, "L")) {
							order_L = database.getNextOrder("L");
							order_L.setStatusL(1);
							database.updateOrder(order_L);
						}
						// SENAO ESPERA QUE TODAS AS PECAS SAIAM DA CELULA
						else if (!existPiecesL) {
							order_L = database.getNextOrder("L");
							order_L.setStatusL(1);
							System.out.println("[L] NAO HA PECAS NA CELULA NEXT: "+order_L.getNumber());
							database.updateOrder(order_L);
						}
					}
					else if (order_L.getTransform().getQuantity1() == order_L.getTransform().getQuantity()){
						order_L.setStatusL(2);
						database.updateOrder(order_L);
					}
				}
				// FINALIZACAO DA ULTIMA ORDEM
				if (order_L.getStatusL() == 1) {
					if (order_L.getType() == 1){
						if (order_L.getTransform().getQuantity1() == order_L.getTransform().getQuantity()){
							order_L.setStatusL(2);
							database.updateOrder(order_L);
						}
						else if (order_L.getTransform().getQuantity3() == 0 ){
							order_L.setStatusL(4);
							database.updateOrder(order_L);
						}
					}
					else if (order_L.getType() == 2){
						if (order_L.getUnload().getQuantity1() == order_L.getUnload().getQuantity()){
							order_L.setStatusL(2);
							database.updateOrder(order_L);
						}
						else if (order_L.getUnload().getQuantity3() == 0 ){
							order_L.setStatusL(4);
							database.updateOrder(order_L);
						}
					}
				}
				//--------------------------------------- ORDEM ATIVA L ------------------------------------------------
				System.out.println("[L] ACTIVE ORDER::" + order_L.getNumber() + "::status::" + order_L.getStatusL());
				//------------------------------------------------------------------------------------------------------

				//--------------------------------- ATUALIZAÇÃO DAS ESTATISTICAS ---------------------------------------
				stats.update("L");
				stats.update("R");
				database.updateMachStats();
				database.updateUnloadStats();
				//------------------------------------------------------------------------------------------------------
				TimeUnit.MILLISECONDS.sleep(500);
			}
			TimeUnit.MILLISECONDS.sleep(1000);
		}
	}

	public static int currentTimeSecs() {
		return (int)( (System.currentTimeMillis() - pStart )/1000);
	}

	public static synchronized boolean checkTools(Order order, String thisCell){

		Machine maq = new Machine();
		int[] newTools, currTools;
		boolean skipToolsChange = false;

		// VERIFICAR SE E NECESSARIO MUDAR AS FERRAMENTAS
		newTools = maq.machinePrep(order);
		currTools = info.getMaqTools(thisCell);

		// SE AS MAQUINAS FOREM EXATAMENTE AS MESMAS
		if(Arrays.equals( newTools, currTools ) ){
			System.out.println("["+thisCell+"] NAO NECESSITA TROCA DE MAQUINAS");
			skipToolsChange = true;
		}
		// SE FOR UMA ORDEM DE PEQUENA QUANTIDADE (1 MAQUINACAO) E HOUVER PELO MENOS 1 MAQUINA COM A FERRAMENTA
		else if(order.getTransform().getQuantity() <= 2 && maq.getNumberMac() == 1){
			for (int element : currTools) {
				if (element == newTools[0]) {
					System.out.println("["+thisCell+"] NAO NECESSITA TROCA DE MAQUINAS");
					skipToolsChange = true;
					break;
				}
			}
		}
		else
			System.out.println("["+thisCell+"] ESPERA TROCA DE MAQUINAS");

		return skipToolsChange;
	}

}