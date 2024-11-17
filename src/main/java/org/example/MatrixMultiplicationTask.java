package org.example;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.RecursiveTask;

public class MatrixMultiplicationTask extends RecursiveTask<CopyOnWriteArrayList<int[]>> {

    private CopyOnWriteArrayList<int[]> matrixA;
    private CopyOnWriteArrayList<int[]> matrixB;
    private int startRow, endRow, startCol, endCol;
    private CopyOnWriteArrayList<int[]> result;

    public MatrixMultiplicationTask(CopyOnWriteArrayList<int[]> matrixA, CopyOnWriteArrayList<int[]> matrixB, int startRow, int endRow, int startCol, int endCol, CopyOnWriteArrayList<int[]> result) {
        this.matrixA = matrixA;
        this.matrixB = matrixB;
        this.startRow = startRow;
        this.endRow = endRow;
        this.startCol = startCol;
        this.endCol = endCol;
        this.result = result;
    }

    @Override
    protected CopyOnWriteArrayList<int[]> compute() {
        if (startRow == endRow && startCol == endCol)
        {
            int resultNumber = 0;
            for (int i = 0; i < matrixA.get(startRow).length; i++)
                resultNumber += matrixA.get(startRow)[i] * matrixB.get(i)[startCol];
            result.get(startRow)[startCol] = resultNumber;
            return result;
        }

        // Разбиение только по оси, если длина этой оси больше 1
        int midRow = (startRow + endRow) / 2;
        int midB = (startCol + endCol) / 2;

        MatrixMultiplicationTask task1 = null;
        MatrixMultiplicationTask task2 = null;

        if (startRow != endRow)
        {
            // Разбиение по оси Y
            task1 = new MatrixMultiplicationTask(matrixA, matrixB, startRow, midRow, startCol, endCol , result);
            task2 = new MatrixMultiplicationTask(matrixA, matrixB, midRow + 1, endRow, startCol, endCol, result);
        }
        else if (startCol != endCol)
        {
            task1 = new MatrixMultiplicationTask(matrixA, matrixB, startRow, endRow, startCol, midB, result);
            task2 = new MatrixMultiplicationTask(matrixA, matrixB, startRow, endRow, midB + 1, endCol, result);
        }


        task1.fork();
        task2.fork();

        task1.join();
        task2.join();

        return result;
    }
}