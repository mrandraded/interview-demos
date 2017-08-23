package com.quickbase.devint;

import com.quickbase.devint.DBManager;
import com.quickbase.devint.IStatService;

import java.util.List;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class PopulationService {
    List<Pair<String, Integer>> populationData = null;
    IStatService iStatDb = null;
    DBManager dbConnection = null;

    private void initializeConnectionsIfNeeded() 
    {
        if (iStatDb == null) {
            iStatDb = IStatService.getService();
        }

        if (dbConnection == null) {
            dbConnection = DBManager.createDataBaseConnection();
        }
        if (!dbConnection.isOpen()) {
            dbConnection.open();
        }

    }
    
    private void constructPopulationData() {
        initializeConnectionsIfNeeded();
        
        populationData = iStatDb.GetCountryPopulations();

        try {
            assert(dbConnection.isOpen());

            for (Pair<String, Integer> countryElement : populationData) {
                String country = countryElement.getKey();
                dbConnection.selectCountry(country);
                if (dbConnection.isDataPresentForSelection()) {
                    Integer population = dbConnection.getPopulationOfSelection();
                    Pair<String, Integer> newData = new ImmutablePair(country, population);
                    populationData.set(populationData.indexOf(countryElement), newData);
                }
            }
        } finally {
            dbConnection.close();
        }
    }

    /* Public interface */
    
    /// Return cached population data on-demand, building it only
    /// if that data has not been built or a refresh has been requested.
    public  List<Pair<String, Integer>> getPopulationData()
    {
        if (populationData == null) {
            constructPopulationData();
        }
        return populationData;
    }
    
    /// Force an explicit refresh
    public void refresh() {
        if (iStatDb.requiresRefresh() || dbConnection.requiresRefresh()) {
            populationData = null;
        }
        dbConnection.close();
    }
    
    /* Useful for testing */
    public Integer getPopulationForCountry(String countryName)
    {
        getPopulationData();
        for (Pair<String, Integer> countryElement : populationData) {
            String country = countryElement.getKey();
            if (country.equals(countryName)) {
                return countryElement.getRight();
            }
        }    
        return 0;
    }   
}
