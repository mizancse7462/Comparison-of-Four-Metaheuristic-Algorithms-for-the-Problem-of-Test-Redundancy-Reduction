

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;


public class SparrowSearchSCA_TRR {

    static int max_iteration = 20; // L_initial = initial looping Metropolis Cycle
    static int population = 100;
    static int dim = 16;

    static int x[][] = new int[population][dim];
    static int lb = 0;
    static int ub = 15;

    static String filename = "data0.txt";
    static String totalreqs = "";
    static String totaltests = "";
    static String test_entry = "";
    static ArrayList<String> testcase_list = new ArrayList<String>();
    static int[] prioritization = new int[100];
    static ArrayList<String> priority_list_for_seq = new ArrayList<String>();
    static ArrayList<String> final_priority_list_for_seq = new ArrayList<String>();
    static ArrayList<Integer> list_of_objective_value = new ArrayList<Integer>();
    static int length_of_objective_value = 31;
    static String cur_seq = "";

    static String cur_seq_long = "";

    static String global_best_seq = "";
    static boolean complete = false;
    static int iteration = 1; // default iteration

    static int px[][] = new int[population][dim];
    static Integer[] x_single = new Integer[dim];
    static int fitness[][] = new int[population][dim];
    static int fitness_seq_count[] = new int[population];
    static int best_x[] = new int[dim];
    static int worst_Index_X[] = new int[dim];
    static int producer_number = (int) (0.2 * population);

    static int arr[] = new int[dim];
    static ArrayList<Integer> index1 = new ArrayList<Integer>(population);

    static double best_Index_XX[] = new double[dim];
    static double[][] A = new double[1][dim];
    static double[][] AT = new double[dim][1];
    static int pFit[] = new int[population];
    static double pbest[] = new double[dim];
    ;//best fit value
    static double L[] = new double[dim];
    static double evaluationA[] = new double[2];

    static double fmin;

    public static void population_generation() {
        for (int i = 0; i < dim; i++) {
            x_single[i] = i;
        }

        String seq = "";
        for (int i = 0; i < population; i++) {
            List<Integer> suffle_Xsingle = Arrays.asList(x_single);
            Collections.shuffle(suffle_Xsingle);
            for (int j = 0; j < dim; j++) {
                x[i][j] = x_single[j];
                if (j == 0) {
                    seq = String.valueOf(x_single[0]);
                } else {
                    seq = seq + "," + x_single[j];
                }
            }
            String s = objective_function(seq);
            String[] arr = s.split(",");
            for (int m = 0; m < arr.length; m++) {
                fitness[i][m] = Integer.parseInt(arr[m]);
            }
            fitness_seq_count[i] = arr.length;
        }
    }


    public static int min_fit_value() {
        int min = fitness_seq_count[0];
        for (int i = 0; i < population; i++) {
            if (min > fitness_seq_count[i]) {
                min = fitness_seq_count[i];
            }
        }
        return min;
    }

    public static int best_fit_value_index() {
        double min = fitness_seq_count[0];
        int index = 0;
        for (int i = 0; i < population; i++) {
            if (min > fitness_seq_count[i]) {
                min = fitness_seq_count[i];
                index = i;
            }
        }
        return index;
    }

    public static void best_x(int best_index) {
        for (int i = 0; i < dim; i++) {
            best_x[i] = x[best_index][i];
        }
    }

    public static int large_fit_value() {
        int max = fitness_seq_count[0];
        for (int i = 0; i < population; i++) {
            if (max < fitness_seq_count[i]) {
                max = fitness_seq_count[i];
            }
        }
        return max;
    }

    public static int large_fit_index() {
        double max = fitness_seq_count[0];
        int index = 0;
        for (int i = 0; i < population; i++) {
            if (max < fitness_seq_count[i]) {
                max = fitness_seq_count[i];
                index = i;
            }
        }
        return index;
    }

    public static void x_value_clone() {
        for (int i = 0; i < population; i++) {
            for (int j = 0; j < dim; j++) {
                px[i][j] = x[i][j];
            }
        }
    }

    public static void large_X_value(int worst_index) {
        for (int i = 0; i < dim; i++) {
            worst_Index_X[i] = x[worst_index][i];
        }
    }

