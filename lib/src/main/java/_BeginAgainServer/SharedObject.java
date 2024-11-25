package _BeginAgainServer;

import java.net.InetAddress;

public class SharedObject {
    private volatile String data;        // 아두이노로부터 받은 데이터
    private volatile InetAddress unityAddress; // 유니티의 IP 주소
    private volatile int unityPort;     // 유니티의 포트 번호

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
