package sample;


import org.sqlite.JDBC;

import javax.xml.namespace.QName;
import java.sql.*;
import java.util.Date;


public class DbHandler {
    //Указыаем адрес базы данных вопросов
    private static final String USERDATA = "jdbc:sqlite:users.db";
    //Будем использовать один экземпляр класса
    private static DbHandler instance = null;

    public static synchronized DbHandler getInstance() throws SQLException {
        if (instance == null)
            instance = new DbHandler();
        return instance;
    }
    //здесь будем хранить соединение с БД
    private Connection conn;

    private DbHandler() throws SQLException {
        //Регистрация драйвера, который обеспечит взаимодействие с БД
        DriverManager.registerDriver(new JDBC());
        //Подключаемся к БД
        this.conn = DriverManager.getConnection(USERDATA);
    }

    public synchronized void addUnit(Unit unit) {
        Date date = new Date();
        String request = "INSERT INTO units(name, aesKey, publicKey, privateKey) VALUES('" + unit.getName() +
                "', '" + unit.getAesKey() + "', '" + unit.getPublicKey() + "', '" + unit.getPrivateKey() + "');";
        try (Statement statement = this.conn.createStatement()) {
            statement.execute(request);
        } catch (SQLException e) {
            System.out.println("INSERTION ERROR at " + unit);
        }
    }

    public Unit getUnit(String unit) {
        try (Statement statement = this.conn.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM units WHERE name = '" + unit + "'");
            if (resultSet.next()) {
                return new Unit(resultSet.getString("name"),
                        resultSet.getString("aesKey"),
                        resultSet.getString("publicKey"),
                        resultSet.getString("privateKey"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
