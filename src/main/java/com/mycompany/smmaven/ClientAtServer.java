/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.smmaven;

import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author schoubey
 */
public class ClientAtServer {
    private Socket socket;
    private String ClientId;
    private ConcurrentLinkedQueue<SMessage> sentMessageList;

    /**
     * @return the socket
     */
    public String toString() {
        return("socket:" + socket.toString() + " cliId " + ClientId);
    }
    public ClientAtServer(String cid, Socket s) {
        socket = s;
        ClientId = cid;
        sentMessageList = new ConcurrentLinkedQueue<>();
    }
    public Socket getSocket() {
        return socket;
    }

    /**
     * @return the ClientId
     */
    public String getClientId() {
        return ClientId;
    }

    /**
     * @return the sentMessageList
     */
    public synchronized void addToSentMessageList(SMessage sm) {
        sentMessageList.add(sm);
    }
    
    public ConcurrentLinkedQueue<SMessage> getSentMessageList() {
        return sentMessageList;
    }
    
    /**
     * @param socket the socket to set
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * @param ClientId the ClientId to set
     */
    public void setClientId(String ClientId) {
        this.ClientId = ClientId;
    }
}
