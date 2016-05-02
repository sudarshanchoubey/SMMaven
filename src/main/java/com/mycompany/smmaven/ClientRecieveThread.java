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
/*            
            TODO: this can be used to implement a double tick like watsapp to
            show that the message has been sent currently we ignore the response
*/
        }
        /*
        Interpret a message recieved from the server and display it.
        JSON for a new message
        {
        	“type”:”message”,
                "message":{
                    "srcId":"2222222222",
                    "destId":"3333333333",
                    "messageString":"Actual message sent to user.",
	}
        */
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
        /*
        This method processed the JSON recieved from the server and displays
        the message recieved or the status of message which was sent earlier.
        JSON Response to a message request
        {
            "type": "response",
            "responseType":"messageResponse",
            "state", "pass/fail",
            "message",”original message JSON”,
            "errorMessage":”reason for failure in case of failure example duplicate messages”
        }
        JSON for a new message
        {
        	“type”:”message”,
                "message":{
                    "srcId":"2222222222",
                    "destId":"3333333333",
                    "messageString":"Actual message sent to user.",
	}
}
        */
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
