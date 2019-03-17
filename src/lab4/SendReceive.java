package lab4;

import mpi.MPI;

import java.util.Arrays;

public class SendReceive {
    public static void main(String[] args) {

        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int[] array = new int[20];
        long start = 0;

        if (rank == 0) {
            start = System.currentTimeMillis();
        }

        if (rank == 0) {
            array = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
            MPI.COMM_WORLD.Send(array, 0, 20, MPI.INT, rank + 1, 0);
            MPI.COMM_WORLD.Recv(array, 0, 20, MPI.INT, size - 1, 0);
            System.out.println("Я ПРОЦЕСС НОМЕР " + rank + ", ПОЛУЧЕНО СООБЩЕНИЕ ОТ ПРОЦЕССА " + (size - 1));
        } else {
            MPI.COMM_WORLD.Recv(array, 0, 20, MPI.INT, rank - 1, 0);
            System.out.println("Я ПРОЦЕСС НОМЕР " + rank + ", ПОЛУЧЕНО СООБЩЕНИЕ ОТ ПРОЦЕССА " + (rank - 1));
            int next = (rank + 1) % size;
            MPI.COMM_WORLD.Send(array, 0, 20, MPI.INT, next, 0);
        }
        MPI.COMM_WORLD.Barrier();
        if (rank == 0) {
            System.out.println("ВРЕМЯ ВЫПОЛНЕНИЯ " + (System.currentTimeMillis() - start) + " МИЛЛИСЕКУНД");
        }
        MPI.Finalize();
    }
}
