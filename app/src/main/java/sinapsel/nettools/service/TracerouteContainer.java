package sinapsel.nettools.service;

import java.io.Serializable;

public class TracerouteContainer implements Serializable {

    private String hostname;
    private String ip;
    private float ms;
    private boolean isSuccessful;

    public TracerouteContainer(String hostname, String ip, float ms, boolean isSuccessful) {
        this.hostname = hostname;
        this.ip = ip;
        this.ms = ms;
        this.isSuccessful = isSuccessful;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public float getMs() {
        return ms;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    @Override
    public String toString() {
        return "Traceroute : \nHostname : " + hostname + "\nip : " + ip + "\nMilliseconds : " + ms;
    }

}
