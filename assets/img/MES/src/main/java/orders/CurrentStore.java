package orders;
import mes.Warehouse;

import java.io.File;
import java.io.FileWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

public class CurrentStore {

	public void newDoc(Warehouse wh) {

		// writes the users
		XMLStreamWriter xsw;
		XMLOutputFactory xof 	= XMLOutputFactory.newInstance();
		String rootPath    		= new File(System.getProperty("user.dir")).getParent();
		String reqLocation 		= rootPath + "\\request.xml";
		try {
		    xsw = xof.createXMLStreamWriter(new FileWriter(reqLocation));
		    xsw.writeStartDocument();
		    xsw.writeCharacters("\n");
		    xsw.writeStartElement("Current_Stores"); xsw.writeCharacters("\n");
		        
			xsw.writeEmptyElement("WorkPiece type=\"P1\" quantity=\"" + Integer.toString(wh.getnP(1)) + "\""); xsw.writeCharacters("\n");
			xsw.writeEmptyElement("WorkPiece type=\"P2\" quantity=\"" + Integer.toString(wh.getnP(2)) + "\""); xsw.writeCharacters("\n");
			xsw.writeEmptyElement("WorkPiece type=\"P3\" quantity=\"" + Integer.toString(wh.getnP(3)) + "\""); xsw.writeCharacters("\n");
			xsw.writeEmptyElement("WorkPiece type=\"P4\" quantity=\"" + Integer.toString(wh.getnP(4)) + "\""); xsw.writeCharacters("\n");
			xsw.writeEmptyElement("WorkPiece type=\"P5\" quantity=\"" + Integer.toString(wh.getnP(5)) + "\""); xsw.writeCharacters("\n");
			xsw.writeEmptyElement("WorkPiece type=\"P6\" quantity=\"" + Integer.toString(wh.getnP(6)) + "\""); xsw.writeCharacters("\n");
			xsw.writeEmptyElement("WorkPiece type=\"P7\" quantity=\"" + Integer.toString(wh.getnP(7)) + "\""); xsw.writeCharacters("\n");
			xsw.writeEmptyElement("WorkPiece type=\"P8\" quantity=\"" + Integer.toString(wh.getnP(8)) + "\""); xsw.writeCharacters("\n");
			xsw.writeEmptyElement("WorkPiece type=\"P9\" quantity=\"" + Integer.toString(wh.getnP(9)) + "\""); xsw.writeCharacters("\n");

		    xsw.writeEndElement();
		    xsw.writeEndDocument();
		    xsw.flush();
		    xsw.close();
		}
		catch (Exception e) {
		        System.err.println("Unable to write the file: " + e.getMessage());
		}
	}
}




//Metodo adicional caso seja necessario
/*xsw.writeStartElement("WorkPiece");


xsw.writeAttribute("nP1", Integer.toString(nP1));
xsw.writeAttribute("nP2", Integer.toString(nP2));
xsw.writeAttribute("nP3", Integer.toString(nP3));
xsw.writeAttribute("nP4", Integer.toString(nP4));
xsw.writeAttribute("nP5", Integer.toString(nP5));
xsw.writeAttribute("nP6", Integer.toString(nP6));
xsw.writeAttribute("nP7", Integer.toString(nP7));
xsw.writeAttribute("nP8", Integer.toString(nP8));
xsw.writeAttribute("nP9", Integer.toString(nP9));*/