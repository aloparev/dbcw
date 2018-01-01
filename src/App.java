/**
 * Created by <kuzne4eg@gmail.com> on 30.12.2017
 */

import java.sql.*;
import java.util.Scanner;

public class App {
    static String url = "jdbc:postgresql://db.f4.htw-berlin.de:5432/_s0559121__beleg";
    static String username = "_s0559121__beleg_generic";
    static String password = "passwortgen";
    static Statement statement = null;
    static Connection connection = null;
    static ResultSet resultSet = null;
    static ResultSetMetaData metaData = null;

    public static void main(String args[]) {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, username, password);
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            menu();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("db has been opened successfully");
    }

    static void printAll(int mode) throws SQLException {
        resultSet = statement.executeQuery("select * from users");
        metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        System.out.println("\nUID-----------------------Name-----------------email-----------------------------------");
        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                System.out.printf("%-15s", resultSet.getString(i));
            }
            System.out.print("\n");
        }

        System.out.println("-------------------------------------------------------------------------------------------");
        if (mode == 1) {
            menu();
        }
    }

    static void add() throws SQLException {
        Scanner sc = new Scanner(System.in);

        System.out.print("\nuser id: ");
        while (sc.hasNextInt() != true) {
            System.out.print("Error: only integers are allowed\n");
            System.out.print("user id: ");
            sc.nextLine();
        }
        int uid = sc.nextInt();
        sc.nextLine();

        System.out.print("first name: ");
        String firstName = sc.nextLine();

        System.out.print("last name: ");
        String lastName = sc.nextLine();

        System.out.print("email: ");
        String email = sc.nextLine();

        try {
            statement.executeUpdate("INSERT INTO users " + "VALUES (" + uid + ", '" + firstName + "', '" + lastName + "', '" + email + "')");
            System.out.print("Done! \n");
        } catch (SQLException ex) {
            System.out.println("\nError: please try again");
        }
        menu();
    }

    static void del() throws SQLException {
        printAll(0);
        Scanner sc = new Scanner(System.in);
        System.out.print("\nType the data set ID you wish to delete: ");

        while (sc.hasNextInt() != true) {
            System.out.print("Error: only integers are allowed\n");
            System.out.print("user id: ");
            sc.nextLine();
        }
        int uid = sc.nextInt();

        String query = "delete from users where uid = ?";
        PreparedStatement preparedStmt = connection.prepareStatement(query);
        preparedStmt.setInt(1, uid);
        preparedStmt.execute();

        System.out.print("Done! \n");
        menu();
    }

    private static void iterate() throws SQLException {
        if (resultSet.isBeforeFirst()) {
            resultSet.last();
        }

        if (resultSet.isAfterLast()) {
            resultSet.first();
        }

        System.out.println("\n---------------------------------------------------------------------------------------");
        for (int i = 1; i <= 4; i++) {
            System.out.printf("%-15s", resultSet.getString(i));
        }

        System.out.println("\n---------------------------------------------------------------------------------------");
        System.out.print("\n");
        einzeln();
    }

    static void einzeln() throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("\n n (next), p (previous), q (quit): ");
        String line = sc.nextLine();

        switch (line) {
            case "n":
                resultSet.next();
                iterate();
                break;
            case "p":
                resultSet.previous();
                iterate();
                break;
            case "q":
                menu();
                break;
            default:
                System.out.print("\nError: please try again\n");
                einzeln();
        }
    }

    private static void clear() throws SQLException {
        resultSet = statement.executeQuery("select * from users");
        metaData = resultSet.getMetaData();
    }

    static void menu() throws SQLException {
        Scanner sc = new Scanner(System.in);

        System.out.println("\n\tusers table");
        System.out.println("1. print all");
        System.out.println("2. add data set");
        System.out.println("3. del data set");
        System.out.println("4. print");
        System.out.println("________________");
        clear();
        System.out.print(">> ");

        if (sc.hasNextInt()) {
            int temp = sc.nextInt();

            switch (temp) {
                case 1:
                    printAll(1);
                    break;
                case 2:
                    add();
                    break;
                case 3:
                    del();
                    break;
                case 4:
                    einzeln();
                    break;
                default:
                    System.out.println("\nError: please try again");
                    menu();
            }
        } else {
            System.out.println("\nError: please try again");
            menu();
        }
    }
}