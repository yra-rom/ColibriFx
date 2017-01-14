package client;

import constants.Authentication;
import constants.SendKeys;
import constants.ServerInfo;
import gui.Controller;
import gui.chat.ChatController;
import gui.contacts.ContactsController;
import lib.FilePart;
import lib.Message;
import logger.Log;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * Singleton
 */
public class ClientThread extends Thread {
    private static final int TIMEOUT_FRIEND_REQUEST_SECONDS = 2;
    public static final int TIMEOUT_WAIT_FOR_AUTHENTICATION_MILLIS = 25;
    private final String TAG = this.getClass().getSimpleName();
    private static final String HOST = ServerInfo.HOST;
    private static final int PORT = ServerInfo.PORT;
    private static Controller controller;

    private Client client;

    private Queue<Object> outcome = new LinkedBlockingDeque<>();
    private Queue<Object> income  = new LinkedBlockingDeque<>();

    private Queue<FilePart> parts = new LinkedBlockingDeque<>();
    private HashMap<String, Long> partsInfo = new HashMap<>();

    private HashMap<String, HashMap<String, Object>> infoMaps = new HashMap<>();

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

            answerRequests();
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
                    if (outcome.isEmpty()) {
                        Thread.yield();
                    } else {
                        Object o = outcome.poll();
                        objectOutput.flush();
                        objectOutput.writeObject(o);
                        objectOutput.flush();
                        //Log.i("", "Sending " + ((HashMap) o).get(SendKeys.TITLE));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
//                if (objectOutput != null) {
//                    try {
//                        objectOutput.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
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
                        income.add(o);
                        //Log.i("", "Receiving " + ((HashMap) o).get(SendKeys.TITLE));
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
//                    if (objectInput != null) {
//                        try {
//                            objectInput.close();
//                        } catch (IOException e1) {
//                            e1.printStackTrace();
//                        }
//                    }
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

        HashMap<String, Object> map = findMapByTitles(SendKeys.AUTHENTICATION_ANSWER);

        status = (String) map.get(SendKeys.AUTHENTICATION_ANSWER);

        Log.d(TAG, "Authentication status: " + status);

        if(Authentication.SUCCESS.equals(status)){
            String nick = (String) map.get(SendKeys.NICK);
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

        HashMap<String, Object> mapIn = findMapByTitles(SendKeys.REGISTRATION_ANSWER);
        status = (String) mapIn.get(SendKeys.REGISTRATION_ANSWER);

        return status;
    }

    public void sendNewNick(String nick) {
        HashMap<String, String> map = new HashMap<>();
        map.put(SendKeys.TITLE, SendKeys.NEWNICK);
        map.put(SendKeys.NEWNICK, nick);
        map.put(SendKeys.EMAIL, client.getEmail());
        outcome.add(map);
    }

    public void askForFriends() {
        new Thread(() -> {
            setPriority(Thread.MAX_PRIORITY);
            while (!ClientThread.this.isInterrupted()) {
                try {
                    TimeUnit.SECONDS.sleep(TIMEOUT_FRIEND_REQUEST_SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (controller == null || !(controller instanceof ContactsController)) {
                    try {
                        TimeUnit.SECONDS.sleep(TIMEOUT_FRIEND_REQUEST_SECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                HashMap<String, String> mapOut = new HashMap<>();
                mapOut.put(SendKeys.TITLE, SendKeys.GET_FRIENDS);

                Log.d(client.getEmail(),new SimpleDateFormat("H:mm:ss").format(new Date().getTime()) + " Asking for friends...");
                outcome.add(mapOut);
            }
        }).start();
    }

    public void sendMessage(Message message) {
        outcome.add(message);
    }

    public void sendFile(File file, Client client){
        String to = client.getEmail();
        try {
            Log.d(TAG, "Sending file...");

            String fileName = file.getName();
            long fileParts = file.length() / (long) FilePart.MAX_SIZE;
            fileParts += file.length() % (long) FilePart.MAX_SIZE > 0 ? 1 : 0 ;

            HashMap<String, String> mapFileStart = new HashMap<>();
            mapFileStart.put(SendKeys.TITLE, SendKeys.FILE_START);
            mapFileStart.put(SendKeys.FILE_NAME, fileName);
            mapFileStart.put(SendKeys.TO, to);
            mapFileStart.put(SendKeys.FROM, this.client.getEmail());
            mapFileStart.put(SendKeys.FILE_PARTS, String.valueOf(fileParts));
            outcome.add(mapFileStart);


            FileInputStream fis = new FileInputStream(file);
            byte[] bytes = new byte[FilePart.MAX_SIZE];
            int length = fis.read(bytes, 0, FilePart.MAX_SIZE);

            int partCounter = 0;

            while (length != -1){
                FilePart part = new FilePart.FilePartBuilder().
                        part(partCounter++).
                        fileName(fileName).
                        bytes(Arrays.copyOf(bytes, length)).
                        length(length).
                        to(to).
                        build();

                outcome.add(part);
                length = fis.read(bytes);
            }

            HashMap<String, String> mapFileEnd = new HashMap<>();
            mapFileEnd.put(SendKeys.TITLE, SendKeys.FILE_END);
            mapFileEnd.put(SendKeys.FILE_NAME, file.getName());
            mapFileEnd.put(SendKeys.FILE_PARTS, String.valueOf(partCounter));
            outcome.add(mapFileEnd);

            Log.d(TAG, "File sent.");
            //System.gc();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void answerRequests() {
        while (client == null || !client.isConfirmed()) {
            try {
                TimeUnit.MILLISECONDS.sleep(TIMEOUT_WAIT_FOR_AUTHENTICATION_MILLIS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        while (!isInterrupted() && socket.isConnected()
                && client != null && client.isConfirmed()) {
            Object o = getNextRequest();
            if (o instanceof Message) {
                Message message = (Message) o;
                receiveMessage(message);
            } else if(o instanceof FilePart){
                FilePart filePart = (FilePart) o;
                this.parts.add(filePart);
            } else if (o instanceof HashMap) {
                HashMap<String, Object> map = (HashMap<String, Object>) o;
                String title = (String) map.get(SendKeys.TITLE);
                if(title.equals(SendKeys.FILE_START)){
                    infoMaps.put((String) map.get(SendKeys.FILE_NAME), map);
                    String fileName = (String) map.get(SendKeys.FILE_NAME);
                    Long partsCount = Long.valueOf((String) map.get(SendKeys.FILE_PARTS));
                    partsInfo.put(fileName, partsCount);
                    File file = waitForConfirmation(map);
                    saveFileFromParts(fileName, file);
                }else if(title.equals(SendKeys.FRIENDS_ANSWER)) {
                    ArrayList<Client> clients = (ArrayList<Client>) map.get(SendKeys.FRIENDS_ANSWER);

                    if (controller != null && controller instanceof ContactsController) {
                        ((ContactsController) controller).addFriends(clients);
                    }

                    Log.d(client.getEmail(), new SimpleDateFormat("H:mm:ss").format(new Date().getTime()) + " received " + clients.size() + " friends.");
                }
                else{
                    Log.d(TAG, "Unknown map " + map.get(SendKeys.TITLE));
                }
            }
        }
    }

    private void saveFileFromParts(String fileName, File file) {
        new Thread(() -> {
            ArrayList<FilePart> partsLocal = new ArrayList<>();
            while(! partsInfo.containsKey(fileName) || partsInfo.get(fileName) != partsLocal.size()) {
                if(this.parts.isEmpty()){
                    try {
                        TimeUnit.MILLISECONDS.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else {
                    for (FilePart part : parts) {
                        if (fileName.equals(part.getFileName())) {
                            partsLocal.add(part);
                            parts.remove(part);
                        }
                    }
                }
            }

            HashMap<String, Object> mapInfo = infoMaps.get(fileName);
            String from = (String) mapInfo.get(SendKeys.FROM);
            String to = (String) mapInfo.get(SendKeys.TO);
            String fromTo = from + "[-]" + to;

            String DIR_PATH = "src/received/" + fromTo;

            File dir = new File(DIR_PATH);
            if(!dir.exists()){
                dir.mkdir();
            }

            if(!file.exists()){
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Collections.sort(partsLocal, (o1, o2) -> o1.getPart() < o2.getPart() ? -1 :  1);

            try {
                FileOutputStream fos = new FileOutputStream(file);

                partsLocal.forEach((part) -> {
                    try {
                        fos.write(part.getBytes(), 0, part.getLength());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });


            } catch ( IOException e) {
                e.printStackTrace();
                Log.d(client.getEmail(), "File received.");
            }


        }).start();
    }

    private void receiveMessage(Message message) {
        Log.d(TAG, "Receive message from " + message.getFrom());
        String from = message.getFrom();
        if (ContactsController.chats.containsKey(from)) {
            Controller controller = ContactsController.chats.get(from);
            ((ChatController) controller).receiveMessage(message);
        } else {
            //TODO if no such stage
        }
    }

    private Object getNextRequest(){
        Object o = null;
        while((o = income.poll()) == null){
            try {
                Thread.yield();
                Thread.sleep(25);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return o;
    }

    public static void setController(Controller c) {
        controller = c;
    }

    public Client getClient() {
        return client;
    }

    private HashMap<String, Object> findMapByTitles(String ...keys){
        HashMap<String, Object> map;
        while (!isInterrupted()) {
            for (Object o : income) {
                if (o instanceof HashMap) {
                    HashMap<String, Object> mapIn = (HashMap<String, Object>) o;
                    for (String key : keys) {
                        if (mapIn.get(SendKeys.TITLE).equals(key)) {
                            map = mapIn;
                            income.remove(mapIn);
                            return map;
                        }
                    }
                }
            }
        }
        return null;
    }

    private File waitForConfirmation(HashMap<String, Object> map){
        if(map.get(SendKeys.TITLE).equals(SendKeys.FILE_START)) {
            ChatController controller = ContactsController.chats.get(map.get(SendKeys.FROM));
            if(controller == null){
                //TODO make notification
            }
            return controller.getConfirmation(map);
        }
        return null;
    }
}
