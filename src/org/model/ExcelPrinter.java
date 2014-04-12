/*
 * El código contenido en este archivo, así como todos
 * los archivos compilados, son propiedad de
 * Marcos Avila Weingartshofer
 */
package org.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author Marcos Avila Weingartshofer
 */
public class ExcelPrinter {

    private Workbook xlsBook;
    private Sheet hoja;
    private Row row;
    private FileOutputStream fos;
    private File mainFile;

    private int rowOffset = 1;
    private int columnOffset = 1;

    private double[][] prueba;

    public ExcelPrinter() {
        this("DefaultName.xls", "default_sheet");
    }

    public ExcelPrinter(String fileName, String sheetName) {
        mainFile = new File(fileName);
        try {
            mainFile.createNewFile();
            fos = new FileOutputStream(mainFile);
            xlsBook = new HSSFWorkbook();
            hoja = xlsBook.createSheet(sheetName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(ExcelPrinter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void printCell(String value, int leftOffset) {
        System.out.println("in printCell: value: " + value);
        rowOffset += 2;
        row = hoja.createRow(rowOffset);
        Cell cell = row.createCell(leftOffset);
        cell.setCellValue(value);
        rowOffset += 2;
    }

    public void printDatos(ResultSet rs, JTextArea jt) {
        System.out.println("in printDatos: rs:" + rs);
        ResultSetMetaData rsmd;
        rowOffset += 2;
        int rowIndex = rowOffset;
        int columns;

        try {
            rs.beforeFirst();
            rsmd = rs.getMetaData();
            columns = rsmd.getColumnCount();

            // write header
            
            row = hoja.createRow(rowIndex);
            System.out.println("row.getRowNum(): " + row.getRowNum());
            jt.append("Writing header in row " + rowIndex + ". \n");
            for (int i = 1; i <= columns; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(rsmd.getColumnLabel(i));
                System.out.print(rsmd.getColumnLabel(i) + "\t");
                columnOffset++;
            }
            System.out.println("");
            rowIndex++;
            rowOffset++;
            
            // result array
            Double[] results = new Double[columns];

            // write
            while (rs.next()) {
                row = hoja.createRow(rowIndex);
                jt.append("Writing row in row " + rowIndex + ". \n");
                for (int j = 1; j <= columns; j++) {
                    Cell cell = row.createCell(j);
                    if (rs.getObject(j) instanceof Number) {
                        if(results[j - 1] == null) {
                            results[j - 1] = 0d;
                        }
                        cell.setCellValue(rs.getDouble(j));
                        results[j - 1] += rs.getDouble(j);
                        jt.append("value: " + cell.getNumericCellValue()  + " ");
                    } else if (rs.getObject(j) instanceof Date) {
                        cell.setCellValue(rs.getDate(j));
                        jt.append("value: " + cell.getDateCellValue() + " ");
                    } else if (rs.getObject(j) instanceof Boolean) {
                        cell.setCellValue(rs.getBoolean(j));
                        jt.append("value: " + cell.getBooleanCellValue() + " ");
                    } else {
                        cell.setCellValue(rs.getString(j));
                        jt.append("value: " + cell.getStringCellValue() + " ");
                    }
                    jt.append("|\n");
                }
                System.out.println("");
                rowIndex++;
                rowOffset++;
            }
            
            // write results at the end of a report
            row = hoja.createRow(rowIndex);
            for(int j = 0; j < columns; j++) {
                
                if(results[j] != null) {
                    Cell cell = row.createCell(j + 1);
                    cell.setCellValue(results[j]);
                }
            }
            
            rowIndex++;
            rowOffset++;

            //xlsBook.write(fos);
        } catch (SQLException ex) {
            System.out.println("SQL exception");
            Logger.getLogger(ExcelPrinter.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public void commitToArchive() {
        try {
            xlsBook.write(fos);
        } catch (IOException ex) {
            Logger.getLogger(ExcelPrinter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void closeExcelPrinter() {
        try {
            if (fos != null) {
                fos.close();
            }
            System.out.println("fos closed");
        } catch (IOException e) {
            System.out.println("error when closing fos");
        }
    }

    public void printDatosPrueba(double[][] datos) {
        Row fila = hoja.createRow(1);
        Cell celda1 = fila.createCell(2);
        celda1.setCellValue("consulta 1");

        for (int i = 0; i < datos.length; i++) {
            fila = hoja.createRow(i + 3);
            System.out.println("i: " + i);
            for (int j = 0; j < datos[i].length; j++) {
                celda1 = fila.createCell(j + 1);
                celda1.setCellValue(datos[i][j]);
            }
        }

        Calendar calendar = Calendar.getInstance();

        try (FileOutputStream fos = new FileOutputStream(new File("PruebaExcelPrinter" + calendar.get(Calendar.YEAR) + "_" + calendar.get(Calendar.MONTH) + "_" + calendar.get(Calendar.DAY_OF_MONTH) + ".xls"))) {
            xlsBook.write(fos);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    Logger.getLogger(ExcelPrinter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
