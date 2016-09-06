package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import shared.ProtocolStrings;

public class EchoClient extends java.util.Observable
{
  Socket socket;
  private int port;
  private InetAddress serverAddress;
  private Scanner input;
  private PrintWriter output;
  private Boolean stop;
  private ClientGUI gui;
  private String ip;
  private String message;
  
    EchoClient(ClientGUI gui) {
        this.gui = gui;
        port = ClientGUI.getPort();
        ip = ClientGUI.getIp();
      try {
          connect(ip, port);
      } catch (IOException ex) {
          System.out.println("NOOO CONNECTION ERROR");
      }
    }

    
    // For the test file!
    public EchoClient() {
    }
    
    
  
  public void connect(String address, int port) throws UnknownHostException, IOException
  {
    this.port = port;
    serverAddress = InetAddress.getByName(address);
    socket = new Socket(serverAddress, port);
    input = new Scanner(socket.getInputStream());
    output = new PrintWriter(socket.getOutputStream(), true);  //Set to true, to get auto flush behaviour
    stop = false;
  }
  
  public void send(String msg)
  {
     output.println(msg);
  }
  
  public Boolean isStopped(){
      return stop;
  }
  
  public void stop() throws IOException{
    output.println(ProtocolStrings.STOP);
    stop=true;
  }
  
  public void listen(){
      String msg ="";
      while(!msg.equals(ProtocolStrings.STOP)){
      msg = input.nextLine();
      setChanged();
      notifyObservers(msg);
      }
      try {
          socket.close();
      } catch (IOException ex) {
          Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
      }
  }
  
  public String receive()
  {
     message = input.nextLine(); // Blocking call
    if(message.equals(ProtocolStrings.STOP)){
      try {
        socket.close();
      } catch (IOException ex) {
        Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    
    setChanged();
    notifyObservers(message);
    return message;
  }
  
//  public static void main(String[] args)
//  {   
//    int port = 7777;
//    String ip = "localhost";
//    if(args.length == 2){
//      ip = args[0];
//      port = Integer.parseInt(args[1]);
//    }
//    try {
//      EchoClient tester = new EchoClient(gui);      
//      tester.connect(ip, port);
//      System.out.println("Sending 'Hello world'");
//      tester.send("Hello World");
//      System.out.println("Waiting for a reply");
//      System.out.println("Received: " + tester.receive()); //Important Blocking call         
//      tester.stop();      
//      //System.in.read();      
//    } catch (UnknownHostException ex) {
//      Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
//    } catch (IOException ex) {
//      Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
//    }
//  }
}