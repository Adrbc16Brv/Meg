package com.mycompany.proyectogaussjordan;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Clase Principal
 * Controla el menú en consola, la captura y validación de la matriz aumentada,
 * la ejecución de los métodos de Gauss y Gauss-Jordan, la impresión de pasos
 * y la exportación del procedimiento a Excel.
 */
public class Principal {

    static int n = 0;
    static double[][] matrizOriginal = null;
    static double[][] matrizGauss = null;
    static double[][] matrizJordan = null;
    static double[] solucionesGauss = null;
    static double[] solucionesJordan = null;
    static ArrayList<Paso> pasosGauss = new ArrayList<>();
    static ArrayList<Paso> pasosJordan = new ArrayList<>();
    static int opcion = 0;
    static Scanner entrada = new Scanner(System.in);

    public static void main(String[] args) {
        do {
            mostrarMenu();
            opcion = leerEntero("Seleccione una opción: ");
            System.out.println();

            switch (opcion) {
                case 1:
                    capturarMatriz();
                    break;

                case 2:
                    if (verificarMatrizCapturada()) {
                        System.out.println("--- MATRIZ AUMENTADA ORIGINAL (" + n + "x" + (n + 1) + ") ---");
                        imprimirMatriz(matrizOriginal);
                    }
                    break;

                case 3:
                    if (verificarMatrizCapturada()) {
                        ejecutarMetodoGauss();
                        System.out.println("==========================================");
                        System.out.println("       PROCEDIMIENTO MÉTODO DE GAUSS      ");
                        System.out.println("==========================================");
                        mostrarPasos(pasosGauss);
                        mostrarResultados("Gauss", matrizGauss, solucionesGauss);
                    }
                    break;

                case 4:
                    if (verificarMatrizCapturada()) {
                        ejecutarMetodoGaussJordan();
                        System.out.println("==========================================");
                        System.out.println("   PROCEDIMIENTO MÉTODO DE GAUSS-JORDAN   ");
                        System.out.println("==========================================");
                        mostrarPasos(pasosJordan);
                        mostrarResultados("Gauss-Jordan", matrizJordan, solucionesJordan);
                    }
                    break;

                case 5:
                    if (verificarMatrizCapturada()) {
                        compararResultados();
                    }
                    break;

                case 6:
                    if (verificarMatrizCapturada()) {
                        if (pasosGauss.isEmpty()) {
                            ejecutarMetodoGauss();
                        }
                        if (pasosJordan.isEmpty()) {
                            ejecutarMetodoGaussJordan();
                        }

                        String tipoSistema = CalculadoraMatrices.clasificarSistema(matrizGauss);
                        boolean exito = ExportadorExcel.exportarExcel(
                                matrizOriginal,
                                pasosGauss,
                                pasosJordan,
                                solucionesGauss,
                                solucionesJordan,
                                tipoSistema
                        );

                        if (exito) {
                            System.out.println("Archivo 'ResultadosGaussJordan.xlsx' generado exitosamente.");
                        }
                    }
                    break;

                case 7:
                    System.out.println("Saliendo del programa. ¡Hasta pronto!");
                    break;

                default:
                    System.out.println("Opción no válida. Por favor, elija una opción entre 1 y 7.");
                    break;
            }

            System.out.println();
        } while (opcion != 7);
    }

    public static void mostrarMenu() {
        System.out.println("==========================================");
        System.out.println("       MÉTODOS GAUSS Y GAUSS-JORDAN       ");
        System.out.println("==========================================");
        System.out.println();
        System.out.println("1. Capturar sistema de ecuaciones");
        System.out.println("2. Mostrar matriz original");
        System.out.println("3. Resolver por método Gauss");
        System.out.println("4. Resolver por método Gauss-Jordan");
        System.out.println("5. Comparar resultados");
        System.out.println("6. Exportar procedimiento a Excel");
        System.out.println("7. Salir");
        System.out.println();
    }

    public static void capturarMatriz() {
        System.out.println("--- CAPTURA DEL SISTEMA DE ECUACIONES ---");
        do {
            n = leerEntero("Ingrese el número de incógnitas (n >= 2): ");
            if (n < 2) {
                System.out.println("El sistema debe tener al menos 2 incógnitas.");
            }
        } while (n < 2);

        matrizOriginal = new double[n][n + 1];

        matrizGauss = null;
        matrizJordan = null;
        solucionesGauss = null;
        solucionesJordan = null;
        pasosGauss.clear();
        pasosJordan.clear();

        System.out.println("\nCapture los coeficientes de la matriz aumentada (" + n + "x" + (n + 1) + "):");
        for (int fila = 0; fila < n; fila++) {
            System.out.println("\n--- Ecuación " + (fila + 1) + " ---");
            for (int columna = 0; columna < n; columna++) {
                imprimirMatrizEnCaptura(matrizOriginal, fila, columna);
                matrizOriginal[fila][columna] = leerDouble(
                        "Coeficiente de x" + (columna + 1) + " [fila " + (fila + 1) + ", col " + (columna + 1) + "]: "
                );
            }
            imprimirMatrizEnCaptura(matrizOriginal, fila, n);
            matrizOriginal[fila][n] = leerDouble(
                    "Término independiente b" + (fila + 1) + " [fila " + (fila + 1) + ", col " + (n + 1) + "]: "
            );
        }

        System.out.println("\nSistema capturado correctamente:");
        imprimirMatriz(matrizOriginal);
    }

