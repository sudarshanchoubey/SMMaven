/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.smmaven;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.json.Json;
import javax.json.JsonObject;

/**
 *
 * @author schoubey
 * Picks up messages from the queue and sends them to destination clients.
 */
public class MessageSenderLoop implements Runnable {

    private ConcurrentHashMap<String, ClientAtServer> clientSocketMap;
    private ArrayBlockingQueue<SMessage> messageQ;

    public MessageSenderLoop(ConcurrentHashMap<String, ClientAtServer> socketMap,
            ArrayBlockingQueue<SMessage> messageQueue) {
        clientSocketMap = socketMap;
        messageQ = messageQueue;
    }

    @Override
    public void run() {
        ExecutorService messageSenderService = Executors.newFixedThreadPool(2);
        System.out.println("1021:in message sender loop");
        while (true) {
            if (!messageQ.isEmpty()) {
                messageSenderService.execute(new MessageSender());
            }
        }
    }

    class MessageSender implements Runnable {

        @Override
        public void run() {
            SMessage sm = messageQ.poll();
            if (sm == null) {
                return;
            }
            ClientAtServer destCas = clientSocketMap.get(sm.getDestId());
            ClientAtServer srcCas = clientSocketMap.get(sm.getSrcId());
            if (destCas == null) {
                messageQ.add(sm);
                return;
            }
            Socket dc = destCas.getSocket();
            Socket sc = srcCas.getSocket();
            if (dc == null || dc.isClosed()) {
                messageQ.add(sm);
                return;
            } else {
                try {
                    PrintWriter destOut
                            = new PrintWriter(dc.getOutputStream(), true);
                    PrintWriter srcOut
                            = new PrintWriter(sc.getOutputStream(), true);
                    ConcurrentLinkedQueue<SMessage> sentMessages = destCas.getSentMessageList();
                    for (SMessage im : sentMessages) {
                        if ((sm.getArrivalTime() - im.getArrivalTime() <= 5000) //The 5 second contraint
                                && sm.getMessageString().contentEquals(im.getMessageString())) {

                            JsonObject jo = Json.createObjectBuilder().
                                    add("type", "response").
                                    add("responseType", "messageResponse").
                                    add("state", "fail").
                                    add("message", sm.messageToJsonStringServer()).
                                    add("errorMessage", "duplicate messages").
                                    build();
                            System.out.println("Sending " + jo.toString());
                            srcOut.println(jo.toString());
                            return;
                        }
                    }
                    JsonObject jo = Json.createObjectBuilder().
                                add("type", "message").
                                add("message", sm.messageToJsonServer()).
                                build();
                        destOut.println(jo.toString());
                        sentMessages.add(sm);
                        jo = Json.createObjectBuilder().
                                add("type", "response").
                                add("responseType", "messageResponse").
                                add("state", "pass").
                                add("message", sm.messageToJsonStringServer()).
                                add("errorMessage", "").
                                build();
                        srcOut.println(jo.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
