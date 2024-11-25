package _BeginAgainServer;

import java.net.InetAddress;

public class SharedObject {
    private volatile String data;        // �Ƶ��̳�κ��� ���� ������
    private volatile InetAddress unityAddress; // ����Ƽ�� IP �ּ�
    private volatile int unityPort;     // ����Ƽ�� ��Ʈ ��ȣ

    public synchronized String getData() {
        return data;
    }

    public synchronized void setData(String data) {
        this.data = data;
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
}
