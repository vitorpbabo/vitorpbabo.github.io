package comms;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.stack.client.UaTcpStackClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;

import java.util.concurrent.ExecutionException;

public class OpcUaConnection {
	
	private static OpcUaClient client;
	private static Object varValue;
	private static final String identifier = "|var|CODESYS Control Win V3 x64.Application.";
	private static final int namespaceIndex = 4;
	private final String clientName;
	static boolean print=false;

	public OpcUaConnection(String clientName) {
		super();
		this.clientName = clientName;
	}

	public static OpcUaClient getClient() {
		return client;
	}

	public static Object getVarValue() {
		return varValue;
	}

	public static void setVarValue(Object varValue) {
		OpcUaConnection.varValue = varValue;
	}

	//Connection Function
	public boolean makeConnection() {

		EndpointDescription[] endpoints;
		try {
			endpoints = UaTcpStackClient.getEndpoints(clientName).get();
			OpcUaClientConfigBuilder cfg = new OpcUaClientConfigBuilder();
			cfg.setEndpoint(endpoints[0]);
			client = new OpcUaClient(cfg.build());
			client.connect().get();
			return true;
		}
		catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return false;
		}
	}

	/*
	 * Function to read a specific variable
	 * String cellName 	->  Symbol where variable is declared (eg. GVL) "See symbol configuration in Codesys
	 * String varName 	->  Variable name
	 */
	public static boolean getValueBOOL(String cellName, String varName) {
		String fullID = identifier + cellName + "." + varName;
		NodeId nodeID = new NodeId(namespaceIndex, fullID);
		DataValue value;

		try {
		value = client.readValue(0, TimestampsToReturn.Both, nodeID).get();
		setVarValue(value);
		varValue = ((DataValue) getVarValue()).getValue().getValue();
		if(print) System.out.println("Variable: " +varName+ " | value:" + varValue);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		if(varValue!=null) {
			return (boolean) varValue;
		} else {
			return false;
		}
	}

	public static int getValueINT(String cellName, String varName) {
		String fullID = identifier + cellName + "." + varName;
		NodeId nodeID = new NodeId(namespaceIndex, fullID);
		DataValue value;

		try {
			value = client.readValue(0, TimestampsToReturn.Both, nodeID).get();
			setVarValue(value);
			varValue = ((DataValue) getVarValue()).getValue().getValue();
			if(print) System.out.println("Variable: " +varName+ " | value:" + varValue);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		if(varValue!=null) {
			return (int) ((Short) varValue);
		} else {
			return 0;
		}

	}

	/*
	 * Function to set a specific boolean variable
	 * String cellName 	->  Symbol where variable is declared (eg. GVL) "See symbol configuration in Codesys
	 * String varName 	->  Variable name
	 */

	public static void setValue(String cellName, String varName, boolean valueSet) {
		String fullID 	= identifier + cellName + "." + varName;
		NodeId nodeID 	= new NodeId(namespaceIndex, fullID);

		Variant v 		= new Variant(valueSet);
		DataValue dv 	= new DataValue(v);
		
		try {
			getClient().writeValue(nodeID, dv).get();
			if(print) System.out.println("Variable: " +varName+ " changed to: " + (client.readValue(0, TimestampsToReturn.Both, nodeID).get()).getValue().getValue());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	public static void setValue(String cellName, String varName, int valueSet) {
		String fullID = identifier + cellName + "." + varName;
		NodeId nodeID = new NodeId(namespaceIndex, fullID);

		Variant v = new Variant((short) valueSet);
		DataValue dv = new DataValue(v);

		try {
			getClient().writeValue(nodeID, dv).get();
			if(print) System.out.println("Variable: " +varName+ " changed to: " + (client.readValue(0, TimestampsToReturn.Both, nodeID).get()).getValue().getValue());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

}