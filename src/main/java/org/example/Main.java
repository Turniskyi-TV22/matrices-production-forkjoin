package org.example;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

public class Main {
    public static CopyOnWriteArrayList<int[]> result = null;
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of rows for the first matrix: ");
        int rowsA = scanner.nextInt();
        System.out.print("Enter the number of columns for the first matrix (and rows for the second matrix): ");
        int colsA = scanner.nextInt();
        System.out.print("Enter the number of columns for the second matrix: ");
        int colsB = scanner.nextInt();

        CopyOnWriteArrayList<int[]> matrixA = generateMatrix(rowsA, colsA);
        CopyOnWriteArrayList<int[]> matrixB = generateMatrix(colsA, colsB);
        result =  generateMatrix(rowsA, colsB);

        System.out.println("\nFirst matrix:");
        printMatrix(matrixA);

        System.out.println("\nSecond matrix:");
        printMatrix(matrixB);


        long startTime = System.currentTimeMillis();
        ForkJoinPool pool = new ForkJoinPool();
        MatrixMultiplicationTask task = new MatrixMultiplicationTask(matrixA, matrixB, 0, rowsA - 1, 0, colsB - 1, result);
        result = pool.invoke(task);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("\nMatrix product:");
        printMatrix(result);

        System.out.println("\nExecution time: " + duration + " ms");

        //part 2 - work-dealing
        startTime = System.currentTimeMillis();
        result = generateMatrix(rowsA, colsB);

        int numThreads = 3;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        int part = (rowsA-1) / numThreads;
        for (int i = 0; i < numThreads; i++) {
            int startRow = i * part;
            int endRow = (i == numThreads - 1) ? (rowsA) : ((i + 1) * part);
            executor.submit(() -> {
                multiMatrix(matrixA, matrixB, startRow, endRow, 0, colsB - 1);
            });
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        endTime = System.currentTimeMillis();
        duration = endTime - startTime;

        System.out.println("\nMatrix product:");
        printMatrix(result);

        System.out.println("\nExecution time: " + duration + " ms");
    }


    public static CopyOnWriteArrayList<int[]> generateMatrix(int rows, int cols) {
        Random random = new Random();
        CopyOnWriteArrayList<int[]> matrix = new CopyOnWriteArrayList<>();
        for (int i = 0; i < rows; i++) {
            matrix.add(new int[cols]);
            for (int j = 0; j < cols; j++) {
                matrix.get(i)[j] = random.nextInt(10);  // Генерація випадкового числа від 0 до 9
            }
        }
        return matrix;
    }

    public static void printMatrix(CopyOnWriteArrayList<int[]> matrix) {
        for (int[] row : matrix) {
            for (int element : row) {
                System.out.print(element + " ");
            }
            System.out.println();
        }
    }

    public static void multiMatrix(CopyOnWriteArrayList<int[]> matrixA, CopyOnWriteArrayList<int[]> matrixB, int startRow, int endRow, int startCol, int endCol) {
        for (int i = startRow; i < endRow; i++) {
            for (int j = startCol; j <= endCol; j++) { // <= для включения endCol
                int resultNumber = 0;
                for (int k = 0; k < matrixA.get(i).length; k++) { // Правильный цикл для умножения
                    resultNumber += matrixA.get(i)[k] * matrixB.get(k)[j];
                }
                result.get(i)[j] = resultNumber;
            }
        }
    }
}