package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.Log;
import shared.ProtocolStrings;


public class EchoServer {

  private static boolean keepRunning = true;
  private static ServerSocket serverSocket;
  private String ip;
  private int port;
  private static ArrayList<ClientHandler> clients;

  public static void stopServer() {
    keepRunning = false;
  }

    public EchoServer() {
        clients = new ArrayList<>();
    }

  

  private void runServer(String ip, int port) {
    this.port = port;
    this.ip = ip;
    try{
        Log.setLogFile("logFile.txt", "serverLog");
    Logger.getLogger(Log.LOG_NAME).log(Level.INFO,"Server started. Listening on: " + port + ", bound to: " + ip);
    try {
      serverSocket = new ServerSocket();
      serverSocket.bind(new InetSocketAddress(ip, port));
      do {
        Socket socket = serverSocket.accept(); //Important Blocking call
        Logger.getLogger(Log.LOG_NAME).log(Level.INFO,"Connected to a client");
        ClientHandler ch = new ClientHandler(socket);
        clients.add(ch);
        ch.start();
      } while (keepRunning);
    } catch (IOException ex) {
      Logger.getLogger(Log.LOG_NAME).log(Level.SEVERE, null, ex);
    }
    }
    finally{
        Log.closeLogger();
    }
  }
  
  public static synchronized void removeHandler(ClientHandler ch){
      clients.remove(ch);
  }
  
  // Provide the EchoServer with a send(String msg) method which should iterate through 
  // all handlers and call their send(..) method with the argument upper cased.
  
  public static synchronized void send(String message){
      for (ClientHandler client : clients) {
          client.send(message);
      }
  }

  public static  void main(String[] args) {
    try {
      if(args.length != 2){
        throw new IllegalArgumentException("Error: Use like: java -jar EchoServer.jar <ip> <port>");
      }
      String ip = args[0];
      int port = Integer.parseInt(args[1]);
      new EchoServer().runServer(ip, port);
    } catch (Exception e) {
        Logger.getLogger(Log.LOG_NAME).log(Level.SEVERE, null, e);
    }
  }
}
