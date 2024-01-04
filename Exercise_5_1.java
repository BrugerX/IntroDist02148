package cum.on;

import org.jspace.*;

import java.io.IOException;
import java.util.Arrays;

public class Exercise_5_1 {

    public static void main(String[] args) throws InterruptedException, IOException {
        SpaceRepository net = new SpaceRepository();
        SequentialSpace q = new SequentialSpace();
        SequentialSpace s = new SequentialSpace();
        SequentialSpace p = new SequentialSpace();

        net.add("q", q);
        net.add("p", p);
        net.add("s", s);

        q.put("token");
        p.put("token");

        net.addGate("tcp://localhost:31415/?keep");
        System.out.println("Server is running..");

        int size = 4;

        Activity[] activities = new Activity[size];
        Thread[] threads = new Thread[size];
        String[] names = {"A", "B", "C", "D"};
        String[][] sender = {{"p"}, {"s"}, {"q"}, {"p", "q"}};
        String[][] receiver = {{"p"}, {"p", "q"}, {"q"}, {"s"}};

        for(int i = 0; i < size; i++){
            activities[i] = new Activity(names[i], sender[i], receiver[i]);
            threads[i] = new Thread(activities[i]);
            threads[i].start();
        }
    }
}


class Activity implements Runnable{
    String name;
    String[] sender;
    String[] receiver;
    RemoteSpace[] rsSender;
    RemoteSpace[] rsReceiver;

    public Activity(String name, String[] sender, String[] receiver) throws IOException {
        this.name = name;
        this.sender = sender;
        this.receiver = receiver;
        rsSender = new RemoteSpace[sender.length];
        rsReceiver = new RemoteSpace[receiver.length];

        for(int i = 0; i < sender.length; i++){
            rsSender[i] = new RemoteSpace("tcp://localhost:31415/" + sender[i] + "?keep");
        }
        for(int i = 0; i < receiver.length; i++){
            rsReceiver[i] = new RemoteSpace("tcp://localhost:31415/" + receiver[i] + "?keep");
        }
    }

    public void run(){
        //System.out.println("name: " + name + ", sender:  " + Arrays.toString(sender) + ", receiver: " + Arrays.toString(receiver));
        while(true){
            try{
                for(int i = 0; i < receiver.length; i++){
                    rsReceiver[i].get(new ActualField("token"));
                }
                Thread.sleep(3000);
                System.out.println(name + " currently does work...");
                for(int i = 0; i < sender.length; i++){
                    rsSender[i].put("token");
                }
            } catch(Exception e){
                System.err.println(e);
            }

        }
    }
}