    public static String Bounds(int index_value) {
        List<Integer> list = new ArrayList<>();
        //System.out.println(" bound check ");
        for (int i = 0; i < dim; i++) {
            //System.out.print(" " + x[index_value][i]);
            list.add(x[index_value][i]);
        }
        List result = list.stream().filter(s -> s > ub).collect(Collectors.toList());
        List result1 = list.stream().filter(s -> s < lb).collect(Collectors.toList());

        missingValue(list, lb, ub);
        //System.out.println(" ");
        for (int i = 0; i < dim; i++) {
            arr[i] = list.get(i);
        }
        for (int i = 0; i < dim; i++) {
            for (int j = i + 1; j < dim; j++) {
                if (arr[i] == arr[j]) {
                    arr[j] = 1000;
                    //break;
                }

            }
        }

        for (int i = 0; i < result.size(); i++) {
            for (int j = 0; j < dim; j++) {
                if ((result.get(i).equals(list.get(j)))) {
                    arr[j] = 1000;
                }
            }
        }

        for (int i = 0; i < result1.size(); i++) {
            for (int j = 0; j < dim; j++) {
                if ((result1.get(i).equals(list.get(j)))) {
                    arr[j] = 1000;
                }
            }
        }

        int ii = 0;
        for (int j = 0; j < dim; j++) {
            if (arr[j] == 1000) {
                arr[j] = index1.get(ii);
                ii++;
            }
        }

        String seq = "";
        //System.out.println(" ");
        //System.out.println(" after bound check");
        for (int i = 0; i < dim; i++) {
            x[index_value][i] = arr[i];
            //System.out.print(" " + x[index_value][i]);
            if (i == 0) {
                seq = String.valueOf(x[index_value][i]);
            } else {
                seq = seq + "," + x[index_value][i];
            }
        }
        //System.out.println(" seq after bound check " + seq);
        return seq;
    }

    public static void missingValue(List<Integer> list, int low, int high) {

        index1.clear();
        boolean[] points_of_range = new boolean[high - low + 1];

        for (int i = 0; i < list.size(); i++) {
            if (low <= list.get(i) && list.get(i) <= high) {
                points_of_range[list.get(i) - low] = true;
            }
        }

        for (int x = 0; x <= high - low; x++) {
            if (points_of_range[x] == false) {
                index1.add(low + x);
                //System.out.print(" missing value " + (low + x));

            }
        }

    }

    public static void best_index_XX_value(int best_index) {
        for (int i = 0; i < dim; i++) {
            best_Index_XX[i] = x[best_index][i];
        }
    }

    public static void L_array_initialization() {
        for (int i = 0; i < dim; i++) {
            L[i] = 1;
        }
    }

    public static void fit_value_clone() {
        for (int i = 0; i < population; i++) {
            pFit[i] = fitness_seq_count[i];
        }
    }

