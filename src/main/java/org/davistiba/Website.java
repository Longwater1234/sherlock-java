package org.davistiba;

public class Website {
    private String service;
    private String url;

    public String getService() {
        return service;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "Website{" +
                "service='" + service + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
