package mes;

import orders.Order;
import orders.Transform;
import static mes.MES.database;

public class Machine {

    private int numberMac;

    public synchronized int[] machinePrep (Order order){
        int[] machine = new int[4];

        //    UMA Maquinacao
        if(     (order.getTransform().getFrom() == 1 && order.getTransform().getTo() == 2) ||
                (order.getTransform().getFrom() == 4 && order.getTransform().getTo() == 5) ||
                (order.getTransform().getFrom() == 6 && order.getTransform().getTo() == 8)    )
        {
            machine[0] = 1;
            machine[1] = 1;
            machine[2] = 1;
            machine[3] = 1;

            numberMac = 1;

            order.setEstimation(estimationTransf(numberMac,0, order.getTransform().getQuantity()));
        }
        else if(    (order.getTransform().getFrom() == 2 && order.getTransform().getTo() == 3)   )
        {
            machine[0] = 2;
            machine[1] = 2;
            machine[2] = 2;
            machine[3] = 2;

            numberMac = 1;

            order.setEstimation(estimationTransf(numberMac, 0, order.getTransform().getQuantity()));
        }
        else if(    (order.getTransform().getFrom() == 5 && order.getTransform().getTo() == 6)   )
        {
            machine[0] = 2;
            machine[1] = 2;
            machine[2] = 2;
            machine[3] = 2;

            numberMac = 1;

            order.setEstimation(estimationTransf(numberMac, 1, order.getTransform().getQuantity()));
        }
        else if(    (order.getTransform().getFrom() == 3 && order.getTransform().getTo() == 4)   )
        {
            machine[0] = 3;
            machine[1] = 3;
            machine[2] = 3;
            machine[3] = 3;

            numberMac = 1;

            order.setEstimation(estimationTransf(numberMac, 0, order.getTransform().getQuantity()));
        }
        else if(    (order.getTransform().getFrom() == 5 && order.getTransform().getTo() == 9) ||
                    (order.getTransform().getFrom() == 6 && order.getTransform().getTo() == 7)   )
        {
            machine[0] = 3;
            machine[1] = 3;
            machine[2] = 3;
            machine[3] = 3;

            numberMac = 1;

            order.setEstimation(estimationTransf(numberMac, 1, order.getTransform().getQuantity()));
        }

        //    DUAS Maquinacoes
        else if(    (order.getTransform().getFrom() == 1 && order.getTransform().getTo() == 3)   )
        {
            machine[0] = 2;
            machine[1] = 2;
            machine[2] = 1;
            machine[3] = 1;

            numberMac = 2;

            order.setEstimation(estimationTransf(numberMac, 0, order.getTransform().getQuantity()));
        }
        else if(    (order.getTransform().getFrom() == 4 && order.getTransform().getTo() == 6)   )
        {
            machine[0] = 2;
            machine[1] = 2;
            machine[2] = 1;
            machine[3] = 1;

            numberMac = 2;

            order.setEstimation(estimationTransf(numberMac, 1, order.getTransform().getQuantity()));
        }
        else if(    (order.getTransform().getFrom() == 2 && order.getTransform().getTo() == 4)   )
        {
            machine[0] = 3;
            machine[1] = 3;
            machine[2] = 2;
            machine[3] = 2;

            numberMac = 2;

            order.setEstimation(estimationTransf(numberMac,  0, order.getTransform().getQuantity()));
        }
        else if(    (order.getTransform().getFrom() == 5 && order.getTransform().getTo() == 7)   )
        {
            machine[0] = 3;
            machine[1] = 3;
            machine[2] = 2;
            machine[3] = 2;

            numberMac = 2;

            order.setEstimation(estimationTransf(numberMac, 2, order.getTransform().getQuantity()));
        }
        else if(    (order.getTransform().getFrom() == 3 && order.getTransform().getTo() == 5))
        {
            machine[0] = 1;
            machine[1] = 1;
            machine[2] = 3;
            machine[3] = 3;

            numberMac = 2;

            order.setEstimation(estimationTransf(numberMac, 0, order.getTransform().getQuantity()));
        }
        else if(    (order.getTransform().getFrom() == 4 && order.getTransform().getTo() == 9))
        {
            machine[0] = 3;
            machine[1] = 3;
            machine[2] = 1;
            machine[3] = 1;

            numberMac = 2;

            order.setEstimation(estimationTransf(numberMac,  1, order.getTransform().getQuantity()));
        }
        else if(    (order.getTransform().getFrom() == 5 && order.getTransform().getTo() == 8))
        {
            machine[0] = 1;
            machine[1] = 1;
            machine[2] = 2;
            machine[3] = 2;

            numberMac = 2;

            order.setEstimation(estimationTransf(numberMac, 1, order.getTransform().getQuantity()));
        }

        //    TRES Maquinacoes
        else if(    (order.getTransform().getFrom() == 1 && order.getTransform().getTo() == 4))
        {
            machine[0] = 3;
            machine[1] = 2;
            machine[2] = 1;
            machine[3] = 1;

            numberMac = 3;

            order.setEstimation(estimationTransf(numberMac, 0, order.getTransform().getQuantity()));
        }
        else if(    (order.getTransform().getFrom() == 2 && order.getTransform().getTo() == 5))
        {
            machine[0] = 1;
            machine[1] = 3;
            machine[2] = 2;
            machine[3] = 2;

            numberMac = 3;

            order.setEstimation(estimationTransf(numberMac, 0, order.getTransform().getQuantity()));
        }
        else if(    (order.getTransform().getFrom() == 3 && order.getTransform().getTo() == 6))
        {
            machine[0] = 2;
            machine[1] = 2;
            machine[2] = 1;
            machine[3] = 3;

            numberMac = 3;

            order.setEstimation(estimationTransf(numberMac, 1, order.getTransform().getQuantity()));
        }
        else if(    (order.getTransform().getFrom() == 3 && order.getTransform().getTo() == 9))
        {
            machine[0] = 3;
            machine[1] = 3;
            machine[2] = 1;
            machine[3] = 3;

            numberMac = 3;

            order.setEstimation(estimationTransf(numberMac, 1, order.getTransform().getQuantity()));
        }
        else if(    (order.getTransform().getFrom() == 4 && order.getTransform().getTo() == 7))
        {
            machine[0] = 3;
            machine[1] = 3;
            machine[2] = 2;
            machine[3] = 1;

            numberMac = 3;

            order.setEstimation(estimationTransf(numberMac, 2, order.getTransform().getQuantity()));
        }
        else if(    (order.getTransform().getFrom() == 4 && order.getTransform().getTo() == 8))
        {
            machine[0] = 1;
            machine[1] = 2;
            machine[2] = 2;
            machine[3] = 1;

            numberMac = 3;

            order.setEstimation(estimationTransf(numberMac,  1, order.getTransform().getQuantity()));
        }

        //    QUATRO Maquinacoes
        else if(    (order.getTransform().getFrom() == 1 && order.getTransform().getTo() == 5))
        {
            machine[0] = 1;
            machine[1] = 3;
            machine[2] = 2;
            machine[3] = 1;

            numberMac = 4;

            order.setEstimation(estimationTransf(numberMac, 0, order.getTransform().getQuantity()));
        }
        else if(    (order.getTransform().getFrom() == 2 && order.getTransform().getTo() == 6))
        {
            machine[0] = 2;
            machine[1] = 1;
            machine[2] = 3;
            machine[3] = 2;

            numberMac = 4;

            order.setEstimation(estimationTransf(numberMac, 1, order.getTransform().getQuantity()));
        }
        else if(    (order.getTransform().getFrom() == 2 && order.getTransform().getTo() == 9))
        {
            machine[0] = 3;
            machine[1] = 1;
            machine[2] = 3;
            machine[3] = 2;

            numberMac = 4;

            order.setEstimation(estimationTransf(numberMac, 1, order.getTransform().getQuantity()));
        }
        else if(    (order.getTransform().getFrom() == 3 && order.getTransform().getTo() == 7))
        {
            machine[0] = 3;
            machine[1] = 2;
            machine[2] = 1;
            machine[3] = 3;

            numberMac = 4;

            order.setEstimation(estimationTransf(numberMac, 2, order.getTransform().getQuantity()));
        }
        else if(    (order.getTransform().getFrom() == 3 && order.getTransform().getTo() == 8))
        {
            machine[0] = 1;
            machine[1] = 2;
            machine[2] = 1;
            machine[3] = 3;

            numberMac = 4;

            order.setEstimation(estimationTransf(numberMac, 1, order.getTransform().getQuantity()));
        }

        //    CINCO Maquinacoes
        else if(    (order.getTransform().getFrom() == 1 && order.getTransform().getTo() == 6))
        {
            Order orderAux      = new Order();
            Transform transf    = new Transform();

            if(order.getTransform().getQuantityAux() < order.getTransform().getQuantity()){ // (order.getTransform().getQuantityAux() == 0)
                transf.setFrom(1); //transf.serFrom(order.getTransform().getFrom());
                transf.setTo(5);
                orderAux.setTransform(transf);

                machine = machinePrep(orderAux);
            }
            else if (order.getTransform().getQuantityAux() >= order.getTransform().getQuantity()){
                transf.setFrom(5); //transf.serFrom(order.getTransform().getfromAUX());
                transf.setTo(6);   //transf.serFrom(order.getTransform().getTo());
                orderAux.setTransform(transf);

                machine = machinePrep(orderAux);
            }

            numberMac = 5;

            order.setEstimation(estimationTransf(numberMac, 1, order.getTransform().getQuantity()) + 15);
        }
        else if(    (order.getTransform().getFrom() == 1 && order.getTransform().getTo() == 9))
        {
            Order orderAux = new Order();
            Transform transf = new Transform();

            if(order.getTransform().getQuantityAux() < order.getTransform().getQuantity()){
                transf.setFrom(1); //transf.serFrom(order.getTransform().getFrom());
                transf.setTo(5);
                orderAux.setTransform(transf);

                machine = machinePrep(orderAux);
            }
            else if (order.getTransform().getQuantityAux() >= order.getTransform().getQuantity()){
                transf.setFrom(5); //transf.serFrom(order.getTransform().getfromAUX());
                transf.setTo(9);   //transf.serFrom(order.getTransform().getTo());
                orderAux.setTransform(transf);

                machine = machinePrep(orderAux);
            }

            numberMac = 5;

            order.setEstimation(estimationTransf(numberMac, 1, order.getTransform().getQuantity()) + 15);
        }
        else if(    (order.getTransform().getFrom() == 2 && order.getTransform().getTo() == 7))
        {
            Order orderAux = new Order();
            Transform transf = new Transform();

            if(order.getTransform().getQuantityAux() < order.getTransform().getQuantity()){
                transf.setFrom(2); //transf.serFrom(order.getTransform().getFrom());
                transf.setTo(6);
                orderAux.setTransform(transf);

                machine = machinePrep(orderAux);
            }
            else if (order.getTransform().getQuantityAux() >= order.getTransform().getQuantity()){
                transf.setFrom(6); //transf.serFrom(order.getTransform().getfromAUX());
                transf.setTo(7);   //transf.serFrom(order.getTransform().getTo());
                orderAux.setTransform(transf);

                machine = machinePrep(orderAux);
            }

            numberMac = 5;

            order.setEstimation(estimationTransf(numberMac, 2, order.getTransform().getQuantity()) + 15 + 15);
        }
        else if(    (order.getTransform().getFrom() == 2 && order.getTransform().getTo() == 8))
        {
            Order orderAux = new Order();
            Transform transf = new Transform();

            if(order.getTransform().getQuantityAux() < order.getTransform().getQuantity()){
                transf.setFrom(2); //transf.serFrom(order.getTransform().getFrom());
                transf.setTo(6);
                orderAux.setTransform(transf);

                machine = machinePrep(orderAux);
            }
            else if (order.getTransform().getQuantityAux() >= order.getTransform().getQuantity()){
                transf.setFrom(6); //transf.serFrom(order.getTransform().getfromAUX());
                transf.setTo(8);   //transf.serFrom(order.getTransform().getTo());
                orderAux.setTransform(transf);

                machine = machinePrep(orderAux);
            }
            numberMac = 5;

            order.setEstimation(estimationTransf(numberMac, 1, order.getTransform().getQuantity()) + 15);
        }

        //    SEIS Maquinacoes
        else if(    (order.getTransform().getFrom() == 1 && order.getTransform().getTo() == 7))
        {
            Order orderAux = new Order();
            Transform transf = new Transform();

            if(order.getTransform().getQuantityAux() < order.getTransform().getQuantity()){
                transf.setFrom(1); //transf.serFrom(order.getTransform().getFrom());
                transf.setTo(5);
                orderAux.setTransform(transf);

                machine = machinePrep(orderAux);
            }
            else if (order.getTransform().getQuantityAux() >= order.getTransform().getQuantity()){
                transf.setFrom(5); //transf.serFrom(order.getTransform().getfromAUX());
                transf.setTo(7);   //transf.serFrom(order.getTransform().getTo());
                orderAux.setTransform(transf);

                machine = machinePrep(orderAux);
            }

            numberMac = 6;

            order.setEstimation(estimationTransf(numberMac, 2, order.getTransform().getQuantity()) + 15 + 15);
        }
        else if(    (order.getTransform().getFrom() == 1 && order.getTransform().getTo() == 8))
        {
            Order orderAux = new Order();
            Transform transf = new Transform();

            numberMac = 6;

            if(order.getTransform().getQuantityAux() < order.getTransform().getQuantity()){
                transf.setFrom(1); //transf.serFrom(order.getTransform().getFrom());
                transf.setTo(5);
                orderAux.setTransform(transf);

                machine = machinePrep(orderAux);
            }
            else if (order.getTransform().getQuantityAux() >= order.getTransform().getQuantity()){
                transf.setFrom(5); //transf.serFrom(order.getTransform().getfromAUX());
                transf.setTo(8);   //transf.serFrom(order.getTransform().getTo());
                orderAux.setTransform(transf);

                machine = machinePrep(orderAux);
            }

            order.setEstimation(estimationTransf(numberMac, 1, order.getTransform().getQuantity()));
        }
        else{
            order.setStatusR(-1);
            order.setStatusL(-1);
            System.out.println("ORDEM INVALIDA: "+order.getNumber());
        }

        database.updateOrder(order);

        return machine;
    }

    //PARA SER TESTADO
    public synchronized int estimationTransf(int numberMac, int extraMach, int quantity){

        int timeMach = 15;
        return ((extraMach + numberMac)* timeMach ) * quantity;
    }

    public int getNumberMac(){ return this.numberMac;}

}
