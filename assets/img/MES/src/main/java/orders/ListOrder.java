package orders;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
	
	
public final class ListOrder {

		public void newDoc(List<Order> orders) {

			// writes the users
			XMLStreamWriter xsw;
			XMLOutputFactory xof 	= XMLOutputFactory.newInstance();
			String rootPath     	= new File(System.getProperty("user.dir")).getParent();
			String listLocation 	= rootPath + "\\list.xml";

			try {
			    xsw = xof.createXMLStreamWriter(new FileWriter(listLocation));
			    xsw.writeStartDocument();
			    xsw.writeCharacters("\n");
			    xsw.writeStartElement("Order_Schedule"); xsw.writeCharacters("\n");

			    for(Order order:orders) {
					xsw.writeStartElement("Order");
					xsw.writeAttribute("Number", Integer.toString(order.getNumber())); xsw.writeCharacters("\n");

					xsw.writeEmptyElement("Transform From=\"" + Integer.toString(order.getTransform().getFrom()) + "\"" +
							" To=\"" + Integer.toString(order.getTransform().getTo()) + "\"" +
							" Quantity=\"" + Integer.toString(order.getTransform().getQuantity()) + "\"" +
							" Quantity1=\"" + Integer.toString(order.getTransform().getQuantity1()) + "\"" +
							" Quantity2=\"" + Integer.toString(order.getTransform().getQuantity2()) + "\"" +
							" Quantity3=\"" + Integer.toString(order.getTransform().getQuantity3()) + "\"" +
							" Time=\"" + Integer.toString(order.getTransform().getTime()) + "\"" +
							" Time1=\"" + Integer.toString(order.getTransform().getTime1()) + "\"" +
							" MaxDelay=\"" + Integer.toString(order.getTransform().getMaxDelay()) + "\"" +
							" Penalty=\"" + Integer.toString(order.getTransform().getPenalty()) + "\"" +
							" Start=\"" + Integer.toString(order.getTransform().getStart()) + "\"" +
							" End=\"" + Integer.toString(order.getTransform().getEnd()) + "\"" +
							" PenaltyIncurred=\"" + Integer.toString(order.getTransform().getPenaltyIncurred()) + "\"" ); xsw.writeCharacters("\n");
							xsw.writeEndElement(); xsw.writeCharacters("\n");
				}

			    xsw.writeEndDocument();
			    xsw.flush();
			    xsw.close();
			}
			catch (Exception e) {
			        System.err.println("Unable to write the file: " + e.getMessage());
			}
		}
}
