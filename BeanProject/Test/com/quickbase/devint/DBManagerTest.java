/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.quickbase.devint;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author david-user
 */
public class DBManagerTest {
    
    DBManager instance = null;
    
    public DBManagerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {

    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
      this.instance = DBManager.createDataBaseConnection();
    }
    
    @After
    public void tearDown() {
        this.instance.close();
    }

    /**
     * Test of open method, of class DBManager.
     */
    @Test
    public void testOpen() {
        System.out.println("open");
        this.instance.open();
        assertEquals(this.instance.isOpen(), true);
    }

    /**
     * Test of isOpen method, of class DBManager.
     */
    @Test
    public void testIsOpen() {
        System.out.println("isOpen");
        this.instance.open();
        assertEquals(this.instance.isOpen(), true);
    }

    /**
     * Test of selectCountry method, of class DBManager.
     */
    @Test
    public void testSelectCountry() {
        System.out.println("selectCountry");
        this.instance.open();
        this.instance.selectCountry("India");
        assertEquals(instance.isDataPresentForSelection(), true);
    }

    @Test
    public void testSelectNonExistentCountry() {
        System.out.println("selectCountry non existent country");
        this.instance.open();
        this.instance.selectCountry("Kasdsada");
        assertEquals(instance.isDataPresentForSelection(), false);
    }

    /**
     * Test of getPopulationOfSelection method, of class DBManager.
     */
    @Test
    public void testGetPopulationOfSelection() {
        System.out.println("getPopulationOfSelection");
        instance.open();
        instance.selectCountry("India");
        Integer result = instance.getPopulationOfSelection();
        assertEquals(result > 300000000, true);
    }
    
}
