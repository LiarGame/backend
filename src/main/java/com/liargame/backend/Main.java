package com.liargame.backend;

import com.liargame.backend.tcpserver.TcpServer;
import com.liargame.backend.proxyserver.ProxyServer;

public class Main {
    public static void main(String[] args) {
        ServerManager serverManager = new ServerManager();
        serverManager.startServers();
    }
}