    public static void ssa() {

        // pFit
        //minimum index value print kora
        double large_fit_value;
        int large_fit_index;
        double r2, r1, Q;
        Random r = new Random();
//////////////////////////////////main loop for work///////////////////////////
        fmin = min_fit_value(); ///fit value theke ber kora
        int best_i = best_fit_value_index(); //fit value theke 
        best_x(best_i);//x theke ber korci
        //sort_index(); //argsort er kaj kora hoice using pFit. pfit value same ace
        Integer sort_index[] = new Integer[population];
        for (int m = 0; m < population; m++) {
            sort_index[m] = m;
        }
        List<Integer> suffle_new1 = Arrays.asList(sort_index);
        Collections.shuffle(suffle_new1);

        large_fit_value = large_fit_value(); // pfit value theke count kora 

        large_fit_value = large_fit_value();
        large_fit_index = large_fit_index();
        large_X_value(large_fit_index);
        r2 = r.nextDouble();
        if (r2 < 0.8) // there is no predator sparrow go for global search
        {
            for (int i = 0; i < producer_number; i++) {
                r1 = r.nextDouble();
                for (int j = 0; j < dim; j++) {
                    x[sort_index[i]][j] = (int) (px[sort_index[i]][j] * Math.exp(-(i) / (r1 * max_iteration)));
                }

                String seq = Bounds(sort_index[i]);
                String seq_new = objective_function(seq);
                String[] arr = seq_new.split(",");
                if (fitness_seq_count[sort_index[i]] > arr.length) {
                    for (int m = 0; m < dim; m++) {
                        if (m < arr.length) {
                            fitness[sort_index[i]][m] = Integer.parseInt(arr[m]);
                        } else {
                            fitness[sort_index[i]][m] = 10000;
                        }
                        // error hower sombabona ace. tahole m er vlue dimention pojjonto check korte hobe
                    }
                    fitness_seq_count[sort_index[i]] = arr.length;
                }

            }
        } else if (r2 >= 0.8)/////// some sparrow detect predator
        {
            for (int i = 0; i < producer_number; i++) {
                Q = r.nextGaussian();
                for (int j = 0; j < dim; j++) {
                    x[sort_index[i]][j] = (int) Math.abs(px[sort_index[i]][j] + 1 * Q);
                }

                String seq = Bounds(sort_index[i]);
                String seq_new = objective_function(seq);
                String[] arr = seq_new.split(",");
                if (fitness_seq_count[sort_index[i]] > arr.length) {
                    for (int m = 0; m < dim; m++) {
                        if (m < arr.length) {
                            fitness[sort_index[i]][m] = Integer.parseInt(arr[m]);
                        } else {
                            fitness[sort_index[i]][m] = 20000;
                        }
                        // error hower sombabona ace. tahole m er vlue dimention pojjonto check korte hobe
                    }
                    //System.out.println(" ");
                    fitness_seq_count[sort_index[i]] = arr.length;
                }

            }
        }
        // equataion 4

        int i;
        //L_array_initialization();
        int best_fit_value_indexII = best_fit_value_index();
        best_index_XX_value(best_fit_value_indexII);
        /////// The position update formula for the scrounger 
        for (int ii = 0; ii < (population - producer_number); ii++) {
            i = ii + producer_number;
            for (int jj = 0; jj < dim; jj++) {
                A[0][jj] = Math.floor((r.nextDouble()) * 2) * 2 - 1;
                AT[jj][0] = A[0][jj];
            }
            double sum = 0;
            for (int jj = 0; jj < dim; jj++) {
                sum = sum + A[0][jj] * AT[jj][0];
            }

            //System.out.println(" sum "+sum);
            if (i > (population / 2))//ith scrounger with the worse fitness value is most likely to be starving
            {
                Q = r.nextGaussian();// Normal distribution range +_15
                for (int q = 0; q < dim; q++) {
                    x[sort_index[i]][q] = (int) Math.round(Math.abs(Q * (Math.exp((worst_Index_X[q] - px[sort_index[i]][q]) / (i * i)))));
                }
            } else {
                for (int q = 0; q < dim; q++) {
                    x[sort_index[i]][q] = (int) Math.round(Math.abs(best_Index_XX[q] + (Math.abs(px[sort_index[i]][q] - best_Index_XX[q]) * (AT[q][0] * (1 / sum)) * L[q])));;
                }
            }
            //Bounds(sort_index[i]);
            //fit_value[sort_index[i]][0] = Single_Row_Fit(sort_index[i]);

            String seq = Bounds(sort_index[i]);
            String seq_new = objective_function(seq);
            String[] arr = seq_new.split(",");

            if (fitness_seq_count[sort_index[i]] > arr.length) {
                //System.out.println(" zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz ");
                for (int m = 0; m < dim; m++) {
                    if (m < arr.length) {
                        fitness[sort_index[i]][m] = Integer.parseInt(arr[m]);
                    } else {
                        fitness[sort_index[i]][m] = 30000;
                    }
                    // error hower sombabona ace. tahole m er vlue dimention pojjonto check korte hobe
                }
                fitness_seq_count[sort_index[i]] = arr.length;
            }

        }
        //equation 5/////////////////////////        
        ///sort index random
        Integer suffle_array[] = new Integer[population];
        Random rx = new Random();
        for (int m = 0; m < population; m++) {
            suffle_array[m] = m;
        }
        List<Integer> suffle_new = Arrays.asList(suffle_array);
        Collections.shuffle(suffle_new);
        int scout = (int) (population * 0.2);
        int b[] = new int[scout];
        for (int m = 0; m < scout; m++) {
            b[m] = suffle_array[m];
        }
        for (int j = 0; j < scout; j++) {
            if (pFit[sort_index[b[j]]] > fmin) {
                //System.out.println(" if value of eq 5 ");
                for (int k = 0; k < dim; k++) {
                    double B = r.nextGaussian();
                    x[sort_index[b[j]]][k] = (int) Math.abs(best_x[k] + (B * Math.abs(px[sort_index[b[j]]][k] - best_x[k])));
                    //System.out.print(" " + x[sort_index[b[j]]][k]);
                }
                //System.out.println(" ");
            } else {
                //System.out.println(" else value of eq 5 ");
                for (int k = 0; k < dim; k++) {
                    double kk = r.nextDouble();
                    x[sort_index[b[j]]][k] = (int) Math.abs(px[sort_index[b[j]]][k] + (2 * kk - 1) * (Math.abs(px[sort_index[b[j]]][k] - worst_Index_X[k]) / ((pFit[sort_index[b[j]]] - large_fit_value) + Math.pow(10, -50))));
                    //System.out.print(" " + x[sort_index[b[j]]][k]);
                }
                //System.out.println(" ");
            }
            String seq = Bounds(sort_index[b[j]]);
            String seq_new = objective_function(seq);
            String[] arr = seq_new.split(",");

            if (fitness_seq_count[sort_index[b[j]]] > arr.length) {
                for (int m = 0; m < dim; m++) {
                    if (m < arr.length) {
                        fitness[sort_index[b[j]]][m] = Integer.parseInt(arr[m]);
                    } else {
                        fitness[sort_index[b[j]]][m] = 10000;
                    }
                }
                fitness_seq_count[sort_index[b[j]]] = arr.length;
            }
        }
        //System.out.println(" ");
        for (int ii = 0; ii < population; ii++) {
            if (fitness_seq_count[ii] < pFit[ii]) {
                pFit[ii] = fitness_seq_count[ii];
                for (int iii = 0; iii < dim; iii++) {
                    px[ii][iii] = x[ii][iii];
                }
            }
            if (pFit[ii] < fmin) {
                fmin = pFit[ii];
                //System.out.println(" fmin value " + fmin);
                for (int iii = 0; iii < dim; iii++) {
                    best_x[iii] = px[ii][iii];
                }
            }
        }

    }