    private static void imprimirMatrizEnCaptura(double[][] matriz, int filaObjetivo, int colObjetivo) {
        int filas = matriz.length;
        int columnas = matriz[0].length;

        System.out.println("\nVista previa de la matriz:");
        for (int f = 0; f < filas; f++) {
            System.out.print("[ ");
            for (int c = 0; c < columnas; c++) {
                if (c == columnas - 1) {
                    System.out.print(" | ");
                }
                String celda;
                if (f < filaObjetivo || (f == filaObjetivo && c < colObjetivo)) {
                    celda = CalculadoraMatrices.formatearNumero(matriz[f][c]);
                } else if (f == filaObjetivo && c == colObjetivo) {
                    celda = "[?]";
                } else {
                    celda = "---";
                }
                System.out.printf("%8s", celda);
            }
            System.out.println(" ]");
        }
        System.out.flush();
    }

    public static void imprimirMatriz(double[][] matriz) {
        if (matriz == null || matriz.length == 0) {
            System.out.println("No hay una matriz disponible para mostrar.");
            return;
        }
        int filas = matriz.length;
        int columnas = matriz[0].length;

        for (int fila = 0; fila < filas; fila++) {
            System.out.print("[ ");
            for (int columna = 0; columna < columnas; columna++) {
                if (columna == columnas - 1) {
                    System.out.print(" | ");
                }
                String valorTexto = CalculadoraMatrices.formatearNumero(matriz[fila][columna]);
                System.out.printf("%8s", valorTexto);
            }
            System.out.println(" ]");
        }
        System.out.flush();
    }

    public static void mostrarPasos(ArrayList<Paso> pasos) {
        for (int i = 0; i < pasos.size(); i++) {
            Paso paso = pasos.get(i);
            System.out.println("\nPaso " + paso.numero + ": " + paso.operacion);
            imprimirMatriz(paso.matriz);
        }
    }

    public static void mostrarResultados(String metodo, double[][] matrizResultado, double[] soluciones) {
        System.out.println("\n--- RESULTADOS MÉTODO DE " + metodo.toUpperCase() + " ---");
        String tipoSistema = CalculadoraMatrices.clasificarSistema(matrizResultado);
        System.out.println("Clasificación del sistema: " + tipoSistema);

        if (soluciones == null) {
            System.out.println("No es posible obtener una solución única para este sistema.");
            return;
        }

        System.out.println("\nSoluciones encontradas:");
        for (int i = 0; i < soluciones.length; i++) {
            System.out.println("  x" + (i + 1) + " = " + CalculadoraMatrices.formatearNumero(soluciones[i]));
        }

        double[] evaluacion = CalculadoraMatrices.comprobarSoluciones(matrizOriginal, soluciones);
        System.out.println("\nComprobación en el sistema original:");
        for (int fila = 0; fila < n; fila++) {
            String obtenido = CalculadoraMatrices.formatearNumero(evaluacion[fila]);
            String esperado = CalculadoraMatrices.formatearNumero(matrizOriginal[fila][n]);
            System.out.println("  Ecuación " + (fila + 1) + ": " + obtenido + " = " + esperado);
        }
    }

    private static void compararResultados() {
        if (pasosGauss.isEmpty()) {
            ejecutarMetodoGauss();
        }
        if (pasosJordan.isEmpty()) {
            ejecutarMetodoGaussJordan();
        }

        System.out.println("==========================================");
        System.out.println("        COMPARACIÓN DE RESULTADOS         ");
        System.out.println("==========================================");
        String tipoSistema = CalculadoraMatrices.clasificarSistema(matrizGauss);
        System.out.println("Clasificación del sistema: " + tipoSistema);
        System.out.println("Operaciones realizadas en Gauss: " + (pasosGauss.size() - 1));
        System.out.println("Operaciones realizadas en Gauss-Jordan: " + (pasosJordan.size() - 1));

        if (solucionesGauss == null || solucionesJordan == null) {
            System.out.println("\nEl sistema no tiene solución única (" + tipoSistema + ").");
            return;
        }

        System.out.println("\nIncógnita\tMétodo Gauss\tMétodo Gauss-Jordan");
        System.out.println("---------------------------------------------------");
        for (int i = 0; i < n; i++) {
            String solG = CalculadoraMatrices.formatearNumero(solucionesGauss[i]);
            String solJ = CalculadoraMatrices.formatearNumero(solucionesJordan[i]);
            System.out.printf("  x%-5d\t%10s\t%15s%n", (i + 1), solG, solJ);
        }
    }

    private static void ejecutarMetodoGauss() {
        matrizGauss = CalculadoraMatrices.copiarMatriz(matrizOriginal);
        solucionesGauss = CalculadoraMatrices.resolverGauss(matrizGauss, pasosGauss);
    }

    private static void ejecutarMetodoGaussJordan() {
        matrizJordan = CalculadoraMatrices.copiarMatriz(matrizOriginal);
        solucionesJordan = CalculadoraMatrices.resolverGaussJordan(matrizJordan, pasosJordan);
    }

    private static boolean verificarMatrizCapturada() {
        if (matrizOriginal == null) {
            System.out.println("Primero debe capturar un sistema de ecuaciones (Opción 1).");
            return false;
        }
        return true;
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            System.out.flush();
            String linea = entrada.nextLine().trim();
            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.println("Entrada no válida. Ingrese un número entero.");
            }
        }
    }

    private static double leerDouble(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            System.out.flush();
            String linea = entrada.nextLine().trim().replace(',', '.');
            try {
                return Double.parseDouble(linea);
            } catch (NumberFormatException e) {
                System.out.println("Entrada no válida. Ingrese un valor numérico.");
            }
        }
    }
}