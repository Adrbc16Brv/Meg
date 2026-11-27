package com.mycompany.proyectogaussjordan;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Clase ExportadorExcel
 * Genera el archivo ResultadosGaussJordan.xlsx mediante Apache POI
 * con las 4 hojas requeridas: Datos, Gauss, Gauss-Jordan y Resultados.
 */
public class ExportadorExcel {

    private static final String NOMBRE_ARCHIVO = "ResultadosGaussJordan.xlsx";

    public static boolean exportarExcel(
            double[][] matrizOriginal,
            ArrayList<Paso> pasosGauss,
            ArrayList<Paso> pasosJordan,
            double[] solucionesGauss,
            double[] solucionesJordan,
            String tipoSistema) {

        try (Workbook libro = new XSSFWorkbook()) {
            CellStyle estiloTitulo = crearEstiloTitulo(libro);
            CellStyle estiloEncabezado = crearEstiloEncabezado(libro);
            CellStyle estiloCeldaTexto = crearEstiloTexto(libro);
            CellStyle estiloCeldaNumero = crearEstiloNumero(libro);

            Sheet hojaDatos = libro.createSheet("Datos");
            Sheet hojaGauss = libro.createSheet("Gauss");
            Sheet hojaJordan = libro.createSheet("Gauss-Jordan");
            Sheet hojaResultados = libro.createSheet("Resultados");

            escribirHojaDatos(hojaDatos, matrizOriginal, estiloTitulo, estiloEncabezado, estiloCeldaNumero);

            escribirPasos(hojaGauss, "PROCEDIMIENTO MÉTODO DE GAUSS", pasosGauss,
                    estiloTitulo, estiloEncabezado, estiloCeldaTexto, estiloCeldaNumero);

            escribirPasos(hojaJordan, "PROCEDIMIENTO MÉTODO DE GAUSS-JORDAN", pasosJordan,
                    estiloTitulo, estiloEncabezado, estiloCeldaTexto, estiloCeldaNumero);

            escribirResultados(hojaResultados, matrizOriginal, solucionesGauss, solucionesJordan,
                    tipoSistema, estiloTitulo, estiloEncabezado, estiloCeldaTexto, estiloCeldaNumero);

            int maxColumnas = Math.max(matrizOriginal[0].length + 2, 6);
            ajustarColumnas(hojaDatos, maxColumnas);
            ajustarColumnas(hojaGauss, maxColumnas);
            ajustarColumnas(hojaJordan, maxColumnas);
            ajustarColumnas(hojaResultados, maxColumnas);

            try (FileOutputStream archivoSalida = new FileOutputStream(NOMBRE_ARCHIVO)) {
                libro.write(archivoSalida);
            }

            return true;
        } catch (IOException e) {
            System.out.println("Error al generar el archivo Excel: " + e.getMessage());
            return false;
        }
    }

    private static void escribirHojaDatos(
            Sheet hoja,
            double[][] matrizOriginal,
            CellStyle estiloTitulo,
            CellStyle estiloEncabezado,
            CellStyle estiloCeldaNumero) {

        int filaActual = 0;
        int n = matrizOriginal.length;

        Row filaTitulo = hoja.createRow(filaActual++);
        Cell celdaTitulo = filaTitulo.createCell(0);
        celdaTitulo.setCellValue("MATRIZ AUMENTADA ORIGINAL (" + n + "x" + (n + 1) + ")");
        celdaTitulo.setCellStyle(estiloTitulo);

        filaActual++;

        Row filaEncabezados = hoja.createRow(filaActual++);
        for (int columna = 0; columna < n; columna++) {
            Cell celda = filaEncabezados.createCell(columna);
            celda.setCellValue("x" + (columna + 1));
            celda.setCellStyle(estiloEncabezado);
        }
        Cell celdaIndep = filaEncabezados.createCell(n);
        celdaIndep.setCellValue("Término Indep.");
        celdaIndep.setCellStyle(estiloEncabezado);

        escribirMatriz(hoja, matrizOriginal, filaActual, estiloCeldaNumero);
    }

    public static int escribirMatriz(
            Sheet hoja,
            double[][] matriz,
            int filaInicial,
            CellStyle estiloCeldaNumero) {

        int filaActual = filaInicial;
        for (int fila = 0; fila < matriz.length; fila++) {
            Row filaExcel = hoja.createRow(filaActual++);
            for (int columna = 0; columna < matriz[fila].length; columna++) {
                Cell celda = filaExcel.createCell(columna);
                celda.setCellValue(CalculadoraMatrices.limpiarCero(matriz[fila][columna]));
                celda.setCellStyle(estiloCeldaNumero);
            }
        }
        return filaActual;
    }

