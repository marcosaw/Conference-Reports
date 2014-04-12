/*
 * El código contenido en este archivo, así como todos
 * los archivos compilados, son propiedad de
 * Marcos Avila Weingartshofer
 */
package org.facade.imple;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import org.facade.Facade;
import org.model.ExcelPrinter;
import org.util.UConnection;

/**
 *
 * @author Marcos Avila Weingartshofer
 */
public class FacadeImple implements Facade {

    private Connection con;
    private UConnection uc;
    private Statement stm;
    private ResultSet rs;
    private ExcelPrinter ep;
    

    @Override
    public void writeReportToXLSFile(String sheetName, String sql, JTextArea jtArea) {
        try {
            rs = stm.executeQuery(sql);
            ep.printDatos(rs, jtArea);
        } catch (SQLException ex) {
            Logger.getLogger(FacadeImple.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    @Override
    public void end() {
        uc.closeConnection();
    }

    @Override
    public void commit() {
        ep.commitToArchive();
    }
    public FacadeImple(String dbType, String url, String port, String dbName, String user, String pass, ExcelPrinter ep) {
        this.ep = ep;
        if (con == null) {
            uc = new UConnection(dbType, url, port, dbName, user, pass);
            con = uc.getConnection();
            try {
                stm = con.createStatement();
            } catch(SQLException sqle) {
                System.out.println("Error when create statement object");
            }
        }
    }

    @Override
    public void writeCell(String value, int leftOffset) {
        ep.printCell(value, leftOffset);
    }
}
