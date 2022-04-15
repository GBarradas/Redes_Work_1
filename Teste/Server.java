import java.io.*;
import java.net.*;
import java.util.*;

class Server
{
    private int port;
    private List<UserClient> clients;
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
        BufferedReader br;
        PrintStream outClient;
        while(true){
            Socket client = server.accept();
            br = new BufferedReader(new InputStreamReader   (client.getInputStream()));
            outClient =new PrintStream( client.getOutputStream());
            String nickname = br.readLine();
            while(!acceptNickname(nickname)){
                //System.out.println(nickname+ " in use");
                outClient.println("NickName already in use!!");
                nickname = br.readLine();
            }
            outClient.println("Connected!!");
            UserClient uc = new UserClient(client,nickname,outClient,br);
            clients.add(uc);
            System.out.println(uc+ " Connected");
            new Thread(new ProcessUser(this , uc)).start();
        }
    }
    public boolean acceptNickname(String nickname){
        for(UserClient a : clients){
            if(a.getNickname().equals(nickname))
                return false;
        }
        return true;
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
}
class ProcessUser implements Runnable{
    private Server server;
    private UserClient client;

    public ProcessUser(Server server, UserClient client){
        this.server = server;
        this.client = client;
    }
    public void run(){
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
                    }
                }
            }
        }
        catch( Exception IOException){}
    }
}

class UserClient{
    private String nickname;
    private Socket client;
    private PrintStream outClient;
    private BufferedReader inputClient;

    public UserClient(Socket client, String nickname, PrintStream pw, BufferedReader br){
        this.client = client;
        this.nickname = nickname;
        this.outClient = pw;
        this.inputClient = br;

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