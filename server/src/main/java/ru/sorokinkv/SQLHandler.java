package ru.sorokinkv;

import java.sql.*;

public class SQLHandler {
    private static Connection connection;
    private static Statement stmt;


    public static void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:server/data.db");
        stmt = connection.createStatement();

    }

    public static boolean addBaseUsers( String login, String pass, String nick) {
           if(searchLoginInBase(login)!=-1 ) {
               addNewUser(login, pass, nick);
           return true;
           } else return false;
     }

    public static String getNickByLoginPass(String login, String pass) {
        try {
            int passHash = pass.hashCode();
            ResultSet rs = stmt.executeQuery(String.format("SELECT nick FROM users WHERE login = '%s' AND password = '%d';", login, passHash));
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getIdByNick(String nick) {
        try {
            ResultSet rs = stmt.executeQuery(String.format("SELECT id FROM users WHERE nick = '%s';", nick));
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int searchLoginInBase(String login) {
        try {
            ResultSet rs = stmt.executeQuery(String.format("SELECT id FROM users WHERE login = '%s';", login));
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean addNewUser(String login, String pass, String nick) {
        try {
            int passHash = pass.hashCode();
            stmt.executeUpdate(String.format("INSERT INTO users (login, password, nick) VALUES ('%s', '%d', '%s');", login, passHash, nick));
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }



    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
