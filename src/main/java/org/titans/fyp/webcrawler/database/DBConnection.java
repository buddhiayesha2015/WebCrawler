/*******************************************************************************
 * Copyright 2016 Titans
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 ******************************************************************************/
package org.titans.fyp.webcrawler.database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection establishment happens here.
 */
public class DBConnection {
    private static DBConnection dbConnection;
    private Connection connection;

    private  DBConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        connection =  (Connection) DriverManager.getConnection(
                "jdbc:mysql://166.62.27.168/oblie?useUnicode=true&characterEncoding=UTF-8"
                //database access credentials
                ,"fyp","nikonD7100");
    }

    public Connection getConnection(){
        return connection;
    }

    public static DBConnection getDBConnection() throws ClassNotFoundException, SQLException{
        if(dbConnection == null){
            dbConnection = new DBConnection(); //make new database
        }
        return dbConnection;
    }

    public static Connection getConnectionToDB() throws ClassNotFoundException, SQLException{
        return getDBConnection().getConnection();
    }
}