    public static double[] calculate_XPT_XPL() {
        double median = 0;
        double medianPop[] = new double[dim];
        double div[] = new double[dim];
        double divFinal = 0;

        // median calculate
        for (int j = 0; j < dim; j++) {
            for (int i = 0; i < population; i++) {
                median = median + x[i][j];
            }
            medianPop[j] = median / population;
            //System.out.println("");
        }
        // divj calculate
        double temp = 0;

        for (int j = 0; j < dim; j++) {
            for (int i = 0; i < population; i++) {
                temp = temp + Math.abs(medianPop[j] - x[i][j]);
            }
            div[j] = temp / population;
        }

        // div calculation
        double divMax = div[0];

        for (int j = 0; j < dim; j++) {
            divFinal = divFinal + div[j];
            if (divMax < div[j]) {
                divMax = div[j];
            }

        }
        divFinal = divFinal / population;
        double evaluationA[] = new double[2];
        double XPL = (divFinal / divMax) * 100;
        evaluationA[0] = XPL;
        //System.out.println("  XPL value is = " + XPL);
        double XPT = (Math.abs(divFinal - divMax) / divMax) * 100;
        evaluationA[1] = XPT;
        //System.out.println(" XPT value = " + XPT);

        return evaluationA;
    }

    public static void sca(int iteration) {

        double r1, r2, r3, r4;
        double M = 10;
        //System.out.println(" Sine Cosine ");

        r1 = M * (1 - iteration / max_iteration);
        //System.out.println(" iteration " + iteration);
        for (int i = 0; i < population; i++) // iterate the population
        {
            int Pbest_index = best_fit_value_index();

            best_x(Pbest_index);
            Random r = new Random();
            r2 = r.nextDouble();
            r3 = r.nextDouble();
            r4 = r.nextDouble();

            int x_new = 0;

            if (r4 < 0.5) {
                for (int j = 0; j < dim; j++) {
                    x_new = (int) (x[i][j] + (r1 * Math.sin(180 * r2 / Math.PI) * Math.abs((r3 * best_x[j]) - x[i][j])));

                        x_single[j] = x_new;
                        //System.out.println(" else " + xSingle[j]);
                    
                }
                String seq = Bounds(i);
                String seqsplit[]=seq.split(",");
                //System.out.println(" ");
                //System.out.println(" seq send " + seq);
                String s = objective_function(seq);
                String[] arr = s.split(",");
                //System.out.println(" previous fitness value " + fitness_seq_count[i]);
                // System.out.println(" new fit value " + arr.length);
                if (fitness_seq_count[i] > arr.length) {
                    fitness_seq_count[i] = arr.length;
                    //System.out.println(" new fitness value is better then previous ");
                    //fitness.set(i, null);
                    for (int n = 0; n < dim; n++) {
                        x[i][n] = Integer.parseInt(seqsplit[n]);
                        if (n < arr.length) {
                            fitness[i][n] = Integer.parseInt(arr[n]);
                        } else {
                            fitness[i][n] = 10000;
                        }
                    }
                }
            } else {
                for (int j = 0; j < dim; j++) {
                    x_new = (int) (x[i][j] + (r1 * Math.cos(180 * r2 / Math.PI) * Math.abs((r3 * best_x[j]) - x[i][j])));

                        x_single[j] = x_new;
                    
                }
                String seq = Bounds(i);
                //System.out.println("---------------------------------------");
                //System.out.println(" ");
                //System.out.println(" seq send " + seq);
                String s = objective_function(seq);
                String[] arr = s.split(",");
                //System.out.println(" previous fitness value " + fitness_seq_count[i]);
                //System.out.println(" new fit value " + arr.length);
                if (fitness_seq_count[i] > arr.length) {
                    fitness_seq_count[i] = arr.length;
                    //System.out.println(" new fitness value is better then previous ");
                    for (int n = 0; n < dim; n++) {
                        x[i][n] = x_single[n];
                        if (n < arr.length) {
                            fitness[i][n] = Integer.parseInt(arr[n]);
                        } else {
                            fitness[i][n] = 10000;
                        }
                    }
                }
            }

        }

        //calculate_XPT_XPL();   
    }

