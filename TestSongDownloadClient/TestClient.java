import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * @author Paul Wynne
 * 
 * Client program used to test downloads from the server. This contains all the code that will be integrated into the main game for the final sprint.
 *  
 */
public class TestClient {
    public static void main(String[] args) throws IOException {
        
        //Set the host and port number. As this is simply a test mode these will be left hardcoded. Next sprint this information will be pulled from a .ini file.
        String hostName = "localhost";
        int portNumber = 8888;

        try{
            //Setup IO streams between client and server
            Socket serverSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);
            
            InputStream is = serverSocket.getInputStream();
            DataInputStream dis = new DataInputStream(is);
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            
            
            //Print the songs options list. This will be later replaced as will tie into main game GUI.
            System.out.println(in.readLine());

            //Ask for user input. This will later take input from the GUI
            System.out.println("Please choose ID:");
            out.println(getUserInt());

            //Write the downloaded data stream to a zip file.
            FileOutputStream fos = new FileOutputStream("test.zip");
            
            byte[] buffer = new byte[4096];
            int length;
            while((length = dis.read(buffer)) >= 0) {
                fos.write(buffer, 0, length);
            }
		
            fos.close();
            dis.close();
            serverSocket.close();

        } catch (UnknownHostException e) {
            System.err.println("Can't find host: " + hostName);
            System.exit(0);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O");
            System.exit(0);
        } 
    }

    /**
     * @author Paul Wynne
     * 
     * Short method used to get user input
     * 
     * @return userInt, the integer input by the user.
     */
    private static int getUserInt(){
        Scanner in = new Scanner(System.in);
        int userInt = in.nextInt();
        return userInt;
    }

}
