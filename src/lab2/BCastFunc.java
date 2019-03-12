package lab2;

import mpi.MPI;

import java.io.File;
import java.util.Scanner;

public class BCastFunc {
    public static void main(String[] args) throws Exception {
        MPI.Init(args); // initialization
        Scanner in = new Scanner(new File("src/lab2/input.txt"));
        int[] num = new int[1]; //array creation
        // 0 - main process
        do {
            if (MPI.COMM_WORLD.Rank() == 0) {
                num[0] = in.nextInt(); // reading an int from file
            }
            MPI.COMM_WORLD.Bcast(num, 0, 1, MPI.INT, 0); // cast an array to all other processes
            System.out.println("hello from process " + MPI.COMM_WORLD.Rank() + ", num = " + num[0]);
        } while (num[0] != 0);
        System.out.println("input equals zero, shutting down process " + MPI.COMM_WORLD.Rank());
        MPI.Finalize();
    }
}