    public static void main(String args[]) throws IOException {

        filename = filename.trim();
        process_input_file(filename, testcase_list, prioritization);

        population_generation();
        //System.out.println(" mizan ");
        fit_value_clone();
        x_value_clone();//px


        double XPLinitial[] = calculate_XPT_XPL();
        System.out.println(" initial xpl " + XPLinitial[0]);
        System.out.println(" initial xpt " + XPLinitial[1]);
        ssa();
        evaluationA = calculate_XPT_XPL();
        //double evaluationA[] = calculate_XPT_XPL();
        System.out.println(" initial xpl SSA " + evaluationA[0]);
        System.out.println(" initial xpt SSA " + evaluationA[1]);
        for (int t = 0; t < max_iteration; t++) {
            System.out.println(" difference " + Math.abs(XPLinitial[0] - evaluationA[0]));
            //explore more
            if ((XPLinitial[0] > evaluationA[0])) {
                System.out.println(" SSA " + t);
                System.out.println(" previouse xpl value SSA " + XPLinitial[0]);
                System.out.println(" previous xpt SSA " + XPLinitial[1]);
                ssa();
                XPLinitial[0] = evaluationA[0];
                XPLinitial[1] = evaluationA[1];

                evaluationA = calculate_XPT_XPL();
                
                                System.out.println(" new xpl SSA " + evaluationA[0]);
                System.out.println(" new xpt SSA " + evaluationA[1]);
                System.out.println(" ");

            } else if (XPLinitial[0] < evaluationA[0]) {// exploite
                System.out.println(" SCA " + t);
                System.out.println(" previouse xpl value SCA " + XPLinitial[0]);
                System.out.println(" previous xpt SCA " + XPLinitial[1]);
                sca(t);
                XPLinitial[0] = evaluationA[0];
                XPLinitial[1] = evaluationA[1];

                evaluationA = calculate_XPT_XPL();
                System.out.println(" new xpl SCA " + evaluationA[0]);
                System.out.println(" new xpt SCA " + evaluationA[1]);
                System.out.println(" ");
            } else {
                System.out.println(" random restart " + t);
                population_generation();
                System.out.println(" new xpl RR " + XPLinitial[0]);
                System.out.println(" new xpt RR " + XPLinitial[1]);
                XPLinitial[0] = evaluationA[0];
                XPLinitial[1] = evaluationA[1];

                evaluationA = calculate_XPT_XPL();
                System.out.println(" new xpl RR " + evaluationA[0]);
                System.out.println(" new xpt RR " + evaluationA[1]);
                System.out.println(" ");
            }

        }

        int min_fit = fitness_seq_count[0];
        int fitness_index = 0;
        for (int i = 0; i < population; i++) {
            if (min_fit > fitness_seq_count[i]) {
                min_fit = fitness_seq_count[i];
                fitness_index = i;
            }
        }
        
        //int best_solution = find_best_index();

        String seq1 = "";
        for (int i = 0; i < fitness_seq_count[fitness_index]; i++) {
            if (i == 0) {
                seq1 = String.valueOf(fitness[fitness_index][i]);
            } else {
                seq1 = seq1 + "," + fitness[fitness_index][i];
            }
        }

        System.out.println("  final seq " + seq1);
        
        priority_list_for_seq = get_priority_list(seq1);
            if (is_recommended(priority_list_for_seq)) {

                final_priority_list_for_seq = priority_list_for_seq;
                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                System.out.println(" [Prioritized] Global Best Sequence =>" + seq1);
                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                display_test_cases(seq1);
            } else {

                priority_list_for_seq = get_priority_list(seq1);
                global_best_seq = sort_final_seq(seq1, priority_list_for_seq,
                        final_priority_list_for_seq);
                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                System.out.println("[Prioritized/Sorted] Global Best Sequence = " + seq1);
                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                display_test_cases(seq1);
            }
        double percent_saving = (double) (100 * (dim - objective_value(seq1)) / dim);
        System.out.println("[Percent reduction] = " + percent_saving + "%");

    }

