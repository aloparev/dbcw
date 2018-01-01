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
        System.out.println("Opened database successfully");
    }

    /**
     * Ausgabe aller Datensaetze
     *
     * @param variante : 1-nach Lauf kommt menu bzw. 0-menu kommt nicht
     * @throws SQLException
     */
    static void printAll(int variante) throws SQLException {
        resultSet = statement.executeQuery("select * from users");
        metaData = resultSet.getMetaData();

        int num_columns = metaData.getColumnCount();
        //System.out.println("Anzahl Splaten: " + num_columns);

        System.out.println("\nUID-------------------------Name----------------email----------------------------------");
        while (resultSet.next()) {
            for (int i = 1; i <= num_columns; i++) {
                System.out.printf("%-15s", resultSet.getString(i));
            }
            System.out.print("\n");
        }

        System.out.println("-----------------------------------------------------------------------------------------");
        if (variante == 1) {
            menu();
        }
    }

    /**
     * Datensatz einfuegen
     *
     * @throws SQLException
     */
    public static void add_element() throws SQLException {
        Scanner fragen = new Scanner(System.in);

        System.out.print("\nAutoNr: ");
        while (fragen.hasNextInt() != true) {
            System.out.print(" *** Error: Nur int-Zahlen erlaubt!\n");
            System.out.print("AutoNr: ");
            fragen.nextLine();
        }
        int autonr = fragen.nextInt();
        fragen.nextLine();

        System.out.print("Hersteller: ");
        String hersteller = fragen.nextLine();

        System.out.print("Autotyp: ");
        String autotyp = fragen.nextLine();

        System.out.print("Kilometerstand: ");
        while (fragen.hasNextInt() != true) {
            System.out.print(" *** Error: Nur int-Zahlen erlaubt!\n");
            System.out.print("Kilometerstand: ");
            fragen.nextLine();
        }
        int kilometerstand = fragen.nextInt();
        fragen.nextLine();

        String regex = "[0-9]{4}-[0-9]{2}-[0-9]{2}";
        System.out.print("TUV (YYYY-MM-DD): ");
        String tuv = "datum";
        tuv = fragen.nextLine();
        while (!tuv.matches(regex)) {
            System.out.print(" *** Error: Bitte Format YYYY-MM-DD beachten!\n");
            System.out.print("TUV (YYYY-MM-DD): ");
            tuv = fragen.nextLine();
            tuv = tuv;
        }

        System.out.print("Kennzeichen: ");
        String kennzeichen = fragen.nextLine();


        try {
            statement.executeUpdate("INSERT INTO users " + "VALUES (" + autonr + ", '" + hersteller + "', '" + autotyp + "', " + kilometerstand + ", '" + tuv + "', '" + kennzeichen + "')");
            System.out.print("Done! \n");
        } catch (SQLException ex) {
            System.out.println("\n *** Error: Sie haben falsch Datum geschrieben! Probieren Sie später noch mal!");
        }
        menu();
    }


    /**
     * Datensatz loeschen
     *
     * @throws SQLException
     */
    public static void delete_element() throws SQLException {
        printAll(0);

        Scanner check = new Scanner(System.in);
        System.out.print("\nWelchen Datensatz wollen Sie loeschen (nur autonr eingeben): ");
        while (check.hasNextInt() != true) {
            System.out.print(" *** Error: Nur int-Zahlen erlaubt!\n");
            System.out.print("Autonr: ");
            check.nextLine();
        }
        int autonr = check.nextInt();


        String query = "delete from users where autonr = ?";
        PreparedStatement preparedStmt = connection.prepareStatement(query);
        preparedStmt.setInt(1, autonr);

        // execute the preparedstatement
        preparedStmt.execute();

        System.out.print("Done! \n");
        menu();
    }

    private static void oneAusgabe() throws SQLException {
        if (resultSet.isBeforeFirst()) {
            System.out.println("\n *** Das war schon Anfang! Liste wird wieder auf Ende gesetzt!");
            resultSet.last();
        }

        if (resultSet.isAfterLast()) {
            System.out.println("\n *** Das war schon Ende! Liste wird wieder auf Anfang gesetzt!");
            resultSet.first();
        }
        System.out.println("\n---------------------------------------------------------------------------------------");
        for (int i = 1; i <= 6; i++) {
            System.out.printf("%-15s", resultSet.getString(i));
        }
        System.out.println("\n---------------------------------------------------------------------------------------");
        System.out.print("\n");

        einzeln();
    }

    /**
     * Einzelne Ausgabe (Naviegieren)
     *
     * @throws SQLException
     */
    public static void einzeln() throws SQLException {
        Scanner nav = new Scanner(System.in);
        System.out.print("\n n (next), p (previous), q (quit): ");
        String nv = nav.nextLine();

        switch (nv) {
            case "n":
                resultSet.next();
                oneAusgabe();
                break;
            case "p":
                resultSet.previous();
                oneAusgabe();
                break;
            case "q":
                menu();
                break;
            default:
                System.out.print("\n *** Error: Bitte aufmerksam eingeben! \n");
                einzeln();
        }
    }

    private static void clear() throws SQLException {
        resultSet = statement.executeQuery("select * from users");
        metaData = resultSet.getMetaData();
    }

    /**
     * Menu
     *
     * @throws SQLException
     */
    public static void menu() throws SQLException {
        Scanner menu = new Scanner(System.in);
        System.out.println("\n +++ MENU users +++");
        System.out.println("(1) Ausgabe (komplett) ");
        System.out.println("(2) Datensatz eingebeben");
        System.out.println("(3) Datensatz loeschen");
        System.out.println("(4) Ausgabe einzeln");
        clear();
        System.out.print("Ihre Wahl: ");

        if (menu.hasNextInt()) {
            int wahl = menu.nextInt();

            switch (wahl) {
                case 1:
                    printAll(1);
                    break;
                case 2:
                    add_element();
                    break;
                case 3:
                    delete_element();
                    break;
                case 4:
                    einzeln();
                    break;
                default:
                    System.out.println("\n*** Error: Bitte aufmerksam noch mal eingeben!");
                    menu();
            }
        } else {
            //falls kein int-wert
            System.out.println("\n*** Error: Bitte aufmerksam noch mal eingeben!");
            menu();
        }
    }
}