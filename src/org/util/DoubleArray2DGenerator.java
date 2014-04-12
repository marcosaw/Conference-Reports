/*
 * El código contenido en este archivo, así como todos
 * los archivos compilados, son propiedad de
 * Marcos Avila Weingartshofer
 */

package org.util;

/**
 *
 * @author Marcos Avila Weingartshofer
 */
public class DoubleArray2DGenerator {
    public static double[][] getDoubleArray(){
        double[][] arreglo = new double[10][10];
        
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                arreglo[i][j] = Math.random();
            }
        }
        
        return arreglo;
    }
}
