package _BeginAgainServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SharedObject {
    private volatile String data;        // �Ƶ��̳�κ��� ���� ������
    private volatile InetAddress unityAddress; // ����Ƽ�� IP �ּ�
    private volatile int unityPort;     // ����Ƽ�� ��Ʈ ��ȣ
    private boolean isConnectUnity = false;
    private DatagramSocket serverSocket;

    public synchronized String getData() {
        return data;
    }

    public synchronized void setData(String data) {
        this.data = data;
        if(isConnectUnity) {
        	try {
        		byte[] sendData = data.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, unityAddress, unityPort);
                serverSocket.send(sendPacket);
                //System.out.println("Sent to Unity " + unityPort + ": " + data);
        	}catch(IOException e) {
        		e.printStackTrace();
        	}
        }
        
    }

    public synchronized InetAddress getUnityAddress() {
        return unityAddress;
    }

    public synchronized void setUnityAddress(InetAddress unityAddress) {
        this.unityAddress = unityAddress;
    }

    public synchronized int getUnityPort() {
        return unityPort;
    }

    public synchronized void setUnityPort(int unityPort) {
        this.unityPort = unityPort;
    }

	public boolean isConnectUnity() {
		return isConnectUnity;
	}

	public void setConnectUnity(boolean isConnectUnity) {
		this.isConnectUnity = isConnectUnity;
	}

	public DatagramSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(DatagramSocket serverSocket) {
		this.serverSocket = serverSocket;
	}
}
