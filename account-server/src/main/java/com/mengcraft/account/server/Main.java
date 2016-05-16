package com.mengcraft.account.server;

import com.mengcraft.account.server.action.Login;
import com.mengcraft.account.server.action.State;
import com.mengcraft.account.server.action.Valid;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newCachedThreadPool;

/**
 * Created by on 16-4-14.
 */
public class Main {

    public static final ExecutorService POOL = newCachedThreadPool();

    public static void main(String[] args) throws IOException, InterruptedException {
        Properties properties = new Properties();
        properties.load(new FileInputStream("server.properties"));

        Database.init(properties);

        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(
                URI.create("http://" + properties.getProperty("account.server.ip") + ':' + properties.getProperty("account.server.port")),
                new ResourceConfig(
                        Login.class,
                        Valid.class,
                        State.class
                ),
                false
        );

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.shutdown();
        }));

        server.start();
        Thread.currentThread().join();
    }

}
