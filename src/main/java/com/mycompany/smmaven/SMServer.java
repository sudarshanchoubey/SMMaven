/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.smmaven;

import java.io.*;
import java.net.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author schoubey
 */
public class SMServer {

//    private static JsonObject buildResponse() {
//        JsonObject model = Json.createObjectBuilder()
//                .add("type", "message")
//                .add("message", (Json.createObjectBuilder()
//                        .add("destId", "555")
//                        .add("srcId", "444")
//                        .add("messageString", "Hey!")
//                        .add("serverTime", "12345")))
//                .build();
//        return model;
//    }

    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.err.println("Usage: java SMServer <port number>");
            System.exit(1);
        }
        ConcurrentHashMap<String, ClientAtServer> socketMap = new ConcurrentHashMap<>();
        ArrayBlockingQueue<SMessage> messageQueue;
        messageQueue = new ArrayBlockingQueue<>(10);
        int portNumber = Integer.parseInt(args[0]);
        boolean listening = true;
        int i = 0;
        ExecutorService connectionResponseService = Executors.newFixedThreadPool(2);
        System.out.println("Starting queuer");
        Thread messageSenderLoop = new Thread(
                new MessageSenderLoop(socketMap, messageQueue));
        Thread messageQueuerLoop = new Thread(
                new MessageQueuerLoop(socketMap, messageQueue));
        messageQueuerLoop.start();
        System.out.println("Starting sender");
        messageSenderLoop.start();
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (listening) {
                Runnable connectionResponder =
                        new ConnectionResponseThread(serverSocket.accept(),
                                                     socketMap);
                connectionResponseService.execute(connectionResponder);
                if(i++ >= 1) {
                    for(String key:socketMap.keySet()) {
                        System.out.println("for "+ key + "socket is " + socketMap.get(key).toString());
                    }
                    messageQueue.forEach((SMessage sm) -> 
                            System.out.println("message in queue: " + sm.toString()));
                }
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }
}