    public static void escribirPasos(
            Sheet hoja,
            String tituloHoja,
            ArrayList<Paso> pasos,
            CellStyle estiloTitulo,
            CellStyle estiloEncabezado,
            CellStyle estiloCeldaTexto,
            CellStyle estiloCeldaNumero) {

        int filaActual = 0;

        Row filaTitulo = hoja.createRow(filaActual++);
        Cell celdaTitulo = filaTitulo.createCell(0);
        celdaTitulo.setCellValue(tituloHoja);
        celdaTitulo.setCellStyle(estiloTitulo);

        filaActual++;

        Row filaEncabezadoResumen = hoja.createRow(filaActual++);
        Cell celdaPasoEnc = filaEncabezadoResumen.createCell(0);
        celdaPasoEnc.setCellValue("Paso");
        celdaPasoEnc.setCellStyle(estiloEncabezado);

        Cell celdaOpEnc = filaEncabezadoResumen.createCell(1);
        celdaOpEnc.setCellValue("Operación");
        celdaOpEnc.setCellStyle(estiloEncabezado);

        for (int i = 0; i < pasos.size(); i++) {
            Paso paso = pasos.get(i);
            Row filaResumen = hoja.createRow(filaActual++);

            Cell celdaNum = filaResumen.createCell(0);
            celdaNum.setCellValue(paso.numero);
            celdaNum.setCellStyle(estiloCeldaTexto);

            Cell celdaOp = filaResumen.createCell(1);
            celdaOp.setCellValue(paso.operacion);
            celdaOp.setCellStyle(estiloCeldaTexto);
        }

        filaActual++;

        for (int i = 0; i < pasos.size(); i++) {
            Paso paso = pasos.get(i);
            Row filaDesc = hoja.createRow(filaActual++);
            Cell celdaDesc = filaDesc.createCell(0);
            if (paso.numero == 0) {
                celdaDesc.setCellValue("Matriz inicial");
            } else {
                celdaDesc.setCellValue("Matriz después del paso " + paso.numero + " (" + paso.operacion + ")");
            }
            celdaDesc.setCellStyle(estiloEncabezado);

            filaActual = escribirMatriz(hoja, paso.matriz, filaActual, estiloCeldaNumero);
            filaActual++;
        }
    }

