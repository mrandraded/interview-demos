/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.quickbase.devint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

@RunWith(Suite.class)
@Suite.SuiteClasses({com.quickbase.devint.DBManagerTest.class})
public class PopulationServiceTests {
    /**
     * Test of open method, of class DBManager.
     */
    @Test
    public void testBasicDataSanity() {
        PopulationService popSvc = new PopulationService();
        List<Pair<String, Integer>> populationData = popSvc.getPopulationData();       
        
        for (Pair<String, Integer> el : populationData) {
            Integer population = el.getRight();
            assertTrue(population > 0);
        }
    }
  
    @Test
    public void testDataSplice() {
        DBManager sqlDb = DBManager.createDataBaseConnection();

        IStatService iStatDb = new ConcreteStatService();
        List<Pair<String, Integer>> iStatePopulationData = iStatDb.GetCountryPopulations();

        PopulationService popSvc = new PopulationService();
        List<Pair<String, Integer>> splicedPopulationData = popSvc.getPopulationData();
        
        assertEquals(splicedPopulationData.size(), iStatePopulationData.size());

        /* Iterate through the spliced data and ensure that the data was
           obtained from the correct src */
        for (int i = 0; i < splicedPopulationData.size(); i++) {
            Pair<String, Integer> countryElement = splicedPopulationData.get(i);
            String countryName = countryElement.getLeft();
            Integer population = countryElement.getRight();
            sqlDb.selectCountry(countryName);
            if (sqlDb.isDataPresentForSelection()) {
                assertEquals(sqlDb.getPopulationOfSelection(), population);
            } else {
                Integer iStatPopulation = iStatePopulationData.get(i).getRight();
                assertEquals(iStatPopulation, population);
            }
        }
    }
    
    
}
