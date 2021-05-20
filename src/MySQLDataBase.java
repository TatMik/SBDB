import java.sql.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

public class MySQLDataBase {
    public static final String DRIVER_NAME = "com.mysql.cj.jdbc.Driver";
    public static final String CONN_STRING = "jdbc:mysql://localhost:3306/?user=root&password=Root&useLegacyDatetimeCode=false&serverTimezone=UTC";
    public Enumeration<Driver> drivers = DriverManager.getDrivers();
    public Connection conn = null;
    public ArrayList<String> list = new ArrayList<>();

    //полкючение к MySQL server при создании объекта через JDBC коннектор
    public MySQLDataBase() {
        while (drivers.hasMoreElements()) {
            System.out.println(drivers.nextElement());
        }
        try {
            Class.forName(DRIVER_NAME);
        } catch (ClassNotFoundException e) {
            System.out.println("Cannot load Driver for MySQL!");
            return;
        }
        try {
            conn = DriverManager.getConnection(CONN_STRING);
        } catch (SQLException throwables) {
            System.out.println("Cannot open connection to DB!" + throwables.getMessage());
            return;
        }
    }

    //Создание быза данных с таблицами: Divisions, Employees, Contacts
    public void createDB() {
        Statement st = null;
        try {
            st = conn.createStatement();
            st.executeUpdate("CREATE DATABASE TestDB");
            st.executeUpdate("USE TestDB");
            st.executeUpdate("CREATE TABLE Divisions (ID int(4) NOT NULL AUTO_INCREMENT, DivName varchar(32), PRIMARY KEY (ID)) ENGINE=INNODB");
            // таблица Employees связана с Divisions через ключ DivID
            st.executeUpdate("CREATE TABLE Employees (ID int(4) NOT NULL AUTO_INCREMENT, Fullname varchar(32), DivID int(4), PRIMARY KEY (ID), FOREIGN KEY (DivID) REFERENCES Divisions(ID) ON DELETE CASCADE) ENGINE=INNODB");
            // таблица Contacts связана с Employees через ключ EmployeeID
            st.executeUpdate("CREATE TABLE Contacts (ID int(4) NOT NULL AUTO_INCREMENT, EmployeeID int(4), Tellnum int(11), Email varchar(32), Address varchar(32), PRIMARY KEY (ID), FOREIGN KEY (EmployeeID) REFERENCES Employees(ID) ON DELETE CASCADE) ENGINE=INNODB");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    //Добавление новых подразделений в таблицу Divisions
    public void insertIntoDivision(String name){
        Statement st = null;
        try {
            st = conn.createStatement();
            st.executeUpdate("USE TestDB");
            st.executeUpdate("INSERT INTO Divisions (DivName) VALUES ('"+name+"')");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    //Добавление новых сотрудников в таблицу Employees
    public void insertIntoEmployees(String name, int divID){
        Statement st = null;
        try {
            st = conn.createStatement();
            st.executeUpdate("USE TestDB");
            st.executeUpdate("INSERT INTO Employees (Fullname, DivID) VALUES ('"+name+"', "+divID+")");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    //Добавление контактов сотрудников в таблицу Contacts
    public void insertIntoContacts(int employeeID,int tellNum, String email, String address ){
        Statement st = null;
        try {
            st = conn.createStatement();
            st.executeUpdate("USE TestDB");
            st.executeUpdate("INSERT INTO Contacts (EmployeeID, Tellnum, Email, Address) VALUES ("+employeeID+", "+tellNum+", '"+email+"', '"+address+"')");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    //Изменение данных в таблице
    public void updateTable(String tableName, String columnName, String value, int id){
        Statement st = null;
        try {
            st = conn.createStatement();
            st.executeUpdate("USE TestDB");
            st.executeUpdate("UPDATE "+tableName+" SET "+columnName+" = '"+value+"' WHERE ID = "+id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    //Удаление записей из таблицы
    public void deleteFromTable(String name, int id){
        Statement st = null;
        try {
            st = conn.createStatement();
            st.executeUpdate("USE TestDB");
            st.executeUpdate("DELETE FROM "+name+" WHERE ID = "+id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    //поиск и получение данных в виде списка из таблицы по условию
    public ArrayList getFromTable(String search, String tablename, String where, String value){
        ArrayList<String> list = new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareStatement("Select ? FROM ? WHERE ? = ?");
            ps.setString(1, search);
            ps.setString(2, tablename);
            ps.setString(3, where);
            ps.setString(4, value);
            ResultSet rs = ps.executeQuery();
            int i = 0;
            while (rs.next()){
                list.add(rs.getString(i));
                i++;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }

    //получение всех данных их таблицы
    public HashMap showTables(String tableName){
        HashMap<String,ArrayList<String>> tabledata = new HashMap<>();
        try {
            PreparedStatement ps = conn.prepareStatement("Select * FROM ?");
            ps.setString(1, tableName);
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            String name;
            while (rs.next()) {
                for (int i = 1; i < rsmd.getColumnCount() + 1; i++) {
                    name = rsmd.getColumnName(i);
                    if (!tabledata.containsKey(name)){
                        tabledata.put(name,new ArrayList<>());
                    }
                    tabledata.get(name).add(rs.getString(name));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return tabledata;
    }

}
