/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.smmaven;
import java.io.*;
import java.net.*;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;


/**
 *
 * @author schoubey
 */
public class SMClient {
    
    /*
     {
	 "type": "message",
	 "message": {
	 	"destId": "555",
		"srcId": "444",
		"messageString": "Hey!",
		"serverTime": 987908989
	}
    }
    {
	 "type": "response",
         "status":"delivered"
	 "message": {
	 	"destId": "555",
		"srcId": "444",
		"messageString": "Hey!",
		"serverTime": 987908989
          }
    }
    */
    
    
    private static void processJson(String line) {
        JsonReader jReader = Json.createReader(new StringReader(line));
        JsonObject jo = jReader.readObject();
        JsonValue jv = jo.get("type");
        String type = jv.toString();
        String message;
        System.out.println("Did the processing type is:" + type);
        if(type.contains("message")) {
            JsonObject jMessage = (JsonObject) jo.get("message");
            JsonValue jfrom = jMessage.get("srcId");
            String from = jfrom.toString();
            JsonValue messageString = jMessage.get("messageString");
            message = messageString.toString();
            System.out.println(from + ": " + message);
        } else if(type.contains("response")) {
            JsonObject jMessage = (JsonObject) jo.get("message");
            JsonValue jTo = jMessage.get("destId");
            String to = jTo.toString();
            JsonValue messageString = jMessage.get("messageString");
            message = messageString.toString();
            System.out.println("Delivery of message :" + message + " to "+
                    to + " failed." );
        }
    }
    @SuppressWarnings("empty-statement")
    public static void main(String[] args) 
            throws IOException, InterruptedException {
        final String clientId;
        if (args.length != 3) {
            System.err.println(
                "Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }
        clientId = args[2];

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try {
            Socket kkSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(kkSocket.getInputStream()));
            ConnectionRequest cn = new ConnectionRequest();
            cn.type = "connection";
            cn.Id = clientId;
            String requestString = cn.convertToJsonString();
            System.out.println(requestString);
            out.println(requestString);
            String inputLine;
            inputLine = in.readLine();
            Response res = Response.getResponseFromJsonString(inputLine);
            if(res == null) {
                System.out.println("Couldn't read server response " +
                        "try restarting client");
                
            } else if(res.state.contains("fail")) {
                System.out.println("Server responded with error: " +
                                    res.errorMessage);
                kkSocket.close();
                System.exit(1);
            } else {
                System.out.println("Connection established");
                Thread sendThread = 
                        new Thread(new ClientSendThread(kkSocket, clientId));
                Thread recieveThread =
                        new Thread(new ClientRecieveThread(kkSocket));
                sendThread.start();
                recieveThread.start();
                while(sendThread.isAlive() && recieveThread.isAlive()) {
                    Thread.sleep(5000);
                }
                if(!kkSocket.isClosed()) {
                    kkSocket.close();
                }
            }
            
            
//            inputLine = in.readLine();
//            processJson(inputLine);
            
//            while ((inputLine = in.readLine()) != null) {
//                processJson(inputLine);
//            }
            in.readLine();
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        }
    }
}
