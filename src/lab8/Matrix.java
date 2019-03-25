package lab8;

import mpi.Cartcomm;
import mpi.MPI;
import mpi.ShiftParms;

import java.util.Arrays;
import java.util.Random;

public class Matrix {
    public static void main(String[] args) {

        int n = 4;
        int m = 4;

        double[] a = new double[n * m];
        double[] b = new double[m * n];
        double[] c = new double[n * n];

        int rank, size;

        MPI.Init(args);

        rank = MPI.COMM_WORLD.Rank();
        size = MPI.COMM_WORLD.Size();

        if (rank == 0) {

            Random r = new Random();
            for (int i = 0; i < a.length; i++) {
                a[i] = r.nextInt(9) + 1;
                b[i] = r.nextInt(10) + 10;
            }

//            a[0] = 1;
//            a[1] = 2;
//            a[2] = 3;
//            a[3] = 4;
//            a[4] = 5;
//            a[5] = 6;
//            a[6] = 7;
//            a[7] = 8;
//
//            b[0] = 9;
//            b[1] = 10;
//            b[2] = 11;
//            b[3] = 12;
//            b[4] = 13;
//            b[5] = 14;
//            b[6] = 15;
//            b[7] = 16;
        }

        int o = n / size;

        //MPI.COMM_WORLD.Scatter(a, );

        MPI.COMM_WORLD.Bcast(a, 0, a.length, MPI.DOUBLE, 0);
        MPI.COMM_WORLD.Bcast(b, 0, b.length, MPI.DOUBLE, 0);

        for (int i = rank; i < n; i += size) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < m; k++) {
                    c[n * i + j] += a[i * m + k] * b[k * n + j];
                }
            }
        }

        if (rank == 0) {

            System.out.println(Arrays.toString(a));
            System.out.println();
            System.out.println(Arrays.toString(b));
            System.out.println();
            System.out.println(Arrays.toString(c));
            System.out.println();
        }

        MPI.Finalize();

    }
}
