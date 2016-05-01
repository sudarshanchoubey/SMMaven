/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.smmaven;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;

/**
 *
 * @author schoubey
 */
public class ClientSendThread implements Runnable {
    Socket socket;
    String clientId;
    public ClientSendThread(Socket sock, String clientId) {
        socket = sock;
        this.clientId = clientId;
    }
    private String createMessageString(String s) {
        s = s.trim();
        String phoneNumber = s.substring(0, 10);
        String message  = s.substring(11);
        JsonObject messageObject = Json.createObjectBuilder().
                                   add("srcId",clientId).
                                   add("destId",phoneNumber).
                                   add("messageString",message).build();
        JsonObject jo = Json.createObjectBuilder().
                                   add("type", "message").
                                   add("message", messageObject).
                                   build();
        return jo.toString();
    }
    @Override
    public void run() {
       try {
                PrintWriter send =
                        new PrintWriter(socket.getOutputStream(),true);
                BufferedReader stdIn = new BufferedReader(
                        new InputStreamReader(System.in));
                String inputFromUser;
                while((inputFromUser = stdIn.readLine()) != null) {
                    if(inputFromUser.matches("^\\d{10}:.*$")) {
                        String output = createMessageString(inputFromUser);
                        System.out.println("sending " + output);
                        send.println(output);
                    } else {
                        System.out.println("Please enter message in " +
                                "the following format:\n" +
                                "99xxxxxxxx:message text\n");
                    }
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
    }
    
}