    public static String sort_final_seq(String seq,
            ArrayList<String> priority_list_for_seq,
            ArrayList<String> final_priority_list_for_seq) {
        String sorted_sequence = "";
        int[] num = {0, 1};
        int[][] multimap = new int[priority_list_for_seq.size()][priority_list_for_seq.size()];
        boolean flag = true;   // set flag to true to begin first pass
        int temp1, temp2;   //holding variable
        int j = 0;

        // setup 2-d array
        String[] m = seq.split(",");
        for (int y = 0; y < m.length; y++) {
            multimap[y][0] = Integer.parseInt(m[y]);
            multimap[y][1] = Integer.parseInt(priority_list_for_seq.get(y));
        }

        // sort the multimap
        int length = priority_list_for_seq.size();
        while (flag) {
            flag = false;    //set flag to false awaiting a possible swap
            for (j = 0; j < length - 1; j++) {
                if (multimap[j][1] > multimap[j + 1][1]) // change to < for descending sort
                {
                    temp1 = multimap[j][1];                //swap elements
                    temp2 = multimap[j][0];
                    multimap[j][1] = multimap[j + 1][1];
                    multimap[j][0] = multimap[j + 1][0];
                    multimap[j + 1][1] = temp1;
                    multimap[j + 1][0] = temp2;
                    flag = true;              //shows a swap occurred
                }
            }
        }

        // reconstruct sorted sequences and assign final priority list
        for (int i = 0; i < length; i++) {
            if (i < length - 1) {
                sorted_sequence = sorted_sequence + Integer.toString(multimap[i][0]) + ",";
            } else {
                sorted_sequence = sorted_sequence + Integer.toString(multimap[i][0]);
            }

            final_priority_list_for_seq.add(Integer.toString(multimap[i][1]));
        }
        return (sorted_sequence);
    }

    //////////////////////////////////////////////////////////////
    //  Display test cases based on sequence
    /////////////////////////////////////////////////////////////
    private final static void display_test_cases(String final_seq) {
        int index = 0;
        StringTokenizer st = new StringTokenizer(final_seq, ",");
        while (st.hasMoreTokens()) {
            String test_id = st.nextToken();
            String test_case = testcase_list.get(Integer.parseInt(test_id));
            System.out.format("[t" + test_id + "] = " + test_case);
            System.out.println("\t[Priority]  = " + final_priority_list_for_seq.get(index));
            index++;
        }

    }

