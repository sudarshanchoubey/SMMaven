/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.smmaven;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author schoubey
 */
public class SMServerTest {
    
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private PrintStream oldstout;
    public SMServerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of main method, of class SMServer.
     */
    @Test
    public void testMain() throws Exception {
        oldstout = System.out;
        System.setOut(new PrintStream(outContent));
        System.out.println("main");
        String[] args = {"5029"};
        SMServer.main(args);
        // TODO review the generated test code and remove the default call to fail.
        assertEquals("Starting queuer\nStarting sender\n", outContent.toString());
    }
    
}
