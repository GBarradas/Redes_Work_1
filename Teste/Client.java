import java.io.*;
import java.net.*;
import java.util.*;

class Client {
    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private final static String serverHost = "localhost";       //Ip adress
    private final static int port = 1234;       //Porta
    private static PrintStream outServer;       // buffer de escrita
    private static Scanner inputServer;         // buffer de leitura
    public static void main(String[] args) throws UnknownHostException, IOException
    {
        Socket server = new Socket(serverHost,port);        //criar o socket
        outServer = new PrintStream(server.getOutputStream());  // inicializar o buffer de escrita
        inputServer = new Scanner(server.getInputStream());     // inicializar o buffer de leitura
        setNickname();                                          // defenir o nickname
        new Thread(new ReciveMessages(server,inputServer)).start(); //cria uma thread para receber mensagens
        SendMenssages sm = new SendMenssages(server,br,outServer);
        sm.sendMsg();
                                            //apos o fim da conexão
        System.out.println("Goodbye!!");    
        br.close();                         //fecha todos os buffers
        outServer.close();              
        inputServer.close();
        server.close();                     // e fecha o socket
        
    }
    public static void setNickname() throws IOException
    {                                          // le o nickname da consola e passa-o para o server
        System.out.println("Enter a nickname:");
        String name = br.readLine();
        outServer.println(name);
        String result = inputServer.nextLine();
        System.out.println(result);


    }
}
class SendMenssages{                // responsavel por enviar mensangens
    private  Socket server;         
    private  BufferedReader br;
    private  PrintStream outServer;
    public SendMenssages(Socket server, BufferedReader br, PrintStream ps){
        this.server = server;
        this.br = br;
        this.outServer = ps;
    }
    public void sendMsg() throws IOException
    {
        String message;
        while((message = br.readLine())!= null){    // le mensagens enquanto o cliente estiver conectado
            outServer.println(message);
            if(message.equals("#Disconect"))       // o cliente pretende desconectar-se
                return;
        }

    }

}
class ReciveMessages implements Runnable{
    private Socket server; 
    private Scanner inputServer;
    public ReciveMessages(Socket server, Scanner is){
        this.server = server;
        this.inputServer = is;
    }
    public void run()       // enquanto o cliente estiver conectado                       
    {                       // le mensagens enviadas pelo servidor e outros clientes
        String message;
        while(inputServer.hasNextLine()){
            message = inputServer.nextLine();
            System.out.println(message);
        }
    }
}