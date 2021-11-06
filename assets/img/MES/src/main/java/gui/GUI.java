package gui;

import org.w3c.dom.css.RGBColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI implements ActionListener {

    private static JLabel dbResetLabel;
    private static JButton button1;
    private static JButton button2;
    private static final JLabel opcStatus = new JLabel("OPC-UA CONNECTION:");
    private static final JLabel dbStatus = new JLabel("DATABASE CONNECTION:");
    private static boolean runMES   = false;
    private static boolean resetDB  = false;
    private static boolean dbConn   = false;

    public static boolean isRunMES() {
        return runMES;
    }

    public static void setRunMES(boolean runMES) {
        GUI.runMES = runMES;
    }

    public static void setResetDB(boolean resetDB) {
        GUI.resetDB = resetDB;
    }

    public static boolean isResetDB() {
        return resetDB;
    }

    public static void setOpcStatus(boolean status) {
        if (status)
            GUI.opcStatus.setText("OPC-UA CONNECTION: OK");
        else
            GUI.opcStatus.setText("OPC-UA CONNECTION: ERROR");
    }

    public static void setDbStatus(boolean status) {
        if (status){
            GUI.dbStatus.setText("DATABASE CONNECTION: OK");
            GUI.dbConn = true;
        }
        else
            GUI.dbStatus.setText("DATABASE CONNECTION: ERROR");
            GUI.dbConn = false;
    }

    public static void setDbResetLabel(boolean state) {
        if(state)
            GUI.dbResetLabel.setText("RESETED");
        else
            GUI.dbResetLabel.setText("");
    }

    public GUI(){

        runMES = false;
        JFrame frame    = new JFrame();
        JPanel panel1   = new JPanel();
        JPanel panel2   = new JPanel();

        // the clickable buttons
        button1 = new JButton("MES OFF");
        button2 = new JButton("RESET DB");
        button1.setBackground(Color.DARK_GRAY);
        button1.setForeground(Color.WHITE);
        button1.addActionListener(this);
        button1.setBackground(new Color(0xA30101));

        button2.setBackground(Color.DARK_GRAY);
        button2.setForeground(Color.WHITE);
        button2.addActionListener(this);

        dbResetLabel = new JLabel("");
        opcStatus.setForeground(Color.WHITE);
        opcStatus.setHorizontalAlignment(SwingConstants.CENTER);
        dbStatus.setForeground(Color.WHITE);
        dbStatus.setHorizontalAlignment(SwingConstants.CENTER);

        // the panel with the button and text
        panel1.add(button1);
        panel1.setBackground(Color.GRAY);
        panel1.add(new JLabel("                                                   "));
        panel1.add(button2);
        panel1.add(dbResetLabel);
        panel1.setBackground(Color.GRAY);
        panel1.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        panel2.setBackground(Color.DARK_GRAY);
        panel2.setLayout(new GridLayout(1, 1));
        panel2.add(opcStatus);
        panel2.add(dbStatus);

        frame.add(panel1, BorderLayout.NORTH);
        frame.add(panel2, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("MES UI");
        frame.setSize(400,200);
        frame.setLocationRelativeTo(null);
        Image icon = Toolkit.getDefaultToolkit().getImage("src/main/java/gui/icon64.png");
        frame.setIconImage(icon);
        frame.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button1) {
            if (runMES) {
                setRunMES(false);
                button1.setText("MES OFF");
                button1.setBackground(new Color(0xA30101));
            } else {
                setRunMES(true);
                button1.setText("MES ON");
                button1.setBackground(new Color(0x3B8D1A));
            }
        }

        if (e.getSource() == button2) {

            setResetDB(true);
            dbResetLabel.setText("RESETED");
        }
    }
}

