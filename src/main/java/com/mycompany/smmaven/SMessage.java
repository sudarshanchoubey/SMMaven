/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.smmaven;

import java.io.Serializable;
import java.util.Date;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;

/**
 *
 * @author schoubey
 */
public class SMessage implements Serializable{
    private final String destId;
    private final String srcId;
    private final String messageString;
    private final long arrivalTime;       //time of arrival at server in milliseconds
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
    /**
     * @return the srcId
     */
    public String getSrcId() {
        return srcId;
    }

    /**
     * @return the messageString
     */
    public String getMessageString() {
        return messageString;
    }

    /**
     * @return the arrivalTime
     */
    public long getArrivalTime() {
        return arrivalTime;
    }
    public static class Builder {
        private String destId;
        private String srcId;
        private String messageString;
        private long arrivalTime = 0;
        
        public Builder destId(String val) {
            destId = val;
            return this;
        }
        public Builder srcId(String val) {
            srcId = val;
            return this;
        }
        public Builder messageString(String val) {
            messageString = val;
            return this;
        }
        public Builder arrivalTime(long val) {
            arrivalTime = val;
            return this;    
        }
        public SMessage build() {
            return new SMessage(this);
        }
    }
    
    @Override
    public String toString() {
        Date dt = new Date(getArrivalTime());
        String tos = getDestId() + "," + getSrcId() + "," + getMessageString() + "," +
                     dt.toString();
        return tos;
    }
    private SMessage(Builder builder) {
        destId = builder.destId;
        srcId = builder.srcId;
        messageString = builder.messageString;
        arrivalTime = builder.arrivalTime;
    }
    public String getDestId() {
        return destId;
    }
    public static SMessage messageFromJSON(JsonObject jMessage, long arrTime) {
        System.out.println("jmessage as passed" + jMessage.toString());
        JsonValue jfrom = jMessage.get("srcId");
        JsonValue jTo = jMessage.get("destId");
        JsonValue jmessageString = jMessage.get("messageString");
              
        String from = jfrom.toString();
        String to = jTo.toString();
        String message = jmessageString.toString();
        SMessage sm = new SMessage.Builder().srcId(from).destId(to).
                arrivalTime(arrTime).messageString(message).build();
        return sm;
    }
    
    public String messageToJsonStringClient() {
        JsonObject jo = Json.createObjectBuilder().add("srcId",srcId).
                add("destId",destId).add("messageString",messageString).build();
        return jo.toString();
    }
    public String messageToJsonStringServer() {
        JsonObject jo = Json.createObjectBuilder().add("srcId",srcId).
                add("destId",destId).add("messageString",messageString).
                add("arrivalTime",arrivalTime).build();
        return jo.toString();
    }
    public JsonObject messageToJsonServer() {
        JsonObject jo = Json.createObjectBuilder().add("srcId",srcId).
                add("destId",destId).add("messageString",messageString).
                add("arrivalTime",arrivalTime).build();
        return jo;
    }
}
