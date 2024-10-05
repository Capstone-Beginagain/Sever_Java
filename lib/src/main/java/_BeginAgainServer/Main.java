package _BeginAgainServer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Main {
    private static final int UNITY_SERVER_PORT = 9876;  // 서버가 수신할 포트 번호
    private static final int[] ARDUTINO_SERVER_PORT= {9877,9878,9879,9880}; // 아두이노 포트 번호
    private static final int BUFFER_SIZE = 1024;  // 수신할 데이터 크기

    public static void main(String[] args) throws Exception {
        System.out.println("UDP server starting...");

        // 아두이노 클라이언트 수 만큼 스레드를 생성해서 데이터 수신을 처리
        for (int i = 0; i < 4; i++) {
            new ArduinoClientHandler(i).start();
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
                    String messageFromUnity = new String(receivePacket.getData(), 0, receivePacket.getLength());// Unity로 부터 받은 데이터
                    System.out.println("unity " + clientId + " from unity" + messageFromUnity);
                    String data = "-1;"+messageFromUnity;
                    System.out.println(data); 
                    byte[] dataByte = data.getBytes(); 
                    DatagramPacket sendPacket = new DatagramPacket(dataByte, dataByte.length, unityAddress, unityPort); //Unity로 전송할 데이터
                    while(true) {
    				serverSocket.send(sendPacket); //유니티로 packet한 데이터 전송
    				Thread.sleep(500); //0.5초마다 전송
                    }
    			}
    			
    		}catch(Exception e) {
    			
    		}
    	}
    }
    // 아두이노 클라이언트로부터 데이터를 처리하는 스레드 클래스
 
    public static class ArduinoClientHandler extends Thread {
        private DatagramSocket serverSocket;
        private int clientId;
        private int arduinoPort;
        private InetAddress arduinoAddress;

        public ArduinoClientHandler(int clientId) {
            try {
				serverSocket=new DatagramSocket(ARDUTINO_SERVER_PORT[clientId]); //아두이노 지정한 포트별로 소켓 생성
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            this.clientId = clientId;  // 각 클라이언트의 고유 ID
        }

        @Override
        public void run() {
            try {
                byte[] receiveData = new byte[BUFFER_SIZE];
                System.out.println(Thread.currentThread());
                while (true) {
                    // 아두이노로부터 데이터 수신
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    serverSocket.receive(receivePacket);
                    arduinoPort=receivePacket.getPort();
                    arduinoAddress=receivePacket.getAddress();
                    
                    String messageFromArduino = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    System.out.println("arduino " + clientId + " from aduino" + messageFromArduino);
                    String message=" "; //아두이노로 전송할 데이터
                    byte[] dataByte = message.getBytes(); 
                    DatagramPacket sendPacket = new DatagramPacket(dataByte, dataByte.length, arduinoAddress, arduinoPort);
                    serverSocket.send(sendPacket); //아두이노로 데이터 전송
                    
                    
                    // 수신한 데이터를 처리하고 유니티에 보낼 형식으로 변환
                    String processedData = processArduinoData(messageFromArduino);

                    // 변환된 데이터를 유니티로 전송
//                    InetAddress unityAddress = InetAddress.getByName("127.0.0.1");  // 유니티 클라이언트 IP 주소
//                    byte[] sendData = processedData.getBytes();
//                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, unityAddress, UNITY_PORT);
//                    serverSocket.send(sendPacket);  // 유니티로 데이터 전송
//                    System.out.println("유니티로 보낸 데이터 (아두이노 " + clientId + "): " + processedData);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 아두이노 데이터를 처리하고 유니티 형식으로 변환하는 메서드
        private String processArduinoData(String data) {
            // 예시: data = "position; x1,y1,z1,x2,y2,z2"
            if (!data.startsWith("position;")) {
                return "";  // 형식이 맞지 않으면 빈 문자열 반환
            }

            // "position;" 부분을 제거하고 나머지 값을 쉼표로 분리
            String[] values = data.substring("position;".length()).split(",");
            if (values.length != 6) {
                return "";  // 값이 부족하면 빈 문자열 반환
            }

            // x, y, z 좌표 값들을 추출
            float x1 = Float.parseFloat(values[0]);
            float y1 = Float.parseFloat(values[1]);
            float z1 = Float.parseFloat(values[2]);
            float x2 = Float.parseFloat(values[3]);
            float y2 = Float.parseFloat(values[4]);
            float z2 = Float.parseFloat(values[5]);

            // 유니티에서 사용할 수 있도록 JSON 형식으로 변환
            return String.format(
                "{ \"positions\": [{ \"x\": %.2f, \"y\": %.2f, \"z\": %.2f }, { \"x\": %.2f, \"y\": %.2f, \"z\": %.2f }] }",
                x1, y1, z1, x2, y2, z2
            );
        }
    }
}
