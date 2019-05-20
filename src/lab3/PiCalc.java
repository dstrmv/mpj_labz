package lab3;

import mpi.*;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Scanner;

public class PiCalc {
    public static void main(String[] args) throws Exception {
        Scanner in = new Scanner(new File("src/lab3/input.txt"));
        int scale = in.nextInt(); // precision
        int iter = in.nextInt(); // iteration num
        // o is an input array, o1 is a reduce array (accumulator)
        User_function function = new User_function() {
            @Override
            public void Call(Object o, int i, Object o1, int i1, int i2, Datatype datatype) throws MPIException {
                BigDecimal arg = ((BigDecimal[]) o)[0];
                Object[] resultArray = ((Object[]) o1);
                BigDecimal accumulator = (BigDecimal) resultArray[0];
                resultArray[0] = accumulator.add(arg);
            }
        };
        // user defined reduce operation
        Op op = new Op(function, false);
        BigDecimal[] res = {BigDecimal.ZERO};
        MPI.Init(args);
        int size = MPI.COMM_WORLD.Size(); // proc amnt
        int rank = MPI.COMM_WORLD.Rank(); // proc num
        // just for equal amount of iterations for each process (like (170 / 4) * 4 == 168 )
        iter = (iter / size) * size;
        int processStep = iter / size; // iter amnt for each proc
        BigDecimal[] stepRes = {BigDecimal.ZERO}; // start from zero
        stepRes[0] = calculatePi(rank * processStep, (rank + 1) * processStep, scale); // calculating for each process
        MPI.COMM_WORLD.Reduce(stepRes, 0, res, 0, 1, MPI.OBJECT, op, 0); // sum all in res
        MPI.Finalize(); // shutting down
        if (rank == 0) {
            System.out.println(res[0]);
            System.out.println(res[0].equals(calculatePi(0, iter, scale))); // correct check
        }
    }

    // Bailey–Borwein–Plouffe formula
    private static BigDecimal calculatePi(int start, int end, int scale) {
        BigDecimal e = BigDecimal.ZERO;
        for (int k = start; k < end; k++) {
            BigDecimal a0 = new BigDecimal(16).pow(k);
            BigDecimal a1 = new BigDecimal(4).divide(new BigDecimal(8 * k + 1), scale, RoundingMode.HALF_UP);
            BigDecimal a2 = new BigDecimal(2).divide(new BigDecimal(8 * k + 4), scale, RoundingMode.HALF_UP);
            BigDecimal a3 = new BigDecimal(1).divide(new BigDecimal(8 * k + 5), scale, RoundingMode.HALF_UP);
            BigDecimal a4 = new BigDecimal(1).divide(new BigDecimal(8 * k + 6), scale, RoundingMode.HALF_UP);
            BigDecimal a5 = a1.subtract(a2).subtract(a3).subtract(a4);
            BigDecimal a6 = BigDecimal.ONE.divide(a0, scale, RoundingMode.HALF_UP);
            BigDecimal elem = a5.multiply(a6);
            e = e.add(elem);
        }
        return e;
    }
}
