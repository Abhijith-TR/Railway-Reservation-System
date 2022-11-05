package server_files;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Date;
import java.util.Arrays;

public class temp {

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

    public static String bookTickets(String trainNo, String date, String[] names, Integer[] ages, Character[] genders) {
        String qstring = String.format(
                "call book_tickets ('%s', '%s', '%s', '%s', '%s', '%s');",
                trainNo, date, "AC", stringify(names), stringify(ages), stringify(genders));

        return qstring;
    }

    public static void main(String[] args) {
        Connection c = null;
        Statement stmt = null;

        String[] names = { "Marty", "Emmett" };
        Integer[] ages = { 20, 22 };
        Character[] genders = { 'M', 'M' };
        String d = "2022-12-12";
        String query = bookTickets("111", d, names, ages, genders);

        System.out.println(query);

        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/train_system",
                "postgres", "****"
            );

            stmt = c.createStatement();
            String sql = query;

            stmt.executeUpdate(sql);
            stmt.close();
            c.close();

            System.out.println("Insertion successfull?");

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }
}
