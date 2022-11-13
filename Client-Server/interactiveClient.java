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

class interactive implements Runnable {
    int bookTicketPort = 7008 ;
    int releaseTrainPort = 7009;

    public void run() {
        try {
            // Establishing a socket connection to the book ticket server
            Socket bookSocketConnection = new Socket("localhost", bookTicketPort) ;
            OutputStreamWriter outputStream = new OutputStreamWriter(bookSocketConnection.getOutputStream());
            BufferedWriter bufferedOutput = new BufferedWriter(outputStream);
            InputStreamReader inputStream = new InputStreamReader(bookSocketConnection.getInputStream());
            BufferedReader bufferedInput = new BufferedReader(inputStream);
            PrintWriter printWriter = new PrintWriter(bufferedOutput, true);
            
            // Establishing a socket connection to the admin server
            Socket addTrainSocketConnection = new Socket("localhost", releaseTrainPort) ;
            OutputStreamWriter ostream = new OutputStreamWriter(addTrainSocketConnection.getOutputStream());
            BufferedWriter bOutput = new BufferedWriter(ostream);
            InputStreamReader istream = new InputStreamReader(addTrainSocketConnection.getInputStream());
            BufferedReader bInput = new BufferedReader(istream);
            PrintWriter pWriter = new PrintWriter(bOutput, true);

            System.out.println("Correctness Testing Program");
            System.out.println("1. Release Trains");
            System.out.println("2. Book Ticket");
            System.out.println("3. Retrieve Ticket");
            System.out.println("Any other key to exit");

            Scanner sc = new Scanner(System.in);
            int choice;
            while (true) {
                System.out.print("Enter your choice: ");
                choice = sc.nextInt();
                sc.nextLine();
                String query = "";
                
                if (choice == 1) {
                    System.out.print("Enter the Train Number: ");
                    query = sc.nextLine() + " ";
                    System.out.print("Enter the Date: ");
                    query += sc.nextLine() + " ";
                    System.out.print("Enter the Number of AC Coaches: ");
                    query += sc.nextLine() + " ";
                    System.out.print("Enter the Number of SL Coaches: ");
                    query += sc.nextLine();
                    pWriter.println(query);
                    String result = bInput.readLine();
                    if (result.length() == 0) {
                        result = "Please Check the Input";
                    }
                    System.out.println(result);
                }
                else if (choice == 2) {
                    System.out.print("Enter the Number of Passengers: ");
                    query = sc.nextLine();
                    int numberOfPassengers = Integer.valueOf(query);
                    query += " ";
                    for (int cnt = 0; cnt < numberOfPassengers-1; cnt++) {
                        System.out.print("Enter Passenger Name: ");
                        query += sc.nextLine() + ", ";
                    }
                    System.out.print("Enter Passenger Name: ");
                    query += sc.nextLine() + " ";
                    System.out.print("Enter the Train Number: ");
                    query += sc.nextLine() + " ";
                    System.out.print("Enter the Date: ");
                    query += sc.nextLine() + " ";
                    System.out.print("Enter the Preference (AC/SL): ");
                    query += sc.nextLine();
                    printWriter.println(query);
                    String result = "";
                    for (int i=0; i<numberOfPassengers; i++) {
                        result = bufferedInput.readLine();
                        if (result.contains("(") == false) {
                            System.out.println("Please Check Input Parameters");
                            break;
                        }
                        System.out.println(result);
                    }
                }
                else if (choice == 3) {
                    System.out.print("Enter the PNR: ");
                    query = sc.nextLine();
                    pWriter.println(query);
                    String result = "";
                    while (true) {
                        result = bInput.readLine();
                        if (result.contains("#")) break;
                        System.out.println(result);
                    }
                }
                else {
                    break;
                }
                System.out.println();
            }   
            
            sc.close();
            printWriter.println("#");
            pWriter.println("#");
            bookSocketConnection.close();
            addTrainSocketConnection.close();
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

public class interactiveClient
{
    public static void main(String args[])throws IOException
    {
        /**************************/
        int firstLevelThreads = 1 ;   // Indicate no of users 
        /**************************/
        // Creating a thread pool
        
        ExecutorService executorService = Executors.newFixedThreadPool(firstLevelThreads);
        
        for(int i = 0; i < firstLevelThreads; i++)
        {
            Runnable interactive = new interactive();    //  Pass arg, if any to constructor sendQuery(arg)
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