/*
 * El código contenido en este archivo, así como todos
 * los archivos compilados, son propiedad de
 * Marcos Avila Weingartshofer
 */

package org.facade;

import javax.swing.JTextArea;
//import org.model.ExcelPrinter;

/**
 *
 * @author toshiba2
 */
public interface Facade {
        
    public void writeReportToXLSFile(String sheetName, String sql, JTextArea statusWriter );
    
    public void writeCell(String value, int leftOffset);
    
    public void commit();
    
    public void end();
    
    
}
