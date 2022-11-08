// package server_files;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Date;
import java.util.Arrays;

public class temp {
    public static String insertTrain(String trainNo) {
        String qstring = String.format(
                "begin;" + 
                "set transaction isolation level serializable;" +
                "call add_train('%s');" +
                "commit;",
                trainNo);

        return qstring;
    }

    public static <O> String stringify(O[] input) {
        String ans = "{";
        for (O element : input) {
            if (ans != "{") {
                ans += ",";
            }
            ans += element.toString();
        }
        ans += "}";
        return ans;
    }

    public static String bookRSTickets(Statement stmt, String trainNo, String date, String pref, String[] names, Integer[] ages, Character[] genders) {
        try {
            stmt.executeUpdate("begin;");
            stmt.executeUpdate("set transaction isolation level serializable;");

            String qstring = String.format(
                    "select book_tickets ('%s', '%s', '%s', '%s', '%s', '%s');",
                    trainNo, date, pref, stringify(names), stringify(ages), stringify(genders));
            ResultSet rs = stmt.executeQuery(qstring);

            rs.next();
            String pnr = rs.getString("book_tickets");
            
            stmt.executeUpdate("commit;");
            return pnr;
        } catch (Exception e) {
            // System.err.println(e.getClass().getName() + ": " + e.getMessage());
            // System.exit(0);
            // String error = e.get
            return e.getMessage();
        }
        // return rs;
    }

    public static String bookTickets(String trainNo, String date, String pref, String[] names, Integer[] ages, Character[] genders) {
        
        String qstring = String.format(
                "begin;" + 
                "set transaction isolation level serializable;" +
                "select book_tickets ('%s', '%s', '%s', '%s', '%s', '%s');",
                "commit;",
                trainNo, date, pref, stringify(names), stringify(ages), stringify(genders));

        return qstring;
    }

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;

        String[] names = { "Marty", "Emmett" };
        Integer[] ages = { 20, 22 };
        Character[] genders = { 'M', 'M' };
        String d = "2022-12-12";
        // String query = insertTrain("1115");
        // String query2 = insertTrain("1116");
        String query = bookTickets("111", d, "AC", names, ages, genders);

        // System.out.println(query);

        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/train_system",
                    "postgres", "admin");

            stmt = conn.createStatement();
            String sql = query;

            long startTime = System.nanoTime();
            String pnr = bookRSTickets(stmt, "111", d, "AC", names, ages, genders);
            long endTime = System.nanoTime();
            System.out.println(endTime - startTime);
            System.out.println(pnr);
            // conn.commit();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        // System.out.println("Opened database successfully");
    }
}
