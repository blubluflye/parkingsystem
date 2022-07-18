package com.parkit.parkingsystem.integration.service;

import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class DataBasePrepareService {

    DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

    public void clearDataBaseEntries(){
        Connection connection = null;
        try{
            connection = dataBaseTestConfig.getConnection();

            //set parking entries to available
            PreparedStatement ps = connection.prepareStatement("update parking set available = true");
            ps.execute();
            ps.close();
            //clear ticket entries;
	        ps = connection.prepareStatement("truncate table ticket");
	        ps.execute();
	        ps.close();
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            dataBaseTestConfig.closeConnection(connection);
        }
    }


}