    /////////////////////////////////////////////////
    //  Recommend the best selection of test sequence
    /////////////////////////////////////////////////
    public static boolean is_recommended(ArrayList<String> priority_list) {
        boolean recommend = true;
        for (int i = 1; i < priority_list.size(); i++) {
            if (Integer.parseInt(priority_list.get(i - 1)) > (Integer.parseInt(priority_list.get(i)))) {
                recommend = false;
            }
        }

        return recommend;
    }
    ///////////////////////////////////////////////
    // Get priority list in a given test sequence
    ///////////////////////////////////////////////

    public static ArrayList<String> get_priority_list(String sequence) {
        ArrayList<String> my_list = new ArrayList<String>();
        int weight_total = 0;
        String[] m = sequence.split(",");
        for (int y = 0; y < m.length; y++) {
            String tc = testcase_list.get(Integer.parseInt(m[y]));
            //System.out.println (tc);
            String[] clmn = tc.split(":");
            weight_total = 0;
            for (int z = 0; z < clmn.length; z++) {
                if (clmn[z].trim().equals("1")) {
                    weight_total = weight_total + prioritization[z];
                }
            }
            my_list.add(Integer.toString(weight_total));

        }

        return (my_list);
    }

    //////////////////////////////////////////////////////////////
    //  Display any string array list for debugging purposes
    /////////////////////////////////////////////////////////////
    private final static void display_list(String title, ArrayList<String> list) {
        int i = 0;
        System.out.println(title);
        for (Iterator it = list.iterator(); it.hasNext();) {
            String s = (String) it.next();  // Downcasting is required pre Java 5.
            System.out.println(s);
            i++;
        }
    }

    ///////////////////////////////////////////////////////////
    //  Process File Input
    ///////////////////////////////////////////////////////////
    private static void process_input_file(String filename,
            ArrayList<String> testcase_list,
            int prioritization[])
            throws IOException {
        RandomAccessFile f = new RandomAccessFile(filename, "rw");
        long length = f.length();
        long position = 0;
        long old_position = 0;
        int id = 0;
        String content;

        // rewind file to position 0
        f.seek(0);
        while (position < length) {
            old_position = position;
            content = f.readLine();

            // parse all the data for processing
            StringTokenizer s = new StringTokenizer(content, "=");
            while (s.hasMoreTokens()) {
                String string_val = s.nextToken();
                if (string_val.trim().equals("priority".trim())) {
                    String plist = s.nextToken().trim();
                    String[] wl = plist.split(":"); // tokenize the weight
                    for (int z = 0; z < wl.length; z++) {
                        prioritization[z] = Integer.parseInt(wl[z]);
                    }

                } else if (string_val.trim().equals("totalreqs".trim())) {
                    totalreqs = s.nextToken().trim();

                } else if (string_val.trim().equals("totaltests".trim())) {
                    totaltests = s.nextToken().trim();

                } else if (string_val.trim().equals("iteration".trim())) {
                    iteration = Integer.parseInt(s.nextToken().trim());
                } else if (string_val.trim().equals(("t" + Integer.toString(id)).trim())) {
                    String test_entry = s.nextToken().trim();
                    testcase_list.add(test_entry);
                    id++;
                }

            }
            position = f.getFilePointer();
        }
        f.close();
    }

    //////////////////////////////////////////////////////////////
    // Generate random number between 0 and 1 for probabilistic function
    //////////////////////////////////////////////////////////////
    public static float getFloatRandomNumberBetween(float numberOne, float numberTwo) {

        float rand = (float) Math.random();
        float highRange = Math.max(numberOne, numberTwo);
        float lowRange = Math.min(numberOne, numberTwo);

        float lowRand = (float) Math.floor(rand - 1);
        float highRand = (float) Math.ceil(rand + 1);

        float genRand = (highRange - lowRange) * ((rand - lowRand) / (highRand - lowRand)) + lowRange;

        return genRand;
    }

