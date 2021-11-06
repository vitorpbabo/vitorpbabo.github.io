package mes;

import orders.Order;
import static mes.MES.database;

public class UnloadManager implements Runnable {

    Warehouse wh;
    Information info;
    Gateway gateway;
    int destination;
    MES mes = MES.getInstance();

    public UnloadManager(Warehouse wh, Information info, Gateway gateway, int destination) {
        this.wh = wh;
        this.info = info;
        this.gateway = gateway;
        this.destination = destination;
    }

    public synchronized void updateOrder(Order order, int receivedPiece) {

        if (order.getType() == 2) {
            // SE FOR UMA ORDEM DO TIPO 2 -> DESCARGA
            if (receivedPiece == order.getUnload().getType()) {
                // TIPO DE PEÇA RECEBIDA E IGUAL AO TIPO DA ORDEM ASSOCIADA
                // AUMENTA Q1 ATE Q1 = Q E DECREMENTA Q2
                order.getUnload().setQuantity1(order.getUnload().getQuantity1() + 1);
                order.getUnload().setQuantity2(order.getUnload().getQuantity2() - 1);
                if (order.getUnload().getQuantity1() == order.getUnload().getQuantity()) {
                    // SE Q1 = Q ENTAO A ORDEM DE DESCARGA ESTA FINALIZADA
                    order.setStatusR(2);
                }
            }
        }
        // ATUALIZA DB
        database.updateOrder(order);
    }

    public void run() {
        int pieceIn, orderID;
        Order order;

        //noinspection InfiniteLoopStatement
        while(true) {
            if (mes.isON()) {
                if (info.isWaitingZD(destination)) {
                    pieceIn = info.getUnloadPiece(destination);
                    orderID = info.getUnloadOrder(destination);

                    if ((pieceIn > 0 && orderID > 0)) {
                        // VER A ORDEM ASSOCIADA NO MES
                        if (mes.getOrder("R").getNumber() == orderID) {
                            order = mes.getOrder("R");
                        } else {
                            //SE NÃO FOR IGUAL VER NA DB
                            order = database.getOrderDB(orderID);
                        }
                        // CONFIRMA STORE
                        info.unloadPiece(destination);
                        //AUTALIZA ORDEM & DB
                        updateOrder(order, pieceIn);
                        // ESPERA PELO PLC
                        //noinspection StatementWithEmptyBody
                        while (info.isWaitingZD(destination)) {
                        }
                    }
                }
            }
        }
    }
}
