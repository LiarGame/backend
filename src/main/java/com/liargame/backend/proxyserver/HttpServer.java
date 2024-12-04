package com.liargame.backend.proxyserver;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class HttpServer {
    public static void start() throws Exception {
        Server server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // 정적 파일 경로 설정
        String resourceBase = "src/main/resources/static";
        context.setResourceBase(resourceBase);

        // DefaultServlet을 사용하여 정적 파일 제공
        ServletHolder staticHolder = new ServletHolder("static", DefaultServlet.class);
        staticHolder.setInitParameter("dirAllowed", "true");
        context.addServlet(staticHolder, "/");

        server.start();
        System.out.println("HTTP 서버가 시작되었습니다.");
    }
}
