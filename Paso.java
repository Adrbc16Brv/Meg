package com.mycompany.proyectogaussjordan;

/**
 * Clase Paso
 * Almacena el numero de paso, la descripcion de la operacion elemental
 * realizada y una copia independiente de la matriz resultante.
 */
public class Paso {

    public int numero;
    public String operacion;
    public double[][] matriz;

    public Paso(int numero, String operacion, double[][] matriz) {
        this.numero = numero;
        this.operacion = operacion;
        this.matriz = CalculadoraMatrices.copiarMatriz(matriz);
    }

    public int getNumero() {
        return numero;
    }

    public String getOperacion() {
        return operacion;
    }

    public double[][] getMatriz() {
        return matriz;
    }
}