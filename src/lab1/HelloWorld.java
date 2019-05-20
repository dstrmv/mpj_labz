package lab1;

import mpi.*;

public class HelloWorld {
    public static void main(String[] args) {

        int rank, size, resultlen;
        double startwtime = 0.0, endwtime;
        String name;

        MPI.Init(args); // MPI initialization
        startwtime = MPI.Wtime(); // system time
        size = MPI.COMM_WORLD.Size(); // total amount of processes
        rank = MPI.COMM_WORLD.Rank(); // process's number
        name = MPI.Get_processor_name(); // pc name
        endwtime = MPI.Wtime();
        System.out.printf("Hello world from process %d of %d at %s as %f second \n",
                rank, size, name, endwtime - startwtime);
        MPI.Finalize(); // finish parallel part of program
    }
}
