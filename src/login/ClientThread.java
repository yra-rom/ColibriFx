package login;

import constants.SendKeys;
import logger.Log;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientThread extends Thread {
    private static final String HOST = "localhost";
    private static final int PORT = 5678;
    private final String TAG = this.getClass().getSimpleName();
    private Queue<HashMap<String, String>> outcome = new ConcurrentLinkedQueue<>();
    private List<HashMap<String, String>> income  = Collections.synchronizedList(new ArrayList<HashMap<String, String>>());

    private Socket socket;

    private static ClientThread instance = new ClientThread();
    public static ClientThread getInstance() {
        return instance;
    }

    private ClientThread() {
        start();
    }

    @Override
    public void run() {
        try {
            initSocket();
            initStreams();

            write();
            read();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write() {
        new Thread(() -> {
            try {
                ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
                while ( !ClientThread.this.isInterrupted() && socket.isConnected()) {
                    while (outcome.isEmpty()){
                        Thread.yield();
                    }
                    Object o = outcome.poll();
                    objectOutput.flush();
                    objectOutput.writeObject(o);
                    objectOutput.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void read() {
        new Thread(() -> {
            try {
                ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());
                while (!ClientThread.this.isInterrupted() && socket.isConnected()) {
                    try {
                        Object o = objectInput.readObject();
                        if (o instanceof HashMap) {
                            income.add((HashMap<String, String>) o);
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void initStreams() throws IOException {
    }

    public String authentication(Client client){
        String email = client.getEmail();
        String pass = client.getPass();
        HashMap<String, String> mapOut = new HashMap<>();
        mapOut.put(SendKeys.TITLE, SendKeys.AUTHENTICATION);
        mapOut.put(SendKeys.EMAIL, email);
        mapOut.put(SendKeys.PASS, pass);

        outcome.add(mapOut);

        String status = "";

        HashMap<String, String> map = findMapByTitle(SendKeys.AUTHENTICATION_ANSWER);

        status = map.get(SendKeys.AUTHENTICATION_ANSWER);

        Log.d(TAG, "Authentication status: " + status);

        return status;
    }

    public String registration(Client client) {
        String email = client.getEmail();
        String nick = client.getPass();
        String pass = client.getPass();
        String status = "";

        HashMap<String, String> mapOut = new HashMap<>();
        mapOut.put(SendKeys.TITLE, SendKeys.REGISTER);
        mapOut.put(SendKeys.EMAIL, email);
        mapOut.put(SendKeys.NICK, nick);
        mapOut.put(SendKeys.PASS, pass);

        outcome.add(mapOut);

        HashMap<String, String> mapIn = findMapByTitle(SendKeys.REGISTRATION_ANSWER);
        status = mapIn.get(SendKeys.REGISTRATION_ANSWER);

        return status;
    }

    private HashMap<String, String> findMapByTitle(String key){
        HashMap<String,String> map = null;
        while (map == null){
            for (HashMap<String, String> mapIn : income) {
                if (mapIn.get(SendKeys.TITLE).equals(key)) {
                    map = mapIn;
                    income.remove(mapIn);
                    break;
                }
            }
        }
        return map;
    }


    private void initSocket() throws IOException {
        Log.d(TAG, "Clinet about to connect...");
        socket = new Socket(HOST, PORT);
        Log.d(TAG, "Clinet connected.");
    }
}
