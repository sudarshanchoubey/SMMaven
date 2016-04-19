/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.smmaven;

import java.io.StringReader;
import java.util.regex.Pattern;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

/**
 *
 * @author schoubey
 */
public class ConnectionRequest {
    public String type;
    public String Id;
    public String convertToJsonString() {
        JsonObject jo = Json.createObjectBuilder().
                add("type", type).add("Id", Id).build();
        return jo.toString();
    }
    public static ConnectionRequest createConnectionRequest(String request) {
        JsonReader jReader = Json.createReader(new StringReader(request));
        JsonObject jo = jReader.readObject();
        JsonValue jType = jo.get("type");
        JsonValue jId = jo.get("Id");
        String rType,rId;
        if(jType == null){
            System.out.println("1:Jtype was null");
            return null;
        }
        if(jId == null){
            System.out.println("2:jid was null");
            return null;
                
        }
        rId = jId.toString();
        rType = jType.toString();
        
//        if((Pattern.matches("\\d{10}", rId) == false) || (rType.contains("connection") == false)) {
//                System.out.println("3:pattern error " + rId + " " + rType);
//                return null;
//        }
        if(Pattern.matches("\\d{10}", rId) == false) {
            System.out.println("pattern failed");
        }
        if(rType.contains(new String("connection")) == false) {
            System.out.println("string contains was false"
                    + "");
        }
           
        ConnectionRequest cn = new ConnectionRequest();
        cn.Id =rId;
        cn.type = rType;
        return cn;
    }
    
}
