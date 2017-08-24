package com.quickbase.devint;

import java.sql.*;
import org.apache.commons.lang3.StringUtils;

public class DBManagerImpl implements DBManager {
    private Connection sqlConnection = null;
    private String selectedCountryCode = "";
    
    @Override
    public boolean isOpen() {
        return this.sqlConnection != null;
    }
    
    public void close() {
        if (isOpen()) {
            try {
                this.sqlConnection.close();
            } catch (Exception e) {
            
            } finally {
                this.sqlConnection = null;
            }
        }
    }
    
    public void open() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.sqlConnection = DriverManager.getConnection("jdbc:sqlite:resources/data/citystatecountry.db");
            System.out.println("Opened database successfully");
        } catch (ClassNotFoundException cnf) {
            System.out.println("could not load driver");
        } catch (SQLException sqle) {
            System.out.println("sql exception:" + sqle.getStackTrace());
        }
    }
    
    public boolean requiresRefresh() 
    {
        return false; /* to be implemented otherwise?? */
    }

    private ResultSet preformDetailedQuery(String operation, String tableName, String columnName, String pattern) {
        String query = String.format("SELECT %s FROM %s WHERE trim(%s) LIKE %s",
                operation, tableName, columnName, pattern
        );
        try {
            Statement activeStmt = this.sqlConnection.createStatement();
            return activeStmt.executeQuery(query);
        } catch (SQLException sqle) {
            System.out.println("sql exception:" + sqle.getStackTrace());
            return null;
        }
    }

    private ResultSet performQuery(String tableName, String columnName, String pattern)
    {
        return this.preformDetailedQuery("*", tableName, columnName, pattern);
    }
    
    private String getCountryCode(String countryName)
    {
        String countryCode = "";
        try {
            ResultSet countryLookUp = this.performQuery("Country", "CountryName", 
                    String.format("'%s'", countryName));
            if (countryLookUp != null) {
                countryCode = Integer.toString(countryLookUp.getInt(2));
            }
        } catch (SQLException sqle) {
            System.out.println("sql exception:" + sqle.getStackTrace());
        }
        return countryCode;
    }
    
    public void selectCountry(String countryName)
    {
         assert (this.isOpen());
         this.selectedCountryCode = getCountryCode(countryName);
    }
    
    public boolean isDataPresentForSelection()
    {
        return !StringUtils.isEmpty(this.selectedCountryCode);
    }
    
    //TODO: Add a method (signature of your choosing) to query the db for population data by country
    public Integer getPopulationOfSelection() {
        assert (this.isOpen());
        Integer population = -1;
        try {
            String countryCode = this.selectedCountryCode;
            assert (!StringUtils.isEmpty(countryCode));
            ResultSet stateLookUp = this.performQuery("State", "CountryId", countryCode);
            population = 0;
            while (stateLookUp != null && stateLookUp.next()) {
                String stateId = stateLookUp.getString(1);
                ResultSet populationCalc = this.preformDetailedQuery(
                        "SUM(POPULATION)", "City", "StateId", stateId);
                Integer statePopulation = populationCalc.getInt(1);
                population = population + statePopulation;
            }
        } catch (SQLException sqle) {
            System.out.println("sql exception:" + sqle.getStackTrace());
        }
        return population;
    }
}
