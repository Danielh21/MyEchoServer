/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.Log;
import shared.ProtocolStrings;

/**
 *
 * @author Daniel
 */
public class ClientHandler extends Thread {
    
    //Provide each ClientHandler with a send(String message) which should output 
    //the string to the handlers output stream (that is, send it to its actual client).
    Scanner input;
    PrintWriter writer;
    Socket s;
    
    public ClientHandler(Socket s) throws IOException{
        input = new Scanner (s.getInputStream());
        writer = new PrintWriter(s.getOutputStream(), true);
        this.s = s;
    }
    
    public void send(String message){
        writer.println(message);
    }
    
    @Override
    public void run(){ 
    String message = input.nextLine(); //IMPORTANT blocking call
    Logger.getLogger(Log.LOG_NAME).log(Level.INFO,String.format("Received the message: %1$S ", message));
    while (!message.equals(ProtocolStrings.STOP)) {
//      writer.println(message.toUpperCase());
        EchoServer.send(message.toUpperCase());
      Logger.getLogger(Log.LOG_NAME).log(Level.INFO,String.format("Received the message: %1$S ", message.toUpperCase()));
      message = input.nextLine(); //IMPORTANT blocking call
    }
    writer.println(ProtocolStrings.STOP);//Echo the stop message back to the client for a nice closedown
        try {
            s.close();
            EchoServer.removeHandler(this);
        } catch (IOException ex) {
             Logger.getLogger(Log.LOG_NAME).log(Level.SEVERE,"Failed at closing");
        }
    Logger.getLogger(Log.LOG_NAME).log(Level.INFO,"Closed a Connection");
        
    }
    
}
