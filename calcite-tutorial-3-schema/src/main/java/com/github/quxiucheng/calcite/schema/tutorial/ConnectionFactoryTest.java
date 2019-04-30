package com.github.quxiucheng.calcite.schema.tutorial;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author quxiucheng
 * @date 2019-04-28 17:06:00
 */
public class ConnectionFactoryTest {

    public void createSchemaFactoryConnection() throws SQLException {
        Properties info = new Properties();
        info.put("lex", "mysql");
        String model = "calcite-tutorial-3-schema/src/main/resources/model.json";
        info.put("model", model);
        DriverManager.getConnection("jdbc:calcite:", info);
    }

    public void createTableFactoryConnection() throws SQLException {
        Properties info = new Properties();
        info.put("lex", "mysql");
        String model = "calcite-tutorial-3-schema/src/main/resources/model.yaml";
        info.put("model", model);
        DriverManager.getConnection("jdbc:calcite:", info);
    }

    public static void main(String[] args) throws SQLException {
        new ConnectionFactoryTest().createTableFactoryConnection();

    }
}
