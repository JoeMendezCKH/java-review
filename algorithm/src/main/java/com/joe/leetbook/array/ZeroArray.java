package com.joe.leetbook.array;

import org.junit.Test;

import java.util.Arrays;

/**
 * 面试题 01.08. 零矩阵
 *
 * @author ckh
 * @since 2020/12/9
 */
@SuppressWarnings("ALL")
public class ZeroArray {

    public void setZeroes(int[][] matrix) {
        if (matrix == null || matrix.length == 0) {
            return;
        }
        int row = matrix.length;
        int col = matrix[0].length;
        boolean[] flag = new boolean[col];

        for (int i = 0; i < row; i++) {
            boolean rowNeedZero = false;
            for (int j = 0; j < col; j++) {
                if (matrix[i][j] == 0) {
                    flag[j] = true;
                    rowNeedZero = true;
                }
            }
            if (rowNeedZero) {
                for (int t = 0; t < col; t++) {
                    matrix[i][t] = 0;
                }
            }
        }

        for (int i = 0; i < flag.length; i++) {
            if (flag[i]) {
                for (int j = 0; j < row; j++) {
                    matrix[j][i] = 0;
                }
            }
        }
    }

    public void setZeroesV2(int[][] matrix) {
        if (matrix == null || matrix.length == 0) {
            return;
        }
        int row = matrix.length;
        int col = matrix[0].length;
        boolean firstRowNeedZero = false;
        boolean firstColNeedZero = false;

        for (int i = 0; i < row; i++) {
            if (matrix[i][0] == 0) {
                firstColNeedZero = true;
                break;
            }
        }

        for (int j = 0; j < col; j++) {
            if (matrix[0][j] == 0) {
                firstRowNeedZero = true;
                break;
            }
        }


        for (int i = 1; i < row; i++) {
            for (int j = 1; j < col; j++) {
                if (matrix[i][j] == 0) {
                    matrix[0][j] = 0;
                    matrix[i][0] = 0;
                }
            }
        }

        for (int i = 1; i < row; i++) {
            for (int j = 1; j < col; j++) {
                if (matrix[0][j] == 0 || matrix[i][0] == 0) {
                    matrix[i][j] = 0;
                }
            }
        }

        for (int i = 0; i < row; i++) {
            if (firstColNeedZero) {
                matrix[i][0] = 0;
            }
        }

        for (int j = 0; j < col; j++) {
            if (firstRowNeedZero) {
                matrix[0][j] = 0;
            }
        }
    }

    @Test
    public void test() {
        int[][] matrix = {
                //[[1,1,1],[1,0,1],[1,1,1]]
                {0, 1, 2, 0},
                {3, 4, 5, 6},
                {1, 3, 1, 5}
        };
        setZeroesV2(matrix);

        System.out.println(Arrays.deepToString(matrix));
    }
}