    public static void escribirResultados(
            Sheet hoja,
            double[][] matrizOriginal,
            double[] solucionesGauss,
            double[] solucionesJordan,
            String tipoSistema,
            CellStyle estiloTitulo,
            CellStyle estiloEncabezado,
            CellStyle estiloCeldaTexto,
            CellStyle estiloCeldaNumero) {

        int filaActual = 0;
        int n = matrizOriginal.length;

        Row filaTitulo = hoja.createRow(filaActual++);
        Cell celdaTitulo = filaTitulo.createCell(0);
        celdaTitulo.setCellValue("RESULTADOS Y COMPROBACIÓN DEL SISTEMA");
        celdaTitulo.setCellStyle(estiloTitulo);

        filaActual++;

        Row filaClasificacion = hoja.createRow(filaActual++);
        Cell celdaEtiquetaClasif = filaClasificacion.createCell(0);
        celdaEtiquetaClasif.setCellValue("Tipo de sistema:");
        celdaEtiquetaClasif.setCellStyle(estiloEncabezado);

        Cell celdaValorClasif = filaClasificacion.createCell(1);
        celdaValorClasif.setCellValue(tipoSistema);
        celdaValorClasif.setCellStyle(estiloCeldaTexto);

        filaActual++;

        if (solucionesGauss == null || solucionesJordan == null) {
            Row filaAviso = hoja.createRow(filaActual);
            Cell celdaAviso = filaAviso.createCell(0);
            celdaAviso.setCellValue("El sistema no tiene solución única (" + tipoSistema + ").");
            celdaAviso.setCellStyle(estiloCeldaTexto);
            return;
        }

        Row filaEncSol = hoja.createRow(filaActual++);
        String[] encabezadosSol = {"Incógnita", "Solución Gauss", "Solución Gauss-Jordan", "Diferencia"};
        for (int i = 0; i < encabezadosSol.length; i++) {
            Cell celda = filaEncSol.createCell(i);
            celda.setCellValue(encabezadosSol[i]);
            celda.setCellStyle(estiloEncabezado);
        }

        for (int i = 0; i < n; i++) {
            Row filaSol = hoja.createRow(filaActual++);

            Cell celdaIncognita = filaSol.createCell(0);
            celdaIncognita.setCellValue("x" + (i + 1));
            celdaIncognita.setCellStyle(estiloCeldaTexto);

            Cell celdaGauss = filaSol.createCell(1);
            celdaGauss.setCellValue(solucionesGauss[i]);
            celdaGauss.setCellStyle(estiloCeldaNumero);

            Cell celdaJordan = filaSol.createCell(2);
            celdaJordan.setCellValue(solucionesJordan[i]);
            celdaJordan.setCellStyle(estiloCeldaNumero);

            Cell celdaDif = filaSol.createCell(3);
            double diferencia = Math.abs(solucionesGauss[i] - solucionesJordan[i]);
            celdaDif.setCellValue(CalculadoraMatrices.limpiarCero(diferencia));
            celdaDif.setCellStyle(estiloCeldaNumero);
        }

        filaActual += 2;

        Row filaTituloComp = hoja.createRow(filaActual++);
        Cell celdaTituloComp = filaTituloComp.createCell(0);
        celdaTituloComp.setCellValue("COMPROBACIÓN EN LAS ECUACIONES ORIGINALES");
        celdaTituloComp.setCellStyle(estiloTitulo);

        Row filaEncComp = hoja.createRow(filaActual++);
        String[] encabezadosComp = {
            "Ecuación",
            "Evaluación Gauss",
            "Evaluación Gauss-Jordan",
            "Término Independiente",
            "Estado"
        };
        for (int i = 0; i < encabezadosComp.length; i++) {
            Cell celda = filaEncComp.createCell(i);
            celda.setCellValue(encabezadosComp[i]);
            celda.setCellStyle(estiloEncabezado);
        }

        double[] comprobacionGauss = CalculadoraMatrices.comprobarSoluciones(matrizOriginal, solucionesGauss);
        double[] comprobacionJordan = CalculadoraMatrices.comprobarSoluciones(matrizOriginal, solucionesJordan);

        for (int fila = 0; fila < n; fila++) {
            Row filaComp = hoja.createRow(filaActual++);
            double esperado = matrizOriginal[fila][n];

            Cell celdaEc = filaComp.createCell(0);
            celdaEc.setCellValue("Ecuación " + (fila + 1));
            celdaEc.setCellStyle(estiloCeldaTexto);

            Cell celdaEvalG = filaComp.createCell(1);
            celdaEvalG.setCellValue(comprobacionGauss[fila]);
            celdaEvalG.setCellStyle(estiloCeldaNumero);

            Cell celdaEvalJ = filaComp.createCell(2);
            celdaEvalJ.setCellValue(comprobacionJordan[fila]);
            celdaEvalJ.setCellStyle(estiloCeldaNumero);

            Cell celdaEsperado = filaComp.createCell(3);
            celdaEsperado.setCellValue(esperado);
            celdaEsperado.setCellStyle(estiloCeldaNumero);

            boolean correcta = Math.abs(comprobacionGauss[fila] - esperado) < 1e-6
                    && Math.abs(comprobacionJordan[fila] - esperado) < 1e-6;
            Cell celdaEstado = filaComp.createCell(4);
            celdaEstado.setCellValue(correcta ? "Correcto" : "Con diferencia");
            celdaEstado.setCellStyle(estiloCeldaTexto);
        }
    }

    private static CellStyle crearEstiloTitulo(Workbook libro) {
        CellStyle estilo = libro.createCellStyle();
        Font fuente = libro.createFont();
        fuente.setBold(true);
        fuente.setFontHeightInPoints((short) 12);
        estilo.setFont(fuente);
        return estilo;
    }

    private static CellStyle crearEstiloEncabezado(Workbook libro) {
        CellStyle estilo = libro.createCellStyle();
        Font fuente = libro.createFont();
        fuente.setBold(true);
        estilo.setFont(fuente);
        estilo.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estilo.setAlignment(HorizontalAlignment.CENTER);
        aplicarBordes(estilo);
        return estilo;
    }

    private static CellStyle crearEstiloTexto(Workbook libro) {
        CellStyle estilo = libro.createCellStyle();
        estilo.setAlignment(HorizontalAlignment.CENTER);
        aplicarBordes(estilo);
        return estilo;
    }

    private static CellStyle crearEstiloNumero(Workbook libro) {
        CellStyle estilo = libro.createCellStyle();
        DataFormat formato = libro.createDataFormat();
        estilo.setDataFormat(formato.getFormat("0.####"));
        estilo.setAlignment(HorizontalAlignment.RIGHT);
        aplicarBordes(estilo);
        return estilo;
    }

    private static void aplicarBordes(CellStyle estilo) {
        estilo.setBorderTop(BorderStyle.THIN);
        estilo.setBorderBottom(BorderStyle.THIN);
        estilo.setBorderLeft(BorderStyle.THIN);
        estilo.setBorderRight(BorderStyle.THIN);
    }

    private static void ajustarColumnas(Sheet hoja, int totalColumnas) {
        for (int col = 0; col < totalColumnas; col++) {
            hoja.autoSizeColumn(col);
            int anchoActual = hoja.getColumnWidth(col);
            if (anchoActual < 3500) {
                hoja.setColumnWidth(col, 3500);
            }
        }
    }
}