import java.io.*;
import java.net.*;
import java.util.*;

class Client {
    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private final static String serverHost = "localhost";
    private final static int port = 12345;
    private static boolean nickname = false;
    private static PrintStream outServer;
    private static Scanner inputServer;
    public static void main(String[] args) throws UnknownHostException, IOException
    {
        Socket server = new Socket(serverHost,port);
        outServer = new PrintStream(server.getOutputStream());
        inputServer = new Scanner(server.getInputStream());
        while(!nickname){
            System.out.println("Enter a nickname:");
            setNickname();
        }
        new Thread(new ReciveMessages(server,inputServer)).start();
        SendMenssages sm = new SendMenssages(server,br,outServer);
        sm.sendMsg();

    }
    public static void setNickname() throws IOException
    {
        String name = br.readLine();
        outServer.println(name);
        String result = inputServer.nextLine();
        System.out.println(result);
        if(result.equals("Connected!!")){
            nickname = true;
        }


    }
}
class SendMenssages{
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
        while((message = br.readLine())!= null){
            outServer.println(message);
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
    public void run()
    {
        try{

            String message;
            while(inputServer.hasNextLine()){
                message = inputServer.nextLine();
                System.out.println(message);
            }
        }catch( Exception IOException){

        }
    }
}