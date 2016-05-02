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
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author schoubey
 * Responds to a client when it creates a new connection
 */
public class ConnectionResponseThread implements Runnable {
    /*
        Connection Request Json
    { "type":"connectionrequest",
      "Id":"idstring"
    }
    */
    private Socket socket;
    private ConcurrentHashMap<String, ClientAtServer> clientMap;

    public ConnectionResponseThread(Socket socket,
                        ConcurrentHashMap<String, ClientAtServer> chm) {
        this.socket = socket;
        this.clientMap = chm;
    }
    
    
    
    
//    private JsonObject getJsonResponseFromString(String input) {
//        JsonReader jReader = Json.createReader(new StringReader(input));
//        JsonObject jo = jReader.readObject();
//        System.out.println("Read json object in thread");
//    }

    @Override
    public void run() {
        try {
            PrintWriter out = 
                        new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));
            String request;
            Response res;
            request = in.readLine();
            System.out.println(request);
            ConnectionRequest cn = ConnectionRequest.createConnectionRequest(request);
            if(cn == null) {
                res = Response.createConnectionFailResponse();
                out.println(res.convertToJson());
                System.out.println("Closing socket" + socket.toString() +
                                   " because of protocol error");
                socket.close();
                return;
            } else {
                if(clientMap.containsKey(cn.Id)) {
                    ClientAtServer cas = clientMap.get(cn.Id);
                    Socket delSocket = cas.getSocket();
                    delSocket.close();
                    cas.setSocket(socket);
                    System.out.println("1014:closing socket " + delSocket.toString() + "for" + cn.Id);
                    res = Response.createConnectionPassResponse();
                    out.println(res.convertToJson());
                    System.out.println("Reestablished connection");
                } else {
                    ClientAtServer cas = new ClientAtServer(cn.Id, socket);
                    clientMap.put(cn.Id, cas);
                    res = Response.createConnectionPassResponse();
                    out.println(res.convertToJson());
                }
                
            }
        } catch (IOException e) {
            System.err.println("Could not send json");
            System.exit(-1);
        }
    }  

}
