package mes;

public class Statistics {
    
    private static final int numUnloadZones        = 3;
    private static final int numMachinesPerCell    = 4;

    private static final int[] d_p1 = new int[numUnloadZones];
    private static final int[] d_p2 = new int[numUnloadZones];
    private static final int[] d_p3 = new int[numUnloadZones];
    private static final int[] d_p4 = new int[numUnloadZones];
    private static final int[] d_p5 = new int[numUnloadZones];
    private static final int[] d_p6 = new int[numUnloadZones];
    private static final int[] d_p7 = new int[numUnloadZones];
    private static final int[] d_p8 = new int[numUnloadZones];
    private static final int[] d_p9 = new int[numUnloadZones];

    private static final int[] m_R_p1p2 = new int[numMachinesPerCell];
    private static final int[] m_R_p2p3 = new int[numMachinesPerCell];
    private static final int[] m_R_p3p4 = new int[numMachinesPerCell];
    private static final int[] m_R_p4p5 = new int[numMachinesPerCell];
    private static final int[] m_R_p5p6 = new int[numMachinesPerCell];
    private static final int[] m_R_p5p9 = new int[numMachinesPerCell];
    private static final int[] m_R_p6p7 = new int[numMachinesPerCell];
    private static final int[] m_R_p6p8 = new int[numMachinesPerCell];
    private static final int[] m_R_time = new int[numMachinesPerCell];

    private static final int[] m_L_p1p2 = new int[numMachinesPerCell];
    private static final int[] m_L_p2p3 = new int[numMachinesPerCell];
    private static final int[] m_L_p3p4 = new int[numMachinesPerCell];
    private static final int[] m_L_p4p5 = new int[numMachinesPerCell];
    private static final int[] m_L_p5p6 = new int[numMachinesPerCell];
    private static final int[] m_L_p5p9 = new int[numMachinesPerCell];
    private static final int[] m_L_p6p7 = new int[numMachinesPerCell];
    private static final int[] m_L_p6p8 = new int[numMachinesPerCell];
    private static final int[] m_L_time = new int[numMachinesPerCell];

    public int[] getD_p1() {
        return d_p1;
    }

    public int[] getD_p2() {
        return d_p2;
    }

    public int[] getD_p3() {
        return d_p3;
    }

    public int[] getD_p4() {
        return d_p4;
    }

    public int[] getD_p5() {
        return d_p5;
    }

    public int[] getD_p6() {
        return d_p6;
    }

    public int[] getD_p7() {
        return d_p7;
    }

    public int[] getD_p8() {
        return d_p8;
    }

    public int[] getD_p9() {
        return d_p9;
    }

    public int[] getM_R_p1p2() {
        return m_R_p1p2;
    }

    public int[] getM_R_p2p3() {
        return m_R_p2p3;
    }

    public int[] getM_R_p3p4() {
        return m_R_p3p4;
    }

    public int[] getM_R_p4p5() {
        return m_R_p4p5;
    }

    public int[] getM_R_p5p6() {
        return m_R_p5p6;
    }

    public int[] getM_R_p5p9() {
        return m_R_p5p9;
    }

    public int[] getM_R_p6p7() {
        return m_R_p6p7;
    }

    public int[] getM_R_p6p8() {
        return m_R_p6p8;
    }

    public int[] getM_R_time() {
        return m_R_time;
    }

    public int[] getM_L_p1p2() {
        return m_L_p1p2;
    }

    public int[] getM_L_p2p3() {
        return m_L_p2p3;
    }

    public int[] getM_L_p3p4() {
        return m_L_p3p4;
    }

    public int[] getM_L_p4p5() {
        return m_L_p4p5;
    }

    public int[] getM_L_p5p6() {
        return m_L_p5p6;
    }

    public int[] getM_L_p5p9() {
        return m_L_p5p9;
    }

    public int[] getM_L_p6p7() {
        return m_L_p6p7;
    }

    public int[] getM_L_p6p8() {
        return m_L_p6p8;
    }

    public int[] getM_L_time() {
        return m_L_time;
    }

    public void update(String side){
        int n;
        String varName;

        for ( n = 0; n < numMachinesPerCell; n++) {
            varName = "m" + (n+1) + "_" + side;
            if ( side.equals("R") ){
                m_R_p1p2[n] = comms.OpcUaConnection.getValueINT("GVL", varName+"_p1p2");
                m_R_p2p3[n] = comms.OpcUaConnection.getValueINT("GVL", varName+"_p2p3");
                m_R_p3p4[n] = comms.OpcUaConnection.getValueINT("GVL", varName+"_p3p4");
                m_R_p4p5[n] = comms.OpcUaConnection.getValueINT("GVL", varName+"_p4p5");
                m_R_p5p6[n] = comms.OpcUaConnection.getValueINT("GVL", varName+"_p5p6");
                m_R_p5p9[n] = comms.OpcUaConnection.getValueINT("GVL", varName+"_p5p9");
                m_R_p6p7[n] = comms.OpcUaConnection.getValueINT("GVL", varName+"_p6p7");
                m_R_p6p8[n] = comms.OpcUaConnection.getValueINT("GVL", varName+"_p6p8");
                m_R_time[n] = comms.OpcUaConnection.getValueINT("GVL", varName+"_time");
            }
            else if ( side.equals("L") ){
                m_L_p1p2[n] = comms.OpcUaConnection.getValueINT("GVL", varName+"_p1p2");
                m_L_p2p3[n] = comms.OpcUaConnection.getValueINT("GVL", varName+"_p2p3");
                m_L_p3p4[n] = comms.OpcUaConnection.getValueINT("GVL", varName+"_p3p4");
                m_L_p4p5[n] = comms.OpcUaConnection.getValueINT("GVL", varName+"_p4p5");
                m_L_p5p6[n] = comms.OpcUaConnection.getValueINT("GVL", varName+"_p5p6");
                m_L_p5p9[n] = comms.OpcUaConnection.getValueINT("GVL", varName+"_p5p9");
                m_L_p6p7[n] = comms.OpcUaConnection.getValueINT("GVL", varName+"_p6p7");
                m_L_p6p8[n] = comms.OpcUaConnection.getValueINT("GVL", varName+"_p6p8");
                m_L_time[n] = comms.OpcUaConnection.getValueINT("GVL", varName+"_time");
            }
        }

        for (n = 0; n < numUnloadZones; n++) {

            varName = "d" + (n+1) + "_";
            d_p1[n] = comms.OpcUaConnection.getValueINT("GVL", varName+"p1");
            d_p2[n] = comms.OpcUaConnection.getValueINT("GVL", varName+"p2");
            d_p3[n] = comms.OpcUaConnection.getValueINT("GVL", varName+"p3");
            d_p4[n] = comms.OpcUaConnection.getValueINT("GVL", varName+"p4");
            d_p5[n] = comms.OpcUaConnection.getValueINT("GVL", varName+"p5");
            d_p6[n] = comms.OpcUaConnection.getValueINT("GVL", varName+"p6");
            d_p7[n] = comms.OpcUaConnection.getValueINT("GVL", varName+"p7");
            d_p8[n] = comms.OpcUaConnection.getValueINT("GVL", varName+"p8");
            d_p9[n] = comms.OpcUaConnection.getValueINT("GVL", varName+"p9");
        }
    }
}
