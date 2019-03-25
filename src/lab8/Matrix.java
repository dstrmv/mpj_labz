package lab8;

import mpi.Cartcomm;
import mpi.MPI;
import mpi.ShiftParms;

import java.util.Arrays;
import java.util.Random;

public class Matrix {
    public static void main(String[] args) {

        //не работает ничего

        int n = 4;
        int m = 4;

        double[][] a = new double[n][m];
        double[][] b = new double[m][n];
        double[][] c = new double[n][n];

        int rank, size;

        MPI.Init(args);

        rank = MPI.COMM_WORLD.Rank();
        size = MPI.COMM_WORLD.Size();

        if (rank == 0) {
            Random r = new Random();

            for (int i = 0; i < a.length; i++) {
                for (int j = 0; j < a[i].length; j++) {
                    a[i][j] = r.nextInt(9) + 1;
                    b[j][i] = r.nextInt(10) + 10;
                }
            }

//            a[0][0] = 1;
//            a[0][1] = 2;
//            a[0][2] = 3;
//            a[0][3] = 4;
//            a[1][0] = 5;
//            a[1][1] = 6;
//            a[1][2] = 7;
//            a[1][3] = 8;
//
//            b[0][0] = 9;
//            b[0][1] = 10;
//            b[1][0] = 11;
//            b[1][1] = 12;
//            b[2][0] = 13;
//            b[2][1] = 14;
//            b[3][0] = 15;
//            b[3][1] = 16;
        }

        MPI.COMM_WORLD.Bcast(a, 0, 1, MPI.OBJECT, 0);
        MPI.COMM_WORLD.Bcast(b, 0, 1, MPI.OBJECT, 0);

        //int start = rank * (n / size);
        //int end = (rank + 1) * (n / size);

        for (int i = rank; i < n; i += size) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < m; k++) {
                    c[i][j] += a[i][k] * b[k][j];
                }
            }
        }

        MPI.COMM_WORLD.Barrier();

        for (int i = rank; i < n; i += size) {
            if (rank != 0) {
                MPI.COMM_WORLD.Send(c[i], 0, c[i].length, MPI.DOUBLE, 0, 0);
            }
        }

        MPI.COMM_WORLD.Barrier();

        if (rank == 0) {

            for (int i = 0; i < n; i++) {
                if (i%size == 0) continue;
                MPI.COMM_WORLD.Recv(c[i], 0, c[i].length, MPI.DOUBLE, i % size, 0);
            }

            for (int i = 0; i < a.length; i++) {
                System.out.println(Arrays.toString(a[i]));
            }
            System.out.println();
            System.out.println();
            for (int i = 0; i < b.length; i++) {
                System.out.println(Arrays.toString(b[i]));
            }
            System.out.println();
            for (int i = 0; i < c.length; i++) {
                System.out.println(Arrays.toString(c[i]));
            }
            System.out.println();

        }

        MPI.Finalize();

    }
}
