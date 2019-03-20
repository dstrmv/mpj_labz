package lab6;

import mpi.*;

public class Shift {
    public static void main(String[] args) {

        final int dimsNum = 1;
        int rank, size;
        Cartcomm cart;
        Status status;
        int[] a, b;
        int[] dims = new int[dimsNum];
        boolean[] periods = {true};
        boolean reorders = false;

        MPI.Init(args);
        rank = MPI.COMM_WORLD.Rank();
        size = MPI.COMM_WORLD.Size();

        a = new int[]{rank};
        b = new int[]{-1};

        Cartcomm.Dims_create(size, dims);
        cart = MPI.COMM_WORLD.Create_cart(dims, periods, reorders);
        ShiftParms shift = cart.Shift(0, 1);

        cart.Sendrecv(a, 0, 1, MPI.INT, shift.rank_dest, 0, b, 0, 1, MPI.INT, shift.rank_source, 0);
        System.out.printf("rank = %d source = %d dest = %d\n", rank, shift.rank_source, shift.rank_dest);
        System.out.printf("rank = %d b = %d\n", rank, b[0]);

        cart.Free();
        MPI.Finalize();

    }
}
