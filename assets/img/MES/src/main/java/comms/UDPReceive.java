package comms;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UDPReceive extends Thread{

    private boolean UDPflag;
    private int port;
    private InetAddress address;
    //Criar socket
    DatagramSocket socket;

    public synchronized void setUDPflag(boolean bool) {
        this.UDPflag = bool;
    }

    public synchronized boolean getUDPflag() {
        return this.UDPflag;
    }

    public void run() {

        try {

            //Criar socket
            socket = new DatagramSocket(54321);

            // Criar buffer
            byte[] buffer = new byte[2048];

            // Criar packet para receber informação para o buffer
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            while (true) {

                socket.receive(packet);
                port = packet.getPort();
                address = packet.getAddress();

                String msg = new String(buffer, 0, packet.getLength());
                System.out.println(port);
                System.out.println(address);

                packet.setLength(buffer.length);

                if(msg!=null) {
                    FileWriter fr   = new FileWriter("order.xml");
                    Writer br       = new BufferedWriter(fr);

                    br.write(msg);
                    br.close();

                    setUDPflag(true);
                }
            }

        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public synchronized void sendListUDP() throws IOException {

        String rootPath     = new File(System.getProperty("user.dir")).getParent();
        String listLocation = rootPath + "\\list.xml";

        System.out.println(listLocation);
        System.out.println(Path.of(listLocation));
        byte [] data= Files.readAllBytes(Path.of(listLocation));
        System.out.println(data);
        DatagramPacket send = new DatagramPacket( data, data.length, address, port );
        socket.send(send);
    }

    public synchronized void sendStorageUDP() throws IOException {

        String rootPath     = new File(System.getProperty("user.dir")).getParent();
        String reqLocation  = rootPath + "\\request.xml";

        System.out.println(reqLocation);
        System.out.println(Path.of(reqLocation));
        byte [] data= Files.readAllBytes(Path.of(reqLocation));
        DatagramPacket send = new DatagramPacket( data, data.length, address, port );
        socket.send(send);
    }
}