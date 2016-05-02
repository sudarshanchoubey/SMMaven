/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.smmaven;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

/**
 *
 * @author schoubey
 * Puts newly received messages in the messageQ
 */
class MessageQueuerLoop implements Runnable {
    private ConcurrentHashMap<String, ClientAtServer> clientSocketMap;
    private ArrayBlockingQueue<SMessage> messageQ;
    
    public MessageQueuerLoop(ConcurrentHashMap<String, ClientAtServer> socketMap, ArrayBlockingQueue<SMessage> messageQueue) {
        clientSocketMap = socketMap;
        messageQ = messageQueue;
    }
    @Override
    public void run() {
        System.out.println("1009:in messagequeuerloop");
        //ExecutorService messageQueuerService = Executors.newFixedThreadPool(2);
        while (true) {
            for (ClientAtServer cas : clientSocketMap.values()) {
                Socket so = cas.getSocket();
                if (so != null && !so.isClosed()) {
                    //messageQueuerService.execute(new MessageQueuer(cas));
                    try {
                        BufferedReader in
                                = new BufferedReader(new InputStreamReader(cas.getSocket().getInputStream()));
                        String input;
                        if (in.ready()) {
                            input = in.readLine();
                            System.out.println("recieved this in queueuer " + input);
                            JsonReader jReader = Json.createReader(new StringReader(input));
                            JsonObject jo = jReader.readObject();
                            Date dt = new Date();
                            if (jo.get("type").toString().contains("message")) {
                                JsonValue jMessage = jo.get("message");
                                System.out.println("jMessage is " + jMessage.toString());
                                jReader = Json.createReader(new StringReader(jMessage.toString()));
                                JsonObject jMessageObject = jReader.readObject();
                                SMessage sm = SMessage.messageFromJSON(jMessageObject, dt.getTime());
                                System.out.println("added " + input + " to queue");
                                messageQ.add(sm);
                                messageQ.forEach((SMessage s)
                                        -> System.out.println("message in queue: " + s.toString()));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } if(so.isClosed()) {
                    clientSocketMap.remove(so);
                }
            }
        }
    }

    class MessageQueuer implements Runnable {
        ClientAtServer cl;
        public MessageQueuer(ClientAtServer c) {
            cl = c;
        }
        @Override
        public void run() {
            try{
                 BufferedReader in
                    = new BufferedReader(new InputStreamReader(cl.getSocket().getInputStream()));
                PrintWriter out
                    = new PrintWriter(cl.getSocket().getOutputStream(), true);
                String input;
                if(in.ready()) {
                    input = in.readLine();
                    System.out.println("recieved this in queueuer " + input);    
                    JsonReader jReader = Json.createReader(new StringReader(input));
                    JsonObject jo = jReader.readObject();
                    Date dt = new Date();
                    if(jo.get("type").toString().contains("message")) {
                        JsonValue jMessage = jo.get("message");
                        System.out.println("jMessage is " + jMessage.toString());
                        jReader = Json.createReader(new StringReader(jMessage.toString()));
                        JsonObject jMessageObject = jReader.readObject();
                        SMessage sm = SMessage.messageFromJSON(jMessageObject, dt.getTime());
                        System.out.println("added " + input + " to queue");
                        messageQ.add(sm);
                        messageQ.forEach((SMessage s) -> 
                            System.out.println("message in queue: " + s.toString()));
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
