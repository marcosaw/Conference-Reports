/*
 * El código contenido en este archivo, así como todos
 * los archivos compilados, son propiedad de
 * Marcos Avila Weingartshofer
 */

package org.util;

import java.sql.Connection;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author toshiba2
 */
public class UConnectionTest {
    public UConnectionTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getConnection method, of class UConnection.
     */
    @Test
    public void testGetConnection() {
        System.out.println("getConnection");
        UConnection instance = null;
        Connection expResult = null;
        Connection result = instance.getConnection();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of closeConnection method, of class UConnection.
     */
    @Test
    public void testCloseConnection() {
        System.out.println("closeConnection");
        UConnection instance = null;
        instance.closeConnection();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
