/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.smmaven;

import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

/**
 *
 * @author schoubey
 */
public class Response {
    public String type, responseType,
                  state, message, errorMessage;
    public static Response createConnectionFailResponse() {
        Response res = new Response();
        res.type = "response";
        res.responseType = "connectionResponse";
        res.state = "fail";
        res.errorMessage = "Incorrect connection response format";
        res.message = "";
        return res;
    }
    
    public static Response createConnectionPassResponse() {
        Response res = new Response();
        res.type = "response";
        res.responseType = "connectionResponse";
        res.state = "pass";
        res.errorMessage = "";
        res.message = "";
        return res;
    }
    public String convertToJson() {
        JsonObject jo = Json.createBuilderFactory(null).createObjectBuilder().
                        add("type", type).add("responseType",responseType).
                        add("state",state).add("errorMessage", errorMessage).
                        add("message",message).build();
        return jo.toString();
    }
    
    public static Response getResponseFromJsonString(String input) {
        JsonReader jReader = Json.createReader(new StringReader(input));
        JsonObject jo = jReader.readObject();
        JsonValue jType = jo.get("type");
        JsonValue jresType = jo.get("responseType");
        JsonValue jState = jo.get("state");
        JsonValue jErrorMessage = jo.get("errormessage");
        String typeFromJson, resType, stateFromJson;
        if((jType != null) && (jresType != null) && (jState != null)) {
            typeFromJson = jType.toString();
            resType  = jresType.toString();
            stateFromJson = jState.toString();
        } else {
            return null;
        }
        if(stateFromJson.contains("pass")){
            Response res = new Response();
            res.type = typeFromJson;
            res.state = stateFromJson;
            res.responseType = resType;
            return res;
        } else {
            Response res = new Response();
            res.type = typeFromJson;
            res.state = stateFromJson;
            res.responseType = resType;
            res.errorMessage = jErrorMessage.toString();
            return res;
        }
    }
}
