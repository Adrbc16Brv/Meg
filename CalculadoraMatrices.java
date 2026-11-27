package com.mycompany.proyectogaussjordan;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Clase CalculadoraMatrices
 * Contiene las operaciones elementales entre filas, los metodos de resolucion
 * por Gauss y Gauss-Jordan, la clasificacion del sistema y la comprobacion.
 */
public class CalculadoraMatrices {

    private static final double EPSILON = 1e-9;

    public static double[][] copiarMatriz(double[][] original) {
        if (original == null) {
            return null;
        }
        int filas = original.length;
        int columnas = original[0].length;
        double[][] copia = new double[filas][columnas];

        for (int fila = 0; fila < filas; fila++) {
            for (int columna = 0; columna < columnas; columna++) {
                copia[fila][columna] = original[fila][columna];
            }
        }
        return copia;
    }

    public static void intercambiarFilas(double[][] matriz, int fila1, int fila2) {
        double[] temp = matriz[fila1];
        matriz[fila1] = matriz[fila2];
        matriz[fila2] = temp;
    }

    public static void multiplicarFila(double[][] matriz, int fila, double factor) {
        int columnas = matriz[fila].length;
        for (int columna = 0; columna < columnas; columna++) {
            matriz[fila][columna] = limpiarCero(matriz[fila][columna] * factor);
        }
    }

    public static void sumarMultiploFila(double[][] matriz, int filaDestino, int filaOrigen, double factor) {
        int columnas = matriz[filaDestino].length;
        for (int columna = 0; columna < columnas; columna++) {
            double nuevoValor = matriz[filaDestino][columna] + (factor * matriz[filaOrigen][columna]);
            matriz[filaDestino][columna] = limpiarCero(nuevoValor);
        }
    }

    public static double[] resolverGauss(double[][] matriz, ArrayList<Paso> pasos) {
        pasos.clear();
        int n = matriz.length;
        int numeroPaso = 0;

        pasos.add(new Paso(numeroPaso, "Matriz inicial", matriz));

        for (int columna = 0; columna < n; columna++) {
            if (Math.abs(matriz[columna][columna]) < EPSILON) {
                int filaMejorPivote = buscarFilaPivote(matriz, columna);
                if (filaMejorPivote != columna) {
                    intercambiarFilas(matriz, columna, filaMejorPivote);
                    numeroPaso++;
                    String op = "F" + (columna + 1) + " <-> F" + (filaMejorPivote + 1);
                    pasos.add(new Paso(numeroPaso, op, matriz));
                }
            }

            double pivote = matriz[columna][columna];
            if (Math.abs(pivote) < EPSILON) {
                continue;
            }

            for (int fila = columna + 1; fila < n; fila++) {
                if (Math.abs(matriz[fila][columna]) > EPSILON) {
                    double factor = matriz[fila][columna] / pivote;
                    sumarMultiploFila(matriz, fila, columna, -factor);
                    numeroPaso++;
                    String op = construirDescripcionResta(fila, columna, factor);
                    pasos.add(new Paso(numeroPaso, op, matriz));
                }
            }
        }

        String tipoSistema = clasificarSistema(matriz);
        if (!tipoSistema.equals("Solución única")) {
            return null;
        }

        return sustitucionRegresiva(matriz);
    }

    public static double[] sustitucionRegresiva(double[][] matriz) {
        int n = matriz.length;
        double[] soluciones = new double[n];

        for (int fila = n - 1; fila >= 0; fila--) {
            double suma = matriz[fila][n];
            for (int columna = fila + 1; columna < n; columna++) {
                suma -= matriz[fila][columna] * soluciones[columna];
            }
            double pivote = matriz[fila][fila];
            soluciones[fila] = limpiarCero(suma / pivote);
        }

        return soluciones;
    }

