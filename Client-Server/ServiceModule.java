import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.sql.*;

class QueryRunner implements Runnable {

    // Declare socket for client access
    protected Socket socketConnection;

    public static String bookTickets(
            Connection conn,
            String trainNo,
            String date,
            String pref,
            String[] names
        ) {
        try {
            // conn.beginRequest();
            
            CallableStatement cstmt = conn.prepareCall("{call book_tickets(?, ?, ?, ?, ?)}");

            cstmt.setString(1, trainNo);
            cstmt.setDate(2, java.sql.Date.valueOf(date));
            cstmt.setString(3, pref);
            cstmt.setArray(4, conn.createArrayOf("text", names));
            cstmt.registerOutParameter(5, java.sql.Types.VARCHAR);
            
            cstmt.executeUpdate();
            // stmt.executeUpdate("commit;");
            
            String result = cstmt.getString(5);

            cstmt.close();
            return result;

        } catch (SQLException e) {
            return e.getSQLState();
        }
    }

    public QueryRunner(Socket clientSocket) {
        this.socketConnection = clientSocket;
    }

    public void run() {
        try {
            // Reading data from client
            InputStreamReader inputStream = new InputStreamReader(socketConnection.getInputStream());
            BufferedReader bufferedInput = new BufferedReader(inputStream);
            OutputStreamWriter outputStream = new OutputStreamWriter(socketConnection.getOutputStream());
            BufferedWriter bufferedOutput = new BufferedWriter(outputStream);
            PrintWriter printWriter = new PrintWriter(bufferedOutput, true);
            String clientCommand = "";
            String responseQuery = "";
            Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/train_system",
                "postgres", "admin"
            );

            conn.setAutoCommit(true);
            conn.setTransactionIsolation(8);

            String[] params; 
            String trainNo; 
            String date; 
            String preference; 
            Integer numberOfPassengers;
            // Read client query from the socket endpoint
            clientCommand = bufferedInput.readLine();
            while (!clientCommand.equals("#")) {
                
                // System.out.println("Recieved data <" + clientCommand + "> from client : " + socketConnection.getRemoteSocketAddress().toString());
                params = clientCommand.split("\\s+");
                numberOfPassengers = Integer.valueOf(params[0]);
                String[] names = new String[numberOfPassengers]; 
                for (int i=0; i<numberOfPassengers-1; i++) {
                    names[i] = params[i+1].substring(0, params[i+1].length()-1);
                }
                names[numberOfPassengers-1] = params[numberOfPassengers];

                trainNo = params[numberOfPassengers+1];
                date = params[numberOfPassengers+2];
                preference = params[numberOfPassengers+3];

                responseQuery = "40001";
                while (responseQuery.equals("40001")) {
                    responseQuery = bookTickets(conn, trainNo, date, preference, names);
                }

                // Sending data back to the client
                if (responseQuery.equals("P0001")){
                    responseQuery = "Not enough seats left";
                }
                printWriter.println(responseQuery);
                // Read next client query
                clientCommand = bufferedInput.readLine();
            }
            conn.close();
            inputStream.close();
            bufferedInput.close();
            outputStream.close();
            bufferedOutput.close();
            printWriter.close();
            socketConnection.close();
        } catch (Exception e) {
            return;
        }
    }
}

/**
 * Main Class to controll the program flow
 */
public class ServiceModule {
    // Server listens to port
    static int serverPort = 7008;
    // Max no of parallel requests the server can process
    static int numServerCores = 5 ;

    // ------------ Main----------------------
    public static void main(String[] args) throws IOException {

        // Creating a thread pool

        ExecutorService executorService = Executors.newFixedThreadPool(numServerCores);

        // Creating a server socket to listen for clients
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            Socket socketConnection = null;

            // Always-ON server
            while (true) {
                // System.out.println("Listening port : " + serverPort
                //         + "\nWaiting for clients...");
                socketConnection = serverSocket.accept(); // Accept a connection from a client
                // System.out.println("Accepted client :"
                //         + socketConnection.getRemoteSocketAddress().toString()
                //         + "\n");
                // Create a runnable task
                Runnable runnableTask = new QueryRunner(socketConnection);
                // Submit task for execution
                executorService.submit(runnableTask);
            }
        }
    }
}
