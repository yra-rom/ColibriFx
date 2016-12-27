package client;

import constants.Authentication;
import constants.SendKeys;
import gui.Controller;
import logger.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Singleton
 */
public class ClientThread extends Thread {
    private static final String HOST = "localhost";
    private static final int PORT = 5678;
    private final String TAG = this.getClass().getSimpleName();
    private static Controller controller;
    private Client client;

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

            write();
            read();
            while (!isInterrupted() && socket.isConnected()) {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            close();
        }
    }

    private void initSocket() throws IOException {
        Log.d(TAG, "Clinet about to connect...");
        socket = new Socket(HOST, PORT);
        Log.d(TAG, "Clinet connected.");
    }

    private void write() {
        new Thread(() -> {
            ObjectOutputStream objectOutput = null;
            try {
                objectOutput = new ObjectOutputStream(socket.getOutputStream());
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
            } finally {
                if (objectOutput != null) {
                    try {
                        objectOutput.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void read() {
        new Thread(() -> {
            ObjectInputStream objectInput = null;
            try {
                objectInput = new ObjectInputStream(socket.getInputStream());
                try {
                    while (!ClientThread.this.isInterrupted() && socket.isConnected()) {
                        Object o = objectInput.readObject();
                        if (o instanceof HashMap) {
                            income.add((HashMap<String, String>) o);
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    if (objectInput != null) {
                        try {
                            objectInput.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void close() {
        try {
            this.interrupt();
            if(socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        if(Authentication.SUCCESS.equals(status)){
            String nick = map.get(SendKeys.NICK);
            client.setNick(nick);
            client.setConfirmed(true);
            this.client = client;
        }

        return status;
    }

    public String registration(Client client) {
        String email = client.getEmail();
        String nick = client.getNick();
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
//            for (HashMap<String, String> mapIn : income) {
//                if (mapIn.get(SendKeys.TITLE).equals(key)) {
//                    map = mapIn;
//                    income.remove(mapIn);
//                    break;
//                }
//            }

//            for (Iterator<HashMap<String, String>> it = income.iterator(); it.hasNext(); ) {
//                HashMap<String, String> mapIn = it.next();
//                if (mapIn.get(SendKeys.TITLE).equals(key)) {
//                    map = mapIn;
//                    it.remove();
//                    break;
//                }
//            }

            Iterator<HashMap<String, String>> iterator = income.iterator();
            while(iterator.hasNext()) {
                HashMap<String, String> mapIn = iterator.next();
                if (mapIn.get(SendKeys.TITLE).equals(key)) {
                    map = mapIn;
                    iterator.remove();
                    break;
                }
            }
        }
        return map;
    }

    public void sendNewNick(String nick) {
        HashMap<String, String> map = new HashMap<>();
        map.put(SendKeys.TITLE, SendKeys.NEWNICK);
        map.put(SendKeys.NEWNICK, nick);
        map.put(SendKeys.EMAIL, client.getEmail());
        outcome.add(map);
    }

    public static void setController(Controller c) {
        controller = c;
    }

    public void askForFriends() {
        HashMap<String, String> mapOut = new HashMap<>();
        mapOut.put(SendKeys.TITLE, SendKeys.GET_FRIENDS);

    }

    public Client getClient() {
        return client;
    }
}
