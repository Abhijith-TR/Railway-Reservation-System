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

    public QueryRunner(Socket clientSocket) {
        this.socketConnection = clientSocket;
    }

    public static String addTrain(Connection conn, String trainNo) {
        try {
            conn.beginRequest();
            CallableStatement cstmt = conn.prepareCall("{call add_train(?)}");

            cstmt.setString(1, trainNo);
            cstmt.executeUpdate();

            cstmt.close();
            return String.format("Train: %s added", trainNo);

        } catch (SQLException e) {

            return e.getSQLState();

        }
    }

    public static String releaseTrain(
            Connection conn,
            String trainNo,
            String date,
            Integer acCoaches,
            Integer slCoaches) {
        try {
            conn.beginRequest();
            CallableStatement cstmt = conn.prepareCall("{call release_train(?, ?, ?, ?)}");

            cstmt.setString(1, trainNo);
            cstmt.setDate(2, Date.valueOf(date));
            cstmt.setInt(3, acCoaches);
            cstmt.setInt(4, slCoaches);
            cstmt.executeUpdate();

            cstmt.close();

            return String.format("Released train %s with %d AC coaches %d SL coaches on %s", trainNo, acCoaches,
                    slCoaches, date);

        } catch (Exception e) {

            return e.getMessage();

        }
    }

    public void run() {
        try {
            // Reading data from client
            InputStreamReader inputStream = new InputStreamReader(socketConnection.getInputStream());
            BufferedReader bufferedInput = new BufferedReader(inputStream);
            OutputStreamWriter outputStream = new OutputStreamWriter(socketConnection.getOutputStream());
            BufferedWriter bufferedOutput = new BufferedWriter(outputStream);
            PrintWriter printWriter = new PrintWriter(bufferedOutput, true);
            String responseQuery = "";

            Connection adminConn = null;
    
            Class.forName("org.postgresql.Driver");

            String adminQuery = bufferedInput.readLine();
            String[] params;
            String trainNo = "";
            String date = "";
            Integer acCoaches = 0;
            Integer slCoaches = 0;

            adminConn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/train_system",
                "postgres", "admin"
            );

            while (!adminQuery.equals("#")) {

                params = adminQuery.split("\\s+");
                trainNo = params[0];
                date = params[1];
                acCoaches = Integer.valueOf(params[2]);
                slCoaches = Integer.valueOf(params[3]);
                responseQuery = releaseTrain(adminConn, trainNo, date, acCoaches, slCoaches);

                if (responseQuery.equals("23505")){
                    responseQuery = "Train already exists on the given date.";
                } else {
                    responseQuery = String.format("Train: %s added on date: %s.", trainNo, date);
                }

                printWriter.println(responseQuery);
                // Read next client query
                adminQuery = bufferedInput.readLine();
            }
            System.out.println("Inserted trains");

            adminConn.close();
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

public class interactiveServer {
    // Server listens to port
    static int serverPort = 7009;
    // Max no of parallel requests the server can process
    static int numServerCores = 1 ;

    
    // ------------ Main----------------------
    public static void main(String[] args) throws IOException {

        // Creating a thread pool
    
        ExecutorService executorService = Executors.newFixedThreadPool(numServerCores);

        // Creating a server socket to listen for clients
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            Socket socketConnection = null;

            // Always-ON server
            while (true) {
                System.out.println("Listening port : " + serverPort
                        + "\nWaiting for clients...");
                socketConnection = serverSocket.accept(); // Accept a connection from a client
                System.out.println("Accepted client :"
                        + socketConnection.getRemoteSocketAddress().toString()
                        + "\n");
                // Create a runnable task
                Runnable runnableTask = new QueryRunner(socketConnection);
                // Submit task for execution
                executorService.submit(runnableTask);
            }
        }
    }
}
    
