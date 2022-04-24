import java.io.*;
import java.net.*;
import java.util.*;

class Server
{
    private int port;
    private  List<UserClient> clients;
    private ServerSocket server;
    public Server(int port){
        this.port = port;
        clients = new ArrayList<UserClient>();
    }
    public static void main(String[] args) throws IOException
    {
        new Server(12345).inicializeServer();
    }
    public void inicializeServer() throws IOException
    {
        this.server = new ServerSocket(port);
        System.out.println("Server connect on "+port);
        aceptClients();
    }
    public void aceptClients() throws IOException
    {
        while(true){
            Socket client = server.accept();
            UserClient uc = new UserClient(client,this,clients);
            addClient(uc);
            new Thread(uc).start();
            
        }
    }
    
    public void serverToAll(String msg){
        for(UserClient a : clients){
            a.getOutput().println("System:"+msg);
        }
    }
    public void serverToClient(String msg, UserClient clientDestin){
        clientDestin.getOutput().println("System: "+msg);
    }
    public void messageForAll(String msg, UserClient userSend){
        for(UserClient a : clients){
            a.getOutput().println(userSend+": "+msg);
        }
    }
    public void messageToClient(String msg, UserClient userSend, String userDestin){
        UserClient destin = getClient(userDestin);
        if(destin == null){
            serverToClient(("<"+userDestin+"> not found"),userSend);
        }
        else{
            destin.getOutput().println(userSend+": -"+msg);
        }
    }
    public UserClient getClient(String nickname){
        for(UserClient a : clients){
            if(a.getNickname().equals(nickname)){
                return a;
            }
        }
        return null;
    }
    public void removeClient(UserClient toRemove){
        clients.remove(toRemove);
    }
    public void addClient (UserClient newClinet){
        clients.add(newClinet);
    }
    public List<UserClient> getUsers(){
        return clients;
    }
}
class ProcessUser{
    private Server server;
    private UserClient client;

    public ProcessUser(Server server, UserClient client){
        this.server = server;
        this.client = client;
    }
    public void HandleRecivedMensages(){
        try{

            String message;
            while((message = client.getInput().readLine())!= null){
                System.out.println(client+" "+message);
                if(message.charAt(0) == '+'){
                    server.messageForAll(message, client);
                }
                if(message.charAt(0) == '-'){
                    if(message.contains(" ")){
                        int space = message.indexOf(" ");
                        String destin = message.substring(1,space);
                        String finalMsg = message.substring(space+1);
                        server.messageToClient(finalMsg,client,destin);
                    }
                }
                if(message.charAt(0)== '#'){
                    String command = message.substring(1);
                    if(command.equals("Disconect")){
                        server.removeClient(client);
                        server.serverToAll(client+ " Disconect");
                        client.Disconect();
                        break;
                    }
                }
            }
        }
        catch( Exception IOException){}
    }
}

class UserClient implements Runnable{
    private Server server;
    private String nickname;
    private Socket client;
    private PrintStream outClient;
    private BufferedReader inputClient;

    public UserClient(Socket client, Server server,List<UserClient> clients) throws IOException
    {
        this.client = client;
        this.server = server;
        this.outClient = new PrintStream( client.getOutputStream());
        this.inputClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
        //run();
    }
    public void run() 
    {
        try{

            String name = inputClient.readLine();
            outClient.println("Connected!!");
            this.nickname = name;
            System.out.println(this+" Connected!!");
            ProcessUser pu = new ProcessUser(server,this);
            pu.HandleRcivedMessages();
        }   
        catch(Exception IOException){

        }
    }
    public void Disconect() throws IOException
    {
        client.close();
    }

    public String getNickname(){
        return nickname;
    }
    public BufferedReader getInput(){
        return inputClient;
    }
    public PrintStream getOutput(){
        return outClient;
    }
    @Override
    public String toString(){
        return "<"+nickname+"> ";
    }

}
