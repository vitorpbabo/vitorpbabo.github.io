package mes;

import comms.OpcUaConnection;
import orders.Order;
import static mes.MES.info;

public class Gateway {

	private int[] tools_R;
	private int[] tools_L;

	public int[] getTools(String side) {

		if(side.equals("R"))
			return tools_R;
		else if (side.equals("L"))
			return tools_L;

		return null;
	}

	public void setTools(String side, int[] tools) {

		if(side.equals("R"))
			tools_R = tools;
		else if (side.equals("L"))
			tools_L = tools;

	}

	public boolean checkTools (Order order, String side){
		// VERIFICAR AS FERRAMENTAS NECESSARIAS
		Machine mach 	= new Machine();
		int[] newTools 	= mach.machinePrep(order);
		int[] currTools = info.getMaqTools(side);
		boolean skip 	= false;

		setTools(side, newTools);
		// VERIFICAR SE E NECESSARIO MUDAR AS FERRAMENTAS
		if(order.getTransform().getQuantity() <= 2 && mach.getNumberMac() == 1) {
			// SE FOR UMA ORDEM DE PEQUENA QUANTIDADE, 1 MAQUINACAO E HOUVER PELO MENOS 1 MAQUINA COM A FERRAMENTA
			if ((	(currTools[0] == newTools[0]) || 	// tool_M1
					(currTools[1] == newTools[0]) || 	// tool_M2
					(currTools[2] == newTools[0]) || 	// tool_M3
					(currTools[3] == newTools[0])) &  	// tool_M4
					newTools[0]!=0) {
				// SE EXISTIR PELO MENOS 1 NAO A TROCA DE MAQUINAS
				skip = true;
				setTools(side, currTools);
			}
			else if (order.getTransform().getQuantity2_L()==0) {
				// SE NAO EXISTIR TROCA PARA MODO "STANDARD"
				newTools[0] = 1;
				newTools[1] = 2;
				newTools[2] = 3;
				newTools[3] = 1;
				skip = false;
				setTools(side, newTools);
			}
		}
		else if (order.getTransform().getQuantity() <= 2 && mach.getNumberMac() == 2) {
			// SE FOR UMA ORDEM DE PEQUENA QUANTIDADE, 2 MAQUINACOES E HOUVER PELO MENOS 2 MAQUINAS COM AS FERRAMENTAS
			if(		(	(currTools[0] == newTools[0])
					|| 	(currTools[1] == newTools[1]) ) &&
					(	(currTools[2] == newTools[2])
					|| 	(currTools[3] == newTools[3]) ) ) {
				// SE EXISTIR PELO MENOS 2 NAO A TROCA DE MAQUINAS
				skip = true;
				setTools(side, currTools);
			}
		}
		System.out.println("TOOLS CELL: "+side
				+" | Tool_M1 :" + getTools(side)[0]
				+" | Tool_M2 :" + getTools(side)[1]
				+" | Tool_M3 :" + getTools(side)[2]
				+" | Tool_M4 :" + getTools(side)[3]);
		return skip;
	}

	public void sendOrder(Order order, String side)
	{
		String cellName = "GVL";
		int spawnPiece;
		boolean skipToolsChange;

		if (side.equals("L")) {

			if (order.getTransform().getFromAUX() == 0)
				// PRIMEIRO CICLO DE TRANSFORMACAO
				spawnPiece = order.getTransform().getFrom();
			else
				// SEGUNDO CICLO DE TRANSFORMACAO
				spawnPiece = order.getTransform().getFromAUX();

			// VERIFICAR AS FERRAMENTAS NECESSARIAS
			skipToolsChange = checkTools(order, side);

			System.out.println("-------------- ORDER L --------------");
			OpcUaConnection.setValue(cellName, "orderType_L", order.getType());
			OpcUaConnection.setValue(cellName, "orderNumber_L", order.getNumber());
			OpcUaConnection.setValue(cellName, "initialPiece_L", spawnPiece);
			OpcUaConnection.setValue(cellName, "finalPiece_L", order.getTransform().getTo());

			if (!skipToolsChange){
				OpcUaConnection.setValue(cellName, "tool_typeM1_L", tools_L[0]);
				OpcUaConnection.setValue(cellName, "tool_typeM2_L", tools_L[1]);
				OpcUaConnection.setValue(cellName, "tool_typeM3_L", tools_L[2]);
				OpcUaConnection.setValue(cellName, "tool_typeM4_L", tools_L[3]);
			}
			OpcUaConnection.setValue(cellName, "orderFlag_L", true);
		}
		else if (side.equals("R")) {

			if (order.getType()==1){
				// TRANSFORMACAO
				if (order.getTransform().getFromAUX() == 0)
					// PRIMEIRO CICLO DE TRANSFORMACAO
					spawnPiece = order.getTransform().getFrom();
				else
					// SEGUNDO CICLO DE TRANSFORMACAO
					spawnPiece = order.getTransform().getFromAUX();

				// VERIFICAR AS FERRAMENTAS NECESSARIAS
				skipToolsChange = checkTools(order, side);

				System.out.println("--------------ORDER R --------------");
				OpcUaConnection.setValue(cellName, "orderType_R", order.getType());
				OpcUaConnection.setValue(cellName, "orderNumber_R", order.getNumber());
				OpcUaConnection.setValue(cellName, "initialPiece_R", spawnPiece);
				OpcUaConnection.setValue(cellName, "finalPiece_R", order.getTransform().getTo());

				if(!skipToolsChange){
					OpcUaConnection.setValue(cellName, "tool_typeM1_R", tools_R[0]);
					OpcUaConnection.setValue(cellName, "tool_typeM2_R", tools_R[1]);
					OpcUaConnection.setValue(cellName, "tool_typeM3_R", tools_R[2]);
					OpcUaConnection.setValue(cellName, "tool_typeM4_R", tools_R[3]);
				}
				OpcUaConnection.setValue(cellName, "orderFlag_R", true);

			}
			else if (order.getType()==2){
				// DESCARGA
				System.out.println("--------------ORDER R --------------");
				OpcUaConnection.setValue(cellName,"orderType_R", order.getType());
				OpcUaConnection.setValue(cellName,"orderNumber_R", order.getNumber());
				OpcUaConnection.setValue(cellName,"finalPiece_R", order.getUnload().getType());
				OpcUaConnection.setValue(cellName,"destination", order.getUnload().getDestination());
				OpcUaConnection.setValue(cellName,"orderFlag_R", true);
			}
		}
	}
}
