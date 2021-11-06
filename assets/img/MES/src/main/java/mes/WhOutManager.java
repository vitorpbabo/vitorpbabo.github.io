package mes;

import orders.Order;
import static mes.MES.database;

public class WhOutManager implements Runnable {

    Warehouse wh;
    Information info;
    Gateway gateway;
    String side;
    Order order;
    MES mes = MES.getInstance();

    public WhOutManager( Warehouse wh, Information info, Gateway gateway, String side) {
        this.wh = wh;
        this.info = info;
        this.gateway = gateway;
        this.side = side;
    }

    public synchronized void updateOrder(Order order) {

        if (order.getType() == 1) {
            // TRANSFORMACAO
            if (order.getTransform().getQuantity3() > 0) {
                // SE AINDA HOUVER PEÇAS PARA DAR SAPWN
                // INCIALIZA A ESTIMACAO
                if(order.getEstimation_curr()==-1)
                    order.setEstimation_curr(order.getEstimation());
                // INCREMENTA Q2: Nº DE PECAS EM PRODUCAO
                order.getTransform().setQuantity2(order.getTransform().getQuantity2()+1);
                if(side.equals("R"))
                    order.getTransform().setQuantity2_R(order.getTransform().getQuantity2_R()+1);
                else if(side.equals("L"))
                    order.getTransform().setQuantity2_L(order.getTransform().getQuantity2_L()+1);
                // DECREMENTA Q3: Nº DE PECAS A PRODUZIR
                order.getTransform().setQuantity3(order.getTransform().getQuantity3()-1);
            }
            if (order.getTransform().getQuantity3() == 0) {
                // A FINALIZAR -> PODE COMECAR NOVA ORDEM
                if (side.equals("R"))
                    order.setStatusR(4);
                else if (side.equals("L"))
                    order.setStatusL(4);
            }
        }
        else if (order.getType() == 2) {
            // DESCARGA
            if (order.getUnload().getQuantity3() > 0) {
                // SE AINDA HOUVER PEÇAS PARA DAR SAPWN
                // INCREMENTA Q2: Nº DE PECAS EM PRODUCAO
                order.getUnload().setQuantity2(order.getUnload().getQuantity2()+1);
                // DECREMENTA Q3: Nº DE PECAS A PRODUZIR
                order.getUnload().setQuantity3(order.getUnload().getQuantity3()-1);
            }
            if (order.getUnload().getQuantity3() == 0) {
                // A FINALIZAR -> PODE COMECAR NOVA ORDEM
                order.setStatusR(4);
                System.out.println("ORDER:"+order.getNumber()+" UNLOAD A FINALIZAR ESTADO: " + order.getStatusR());
            }
        }
        database.updateOrder(order);
    }

    public synchronized void spawnCycle(Order order, String side) {
        // ENVIA A ORDEM OPC-UA PARA PLC
        gateway.sendOrder(order, side);
        // ESPERA PELO PEDIDO DO PLC
        while(!info.getRequestPiece(side)){ }
        // REMOVE PEÇA DO ARMAZEM
        if (order.getType()==1){
            wh.takeWH(order.getTransform().getFrom());
        }else if (order.getType()==2){
            wh.takeWH(order.getUnload().getType());
        }
        // CONFIRMA SPAWN
        info.spawnPiece(side);
        // ATUALIZA ORDEM & DB
        updateOrder(order);
    }

    public void run() {
        int spawnType, status=0;

        //noinspection InfiniteLoopStatement
        while(true) {

            if (mes.isON()) {

                order = mes.getOrder(side);

                // VER SE O TAPETE DE SAIDA DE PEÇAS DO ARMAZEM ESTA LIVRE
                if (info.getStateWhOut(side) == 0) {
                    // VER TIPO DA ORDEM: TRANSFORMACAO OU UNLOAD E GARANTIR O ESTADO DA ORDEM
                    if (side.equals("R"))
                        status = order.getStatusR();
                    else if (side.equals("L"))
                        status = order.getStatusL();

                    if (order.getType() == 1 && status == 1 && order.getTransform().getQuantity3() > 0) {
                        // SE FOR DO TIPO 1 -> TRANSFORMACAO
                        if (order.getTransform().getFromAUX() > 0) {
                            // VERIFICAR SE A TRANSFORMACAO ESTA NO 2ºCICLO
                            spawnType = order.getTransform().getFromAUX();
                        }
                        else {
                            // SE NAO O TIPO DE PEÇA PARA SPAWN SERA O INICIAL
                            spawnType = order.getTransform().getFrom();
                        }
                        // VERIFICAR SE HA PEÇAS NO ARMAZEM
                        if (wh.getnP(spawnType) > 0) {
                            // SE SIM COMEÇAR SPAWN
                            System.out.println("WHOUT ORDER["+side+"]::"+order.getNumber()+" TYPE P:"+spawnType);
                            spawnCycle(order, side);
                        }
                        else if (wh.getnP(spawnType) == 0) {
                            // SE NAO COLOCAR A ORDEM EM ESPERA
                            if(side.equals("R"))
                                order.setStatusR(3);
                            else if(side.equals("L"))
                                order.setStatusL(3);
                            database.updateOrder(order);
                            System.out.println("SEM PEÇAS P["+spawnType+"]: ORDER["+side+"]::"+order.getNumber());
                        }
                    }
                    else if (order.getType() == 2 && status == 1 && order.getUnload().getQuantity3() > 0) {
                        // SE FOR DO TIPO 2 -> UNLOAD
                        // VERIFICAR SE HA PECAS NO ARMAZEM
                        if (wh.getnP(order.getUnload().getType()) > 0) {
                            // SE SIM COMEÇAR SPAWN
                            System.out.println("WHOUT ORDER[" + side + "]::"+order.getNumber()+" TYPE P:"+order.getUnload().getType());
                            spawnCycle(order, side);
                        }
                        else if (wh.getnP(order.getUnload().getType()) == 0) {
                            // SE NAO COLOCAR A ORDEM EM ESPERA
                            order.setStatusR(3);
                            database.updateOrder(order);
                            System.out.println("SEM PEÇAS P["+order.getUnload().getType()+"]: ORDER["+side+"]::"+order.getNumber());
                        }
                    }
                }
            }
        }
    }
}
