/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.smmaven;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.Socket;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

/**
 *
 * @author schoubey
 */
public class ClientRecieveThread implements Runnable {
        Socket socket = null;
          
        public ClientRecieveThread(Socket sock) {
            this.socket = sock;
        }
        private void processAsMessageResponse(JsonObject jo) {
/*            JsonValue jState = jo.get("state");
            if(jState.toString().contains("pass")) {
                JsonValue jMessage = jo.get("message");
                JsonObject jMObject = 
                        Json.createReader(new StringReader(jMessage.toString())).readObject();
                System.out.println("Message " + 
                        jMObject.get("message").toString() +
                        "\nto " + jMObject.get("destId").toString() +
                        " delivered");
            } else {
                JsonObject jMessage = jo.getJsonObject("message");
                System.out.println("Message " + 
                        jMessage.get("message").toString() +
                        "\nto " + jMessage.get("destId").toString() +
                        " failed to deliver. Error message:" +
                        jo.get("errormessage"));
            }
*/
        }
        private void processAsRecievedMessage(JsonObject jo) {
            JsonValue jfrom = jo.get("srcId");
            String from = jfrom.toString();
            from = from.replace("\"", "");
            from = from.replace("\\", "");
            JsonValue messageString = jo.get("messageString");
            String message = messageString.toString();
            message = message.replace("\"", "");
            message = message.replace("\\", "");
            System.out.println(from + ": " + message);
        }
        private void processRecievedString(String recieved) {
            System.out.println("recieved in client is " + recieved);
            JsonReader jReader = Json.createReader(new StringReader(recieved));
            JsonObject jo = jReader.readObject();
            JsonValue jType = jo.get("type");
            if((jType != null) && (jType.toString().contains("message"))) {
                JsonValue jMessage;
                jMessage = jo.get("message");
                jReader = Json.createReader(new StringReader(jMessage.toString()));
                jo = jReader.readObject();
                processAsRecievedMessage(jo);
            } else if((jType != null) && (jType.toString().contains("response"))) {
                processAsMessageResponse(jo);
            }
        }
         
        @Override
        public void run() {
            try {
                BufferedReader recieve =
                            new BufferedReader(
                                new InputStreamReader(socket.getInputStream()));
                String stringRecieved;
                while((stringRecieved = recieve.readLine()) != null) {
                    processRecievedString(stringRecieved);
                }
                
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