    public static double[] resolverGaussJordan(double[][] matriz, ArrayList<Paso> pasos) {
        pasos.clear();
        int n = matriz.length;
        int numeroPaso = 0;

        pasos.add(new Paso(numeroPaso, "Matriz inicial", matriz));

        for (int columna = 0; columna < n; columna++) {
            if (Math.abs(matriz[columna][columna]) < EPSILON) {
                int filaMejorPivote = buscarFilaPivote(matriz, columna);
                if (filaMejorPivote != columna) {
                    intercambiarFilas(matriz, columna, filaMejorPivote);
                    numeroPaso++;
                    String op = "F" + (columna + 1) + " <-> F" + (filaMejorPivote + 1);
                    pasos.add(new Paso(numeroPaso, op, matriz));
                }
            }

            double pivote = matriz[columna][columna];
            if (Math.abs(pivote) < EPSILON) {
                continue;
            }

            for (int fila = columna + 1; fila < n; fila++) {
                if (Math.abs(matriz[fila][columna]) > EPSILON) {
                    double factor = matriz[fila][columna] / pivote;
                    sumarMultiploFila(matriz, fila, columna, -factor);
                    numeroPaso++;
                    String op = construirDescripcionResta(fila, columna, factor);
                    pasos.add(new Paso(numeroPaso, op, matriz));
                }
            }
        }

        String tipoSistema = clasificarSistema(matriz);
        if (!tipoSistema.equals("Solución única")) {
            return null;
        }

        for (int columna = n - 1; columna >= 0; columna--) {
            double pivote = matriz[columna][columna];

            if (Math.abs(pivote) > EPSILON && Math.abs(pivote - 1.0) > EPSILON) {
                multiplicarFila(matriz, columna, 1.0 / pivote);
                numeroPaso++;
                String divisorTexto = (pivote < 0)
                        ? "(" + formatearNumero(pivote) + ")"
                        : formatearNumero(pivote);
                String op = "F" + (columna + 1) + " = F" + (columna + 1) + " / " + divisorTexto;
                pasos.add(new Paso(numeroPaso, op, matriz));
            }

            for (int fila = columna - 1; fila >= 0; fila--) {
                if (Math.abs(matriz[fila][columna]) > EPSILON) {
                    double factor = matriz[fila][columna];
                    sumarMultiploFila(matriz, fila, columna, -factor);
                    numeroPaso++;
                    String op = construirDescripcionResta(fila, columna, factor);
                    pasos.add(new Paso(numeroPaso, op, matriz));
                }
            }
        }

        double[] soluciones = new double[n];
        for (int fila = 0; fila < n; fila++) {
            soluciones[fila] = limpiarCero(matriz[fila][n]);
        }

        return soluciones;
    }

    public static String clasificarSistema(double[][] matriz) {
        int n = matriz.length;
        boolean tieneFilaCeros = false;

        for (int fila = 0; fila < n; fila++) {
            boolean todosCoeficientesCero = true;
            for (int columna = 0; columna < n; columna++) {
                if (Math.abs(matriz[fila][columna]) > EPSILON) {
                    todosCoeficientesCero = false;
                    break;
                }
            }

            if (todosCoeficientesCero) {
                if (Math.abs(matriz[fila][n]) > EPSILON) {
                    return "Sin solución";
                } else {
                    tieneFilaCeros = true;
                }
            }
        }

        if (tieneFilaCeros) {
            return "Infinitas soluciones";
        }

        return "Solución única";
    }

    public static double[] comprobarSoluciones(double[][] matrizOriginal, double[] soluciones) {
        if (matrizOriginal == null || soluciones == null) {
            return null;
        }
        int n = matrizOriginal.length;
        double[] resultadosEvaluados = new double[n];

        for (int fila = 0; fila < n; fila++) {
            double suma = 0.0;
            for (int columna = 0; columna < n; columna++) {
                suma += matrizOriginal[fila][columna] * soluciones[columna];
            }
            resultadosEvaluados[fila] = limpiarCero(suma);
        }

        return resultadosEvaluados;
    }

    private static int buscarFilaPivote(double[][] matriz, int columnaActual) {
        int n = matriz.length;
        int filaMayor = columnaActual;
        double maxValor = Math.abs(matriz[columnaActual][columnaActual]);

        for (int fila = columnaActual + 1; fila < n; fila++) {
            double valorActual = Math.abs(matriz[fila][columnaActual]);
            if (valorActual > maxValor) {
                maxValor = valorActual;
                filaMayor = fila;
            }
        }
        return filaMayor;
    }

    private static String construirDescripcionResta(int filaDestino, int filaOrigen, double factor) {
        String fDest = "F" + (filaDestino + 1);
        String fOrig = "F" + (filaOrigen + 1);
        String signo = (factor >= 0) ? " - " : " + ";
        double absFactor = Math.abs(factor);

        if (Math.abs(absFactor - 1.0) < EPSILON) {
            return fDest + " = " + fDest + signo + fOrig;
        } else {
            return fDest + " = " + fDest + signo + formatearNumero(absFactor) + "*" + fOrig;
        }
    }

    public static double limpiarCero(double valor) {
        if (Math.abs(valor) < 1e-11) {
            return 0.0;
        }
        return valor;
    }

    public static String formatearNumero(double valor) {
        valor = limpiarCero(valor);
        if (Math.abs(valor - Math.round(valor)) < 1e-9) {
            return String.valueOf(Math.round(valor));
        }
        String texto = String.format(Locale.US, "%.4f", valor);
        while (texto.contains(".") && (texto.endsWith("0") || texto.endsWith("."))) {
            texto = texto.substring(0, texto.length() - 1);
        }
        return texto;
    }
}