package lab5;

import mpi.MPI;

import java.util.Locale;
import java.util.Random;

public class Dirichlet {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        int n = 8;
        int m = 5;

        double[][] a = new double[n][m];
        double[][] b = new double[n][m];


        MPI.Init(args);

        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int strNum = n / size;

        if (rank == 0) {
            //generateRandomMatrix(a);

            a[0][0] = 1.0;
            a[0][1] = 2.0;
            a[0][2] = 3.0;
            a[0][3] = 4.0;
            a[0][4] = 5.0;

            a[1][0] = 10.0;
            a[1][1] = 9.0;
            a[1][2] = 8.0;
            a[1][3] = 7.0;
            a[1][4] = 6.0;

            a[2][0] = 11.0;
            a[2][1] = 12.0;
            a[2][2] = 13.0;
            a[2][3] = 14.0;
            a[2][4] = 15.0;

            a[3][0] = 20.0;
            a[3][1] = 19.0;
            a[3][2] = 18.0;
            a[3][3] = 17.0;
            a[3][4] = 16.0;

            a[4][0] = 21.0;
            a[4][1] = 22.0;
            a[4][2] = 23.0;
            a[4][3] = 24.0;
            a[4][4] = 25.0;

            a[5][0] = 30.0;
            a[5][1] = 29.0;
            a[5][2] = 28.0;
            a[5][3] = 27.0;
            a[5][4] = 26.0;

            a[6][0] = 31.0;
            a[6][1] = 32.0;
            a[6][2] = 33.0;
            a[6][3] = 34.0;
            a[6][4] = 35.0;

            a[7][0] = 40.0;
            a[7][1] = 39.0;
            a[7][2] = 38.0;
            a[7][3] = 37.0;
            a[7][4] = 36.0;

        }

        MPI.COMM_WORLD.Bcast(a, 0, a.length, MPI.OBJECT, 0);

        for (int i = rank * strNum; i < (rank + 1) * strNum; i++) {
            System.arraycopy(a[i], 0, b[i], 0, a[i].length);
        }

        if (rank == 0) {

            for (int i = 1; i < strNum; i++) {
                for (int j = 1; j < m - 1; j++) {
                    b[i][j] = (a[i - 1][j] + a[i + 1][j] + a[i][j - 1] + a[i][j + 1]) * 0.25;
                }
            }



        } else if (rank == size - 1) {

            for (int i = rank * strNum; i < (rank + 1) * strNum - 1; i++) {
                for (int j = 1; j < m - 1; j++) {
                    b[i][j] = (a[i - 1][j] + a[i + 1][j] + a[i][j - 1] + a[i][j + 1]) * 0.25;
                }
            }

        } else {

        }



        if (rank == 1) {
            printMatrix(b);
        }


        MPI.Finalize();
    }

    public static void printMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.printf("%6.0f", matrix[i][j]);
            }
            System.out.println();
        }
    }


    static void generateRandomMatrix(double[][] array) {
        Random r = new Random();
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                array[i][j] = r.nextInt(11);
            }
        }
    }

}

