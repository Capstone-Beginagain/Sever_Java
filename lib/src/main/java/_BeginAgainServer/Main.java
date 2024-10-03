package _BeginAgainServer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Main {
    private static final int UNITY_SERVER_PORT = 9876;  // ������ ������ ��Ʈ ��ȣ
    private static final int[] ARDUTINO_SERVER_PORT= {9877,9878,9879,9880}; // ���� ��

    private static final int BUFFER_SIZE = 1024;  // ������ ������ ũ��

    public static void main(String[] args) throws Exception {
        System.out.println("UDP server starting...");

        // �Ƶ��̳� Ŭ���̾�Ʈ �� ��ŭ �����带 �����ؼ� ������ ������ ó��
        for (int i = 0; i < 4; i++) {
            new ArduinoClientHandler( i).start();
        }
        new UnityClientHandler(4).start();
    }
    public static class UnityClientHandler extends Thread{
    
    	private DatagramSocket serverSocket;
    	private int clientId;
    	private InetAddress unityAddress;
    	private int unityPort;
    	
    	public UnityClientHandler(int clientId) {
    		try {
				serverSocket = new DatagramSocket(UNITY_SERVER_PORT);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		this.clientId=clientId;
    	}
    	@Override
    	public void run() {
    		try {
    			byte[] receiveData = new byte[BUFFER_SIZE];
                System.out.println(Thread.currentThread());
                
    			while(true) {
    				
    				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    serverSocket.receive(receivePacket);
                    unityAddress = receivePacket.getAddress();
                    unityPort = receivePacket.getPort();
                    String messageFromArduino = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    System.out.println("unity " + clientId + " from unity" + messageFromArduino);

                    String data = "-1;"+messageFromArduino;
                    System.out.println(data);
                    byte[] dataByte = data.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(dataByte, dataByte.length, unityAddress, unityPort);
                    while(true) {
    				serverSocket.send(sendPacket);
    				Thread.sleep(500);
                    }
    			}
    			
    		}catch(Exception e) {
    			
    		}
    	}
    }
    // �Ƶ��̳� Ŭ���̾�Ʈ�κ��� �����͸� ó���ϴ� ������ Ŭ����
 
    public static class ArduinoClientHandler extends Thread {
        private DatagramSocket serverSocket;
        private int clientId;

        public ArduinoClientHandler( int clientId) {
            try {
				serverSocket=new DatagramSocket(ARDUTINO_SERVER_PORT[clientId]);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            this.clientId = clientId;  // �� Ŭ���̾�Ʈ�� ���� ID
        }

        @Override
        public void run() {
            try {
                byte[] receiveData = new byte[BUFFER_SIZE];
                System.out.println(Thread.currentThread());
                while (true) {
                    // �Ƶ��̳�κ��� ������ ����
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    serverSocket.receive(receivePacket);
                    String messageFromArduino = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    System.out.println("arduino " + clientId + " from aduino" + messageFromArduino);

                    // ������ �����͸� ó���ϰ� ����Ƽ�� ���� �������� ��ȯ
                    String processedData = processArduinoData(messageFromArduino);

                    // ��ȯ�� �����͸� ����Ƽ�� ����
//                    InetAddress unityAddress = InetAddress.getByName("127.0.0.1");  // ����Ƽ Ŭ���̾�Ʈ IP �ּ�
//                    byte[] sendData = processedData.getBytes();
//                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, unityAddress, UNITY_PORT);
//                    serverSocket.send(sendPacket);  // ����Ƽ�� ������ ����
//                    System.out.println("����Ƽ�� ���� ������ (�Ƶ��̳� " + clientId + "): " + processedData);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // �Ƶ��̳� �����͸� ó���ϰ� ����Ƽ �������� ��ȯ�ϴ� �޼���
        private String processArduinoData(String data) {
            // ����: data = "position; x1,y1,z1,x2,y2,z2"
            if (!data.startsWith("position;")) {
                return "";  // ������ ���� ������ �� ���ڿ� ��ȯ
            }

            // "position;" �κ��� �����ϰ� ������ ���� ��ǥ�� �и�
            String[] values = data.substring("position;".length()).split(",");
            if (values.length != 6) {
                return "";  // ���� �����ϸ� �� ���ڿ� ��ȯ
            }

            // x, y, z ��ǥ ������ ����
            float x1 = Float.parseFloat(values[0]);
            float y1 = Float.parseFloat(values[1]);
            float z1 = Float.parseFloat(values[2]);
            float x2 = Float.parseFloat(values[3]);
            float y2 = Float.parseFloat(values[4]);
            float z2 = Float.parseFloat(values[5]);

            // ����Ƽ���� ����� �� �ֵ��� JSON �������� ��ȯ
            return String.format(
                "{ \"positions\": [{ \"x\": %.2f, \"y\": %.2f, \"z\": %.2f }, { \"x\": %.2f, \"y\": %.2f, \"z\": %.2f }] }",
                x1, y1, z1, x2, y2, z2
            );
        }
    }
}
