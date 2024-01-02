package cum.on;

import org.jspace.*;

import java.math.*;

import java.io.Writer;
import java.util.List;
import java.util.Objects;

public class LockedTable
{
    static int nr_philosophers = 10;
    int nr_locks;
    Space table;

    public static int calculate_nr_locks(int nr_philosophers)
    {
        return Math.floorDiv(nr_philosophers,2);
    }


    public static void place_forks(int nr_philosophers,Space table) throws InterruptedException {
        for(int i = 0; i < nr_philosophers;i++)
        {
            table.put("fork",i);
        }
    }

    public static void place_locks(int nr_locks,Space table) throws InterruptedException
    {
        for(int i = 0; i < nr_locks;i++)
        {
            table.put("lock");
        }

    }

    public static void main(String[] args){
        Space table = new SequentialSpace();
        int nr_locks = calculate_nr_locks(nr_philosophers);

        try {

            place_forks(nr_philosophers,table);
            place_locks(nr_locks,table);

        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Philosopher[] philosophers = new Philosopher[nr_philosophers];
        Thread[] threads = new Thread[nr_philosophers];

        for(int i = 0; i<nr_philosophers;i++)
        {
            philosophers[i] = new Philosopher(table,i,nr_philosophers);
        }

        for(int i = 0; i<nr_philosophers;i++)
        {
            threads[i] = new Thread(philosophers[i]);
            threads[i].start();
        }
    }
}

class Philosopher implements Runnable {

    Space table;
    int seat_nr;
    int nr_philosophers;

    Philosopher(Space table, int seat_nr, int nr_philosophers)
    {
        this.table = table;
        this.seat_nr = seat_nr;
        this.nr_philosophers = nr_philosophers;
    }

    public void run() {
        while (true) {
            try {

                table.get(new ActualField("lock"));
                System.out.println("Philospher nr " + this.seat_nr + " took the lock");
                table.get(new ActualField("fork"),new ActualField(this.seat_nr));
                System.out.println("Philospher nr " + this.seat_nr + " took the right fork");
                if(seat_nr != 0)
                {
                    table.get(new ActualField("fork"),new ActualField(this.seat_nr-1));
                }
                else
                {
                    table.get(new ActualField("fork"),new ActualField(this.nr_philosophers - 1));
                }

                System.out.println("Philospher nr " + this.seat_nr + " took the left fork");
                table.put("fork",this.seat_nr);

                if(seat_nr != 0)
                {
                    table.put("fork",this.seat_nr-1);
                }
                else
                {
                    table.put("fork",this.nr_philosophers - 1);
                }

                System.out.println("Philospher nr " + this.seat_nr + " put back the forks");
                table.put("lock");

            } catch (InterruptedException e) {
                System.out.println("Writer thread interrupted:" + e.getMessage() + "\nDue to:" + e.getCause());
            }

        }
    }
}