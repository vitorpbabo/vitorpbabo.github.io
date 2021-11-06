package orders;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import db.Database;
import mes.MES;
import mes.Machine;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import java.io.File;

public class OrderXMLParser {

	private boolean requestOrderFlag = false;
	private boolean requestStoreFlag = false;
	Database db = new Database();

	public synchronized void setRequestOrderFlag(boolean requestOrderFlag) {
		this.requestOrderFlag = requestOrderFlag;
	}

	public synchronized boolean isRequestOrderFlag() {
		return requestOrderFlag;
	}

	public synchronized void setRequestStoreFlag(boolean requestStoreFlag) {
		this.requestStoreFlag = requestStoreFlag;
	}

	public synchronized boolean isRequestStoreFlag() {
		return requestStoreFlag;
	}

	public void execute(){

		final Order order 	= new Order();

		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();  
			SAXParser saxParser = factory.newSAXParser();  
			DefaultHandler handler = new DefaultHandler() {

				@Override
				public void startElement(String uri, String localName, String qName, Attributes attributes) {
					
					//localName 	= Elements of start / end tag
					//qName 		= Variables/Attributes of a localName
					//Attributes 	= value qualified names (qName/Attributes)

					if(qName.equals("Order")) {

						order.setNumber(Integer.parseInt(attributes.getValue("Number")));

					} else if (qName.equalsIgnoreCase("UNLOAD")) {

						order.setUnload(new Unload());

						order.setType(2);

						order.setPriority(1);

						System.out.println(" ");
						System.out.println("New Order: UNLOAD || Number:" + order.getNumber());
						System.out.print(" || ");

						order.getUnload().setType(attributes.getValue("Type"));
						System.out.print("Type: " + order.getUnload().getType() + " || ");

						order.getUnload().setDestination(attributes.getValue("Destination"));
						System.out.print("Destination: " + order.getUnload().getDestination() + " || ");

						order.getUnload().setQuantity(Integer.parseInt(attributes.getValue("Quantity")));
						System.out.print("Quantity: " + order.getUnload().getQuantity());

						order.getUnload().setQuantity3(Integer.parseInt(attributes.getValue("Quantity")));

						System.out.println(" ");

						db.sendOrderToDB(order);

					} else if (qName.equalsIgnoreCase("TRANSFORM")) {

						order.setTransform(new Transform());

						order.setType(1);

						System.out.println(" ");
						System.out.println("New Order: TRANSFORM || Number:" + order.getNumber());
						System.out.print(" || ");

						order.getTransform().setFrom(attributes.getValue("From"));
						System.out.print("From Type: " + order.getTransform().getFrom() + " || ");

						order.getTransform().setTo(attributes.getValue("To"));
						System.out.print("To Type: " + order.getTransform().getTo() + " || ");

						order.getTransform().setQuantity(Integer.parseInt(attributes.getValue("Quantity")));
						System.out.print("Quantity: " + order.getTransform().getQuantity() + " || ");

						order.getTransform().setQuantity3(Integer.parseInt(attributes.getValue("Quantity")));

						order.getTransform().setTime(Integer.parseInt(attributes.getValue("Time")));
						System.out.print("Time: " + order.getTransform().getTime() + " || ");

						order.getTransform().setTime1(MES.currentTimeSecs());

						System.out.print("Time1: " + order.getTransform().getTime1() + " || ");
						
						order.getTransform().setMaxDelay(Integer.parseInt(attributes.getValue("MaxDelay")));
						System.out.print("MaxDelay: " + order.getTransform().getMaxDelay() + " || ");
						
						order.getTransform().setPenalty(Integer.parseInt(attributes.getValue("Penalty")));
						System.out.print("Penalty: " + order.getTransform().getPenalty());

						System.out.println(" ");

						// ----- ESTIMACAO ------
						Machine mach = new Machine();
						mach.machinePrep(order);
						order.setEstimation_curr(-1);
						// ----- --------- -------

						// ----- PRIORIDADE ------
						int t0, t1, maxd, penalty;
						t0 		= order.getTransform().getTime();
						t1 		= order.getTransform().getTime1();
						maxd 	= order.getTransform().getMaxDelay();
						penalty = order.getTransform().getPenalty();

						order.setPriority((t1+t0 + maxd)*1000/penalty);
						// ---------------------

						db.sendOrderToDB(order);
						db.updateTime();

					} else if (qName.equalsIgnoreCase("Request_Orders")) {

						System.out.println(" ");
						System.out.println("New Order: REQUEST ORDERS");
						System.out.println(" ");

						setRequestOrderFlag(true);

					} else if (qName.equalsIgnoreCase("Request_Stores")) {

						System.out.println(" ");
						System.out.println("New Order: REQUEST STORES");
						System.out.println(" ");

						setRequestStoreFlag(true);
					}
				}
			};
			saxParser.parse("order.xml", handler);
		}
		catch (Exception e)   
		{  
			e.printStackTrace();  
		}
	}


}  