package ru.sorokinkv;

import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

import static ru.sorokinkv.ServerConst.*;



//------------Block for testing ------------------


/*

class TestDatabaseMain {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        System.out.println("Starting");
       SQLHandlerDB sqlHandlerDB = new SQLHandlerDB();
        sqlHandlerDB.connect();
//        sqlHandlerDB.createDatabase();
//        sqlHandlerDB.addNewUser("user1","123","User1");
//        sqlHandlerDB.addNewUser("user2","123","User2");
 //       sqlHandlerDB.addNewUser("user3","123","User3");
 //      sqlHandlerDB.deleteUser("User1");
//       sqlHandlerDB.deleteUser("User2");
//        sqlHandlerDB.deleteUser("123");
//        System.out.println(sqlHandlerDB.getNickByLoginPass("user2","123"));
 //      System.out.println(sqlHandlerDB.getIdByNick("User3"));
        System.out.println(sqlHandlerDB.getAllUserInfo());
        System.out.println("Stopped");
        sqlHandlerDB.disconnect();
    }

}

*/




public class SQLHandlerDB {
    private static Connection conn = null;
    private static Statement stmt;
    private static String sql;

    public static void connect() throws ClassNotFoundException, SQLException {
        System.out.println("First");

        System.out.println("Starting...");
            System.out.println("Loading Class org.postgresql.Driver");
            Class.forName("org.postgresql.Driver");
            System.out.println("Loading org.postgresql.Driver Successful");
            String url = String.format("jdbc:postgresql://%s:%s/%s",DB_SERVER_URL,DB_PORT, DB_USERS_NAME);
            Properties props = new Properties();
            props.setProperty("user", DB_LOGIN);
            props.setProperty("password", DB_PASSWORD);
            props.setProperty("ssl", "false");
            conn = DriverManager.getConnection(url, props);
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            System.out.println("-- Opened database successfully");
    }

    public static void createDatabase() {
        try {
            //-------------- CREATE TABLE ---------------
            stmt = conn.createStatement();
            sql = "CREATE TABLE USERS " +
                    "(ID SERIAL PRIMARY KEY    ," +
                    " LOGIN         TEXT    NOT NULL, " +
                    " PASSWORD      INT     NOT NULL, " +
                    " NICK          TEXT    NOT NULL)";
            stmt.executeUpdate(sql);
            stmt.close();
            conn.commit();
            System.out.println("-- Table created successfully");
        } catch (Exception e) {
            System.out.println("Создание таблицы невозможно.");
            e.printStackTrace();
         }
    }


    public static boolean searchLoginUser(String login) {
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM USERS;");
            while (rs.next()) {
                String  name = rs.getString("login");
                System.out.println(String.format("LOGIN=%s", name)); //for test
                if (name.equals(login)) {
                    System.out.println(String.format("User %s already exists in the database", login));
                    return true;
                }
            }
            rs.close();
            stmt.close();
            conn.commit();
            System.out.println("-- Searching ID done successfully");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return false;
    }

    //--------------- INSERT ROWS ---------------
    public static boolean addNewUser(String nick, String login, String pass) throws SQLException {
        try {
            if(searchLoginUser(login)) {
                String sql;
                int passHash = pass.hashCode();
                   System.out.println("SQLHandler_addNewUser :" + nick + " " + login + " " + passHash);
               sql = (String.format("INSERT INTO users (nick,login,password) VALUES ('%s', %s, '%s');",nick, login, passHash));
                stmt = conn.createStatement();
                stmt.executeUpdate(sql);
                stmt.close();
                conn.commit();
                System.out.println("-- User added");
           }else{
                System.out.println("-- User already exists in the database");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            stmt.close();
            conn.commit();
            return false;
        }
        return true;
    }

    public static boolean deleteUser(String login) throws SQLException {
        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM USERS;");
            sql = (String.format("DELETE from USERS where LOGIN = '%s';", login));
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            conn.commit();
            stmt.close();
            System.out.println("-- Operation DELETE done successfully");
            conn.close();

    } catch (SQLException e) {
        e.printStackTrace();
        stmt.close();
        conn.commit();
        return false;
    }
        return true;
}

            //--------------- SELECT DATA ------------------
  public static ArrayList<String> getAllUserInfo() {
      ArrayList arrayList = new ArrayList();
      try {

          stmt = conn.createStatement();
          ResultSet rs = stmt.executeQuery("SELECT * FROM USERS;");
          while (rs.next()) {
              int id = rs.getInt("id");
              String login = rs.getString("login");
              String nick = rs.getString("nick");
              String fullUserInfo = String.valueOf(id)+" "+login+" "+nick+" ;";
              arrayList.add(fullUserInfo);
          }
          rs.close();
          stmt.close();
          conn.commit();
          System.out.println("-- Operation SELECT done successfully");


      } catch (Exception e) {
          e.printStackTrace();
          System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
     return arrayList;
    }

    public static String getNickByLoginPass (String login, String pass) {
        String fullUserInfo = "";
        try {
                stmt = conn.createStatement();
                int passHash = pass.hashCode();
                ResultSet rs = stmt.executeQuery("SELECT * FROM USERS;");
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String  loginDb = rs.getString("login");
                    int passHashDb = rs.getInt("password");
                    String nick = rs.getString("nick");
                    fullUserInfo = String.valueOf(id)+" "+login+" "+nick+" ;";

                    if (login.equals(loginDb)) {
                        if(passHashDb == passHash) {
                            System.out.println(String.format("User %s found", login));
                            return fullUserInfo;
                        }else {
                            System.out.println("Incorrect password");
                            fullUserInfo = "";
                            return null;
                        }
                    }
                }
                rs.close();
                stmt.close();
                conn.commit();
                System.out.println("-- Searching user info done successfully");
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }
        return null;
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

    public static void disconnect() {
        try {
            conn.close();
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

