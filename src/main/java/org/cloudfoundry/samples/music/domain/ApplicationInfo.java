package org.cloudfoundry.samples.music.domain;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ApplicationInfo {
    private String[] profiles;
    private String[] services;
    private String serverInfo;

    public ApplicationInfo(String[] profiles, String[] services) {
        this.profiles = profiles;
        this.services = services;
    }

    public ApplicationInfo(String[] profiles, String[] services, HttpServletRequest request) {
        this.profiles = profiles;
        this.services = services;
        this.serverInfo = "";
        try {
            this.serverInfo  = InetAddress.getLocalHost().getHostAddress() + ":" + request.getLocalPort();
        } catch (UnknownHostException e) {
        }
    }

    public String[] getProfiles() {
        return profiles;
    }

    public void setProfiles(String[] profiles) {
        this.profiles = profiles;
    }

    public String[] getServices() {
        return services;
    }

    public void setServices(String[] services) {
        this.services = services;
    }

    public String getServerInfo() {return serverInfo; }

    public void setServerInfo(String serverInfo) { this.serverInfo = serverInfo; }
}
