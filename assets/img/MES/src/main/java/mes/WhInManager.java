package mes;

import orders.Order;

import static mes.MES.currentTimeSecs;
import static mes.MES.database;

public class WhInManager implements Runnable {

    Warehouse wh;
    Information info;
    Gateway gateway;
    String side, otherSide;
    MES mes = MES.getInstance();

    public WhInManager( Warehouse wh, Information info, Gateway gateway, String side, String otherSide) {
        this.wh = wh;
        this.info = info;
        this.gateway = gateway;
        this.side = side;
        this.otherSide = otherSide;
    }

    public synchronized void updateOrder(Order order, int receivedPiece) {
        int curr_estim, final_delay, delay, penalty;

        if (order.getType() == 1) {
            // SE FOR UMA ORDEM DO TIPO 1 -> TRANSFORMACAO
            if (receivedPiece == order.getTransform().getTo()) {
                // TIPO DE PEÇA RECEBIDA E IGUAL AO TIPO FINAL DA ORDEM ASSOCIADA
                // AUMENTA Q1 ATE Q1 = Q
                order.getTransform().setQuantity1(order.getTransform().getQuantity1()+1);
                // ATUALIZAR Q2 EM CADA UM DOS LADOS
                order.getTransform().setQuantity2(order.getTransform().getQuantity2()-1);

                if(side.equals("R") && order.getTransform().getQuantity2_R() > 0)
                    order.getTransform().setQuantity2_R(order.getTransform().getQuantity2_R()-1);
                else if(side.equals("L") && order.getTransform().getQuantity2_L() > 0)
                    order.getTransform().setQuantity2_L(order.getTransform().getQuantity2_L()-1);

                if (order.getTransform().getQuantity1() == order.getTransform().getQuantity()) {
                    // SE Q1 = Q ENTAO A ORDEM DE TRANSFORMACAO ESTA FINALIZADA
                    if (side.equals("R") ){
                        order.setStatusR(2);
                        if( (order.getStatusL()==4 || order.getStatusL()==1))
                            order.setStatusL(2);
                    }
                    else if (side.equals("L") ){
                        order.setStatusL(2);
                        if( (order.getStatusR()==4 || order.getStatusR()==1))
                            order.setStatusR(2);
                    }
                    // ATUALIZA OS DELAYS E PENALTYS
                    order.getTransform().setEnd( currentTimeSecs() );
                    final_delay = order.getTransform().getEnd() - order.getTransform().getTime1();
                    delay       = final_delay - order.getTransform().getMaxDelay();
                    if (delay > 0){
                        // HOUVE DE FACTO ATRASO
                        penalty     = (int) Math.round((double)delay/50 + 0.49);
                        order.getTransform().setPenaltyIncurred( penalty * order.getTransform().getPenalty() );
                    }
                }
            }
            else if (receivedPiece != order.getTransform().getTo()) {
                // TIPO DE PEÇA RECEBIDA E DIFENTE AO TIPO FINAL DA ORDEM ASSOCIADA
                // AUMENTA QAUX ATE QAUX = Q
                order.getTransform().setQuantityAux(order.getTransform().getQuantityAux()+1);
                order.getTransform().setQuantity2(order.getTransform().getQuantity2()-1);

                if(side.equals("R") && order.getTransform().getQuantity2_R() > 0)
                    order.getTransform().setQuantity2_R(order.getTransform().getQuantity2_R()-1);
                else if(side.equals("L") && order.getTransform().getQuantity2_L() > 0)
                    order.getTransform().setQuantity2_L(order.getTransform().getQuantity2_L()-1);

                if(order.getTransform().getQuantityAux() == order.getTransform().getQuantity()){
                    // SE QAUX = Q ENTÃO A ORDEM DE TRANSFORMAÇÃO VAI ENTRAR NO 2º CICLO
                    order.getTransform().setQuantity3(order.getTransform().getQuantity());
                    order.getTransform().setFromAUX(receivedPiece);
                    order.getTransform().setQuantityAux(order.getTransform().getQuantity());
                    // REINCIA O ESTADO DA ORDEM PARA O SEGUNDO CICLO
                    //if(side.equals("R"))
                    order.setStatusR(0);
                    //else if(side.equals("L"))
                    order.setStatusL(0);
                }
            }
            // ATUALIZA O TEMPO ESTIMADADO RESTANTE
            curr_estim =  ( order.getEstimation()/order.getTransform().getQuantity()) * order.getTransform().getQuantity1()  ;
            order.setEstimation_curr(order.getEstimation() - curr_estim);
        }
        database.updateOrder(order);
    }

    public void run() {
        int pieceIn, orderID;
        Order order;

        //noinspection InfiniteLoopStatement
        while(true) {
            if (mes.isON()) {
                if (info.isWaitingWhIn(side)) {
                    pieceIn = info.getStorePiece(side);
                    orderID = info.getStorePieceOrder(side);

                    if (pieceIn > 0 && orderID > 0) {
                        // VER A ORDEM ASSOCIADA NO MES
                        if (mes.getOrder(side).getNumber() == orderID) {
                            order = mes.getOrder(side);
                        }
                        // SE A ORDEM FOI DIVIDIDA ENTRE OS DOIS LADOS
                        else if (mes.getOrder(otherSide).getNumber() == orderID){
                            order = mes.getOrder(otherSide);
                        }
                        // SE FOR LOAD -> CRIAR ORDEM E ENVIAR DB
                        else if ((pieceIn == 1 || pieceIn == 2) && orderID == 3) {
                            // LOAD
                            System.out.println("WHIN LOAD TYPE: " + pieceIn);
                            orderID = database.createLoad(pieceIn);
                            order = database.getOrderDB(orderID);
                        }
                        // SE NÃO FOR IGUAL VER NA DB
                        else {
                            order = database.getOrderDB(orderID);
                        }
                        // CONFIRMA STORE
                        info.storePiece(side);
                        // AUTALIZA WH & DB
                        wh.sendWH(pieceIn);
                        // database.updateWh(wh);
                        //AUTALIZA ORDEM & DB
                        updateOrder(order, pieceIn);
                        // ESPERA PELO PLC
                        //noinspection StatementWithEmptyBody
                        while (info.isWaitingWhIn(side)) {
                        }
                    }
                }
            }
        }
    }
}
