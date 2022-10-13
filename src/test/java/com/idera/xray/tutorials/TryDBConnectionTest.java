package com.idera.xray.tutorials;

import app.getxray.xray.junit.customjunitxml.XrayTestReporter;
import app.getxray.xray.junit.customjunitxml.XrayTestReporterParameterResolver;
import app.getxray.xray.junit.customjunitxml.annotations.XrayTest;
import com.idera.xray.tutorials.RepositoryParser;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(XrayTestReporterParameterResolver.class)
public class TryDBConnectionTest {
    // Connection object
    static Connection con = null;
    // Statement object
    private static Statement stmt;
    // Constant for Database URL
    public static String DB_URL = "jdbc:postgresql://10.230.47.4:5432/azdev02db";
    //Database Username
    public static String DB_USER = "pas@azdev-db01";
    // Database Password
    public static String DB_PASSWORD = "pas";
    // Repository
    RepositoryParser repo;

    @BeforeEach
    public void setup() {
        try{
        // Get connection to DB
        con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        // Statement object to send the SQL statement to the Database
        stmt = con.createStatement();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        repo = new RepositoryParser("./src/configs/object.properties");
    }

    @AfterEach
    public void tearDown() throws Exception {
//        driver.quit();
//        driver = null;
        repo = null;
        con.close();
    }

    @Test
    @XrayTest(key = "EINSTPIN-692")
    public void successLogin(XrayTestReporter xrayReporter) throws SQLException {
        xrayReporter.addComment("Creating statment");
        String query = "select * from pas.pocket_type";
        stmt = con.createStatement();
        xrayReporter.addComment("Executing statment");
        ResultSet rs = stmt.executeQuery(query);
        while(rs.next()) {
            assertEquals(rs.getString(1), repo.getBy("testdbconnection.azdev02db.pas"));
        }
        xrayReporter.addComment("Connection success");
    }
}
