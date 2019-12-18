import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.io.File;
/**
 * @author Paul Wynne 
 * 
 * Manages interactions with client, listening for song package requests then delivering them. 
 */
public class Worker implements Runnable {

    private Socket clientSocket;
    Worker(Socket clientSocket){this.clientSocket = clientSocket;}

    /**
     * @author Paul Wynne
     * 
     * run method that handles client interaction. each new thread will run this.
     */
    public void run() {

        //Get song packages list
        ArrayList<String> Songs = StoreManager.GetSongsArray();
        int songCount = Songs.size();
        
        try {
            //Get input/output streams
            final InputStream  in  = clientSocket.getInputStream();
            final OutputStream out = clientSocket.getOutputStream();
            
            //Get reader for input stream
            final BufferedReader reader = new BufferedReader( new InputStreamReader(in)); 

            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                int ID = -1;
                try{
                    //Get ID from client.
                    ID = Integer.parseInt(inputLine);
                }catch(NumberFormatException e){
                    clientSocket.close();
                    reader.close();
                    System.out.println("Bad request from client, closing socket.");
                    return; //No need for a full system exit here
                }
                if(ID >= 0 && ID <= songCount){
                    if(ID == 0){
                        sendFile(new File("AvailableContent.zip"),out);
                    }else{
                        sendFile( (new File("Songs/" + Songs.get(ID - 1))) , out);
                    }
                    break;
                }
            }

            clientSocket.close();
            reader.close();
        } catch ( Exception exn ) {
            System.err.println( "Error serving client. Closing Connection..." );
            return;
        }
    }
    /**
     * @author Paul Wynne
     * 
     * Method called to send a song package to a given output stream
     * 
     * @param packageName The name of the song package to be delivered
     * @param out The output stream to deliver the song via
     * 
     */
    private void sendFile(File assetPackage, OutputStream out) throws IOException{
        try{
                        
            //Setup IO streams
            DataOutputStream dataOut = new DataOutputStream(out);
            FileInputStream fileIn = new FileInputStream(assetPackage);
            
            //Write song package to stream
            byte[] buffer = new byte[4096];
            while (fileIn.read(buffer) > 0 ){
                dataOut.write(buffer);
            }

            //Close IO streams
            fileIn.close();
            dataOut.close();

        }catch(FileNotFoundException e){
            System.err.println("Attempted to send non-existant file. Exiting...");
            return;
        }catch(IOException e){
            System.err.println("IO Exception whilst writing song package to socket. Exiting...");
            return;
        }
    }
}
