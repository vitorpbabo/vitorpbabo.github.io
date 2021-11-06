package db;

import mes.*;
import orders.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    //------------------------------------------------------------------------------------------------------«
    //--------------------------------------- DATABASE FEUP ------------------------------------------------«
    private final String url    = "jdbc:postgresql://db.fe.up.pt:5432/siem2031?currentSchema=fabrica";
    private final String user   = "siem2031";
    private final String pass   = "GXJmRYvk";

    //------------------------------------------------------------------------------------------------------«
    //--------------------------------------- DATABASE LOCAL -----------------------------------------------«
//    private final String url    = "jdbc:postgresql://localhost:5432/postgres?currentSchema=public";
//    private final String user   = "postgres";
//    private final String pass   = "postgres";

    //-----------------------------------------------------------------------------------------------------«
    //--------------------------------------- TESTAR QUERIES ----------------------------------------------«
    public boolean connectionTest() {
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
           return true;
        }
        catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }
    //------------------------------------------------------------------------------------------------------«
    //--------------------------------------- APAGAR ORDENS DB ---------------------------------------------«
    public void clearOrders() {
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("TRUNCATE ordem;");
                conn.close();
            } catch (SQLException t) {
                System.out.println(t);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    //------------------------------------------------------------------------------------------------------«
    //--------------------------------------- ENVIAR ORDENS DB ---------------------------------------------«
    public void sendOrderToDB(Order order) {

        String str = null;

        if (order.getType() == 1) {
            str = "INSERT INTO ordem (id,de,para,qt,qt1,qt2,qt2_r,qt2_l,qt3,t,t1,maxd,penalty,t_start,t_end,penalty_inc,qtaux,fromaux,estado_r,estado_l,piece,dest,tipo,prioridade,estimacao,estimacao_curr) VALUES ("
                    + order.getNumber() + "," + order.getTransform().getFrom() + "," + order.getTransform().getTo() + "," + order.getTransform().getQuantity() + ","
                    + order.getTransform().getQuantity1() + "," + 0 + "," + 0 + "," + 0 + ","+ order.getTransform().getQuantity3() + "," + order.getTransform().getTime()
                    + "," + order.getTransform().getTime1() + "," + order.getTransform().getMaxDelay() + "," + order.getTransform().getPenalty()
                    + "," + order.getTransform().getStart() + "," + order.getTransform().getEnd() + "," + order.getTransform().getPenaltyIncurred()
                    + "," + order.getTransform().getQuantityAux() + "," + order.getTransform().getFromAUX() + "," + 0 + "," + 0 + "," + 0 + "," + 0
                    + "," + order.getType() + "," + order.getPriority() + "," + order.getEstimation() + "," + order.getEstimation_curr() + ");";
        }
        else if (order.getType() == 2) {
            str = "INSERT INTO ordem (id,de,para,qt,qt1,qt2,qt2_r,qt2_l,qt3,t,t1,maxd,penalty,t_start,t_end,penalty_inc,qtaux,fromaux,estado_r,estado_l, piece, dest, tipo, prioridade, estimacao, estimacao_curr) VALUES ("
                    + order.getNumber() + "," + 0 + "," + 0 + "," + order.getUnload().getQuantity() + ","
                    + 0 + "," + 0 + "," + 0 + "," + 0 + "," + order.getUnload().getQuantity3() + "," + 0
                    + "," + 0 + "," + 0 + "," + 1 + "," + 0 + "," + 0 + "," + 0
                    + "," + 0 + "," + 0 + "," + 0 + "," + 0 +  "," + order.getUnload().getType() + "," + order.getUnload().getDestination()
                    + "," + order.getType() + "," + order.getPriority() + "," + order.getEstimation() + "," + order.getEstimation_curr() +  ");";
        }

        System.out.println(str);

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(str);
                conn.close();
            } catch (SQLException t) {
                System.out.println(t);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    //------------------------------------------------------------------------------------------------------«
    //------------------------ VERIFICAR SE HA PECAS A TRANSFORMAR NA CELULA -------------------------------«
    public boolean existPieces(String side) {

        String estado = null, qt2 = null, str;

        if(side.equals("R")){
            estado = "estado_r";
            qt2 = "qt2_r";
        }
        else if(side.equals("L")){
            estado = "estado_l";
            qt2 = "qt2_l";
        }

        str = "SELECT * FROM ordem WHERE tipo=1 AND "+qt2+">0 AND ("+estado+"=1 OR "+estado+"=4) ORDER BY prioridade, id LIMIT 1;";

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(str);

            if (rs.next())
                return true;

        } catch (SQLException e) {
            System.out.println(e);
        }

        return false;
    }

    //------------------------------------------------------------------------------------------------------«
    //--------------------------------------- IR BUSCAR ORDENS POR ESTADO DB -------------------------------«
    public Order getOrder(String side, int state) {

        Order order = new Order();
        String estado = null, str;

        if(side.equals("R"))
            estado = "estado_r";
        else if(side.equals("L"))
            estado = "estado_l";

        str = "SELECT * FROM ordem WHERE "+estado+"="+state+" ORDER BY prioridade, id LIMIT 1;";

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(str);

            while (rs.next()) {

                order.setNumber(rs.getInt("id"));

                if (rs.getInt("tipo") == 1) {

                    order.setTransform(new Transform());

                    order.setType(rs.getInt("tipo"));
                    order.getTransform().setFrom(rs.getInt("de"));
                    order.getTransform().setTo(rs.getInt("para"));
                    order.getTransform().setQuantity(rs.getInt("qt"));
                    order.getTransform().setQuantity1(rs.getInt("qt1"));
                    order.getTransform().setQuantity2(rs.getInt("qt2"));
                    order.getTransform().setQuantity3(rs.getInt("qt3"));
                    order.getTransform().setTime(rs.getInt("t"));
                    order.getTransform().setTime1(rs.getInt("t1"));
                    order.getTransform().setMaxDelay(rs.getInt("maxd"));
                    order.getTransform().setStart(rs.getInt("t_start"));
                    order.getTransform().setEnd(rs.getInt("t_end"));
                    order.getTransform().setPenalty(rs.getInt("penalty"));
                    order.getTransform().setPenaltyIncurred(rs.getInt("penalty_inc"));
                    order.getTransform().setFromAUX(rs.getInt("fromaux"));
                    order.getTransform().setQuantityAux(rs.getInt("qtaux"));
                    order.setPriority(rs.getInt("prioridade"));
                    order.setEstimation(rs.getInt("estimacao"));
                    order.setEstimation_curr(rs.getInt("estimacao_curr"));
                    order.setStatusR(rs.getInt("estado_r"));
                    order.setStatusL(rs.getInt("estado_l"));
                    order.getTransform().setQuantity2_R(rs.getInt("qt2_r"));
                    order.getTransform().setQuantity2_L(rs.getInt("qt2_l"));
                }
                else if (rs.getInt("tipo") == 2) {

                    order.setUnload(new Unload());

                    order.setType(rs.getInt("tipo"));
                    order.getUnload().setType(rs.getInt("piece"));
                    order.getUnload().setDestination(rs.getInt("dest"));
                    order.getUnload().setQuantity(rs.getInt("qt"));
                    order.getUnload().setQuantity1(rs.getInt("qt1"));
                    order.getUnload().setQuantity2(rs.getInt("qt2"));
                    order.getUnload().setQuantity3(rs.getInt("qt3"));
                    order.setPriority(rs.getInt("prioridade"));
                    order.setEstimation(rs.getInt("estimacao"));
                    order.setEstimation_curr(rs.getInt("estimacao_curr"));
                    order.setStatusR(rs.getInt("estado_r"));
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        return order;
    }

    //------------------------------------------------------------------------------------------------------«
    //--------------------------------------- RECEBER ORDENS DB POR ID -------------------------------------«
    public Order getOrderDB(int ID) {

        Order order = new Order();

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM ordem WHERE id=" + ID + ";");

            while (rs.next()) {

                order.setNumber(rs.getInt("id"));

                if (rs.getInt("tipo") == 1) {

                    order.setTransform(new Transform());

                    order.setType(rs.getInt("tipo"));
                    order.getTransform().setFrom(rs.getInt("de"));
                    order.getTransform().setTo(rs.getInt("para"));
                    order.getTransform().setQuantity(rs.getInt("qt"));
                    order.getTransform().setQuantity1(rs.getInt("qt1"));
                    order.getTransform().setQuantity2(rs.getInt("qt2"));
                    order.getTransform().setQuantity3(rs.getInt("qt3"));
                    order.getTransform().setTime(rs.getInt("t"));
                    order.getTransform().setTime1(rs.getInt("t1"));
                    order.getTransform().setMaxDelay(rs.getInt("maxd"));
                    order.getTransform().setStart(rs.getInt("t_start"));
                    order.getTransform().setEnd(rs.getInt("t_end"));
                    order.getTransform().setPenalty(rs.getInt("penalty"));
                    order.getTransform().setPenaltyIncurred(rs.getInt("penalty_inc"));
                    order.getTransform().setFromAUX(rs.getInt("fromaux"));
                    order.getTransform().setQuantityAux(rs.getInt("qtaux"));
                    order.setPriority(rs.getInt("prioridade"));
                    order.setEstimation(rs.getInt("estimacao"));
                    order.setEstimation_curr(rs.getInt("estimacao_curr"));
                    order.setStatusR(rs.getInt("estado_r"));
                    order.setStatusL(rs.getInt("estado_l"));
                    order.getTransform().setQuantity2_R(rs.getInt("qt2_r"));
                    order.getTransform().setQuantity2_L(rs.getInt("qt2_l"));
                }
                else if (rs.getInt("tipo") == 2) {

                    order.setUnload(new Unload());

                    order.setType(rs.getInt("tipo"));
                    order.getUnload().setType(rs.getInt("piece"));
                    order.getUnload().setDestination(rs.getInt("dest"));
                    order.getUnload().setQuantity(rs.getInt("qt"));
                    order.getUnload().setQuantity1(rs.getInt("qt1"));
                    order.getUnload().setQuantity2(rs.getInt("qt2"));
                    order.getUnload().setQuantity3(rs.getInt("qt3"));
                    order.setPriority(rs.getInt("prioridade"));
                    order.setEstimation(rs.getInt("estimacao"));
                    order.setEstimation_curr(rs.getInt("estimacao_curr"));
                    order.setStatusR(rs.getInt("estado_r"));
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        return order;
    }

    //------------------------------------------------------------------------------------------------------«
    //--------------------------------------- PROCURAR ORDENS DB POR PRIORIDADE ----------------------------«
    public synchronized Order getNextOrder(String side) {

        Order order = new Order();
        String str = null;

        if(side.equals("R")) {
            str = "SELECT * FROM ordem WHERE estado_r=0 AND estado_l=0 ORDER BY prioridade , id LIMIT 1";
        }
        else if(side.equals("L")) {
            str = "SELECT * FROM ordem WHERE estado_l=0 AND estado_r=0 AND tipo=1 ORDER BY prioridade , id LIMIT 1";
        }

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(str);

            while (rs.next()) {

                order.setNumber(rs.getInt("id"));

                if (rs.getInt("tipo") == 1) {

                    order.setTransform(new Transform());

                    order.setType(rs.getInt("tipo"));
                    order.getTransform().setFrom(rs.getInt("de"));
                    order.getTransform().setTo(rs.getInt("para"));
                    order.getTransform().setQuantity(rs.getInt("qt"));
                    order.getTransform().setQuantity1(rs.getInt("qt1"));
                    order.getTransform().setQuantity2(rs.getInt("qt2"));
                    order.getTransform().setQuantity3(rs.getInt("qt3"));
                    order.getTransform().setTime(rs.getInt("t"));
                    order.getTransform().setTime1(rs.getInt("t1"));
                    order.getTransform().setMaxDelay(rs.getInt("maxd"));
                    order.getTransform().setStart(rs.getInt("t_start"));
                    order.getTransform().setEnd(rs.getInt("t_end"));
                    order.getTransform().setPenalty(rs.getInt("penalty"));
                    order.getTransform().setPenaltyIncurred(rs.getInt("penalty_inc"));
                    order.getTransform().setFromAUX(rs.getInt("fromaux"));
                    order.getTransform().setQuantityAux(rs.getInt("qtaux"));
                    order.setPriority(rs.getInt("prioridade"));
                    order.setEstimation(rs.getInt("estimacao"));
                    order.setEstimation_curr(rs.getInt("estimacao_curr"));
                    order.setStatusR(rs.getInt("estado_r"));
                    order.setStatusL(rs.getInt("estado_l"));
                    order.getTransform().setQuantity2_R(rs.getInt("qt2_r"));
                    order.getTransform().setQuantity2_L(rs.getInt("qt2_l"));
                }
                else if (rs.getInt("tipo") == 2) {

                    order.setUnload(new Unload());

                    order.setType(rs.getInt("tipo"));
                    order.getUnload().setType(rs.getInt("piece"));
                    order.getUnload().setDestination(rs.getInt("dest"));
                    order.getUnload().setQuantity(rs.getInt("qt"));
                    order.getUnload().setQuantity1(rs.getInt("qt1"));
                    order.getUnload().setQuantity2(rs.getInt("qt2"));
                    order.getUnload().setQuantity3(rs.getInt("qt3"));
                    order.setPriority(rs.getInt("prioridade"));
                    order.setEstimation(rs.getInt("estimacao"));
                    order.setEstimation_curr(rs.getInt("estimacao_curr"));
                    order.setStatusR(rs.getInt("estado_r"));
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return order;
    }

    //------------------------------------------------------------------------------------------------------«
    //--------------------------------------- ATUALIZAR ORDENS DB ------------------------------------------«
    public synchronized void updateOrder(Order order){
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            try (Statement stmt = conn.createStatement()) {
                String str = null;
                if(order.getType()==1) {
                     str = "UPDATE ordem SET"
                             + " qt1=" + order.getTransform().getQuantity1()
                             + ", qt2=" + order.getTransform().getQuantity2()
                             + ", qt2_R=" + order.getTransform().getQuantity2_R()
                             + ", qt2_L=" + order.getTransform().getQuantity2_L()
                             + ", qt3=" + order.getTransform().getQuantity3()
                             + ", t1=" + order.getTransform().getTime1()
                             + ", t_start=" + order.getTransform().getStart()
                             + ", t_end=" + order.getTransform().getEnd()
                             + ", penalty_inc=" + order.getTransform().getPenaltyIncurred()
                             + ", qtaux=" + order.getTransform().getQuantityAux()
                             + ", fromaux=" + order.getTransform().getFromAUX()
                             + ", estado_r=" + order.getStatusR()
                             + ", estado_l=" + order.getStatusL()
                             + ", estimacao_curr= " + order.getEstimation_curr()
                             + " WHERE id=" + order.getNumber() + ";";

                }
                else if(order.getType()==2) {
                    str = "UPDATE ordem SET"
                            + " qt1=" + order.getUnload().getQuantity1()
                            + ", qt2=" + order.getUnload().getQuantity2()
                            + ", qt3=" + order.getUnload().getQuantity3()
                            + ", estado_r=" + order.getStatusR()
                            + ", estimacao_curr= " + order.getEstimation_curr()
                            + " WHERE id=" + order.getNumber() + ";";
                }

                if (str != null)
                    stmt.executeUpdate(str);
                conn.close();
            } catch (SQLException t) {
                System.out.println(t);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    //------------------------------------------------------------------------------------------------------«
    //--------------------------------------- GET ORDER LIST -----------------------------------------------«
    public List getOrderList() {

        List<Order> orders = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM ordem WHERE tipo=1 ORDER BY id");

            while (rs.next()) {
                Order order = new Transform();

                order.setNumber(rs.getInt("id"));

                Transform transform = new Transform();

                transform.setFrom(rs.getInt("de"));
                transform.setTo(rs.getInt("para"));
                transform.setQuantity(rs.getInt("qt"));
                transform.setQuantity1(rs.getInt("qt1"));
                transform.setQuantity2(rs.getInt("qt2"));
                transform.setQuantity3(rs.getInt("qt3"));
                transform.setTime(rs.getInt("t"));
                transform.setTime1(rs.getInt("t1"));
                transform.setMaxDelay(rs.getInt("maxd"));
                transform.setStart(rs.getInt("t_start"));
                transform.setEnd(rs.getInt("t_end"));
                transform.setPenalty(rs.getInt("penalty"));
                transform.setPenaltyIncurred(rs.getInt("penalty_inc"));

                order.setTransform(transform);

                orders.add(order);
            }
        }
        catch (SQLException e) {
            System.out.println(e);
        }

        return orders;
    }

    //------------------------------------------------------------------------------------------------------«
    //--------------------------------------- ATUALIZAR ARMAZEM --------------------------------------------«
    public synchronized void updateWh(Warehouse wh) {

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("UPDATE storage SET"
                        + "  p1=" + wh.getnP(1)
                        + ", p2=" + wh.getnP(2)
                        + ", p3=" + wh.getnP(3)
                        + ", p4=" + wh.getnP(4)
                        + ", p5=" + wh.getnP(5)
                        + ", p6=" + wh.getnP(6)
                        + ", p7=" + wh.getnP(7)
                        + ", p8=" + wh.getnP(8)
                        + ", p9=" + wh.getnP(9) + ";");
                conn.close();
            } catch (SQLException t) {
                System.out.println(t);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

    }

    //------------------------------------------------------------------------------------------------------«
    //--------------------------------------- INICIALIZAR ARMAZEM ------------------------------------------«
    public void initWh() {

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("UPDATE storage SET"
                        + "  p1=400"
                        + ", p2=40"
                        + ", p3=20"
                        + ", p4=20"
                        + ", p5=20"
                        + ", p6=20"
                        + ", p7=0"
                        + ", p8=0"
                        + ", p9=0;");
                conn.close();
            } catch (SQLException t) {
                System.out.println(t);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

    }

    //------------------------------------------------------------------------------------------------------«
    //--------------------------------------- GET ARMAZEM --------------------------------------------------«

    public int []getWhDB() {
        int[] wh = new int[10];

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM storage");

            while (rs.next()) {

                wh[1] = (rs.getInt("p1"));
                wh[2] = (rs.getInt("p2"));
                wh[3] = (rs.getInt("p3"));
                wh[4] = (rs.getInt("p4"));
                wh[5] = (rs.getInt("p5"));
                wh[6] = (rs.getInt("p6"));
                wh[7] = (rs.getInt("p7"));
                wh[8] = (rs.getInt("p8"));
                wh[9] = (rs.getInt("p9"));
            }
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        return wh;
    }

    public int createLoad(int type) {

        int id = 0;

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(id) AS id FROM ordem");

            while (rs.next()) {
                id = rs.getInt("id");
            }
        }

        catch (SQLException e) {
            System.out.println(e);
        }

        if(id<1000) {
            id = 1000;
        } else {
            id++;
        }
        System.out.println("NEW LOAD ID: " + id);

        String str = null;

        if(type==1) {
            str = "INSERT INTO ordem (id,de,para,qt,qt1,qt2,qt3,t,t1,maxd,penalty,t_start,t_end,penalty_inc,qtaux,fromaux," +
                    "piece,dest,tipo,prioridade,estimacao,estimacao_curr,qt2_r,qt2_l,estado_r,estado_l) VALUES ("
                    + id + "," + 0 + "," + 0 + "," + 1 + ","
                    + 0 + "," + 0 + "," + 0 + "," + 0
                    + "," + 0 + "," + 0 + "," + 0 + "," + 0 + "," + 0 + "," + 0
                    + "," + 0 + "," + 0 + "," + 1 + "," + 0
                    + "," + 3 + "," + 0 + "," + 0  + "," + 0 + "," + 0 + "," + 0 + ","+ 2 + "," + 0 + ");";
        }
        else if(type==2) {
            str = "INSERT INTO ordem (id,de,para,qt,qt1,qt2,qt3,t,t1,maxd,penalty,t_start,t_end,penalty_inc,qtaux,fromaux," +
                    "piece,dest,tipo,prioridade,estimacao,estimacao_curr,qt2_r,qt2_l,estado_r,estado_l) VALUES ("
                    + id + "," + 0 + "," + 0 + "," + 1 + ","
                    + 0 + "," + 0 + "," + 0 + "," + 0
                    + "," + 0 + "," + 0 + "," + 0 + "," + 0 + "," + 0 + "," + 0
                    + "," + 0 + "," + 0 + "," + 2 + "," + 0
                    + "," + 3 + "," + 0 + "," + 0 + "," + 0 + "," + 0 + "," + 0 + ","+ 2 + "," + 0 + ");";
        }

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(str);
                conn.close();
            } catch (SQLException t) {
                System.out.println(t);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return id;
    }

    public List getWaitingOrders() {

        List<Order> orders = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM ordem WHERE estado_r=3 OR estado_l=3");

            while (rs.next()) {
                Order order = new Transform();

                order.setNumber(rs.getInt("id"));

                if (rs.getInt("tipo") == 1) {

                    order.setTransform(new Transform());

                    order.setType(rs.getInt("tipo"));
                    order.getTransform().setFrom(rs.getInt("de"));
                    order.getTransform().setTo(rs.getInt("para"));
                    order.getTransform().setQuantity(rs.getInt("qt"));
                    order.getTransform().setQuantity1(rs.getInt("qt1"));
                    order.getTransform().setQuantity2(rs.getInt("qt2"));
                    order.getTransform().setQuantity3(rs.getInt("qt3"));
                    order.getTransform().setTime(rs.getInt("t"));
                    order.getTransform().setTime1(rs.getInt("t1"));
                    order.getTransform().setMaxDelay(rs.getInt("maxd"));
                    order.getTransform().setStart(rs.getInt("t_start"));
                    order.getTransform().setEnd(rs.getInt("t_end"));
                    order.getTransform().setPenalty(rs.getInt("penalty"));
                    order.getTransform().setPenaltyIncurred(rs.getInt("penalty_inc"));
                    order.getTransform().setFromAUX(rs.getInt("fromaux"));
                    order.getTransform().setQuantityAux(rs.getInt("qtaux"));
                    order.setPriority(rs.getInt("prioridade"));
                    order.setEstimation(rs.getInt("estimacao"));
                    order.setEstimation_curr(rs.getInt("estimacao_curr"));
                    order.setStatusR(rs.getInt("estado_r"));
                    order.setStatusL(rs.getInt("estado_l"));
                    order.getTransform().setQuantity2_R(rs.getInt("qt2_r"));
                    order.getTransform().setQuantity2_L(rs.getInt("qt2_l"));

                } else if (rs.getInt("tipo") == 2) {

                    order.setUnload(new Unload());

                    order.setType(rs.getInt("tipo"));
                    order.getUnload().setType(rs.getInt("piece"));
                    order.getUnload().setDestination(rs.getInt("dest"));
                    order.getUnload().setQuantity(rs.getInt("qt"));
                    order.getUnload().setQuantity1(rs.getInt("qt1"));
                    order.getUnload().setQuantity2(rs.getInt("qt2"));
                    order.getUnload().setQuantity3(rs.getInt("qt3"));
                    order.setPriority(rs.getInt("prioridade"));
                    order.setEstimation(rs.getInt("estimacao"));
                    order.setEstimation_curr(rs.getInt("estimacao_curr"));
                    order.setStatusR(rs.getInt("estado_r"));
                }
                orders.add(order);
            }
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        return orders;
    }

    //------------------------------------------------------------------------------------------------------«
    //--------------------------------------- ATUALIZAR ESTATISTICAS ---------------------------------------«
    public synchronized void updateMachStats() {
        String str = "";
        Statistics stats = new Statistics();

        int j=1;
        for(int i=3; i>=0; i--) {
            str = str + "UPDATE stats_mach SET" +
                    " p1p2=" +  stats.getM_L_p1p2()[i] +
                    " ,p2p3=" + stats.getM_L_p2p3()[i] +
                    " ,p3p4=" + stats.getM_L_p3p4()[i] +
                    " ,p4p5=" + stats.getM_L_p4p5()[i] +
                    " ,p5p6=" + stats.getM_L_p5p6()[i] +
                    " ,p5p9=" + stats.getM_L_p5p9()[i] +
                    " ,p6p7=" + stats.getM_L_p6p7()[i] +
                    " ,p6p8=" + stats.getM_L_p6p8()[i] +
                    " ,machtime=" + stats.getM_L_time()[i] +
                    " WHERE machid=" + j + ";";
            j++;
        }

        for(int i=3; i>=0; i--) {
            str = str + "UPDATE stats_mach SET" +
                    " p1p2="  + stats.getM_R_p1p2()[i] +
                    " ,p2p3=" + stats.getM_R_p2p3()[i] +
                    " ,p3p4=" + stats.getM_R_p3p4()[i] +
                    " ,p4p5=" + stats.getM_R_p4p5()[i] +
                    " ,p5p6=" + stats.getM_R_p5p6()[i] +
                    " ,p5p9=" + stats.getM_R_p5p9()[i] +
                    " ,p6p7=" + stats.getM_R_p6p7()[i] +
                    " ,p6p8=" + stats.getM_R_p6p8()[i] +
                    " ,machtime=" + stats.getM_R_time()[i] +
                    " WHERE machid=" + j + ";";
            j++;
        }

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(str);
                conn.close();
            } catch (SQLException t) {
                System.out.println(t);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public synchronized void updateUnloadStats() {
        String str = "";
        Statistics stats = new Statistics();

        for(int i=0; i<3; i++) {
            str = str + "UPDATE stats_unload SET" +
                    " p1="  + stats.getD_p1()[i] +
                    " ,p2=" + stats.getD_p2()[i] +
                    " ,p3=" + stats.getD_p3()[i] +
                    " ,p4=" + stats.getD_p4()[i] +
                    " ,p5=" + stats.getD_p5()[i] +
                    " ,p6=" + stats.getD_p6()[i] +
                    " ,p7=" + stats.getD_p7()[i] +
                    " ,p8=" + stats.getD_p8()[i] +
                    " ,p9=" + stats.getD_p9()[i] +
                    " WHERE id=" + (i+1) + ";";
        }

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(str);
                conn.close();
            } catch (SQLException t) {
                System.out.println(t);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public synchronized void updateTime() {

        List<Integer> ids = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id FROM ordem WHERE estado_r=0 AND estado_l=0 AND tipo=1 AND qt=qt3 and qtaux=0 ORDER BY prioridade, id;");

            while (rs.next()) {
                ids.add(rs.getInt("id"));
            }
        }
        catch (SQLException e) {
            System.out.println(e);
        }

        for(Integer id: ids) {
            String str = "WITH auxiliar AS (SELECT MIN(t1) AS minimo, SUM(estimacao) AS tempo FROM ordem " +
                    "WHERE prioridade<=(SELECT prioridade FROM ordem WHERE id=" + id + ") AND tipo=1 AND estado_l=0 AND estado_r=0 AND qt3=qt AND qtaux=0)" +
                    "UPDATE ordem SET t_start=GREATEST(auxiliar.minimo + (auxiliar.tempo - (SELECT estimacao FROM ordem WHERE id=" + id + "))/2,0) FROM auxiliar WHERE id=" + id + ";" +
                    "UPDATE ordem SET t_end=(SELECT t_start + estimacao FROM ordem WHERE id=" + id + ") WHERE id=" + id + ";" +
                    "UPDATE ordem SET penalty_inc=ROUND(GREATEST((SELECT t_end - t1 - maxd FROM ordem WHERE id=" + id + "),0)/50+0.49)*penalty WHERE id=" + id + ";";

            try (Connection conn = DriverManager.getConnection(url, user, pass)) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(str);
                    conn.close();
                } catch (SQLException t) {
                    System.out.println(t);
                }
            } catch (SQLException e) {
                System.out.println(e);
            }
        }
    }
}
