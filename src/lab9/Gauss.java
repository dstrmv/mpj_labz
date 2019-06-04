package lab9;

import mpi.MPI;

import java.util.Locale;
import java.util.Random;

public class Gauss {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        int m = 400;
        int n = 50;
        int tag = 1;


        double[][] a = new double[n][m + 1];
        double[] v = new double[m + 1];
        double mad;
        double[] r = new double[1];


        MPI.Init(args);

        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if ((n * rank + i) == j) {
                    a[i][j] = 2.0;
                } else {
                    a[i][j] = 1.0;
                }
            }

            a[i][m] = 1.0 * (m) + 1.0;
        }

        for (int p = 0; p < size; p++) {
            /* Цикл k - цикл по строкам. (Все веиви "крутят" этот цикл). */
            for (int k = 0; k < n; k++) {
                if (rank == p) {

                    /* Активная ветвь с номером rank == p приводит свои строки к
                     * диагональному виду.
                     * Активная строка k передается ветвям, с номером большим чем MyP */

                    mad = 1.0 / a[k][n * p + k];
                    for (int j = m; j >= n * p + k; j--)
                        a[k][j] = a[k][j] * mad;

                    for (int d = p + 1; d < size; d++)
                        MPI.COMM_WORLD.Send(a[k], 0, m + 1, MPI.DOUBLE, d, 1);

                    for (int i = k + 1; i < n; i++) {
                        for (int j = m; j >= m * p + k; j--)
                            a[i][j] = a[i][j] - a[i][n * p + k] * a[k][j];
                    }

                }

                /* Работа принимающих ветвей с номерами MyP > p */

                else if (rank > p) {
                    MPI.COMM_WORLD.Recv(v, 0, v.length, MPI.DOUBLE, p, 1);

                    for (int i = 0; i < n; i++) {
                        for (int j = m; j >= n * p + k; j--)
                            a[i][j] = a[i][j] - a[i][n * p + k] * v[j];
                    }
                }
            }        /* for k */
        }           /* for p */

        /* Обратный ход */
        /* Циклы по p и k аноалогичны, как и при прямом ходе. */

        for (int p = size - 1; p >= 0; p--) {
            for (int k = n - 1; k >= 0; k--) {

                /* Работа активной ветви */
                if (rank == p) {
                    for (int d = p - 1; d >= 0; d--)
                        MPI.COMM_WORLD.Send(a[k], m, 1, MPI.DOUBLE, d, tag);
                    for (int i = k - 1; i >= 0; i--)
                        a[i][m] -= a[k][m] * a[i][n * p + k];
                }

                /* Работа ветвей с номерами MyP < p */

                else {
                    if (rank < p) {
                        MPI.COMM_WORLD.Recv(r, 0, 1, MPI.DOUBLE, p, tag);
                        for (int i = n - 1; i >= 0; i--)
                            a[i][m] -= r[0] * a[i][n * p + k];
                    }
                }
            }               /* for k */

        }                    /* for p */

        /* Все ветви засекают время и печатают */

        System.out.printf("rank = %d %f %f %f %f\n", rank, a[0][m], a[1][m], a[2][m], a[3][m]);

        /* Все ветви завершают выполнение */


        MPI.Finalize();
    }


}

