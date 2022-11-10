import java.sql.*;

public class db_helper {

    public static String addTrain(Connection conn, String trainNo) {
        try {
            conn.beginRequest();
            CallableStatement cstmt = conn.prepareCall("{call add_train(?)}");

            cstmt.setString(1, trainNo);
            cstmt.executeUpdate();

            cstmt.close();
            return String.format("Train: %s added", trainNo);

        } catch (Exception e) {

            return e.getMessage();

        }
    }

    // IN train_number character varying, IN dep_date date, IN ac_coaches integer,
    // IN sl_coaches integer
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

    public static String bookTickets(
            Connection conn,
            String trainNo,
            String date,
            String pref,
            String[] names,
            Integer[] ages,
            Character[] genders) {
        try {
            conn.beginRequest();

            Statement stmt = conn.createStatement();
            stmt.executeUpdate("begin;");
            stmt.executeUpdate("set transaction isolation level serializable;");

            CallableStatement cstmt = conn.prepareCall("{call book_tickets(?, ?, ?, ?, ?, ?, ?)}");

            cstmt.setString(1, trainNo);
            cstmt.setDate(2, java.sql.Date.valueOf(date));
            cstmt.setString(3, pref);
            cstmt.setArray(4, conn.createArrayOf("text", names));
            cstmt.setArray(5, conn.createArrayOf("INTEGER", ages));
            cstmt.setArray(6, conn.createArrayOf("text", genders));
            cstmt.registerOutParameter(7, java.sql.Types.VARCHAR);

            cstmt.executeUpdate();
            stmt.executeUpdate("commit;");

            String result = cstmt.getString(7);

            cstmt.close();
            return result;

        } catch (Exception e) {
            return e.getMessage();

        }
    }

    public static void main(String[] args) {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/train_system",
                    "postgres", "****");

            String[] names = { "Bonnie", "Clyde" };
            Integer[] ages = { 20, 20 };
            Character[] genders = { 'F', 'M' };

            // conn.setTransactionIsolation(8);
            System.out.println(addTrain(conn, "115"));
            System.out.println(releaseTrain(conn, "115", "2022-12-12", 3, 5));
            System.out.println(bookTickets(conn, "115", "2022-12-12", "SL", names, ages, genders));
            conn.close();

        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }
}
