package com.liargame.backend.config;

public class Config {
    private WebSocketConfig websocket;
    private TcpConfig tcp;

    public static class WebSocketConfig {
        private String host;
        private int port;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }

    public static class TcpConfig {
        private String host;
        private int port;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }

    public WebSocketConfig getWebsocket() {
        return websocket;
    }

    public void setWebsocket(WebSocketConfig websocket) {
        this.websocket = websocket;
    }

    public TcpConfig getTcp() {
        return tcp;
    }

    public void setTcp(TcpConfig tcp) {
        this.tcp = tcp;
    }
}
