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

        // 공유 객체 생성 (클라이언트 개수만큼 생성)
        SharedObject[] sharedObjects = new SharedObject[4];
        for (int i = 0; i < 4; i++) {
            sharedObjects[i] = new SharedObject(); // 각 clientId별 공유 객체 생성
        }

        // 스레드 생성
        for (int i = 0; i < 4; i++) {
            new ArduinoClientHandler(i, sharedObjects[i]).start(); // 각 아두이노 포트별 스레드
            new UnityClientHandler(i, sharedObjects[i]).start();   // 각 유니티 포트별 스레드
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
                    // 유니티에서 데이터 수신
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    serverSocket.receive(receivePacket);

                    // 수신 데이터 처리
                    InetAddress unityAddress = receivePacket.getAddress();
                    int unityPort = receivePacket.getPort();
                    String messageFromUnity = new String(receivePacket.getData(), 0, receivePacket.getLength());

                    System.out.println("Unity " + clientId + " received: " + messageFromUnity);

                    // 유니티 데이터 업데이트
                    sharedObject.setUnityAddress(unityAddress);
                    sharedObject.setUnityPort(unityPort);
                    sharedObject.setConnectUnity(true);
                    sharedObject.setServerSocket(serverSocket);
                    // 아두이노에서 받은 데이터 유니티로 전송
//                    String dataToSend = sharedObject.getData(); // 공유 객체에서 데이터 읽기
//                    if (dataToSend != null) {
//                        byte[] sendData = dataToSend.getBytes();
//                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, unityAddress, unityPort);
//                        serverSocket.send(sendPacket);
//                        System.out.println("Sent to Unity " + clientId + ": " + dataToSend);
//                    }

                    //Thread.sleep(500); // 0.5초마다 전송
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
                    // 아두이노에서 데이터 수신
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    serverSocket.receive(receivePacket);

                    // 수신 데이터 처리
                    String messageFromArduino = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    System.out.println("Arduino " + clientId + " received: " + messageFromArduino);

                    // 데이터 공유 객체에 저장
                    sharedObject.setData(messageFromArduino);

                    //Thread.sleep(100); // 0.1초마다 수신
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
