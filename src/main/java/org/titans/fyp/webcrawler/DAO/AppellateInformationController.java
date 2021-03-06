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
package org.titans.fyp.webcrawler.DAO;

import org.titans.fyp.webcrawler.database.DBConnection;
import org.titans.fyp.webcrawler.database.DBHandler;
import org.titans.fyp.webcrawler.models.AppellateInformation;

import java.sql.SQLException;

public class AppellateInformationController {
    
    //add Appellate information to database
    public static int addAppellateInformation(AppellateInformation appellateInformation) throws
            SQLException, ClassNotFoundException {
        
        String sql = "INSERT INTO appellate_information (case_id, type, date) VALUES('"
                + appellateInformation.getCaseId()+ "','" + appellateInformation.getType()+ "','" +
                appellateInformation.getDate()+ "')";

        int res = DBHandler.setData(sql, DBConnection.getConnectionToDB());
        
        return res;
    }
    
}
