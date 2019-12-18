import java.net.ServerSocket;
import java.net.Socket;
/**
 * @author Paul Wynne
 * 
 * Launches the server, handles connection requests and assigns them to "workers" 
 */
public class Server {
    //Set port to 8888
    private final static int PORT = 8888;

    public static void startServer() {
        try {
            //Assign socket to port
            final ServerSocket serverSocket = new ServerSocket( PORT );

            while ( true ) {
                //Upon connection attempt, give socket to worker to manage, then launch worker on new thread.
                final Socket clientSocket = serverSocket.accept();
                final Worker worker = new Worker( clientSocket );
                new Thread(worker).start();
            }
        } catch ( Exception exn ) {
            System.out.println( "Error launching server, exiting..." );
            System.exit(0);
        }
    }
}
