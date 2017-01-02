package gui.chat;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class MessengerThread extends Thread {

    private String from;
    private String to;

    private Queue<Message> income = new ConcurrentLinkedQueue<>();
    private Queue<Message> outcome = new ConcurrentLinkedQueue<>();

    public MessengerThread(String from, String to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public void run() {
        read();
        write();
    }

    private void read() {
        new Thread(() -> {
            while (MessengerThread.this.isInterrupted()){
                //TODO receive meassge and pass to controller
            }
        }).start();
    }

    private void write() {
        new Thread(() -> {
            while (MessengerThread.this.isInterrupted()){
                while(outcome.isEmpty()){
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Message message = outcome.poll();
                //TODO send Message
            }
        }).start();
    }

    public void send(Message message){
        income.add(message);
    }

}