    //////////////////////////////////////////////////////////////
    // Initialize the sequence based on the number of test cases
    /////////////////////////////////////////////////////////////
    public static String init_sequence(ArrayList<String> testcase_list) {
        String myseq = "";
        for (int i = 0; i < testcase_list.size(); i++) {
            if (i < testcase_list.size() - 1) {
                myseq = myseq + Integer.toString(i) + ",";
            } else {
                myseq = myseq + Integer.toString(i);
            }
        }
        //dummy myseq = "4,28,12,5,3,2,6,9,17,10,11,0,1,7,8,13,14,15,16,18,19,20,21,22,23,24,25,26,27,29,30";
        return myseq;
    }

    //////////////////////////////////////////////////////////////
    // Generate random neighbour sequence based on a give sequence
    /////////////////////////////////////////////////////////////
    public static String generate_random_sequence(String sequence) {

        String new_sequence = "";
        String[] item = sequence.split(",");

        int[] ar = new int[item.length];
        int d, tmp;
        Random generator = new Random();

        // assign each number seq to an array
        for (int i = 0; i < item.length; i++) {
            ar[i] = Integer.parseInt(item[i]);
        }

        // swap with new ar with random index
        for (int i = 0; i < item.length; i++) {
            d = i + (generator.nextInt() & (item.length - 1 - i));
            tmp = ar[i];
            ar[i] = ar[d];
            ar[d] = tmp;
        }

        // reconstruct new sequences
        for (int i = 0; i < item.length; i++) {
            if (i < item.length - 1) {
                new_sequence = new_sequence + Integer.toString(ar[i]) + ",";
            } else {
                new_sequence = new_sequence + Integer.toString(ar[i]);
            }
        }

        return new_sequence;

    }

    //////////////////////////////////////////////////////////////
    // Calculate energy a.k.a. objective value
    // only meaningful after objective function call
    /////////////////////////////////////////////////////////////
    public static int objective_value(String s) {

        String[] plist = s.split(","); //split sequence
        return (plist.length);

    }

    //////////////////////////////////////////////////////////////
    // Objective function - based on reduce sequence
    // concatenate long sequences
    /////////////////////////////////////////////////////////////
    public static String objective_function(String sequence) {
        //System.out.println(" mizan ");
        int count = 0;
        String[] plist = sequence.split(","); //split sequence
        //System.out.println(" plist length "+plist.length);
        String answer = "";
        String cur_seq = ""; // track of current seq to break early
        for (int j = 0; j < plist.length; j++) {
            if (j == 0) {
                //System.out.println(" inside if of objective ");
                answer = testcase_list.get(Integer.parseInt(plist[0]));
                cur_seq = plist[0];
                count++;
            } else {
                answer = merge_elements(answer, testcase_list.get(Integer.parseInt(plist[j])));
                cur_seq = cur_seq + "," + plist[j];
                count++;
            }

            if (complete_merge_sequence(answer)) {
                break;
            }
        }

        return cur_seq;

    }

    ////////////////////////////////////////////////////////////
    // Merge elements
    ///////////////////////////////////////////////////////////
    public static String merge_elements(String s1, String s2) {
        String value = new String("NULL");
        String v = new String();

        String[] result1 = s1.split(":");
        String[] result2 = s2.split(":");

        for (int x = 0; x < result1.length; x++) {

            if (result1[x].equals(result2[x])) {
                if (x == 0) {
                    v = result1[x];
                } else {
                    v = v + ":" + result1[x];
                }
            } else if (result1[x].equals("0") && result2[x].equals("1")) {
                if (x == 0) {
                    v = result2[x];
                } else {
                    v = v + ":" + result2[x];
                }
            } else if (result1[x].equals("1") && result2[x].equals("0")) {
                if (x == 0) {
                    v = result1[x];
                } else {
                    v = v + ":" + result1[x];
                }
            }
        }

        value = v;
        return value;
    }
    //////////////////////////////////////////////////////////
    //  Check for Complete Sequence
    //////////////////////////////////////////////////////////

    public static boolean complete_merge_sequence(String s1) {
        String[] result1 = s1.split(":");
        boolean outcome = true;
        for (int x = 0; x < result1.length; x++) {
            if (result1[x].equals("0")) {
                outcome = false;
                break;
            }
        }
        return outcome;
    }

}
