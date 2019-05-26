package lab8;

import mpi.MPI;

public class Matrix {
    public static void main(String[] args) {

        int n = 4;
        int m = 2;
        int k = 5;

        double[] a = new double[n * m];
        double[] b = new double[m * k];
        double[] c = new double[n * k];
        double[] cr = new double[n * k];

        int rank, size;

        MPI.Init(args);

        long startTime = System.currentTimeMillis();

        rank = MPI.COMM_WORLD.Rank();
        size = MPI.COMM_WORLD.Size();

        if (rank == 0) {
//
//            Random r = new Random();
//            for (int i = 0; i < a.length; i++) {
//                a[i] = r.nextInt(9) + 1;
//                b[i] = r.nextInt(10) + 10;
//            }

            a[0] = 1;
            a[1] = 2;
            a[2] = 3;
            a[3] = 4;
            a[4] = 5;
            a[5] = 6;
            a[6] = 7;
            a[7] = 8;

            b[0] = 9;
            b[1] = 10;
            b[2] = 11;
            b[3] = 12;
            b[4] = 13;
            b[5] = 14;
            b[6] = 15;
            b[7] = 16;
            b[8] = 17;
            b[9] = 18;
        }

        int part = (n / size) * m;
        double[] ai = new double[part];

        MPI.COMM_WORLD.Scatter(a, 0, part, MPI.DOUBLE, ai, 0, part, MPI.DOUBLE, 0);
        System.arraycopy(ai, 0, a, part * rank, ai.length);
        MPI.COMM_WORLD.Bcast(b, 0, b.length, MPI.DOUBLE, 0);

        int start = rank * n / size;
        int end = (rank + 1) * n / size;

        for (int i = start; i < end; i++) {
            for (int j = 0; j < k; j++) {
                for (int l = 0; l < m; l++) {
                    c[k * i + j] += a[i * m + l] * b[l * k + j];
                }
            }
        }

        MPI.COMM_WORLD.Barrier();

        MPI.COMM_WORLD.Gather(c, rank * n * k / size, n * k / size, MPI.DOUBLE, cr, 0, n * k / size, MPI.DOUBLE, 0);

        MPI.COMM_WORLD.Barrier();

        if (rank == 0) {

            System.out.println("first matrix:");
            printMatrix(a, n, m);
            System.out.println("second matrix:");
            printMatrix(b, m, k);
            System.out.println("result matrix:");
            printMatrix(cr, n, k);
            //System.out.println((System.currentTimeMillis() - startTime) + "ms");

        }

        MPI.Finalize();

    }

    public static void printMatrix(double[] matrix, int n, int m) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                System.out.printf("%6.0f", matrix[m*i+j]);
            }
            System.out.println();
        }
    }
}
