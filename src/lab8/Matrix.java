package lab8;

import mpi.Cartcomm;
import mpi.MPI;
import mpi.ShiftParms;

import java.util.Arrays;
import java.util.Random;

public class Matrix {
    public static void main(String[] args) {

        //не работает ничего

        int m = 6;
        int n = 4;

        int numDims = 1;
        double[][] a = new double[n][m];
        double[][] b = new double[m][n];
        double[][] c = new double[n][m];

        int rank, size, d;
        int[] dims = new int[numDims];
        int[] newCoords = new int[numDims];
        boolean[] periods = {true};
        boolean reorder = false;
        Cartcomm cart;

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

        }

        MPI.COMM_WORLD.Bcast(a, 0, 1, MPI.OBJECT, 0);
        MPI.COMM_WORLD.Bcast(b, 0, 1, MPI.OBJECT, 0);

        Cartcomm.Dims_create(size, dims);
        cart = MPI.COMM_WORLD.Create_cart(dims, periods, reorder);

        newCoords = cart.Coords(rank);

        ShiftParms shift = cart.Shift(0, -1);


        for (int k = 0; k < size; k++) {
            d = k;
            for (int j = 0; j < n; j++) {
                for (int i1 = 0, j1 = d; j1 < d + n; i1++, j1++) {
                    for (int i = 0; i < m; i++) {
                        c[j][j1] += a[j][i] * b[i][i1];
                    }
                }
            }

            //cart.Sendrecv_replace(b, 0, 1, MPI.OBJECT, shift.rank_dest, 12, shift.rank_source, 12);
        }

        if (rank == 0) {
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

        cart.Free();
        MPI.Finalize();

    }
}
