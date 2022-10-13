package com.idera.xray.tutorials;

import app.getxray.xray.junit.customjunitxml.XrayTestReporter;
import app.getxray.xray.junit.customjunitxml.XrayTestReporterParameterResolver;
import app.getxray.xray.junit.customjunitxml.annotations.XrayTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(XrayTestReporterParameterResolver.class)
public class DBCoreConnectionTests {

    // Connection object
    static Connection con = null;
    // Statement object
    private static Statement stmt;
    // Constant for Database URL
    public static String DB_URL = "jdbc:postgresql://10.230.47.4:5432/azdev02db";
    //Database Username
    public static String DB_USER = "core@azdev-db01";
    // Database Password
    public static String DB_PASSWORD = "core";
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
    @XrayTest(key = "EINSTPIN-696")
    public void  VerifywinningtransactionInDB(XrayTestReporter xrayReporter) throws SQLException {
        xrayReporter.addComment("Creating statment");
        String query = "select * from core.try_winnings_transaction";
        stmt = con.createStatement();
        xrayReporter.addComment("Executing statment");
        ResultSet rs = stmt.executeQuery(query);
        assertEquals(rs.next(), true);
        xrayReporter.addComment("----- Last Row print start -----");
        while(rs.next()) {
            if (rs.isLast()){
                xrayReporter.addComment(rs.getString(1) + " " + rs.getString(2) + " " + rs.getString(3) + " " + rs.getString(4) + " " + rs.getString(5) + " " + rs.getString(6) + " " + rs.getString(7) + " " + rs.getString(8) + " " + rs.getString(9) + " " + rs.getString(10) + " " + rs.getString(11) + " " + rs.getString(12) + " " + rs.getString(13) + " " + rs.getString(14) + " " + rs.getString(15) + " " + rs.getString(16) + " " + rs.getString(17) + " " + rs.getString(18) + " " + rs.getString(19) + " " + rs.getString(20) + " " + rs.getString(21) + " " + rs.getString(22));
                System.out.print(rs.getString(1));
                System.out.print(" " + rs.getString(2));
                System.out.print(" " + rs.getString(3));
                System.out.print(" " + rs.getString(5));
                System.out.print(" " + rs.getString(6));
                System.out.print(" " + rs.getString(7));
                System.out.print(" " + rs.getString(8));
                System.out.print(" " + rs.getString(9));
                System.out.print(" " + rs.getString(10));
                System.out.print(" " + rs.getString(11));
                System.out.print(" " + rs.getString(12));
                System.out.print(" " + rs.getString(13));
                System.out.print(" " + rs.getString(14));
                System.out.print(" " + rs.getString(15));
                System.out.print(" " + rs.getString(16));
                System.out.print(" " + rs.getString(17));
                System.out.print(" " + rs.getString(18));
                System.out.print(" " + rs.getString(19));
                System.out.print(" " + rs.getString(20));
                System.out.print(" " + rs.getString(21));
                System.out.println(" " + rs.getString(22));
            }
        }
        xrayReporter.addComment("----- Last Row printed successfully -----");
    }
}
