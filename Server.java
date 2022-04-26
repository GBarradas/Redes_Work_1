import java.io.*;
import java.net.*;
import java.util.*;

class Server
{
    private int port;               // Porta
    private  List<UserClient> clients;      // lista de clientes conectados
    private ServerSocket server;            // socket do servidor
    public Server(int port){
        this.port = port;
        clients = new ArrayList<UserClient>();
    }
    public static void main(String[] args) throws IOException
    {
        new Server(1234).inicializeServer();
    }
    public void inicializeServer() throws IOException
    {
        this.server = new ServerSocket(port);       //abre o socket na porta desejada
        System.out.println("Server connect on "+port);
        aceptClients();
    }
    public void aceptClients() throws IOException
    {
        while(true){                        //enquanto o socket estiver aberto aceita clientes
            Socket client = server.accept();    // socket por cliente
            UserClient uc = new UserClient(client,this,clients);    
            addClient(uc);
            new Thread(uc).start();
            
        }
    }
    
    public void serverToAll(String msg){        // servidor envia mensagem para todos os clientes
        for(UserClient a : clients){            // Ex: aviso de um cliente que se ddesconectou
            a.getOutput().println("System: "+msg);
        }
    }
    public void serverToClient(String msg, UserClient clientDestin){    // comunicaçao entre o servidor e um cliente especifico
        clientDestin.getOutput().println("System: "+msg);               
    }
    public void messageForAll(String msg, UserClient userSend){     // mensagem para todos os clientes
        for(UserClient a : clients){            // Ex: quando um cliente quer enviar uma mensagem para todos os clientes
            if(a != userSend){           
                a.getOutput().println(userSend+": "+msg);

            }
        }
    }
    //mensagem para um determianado cliente. Ex: mensagem privada
    public void messageToClient(String msg, UserClient userSend, String userDestin){
        UserClient destin = getClient(userDestin);              // descobre o destinatario da mensagem   
        if(destin == null){                                 
            serverToClient(("<"+userDestin+"> not found"),userSend);
        }
        else{
            destin.getOutput().println(userSend+": -"+msg);
        }
    }
    public UserClient getClient(String nickname){     // devolve o userClient com o nickname do destinatario caso este exista
        for(UserClient a : clients){
            if(a.getNickname().equals(nickname)){
                return a;
            }
        }
        return null;
    }
    public void removeClient(UserClient toRemove){      //remove um cliente da lista de clientes conectados
        clients.remove(toRemove);
    }
    public void addClient (UserClient newClinet){       // adiciona um clinte á lista de clientes conectados
        clients.add(newClinet);
    }
}
class ProcessUser{          // processa as mensagens que um determinado cliente pretende enviar
    private Server server;
    private UserClient client;

    public ProcessUser(Server server, UserClient client){
        this.server = server;
        this.client = client;
    }
    public void HandleRcivedMessages(){         
        try{

            String message;
            while((message = client.getInput().readLine())!= null){
                System.out.println(client+" "+message);
                if(message.charAt(0) == '+'){     // define que é mensagem para todos
                    server.messageForAll(message, client);
                }
                if(message.charAt(0) == '-'){   // define que é mensagem privada
                    if(message.contains(" ")){  
                        int space = message.indexOf(" ");
                        String destin = message.substring(1,space); //obter nickname do destinatario
                        String finalMsg = message.substring(space+1);   //obter mensagem 
                        server.messageToClient(finalMsg,client,destin);
                    }
                }
                if(message.charAt(0)== '#'){        // mensagens para o servidor
                    String command = message.substring(1);
                    if(command.equals("Disconect")){        // cliente deseja desconectar-se
                        server.removeClient(client);
                        server.serverToAll(client+ " Disconect");

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
        try{        //define o nickname do cliente

            String name = inputClient.readLine();
            outClient.println("Connected!!");
            this.nickname = name;
            System.out.println(this+" Connected!!");
            ProcessUser pu = new ProcessUser(server,this);
            pu.HandleRcivedMessages();             // chama o processUser para processar as mensagens enviadas pelo cliente
        }   
        catch(Exception IOException){

        }
    }

    public String getNickname(){      //devolve o nickname do cliente
        return nickname;
    }
    public BufferedReader getInput(){   //retorna o buffer de leitura do cliente
        return inputClient;
    }
    public PrintStream getOutput(){     //retorna o buffer de escrita do cliente
        return outClient;
    }
    @Override
    public String toString(){      
        return "<"+nickname+"> ";
    }

}