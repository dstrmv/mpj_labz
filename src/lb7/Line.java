package lb7;

import mpi.*;

public class Line {
    public static void main(String[] args) {

        final int dimsNum = 1;
        int rank, size;
        Cartcomm cart;
        int[] a, b, c;
        int[] dims = new int[dimsNum];
        boolean[] periods = {false};
        int[] newCoords = new int[dimsNum];
        boolean reorders = false;

        MPI.Init(args);
        rank = MPI.COMM_WORLD.Rank();
        size = MPI.COMM_WORLD.Size();

        a = new int[]{rank};
        b = new int[]{-1};
        c = new int[]{-1};

        Cartcomm.Dims_create(size, dims);
        cart = MPI.COMM_WORLD.Create_cart(dims, periods, reorders);
//        newCoords = cart.Coords(rank);
//        a[0] = newCoords[0];
//
//        if (newCoords[0] == 0) {
//            sourcem = destm = MPI.PROC_NULL;
//        } else {
//            sourcem = destm = newCoords[0] - 1;
//        }
//        if (newCoords[0] == dims[0] - 1) {
//            destb = sourceb = MPI.PROC_NULL;
//        } else {
//            destb = sourceb = newCoords[0] + 1;
//        }
//
//        try {
//            cart.Sendrecv(a, 0, 1, MPI.INT, destb, 12, b, 0, 1, MPI.INT, sourcem, 12);
//            System.out.printf("rank %d firstbock new coords = %d, b = %d\n", rank, newCoords[0], b[0]);
//
//            cart.Sendrecv(a, 0, 1, MPI.INT, destm, 12, b, 0, 1, MPI.INT, sourceb, 12);
//            System.out.printf("rank %d new coords = %d, b = %d\n", rank, newCoords[0], b[0]);
//        } catch (MPIException e) {
//            System.out.println(rank);
//        }

        ShiftParms shift = cart.Shift(0, 1);

        //System.out.printf("rank = %d source = %d dest = %d\n", rank, shift.rank_source, shift.rank_dest);

        if (rank == 0) {
            cart.Send(a, 0, 1, MPI.INT, shift.rank_dest, 12);
        } else if (rank == size - 1) {
            cart.Recv(b, 0, 1, MPI.INT, shift.rank_source, 12);
        } else {
            cart.Sendrecv(a, 0, 1, MPI.INT, shift.rank_dest, 12, b, 0, 1, MPI.INT, shift.rank_source, 12);
        }

        cart.Barrier();

        System.out.printf("shift right -> rank %d b = %d\n", rank, b[0]);

        cart.Barrier();

        shift = cart.Shift(0, -1);

        if (rank == 0) {
            cart.Recv(c, 0, 1, MPI.INT, shift.rank_source, 12);
        } else if (rank == size - 1) {
            cart.Send(b, 0, 1, MPI.INT, shift.rank_dest, 12);
        } else {
            cart.Sendrecv(b, 0, 1, MPI.INT, shift.rank_dest, 12, c, 0, 1, MPI.INT, shift.rank_source, 12);
        }

        cart.Barrier();

        System.out.printf("shift left  <- rank %d b = %d\n", rank, c[0]);
        cart.Free();
        MPI.Finalize();


    }
}
