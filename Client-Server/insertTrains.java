import java.util.Scanner;
import java.util.concurrent.ExecutorService ;
import java.util.concurrent.Executors   ;
import java.util.concurrent.TimeUnit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException  ;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.File;

class interactive implements Runnable {
    int bookTicketPort = 7008 ;
    int releaseTrainPort = 7009;
    String fileName;

    interactive(String src) {
        fileName = src;
    }

    public void run() {
        try {
            
            // Establishing a socket connection to the admin server
            Socket addTrainSocketConnection = new Socket("localhost", releaseTrainPort) ;
            OutputStreamWriter ostream = new OutputStreamWriter(addTrainSocketConnection.getOutputStream());
            BufferedWriter bOutput = new BufferedWriter(ostream);
            InputStreamReader istream = new InputStreamReader(addTrainSocketConnection.getInputStream());
            BufferedReader bInput = new BufferedReader(istream);
            PrintWriter pWriter = new PrintWriter(bOutput, true);

            File queries = new File(fileName);
            if (queries.canRead() == false) {
                System.out.println("File Not Found");
                addTrainSocketConnection.close();
                return;
            }
            Scanner queryScanner = new Scanner(queries);
            
            String query = "";

            // Read input queries and write to the output stream
            while (queryScanner.hasNextLine()) {
                query = queryScanner.nextLine();
                pWriter.println(query);
            }
            
            String result = "";
            while ((result = bInput.readLine()) != null) {
                System.out.println(result);
            }

            System.out.println("Inserted train/s");
            queryScanner.close();
            
            pWriter.println("#");
            addTrainSocketConnection.close();
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

public class insertTrains
{
    public static void main(String args[])throws IOException
    {
        /**************************/
        int firstLevelThreads = 1 ;   // Indicate no of users 
        /**************************/
        // Creating a thread pool
        if (args.length == 0) {
            System.out.println("Enter Valid File Name");
            return;
        }
        
        ExecutorService executorService = Executors.newFixedThreadPool(firstLevelThreads);
        
        for(int i = 0; i < firstLevelThreads; i++)
        {
            Runnable interactive = new interactive(args[0]);    //  Pass arg, if any to constructor sendQuery(arg)
            executorService.submit(interactive) ;
        }

        executorService.shutdown();
        try
        {    // Wait for 8 sec and then exit the executor service
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS))
            {
                executorService.shutdownNow();
            } 
        } 
        catch (InterruptedException e)
        {
            executorService.shutdownNow();
        }
    }
}