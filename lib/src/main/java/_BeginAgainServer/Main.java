package _BeginAgainServer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


public class Main {
    private static final int[] UNITY_SERVER_PORT = {9876, 9875, 9874, 9873}; 
    private static final int[] ARDUINO_SERVER_PORT = {9877, 9878, 9879, 9870};
    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) throws Exception {
        System.out.println("UDP server starting...");

        // ���� ��ü ���� (Ŭ���̾�Ʈ ������ŭ ����)
        SharedObject[] sharedObjects = new SharedObject[4];
        for (int i = 0; i < 4; i++) {
            sharedObjects[i] = new SharedObject(); // �� clientId�� ���� ��ü ����
        }

        // ������ ����
        for (int i = 0; i < 4; i++) {
            new ArduinoClientHandler(i, sharedObjects[i]).start(); // �� �Ƶ��̳� ��Ʈ�� ������
            new UnityClientHandler(i, sharedObjects[i]).start();   // �� ����Ƽ ��Ʈ�� ������
        }
    }

    public static class UnityClientHandler extends Thread {
        private DatagramSocket serverSocket;
        private int clientId;
        private SharedObject sharedObject;

        public UnityClientHandler(int clientId, SharedObject sharedObject) {
            this.clientId = clientId;
            this.sharedObject = sharedObject;
            try {
                serverSocket = new DatagramSocket(UNITY_SERVER_PORT[clientId]);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                byte[] receiveData = new byte[BUFFER_SIZE];

                while (true) {
                    // ����Ƽ���� ������ ����
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    serverSocket.receive(receivePacket);

                    // ���� ������ ó��
                    InetAddress unityAddress = receivePacket.getAddress();
                    int unityPort = receivePacket.getPort();
                    String messageFromUnity = new String(receivePacket.getData(), 0, receivePacket.getLength());

                    System.out.println("Unity " + clientId + " received: " + messageFromUnity);

                    // ����Ƽ ������ ������Ʈ
                    sharedObject.setUnityAddress(unityAddress);
                    sharedObject.setUnityPort(unityPort);
                    sharedObject.setConnectUnity(true);
                    sharedObject.setServerSocket(serverSocket);
                    // �Ƶ��̳뿡�� ���� ������ ����Ƽ�� ����
//                    String dataToSend = sharedObject.getData(); // ���� ��ü���� ������ �б�
//                    if (dataToSend != null) {
//                        byte[] sendData = dataToSend.getBytes();
//                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, unityAddress, unityPort);
//                        serverSocket.send(sendPacket);
//                        System.out.println("Sent to Unity " + clientId + ": " + dataToSend);
//                    }

                    //Thread.sleep(500); // 0.5�ʸ��� ����
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class ArduinoClientHandler extends Thread {
        private DatagramSocket serverSocket;
        private int clientId;
        private SharedObject sharedObject;
        public ArduinoClientHandler(int clientId, SharedObject sharedObject) {
            this.clientId = clientId;
            this.sharedObject = sharedObject;
            try {
                serverSocket = new DatagramSocket(ARDUINO_SERVER_PORT[clientId]);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                byte[] receiveData = new byte[BUFFER_SIZE];

                while (true) {
                    // �Ƶ��̳뿡�� ������ ����
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    serverSocket.receive(receivePacket);

                    // ���� ������ ó��
                    String messageFromArduino = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    System.out.println("Arduino " + clientId + " received: " + messageFromArduino);

                    // ������ ���� ��ü�� ����
                    sharedObject.setData(messageFromArduino);

                    //Thread.sleep(100); // 0.1�ʸ��� ����
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
