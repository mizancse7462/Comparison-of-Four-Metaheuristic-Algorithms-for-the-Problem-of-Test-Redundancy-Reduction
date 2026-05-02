
/**
 *
 * @author mizan
 */
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class ssa_original_trr {

    static String filename = "data0.txt";
    static String totalreqs = "";
    static String totaltests = "";
    static String test_entry = "";
    static ArrayList<String> testcase_list = new ArrayList<String>();
    static int[] prioritization = new int[100];
    static ArrayList<String> priority_list_for_seq = new ArrayList<String>();
    static ArrayList<String> final_priority_list_for_seq = new ArrayList<String>();
    static ArrayList<Integer> list_of_objective_value = new ArrayList<Integer>();
    static String cur_seq = "";
    static String local_best_seq = "";
    static String global_best_seq = "";
    static boolean already_prioritized_sequence = false;

    static int max_iteration = 50; // L_initial = initial looping Metropolis Cycle
    static int population = 50;
    static int dim = 16;
    static int x[][] = new int[population][dim];
    static int px[][] = new int[population][dim];
    static Integer[] x_single = new Integer[dim];
    static int fitness[][] = new int[population][dim];
    static int fitness_seq_count[] = new int[population];
    static int best_x[] = new int[dim];
    static int worst_Index_X[] = new int[dim];
    static int producer_number = (int) (0.2 * population);
    static int lb = 0;
    static int ub = 15;
    static int arr[] = new int[dim];//
    static ArrayList<Integer> index1 = new ArrayList<Integer>(population);
    static int best_Index_XX[] = new int[dim];
    static int[][] A = new int[1][dim];
    static int[][] AT = new int[dim][1];
    static int L[] = new int[dim];
    static int pFit[] = new int[population];

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
            //System.out.print(" best x"+best_x[i]);
        }
        //System.out.println("");
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

    public static void main(String[] args) throws IOException {
        int output_limit = 30;
        filename = filename.trim();
        process_input_file(filename, testcase_list, prioritization);
        double temp_percent[] = new double[output_limit];
        long lStartTime = System.currentTimeMillis();
        for (int tt = 0; tt < output_limit; tt++) {

            population_generation();
//min fitness value from fitness sequence
            x_value_clone();

            //fit_sequence_value();
            fit_value_clone();
            int fmin = min_fit_value();
            //System.out.println(" fmin initial " + fmin);
            int best_i = best_fit_value_index();
            best_x(best_i);
            L_array_initialization();
            int large_fit_value;
            int large_fit_index;
            double r2, Q, r1;
            Random r = new Random();
            
            for (int t = 0; t < max_iteration; t++) {
                Integer sort_index[] = new Integer[population];
                for (int m = 0; m < population; m++) {
                    sort_index[m] = m;
                }
                List<Integer> suffle_new1 = Arrays.asList(sort_index);
                Collections.shuffle(suffle_new1);

                large_fit_value = large_fit_value();
                large_fit_index = large_fit_index();
                large_X_value(large_fit_index);
                r2 = r.nextDouble();

                if (r2 < 0.8) {
                    //System.out.println(" x value r2<0.8 ");
                    for (int i = 0; i < producer_number; i++) {
                        r1 = r.nextDouble();
                        for (int j = 0; j < dim; j++) {
                            x[sort_index[i]][j] = (int) (px[sort_index[i]][j] * Math.exp(-(i) / (r1 * max_iteration)));
                            //System.out.print(" " + x[sort_index[i]][j]);
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
                } else if (r2 >= 0.8) {
                    //System.out.println(" x value r2>0.8 ");
                    for (int i = 0; i < producer_number; i++) {
                        // originally random number from 0 to 1 within range 
                        Q = r.nextGaussian();
                        for (int j = 0; j < dim; j++) {
                            x[sort_index[i]][j] = (int) Math.abs(px[sort_index[i]][j] + 1 * Q);
                            //System.out.print(" " + x[sort_index[i]][j]);
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
                int best_fit_value_indexII = best_fit_value_index();
                best_index_XX_value(best_fit_value_indexII);
                int i;
                /////// The position update formula for the scrounger 
                for (int ii = 0; ii < (population - producer_number); ii++) {
                    i = ii + producer_number;
                    for (int jj = 0; jj < dim; jj++) {
                        A[0][jj] = (int) (Math.floor((r.nextDouble()) * 2) * 2 - 1);
                        //System.out.println(" A " + A[0][jj]);
                        AT[jj][0] = A[0][jj];
                    }
                    double sum = 0;
                    for (int jj = 0; jj < dim; jj++) {
                        sum = (sum + A[0][jj] * AT[jj][0]);
                    }

                    //System.out.println(" sum " + sum);
                    if (i > (population / 2))//ith scrounger with the worse fitness value is most likely to be starving
                    {
                        //System.out.println(" eq 4 iffffffffffffffff ");

                        Q = r.nextGaussian();
                        for (int q = 0; q < dim; q++) {
                            //Q = r.nextGaussian();
                            x[sort_index[i]][q] = (int) Math.round(Math.abs(Q * (Math.exp((worst_Index_X[q] - px[sort_index[i]][q]) / (i * i)))));
                            //System.out.println(" original value " + Q * (Math.exp((worst_Index_X[q] - px[sort_index[i]][q]) / (i * i))));
                            //System.out.println(" math.exp " + Math.exp((worst_Index_X[q] - px[sort_index[i]][q]) / (i * i)));
                            //System.out.println(" worst x " + worst_Index_X[q]);
                            //System.out.println(" px " + px[sort_index[i]][q]);
                            //System.out.println(" i value " + i);
                            //System.out.print(" " + x[sort_index[i]][q]);
                            //System.out.println(" q value " + Q);
                        }
                        //System.out.println(" ");
                    } else {
                        //System.out.println(" eq 4 elsexxxxxxxxxxxx ");
                        for (int q = 0; q < dim; q++) {
                            x[sort_index[i]][q] = (int) Math.round(Math.abs(best_Index_XX[q] + (Math.abs(px[sort_index[i]][q] - best_Index_XX[q]) * (AT[q][0] * (1 / sum)) * L[q])));
                            //System.out.print(" " + x[sort_index[i]][q]);

                        }
                        //System.out.println(" ");
                    }
                    //System.out.println(" equation 4 ");
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

                Integer suffle_array[] = new Integer[population];

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
                        //System.out.println(" 5 if ");
                        for (int k = 0; k < dim; k++) {
                            double B = r.nextGaussian();
                            x[sort_index[b[j]]][k] = (int) Math.abs(best_x[k] + (B * Math.abs(px[sort_index[b[j]]][k] - best_x[k])));
                            //System.out.print(" " + x[sort_index[b[j]]][k]);
                        }
                    } else {
                        //System.out.println(" 5 else ");
                        for (int k = 0; k < dim; k++) {
                            double kk = r.nextDouble();
                            int xnew = (int) Math.abs(px[sort_index[b[j]]][k] + (2 * kk - 1) * (Math.abs(px[sort_index[b[j]]][k] - worst_Index_X[k]) / ((pFit[sort_index[b[j]]] - large_fit_value) + Math.pow(10, -50))));
                            //System.out.print(" " + xnew);
                            x[sort_index[b[j]]][k] = xnew;
                        }
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
                //System.out.println(" pfit value");

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

            //System.out.println(" fmin " + fmin);
            int min_fit = fitness_seq_count[0];
            int fitness_index = 0;
            for (int i = 0; i < population; i++) {
                if (min_fit > fitness_seq_count[i]) {
                    min_fit = fitness_seq_count[i];
                    fitness_index = i;
                }
            }
            //System.out.println(" global min fit value " + min_fit);
            //System.out.println("");

            //System.out.println(" list of objective " + list_of_objective_value);
            String seq = "";
            for (int i = 0; i < fmin; i++) {
                if (i == 0) {
                    seq = String.valueOf(fitness[fitness_index][i]);
                } else {
                    seq = seq + "," + fitness[fitness_index][i];
                }
            }
            //System.out.println("  final seq " + seq);
            //priority_list_for_seq = get_priority_list(seq);
            //if (is_recommended(priority_list_for_seq)) {
                //final_priority_list_for_seq = priority_list_for_seq;
                //System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                //System.out.println(" [Prioritized] Global Best Sequence =>" + seq);
                //System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                //display_test_cases(seq);
            //} else {
                priority_list_for_seq = get_priority_list(seq);
                global_best_seq = sort_final_seq(seq, priority_list_for_seq,
                        final_priority_list_for_seq);
                //System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                //System.out.println("[Prioritized/Sorted] Global Best Sequence = " + seq);
                //System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                display_test_cases(seq);
            //}


            double percent_saving = (double) (100 * (dim - objective_value(seq)) / dim);
            temp_percent[tt] = percent_saving;
            //System.out.println("[Percent reduction] = " + percent_saving + "%");
            
        }
        double sum = 0, avg = 0;
        for (int x = 0; x < output_limit; x++) {
            System.out.println(" " + temp_percent[x]);
            sum = sum + temp_percent[x];
        }
        System.out.println(" average reduction is= " + sum / output_limit);
                    long lEndTime = System.currentTimeMillis();
            double time_elapsed = (double) (lEndTime - lStartTime) / 1000;
            System.out.println("[Execution time] = " + time_elapsed + " sec");
    }
    //////////////////////////////////////////////////////////////
    //  Sort final seq according to prioritization
    //  using 2 d arrays to do multimap of seq <-> priority
    /////////////////////////////////////////////////////////////

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
        System.out.println(" final seq display " + final_seq);
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
        //System.out.println(" priority list seq "+sequence);
        for (int y = 0; y < m.length; y++) {
            String tc = testcase_list.get(Integer.parseInt(m[y]));
            //System.out.println (" tc "+tc);
            String[] clmn = tc.split(":");
            weight_total = 0;
            for (int z = 0; z < clmn.length; z++) {
                if (clmn[z].trim().equals("1")) {
                    weight_total = weight_total + prioritization[z];
                }
            }
            //System.out.println(" total weight "+weight_total);
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
        //System.out.println(" "+length);
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
                    //System.out.println(" length "+wl.length);
                    for (int z = 0; z < wl.length; z++) {
                        //System.out.println(" w1 "+wl[z]);
                        prioritization[z] = Integer.parseInt(wl[z]);
                    }

                } else if (string_val.trim().equals("totalreqs".trim())) {
                    totalreqs = s.nextToken().trim();

                } else if (string_val.trim().equals("totaltests".trim())) {
                    totaltests = s.nextToken().trim();

                } else if (string_val.trim().equals(("t" + Integer.toString(id)).trim())) {
                    String test_entry = s.nextToken().trim();
                    testcase_list.add(test_entry);
                    //System.out.println("test case "+testcase_list);
                    id++;
                }
            }
            position = f.getFilePointer();
        }
        f.close();
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
        int count = 0;
        String[] plist = sequence.split(","); //split sequence
        //System.out.println(" plist length " + plist.length);
        String answer = "";
        String cur_seq = ""; // track of current seq to break early
        for (int j = 0; j < plist.length; j++) {
            //System.out.println(" j value in objective function  "+j);
            if (j == 0) {
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
        //System.out.println(" merge elements");
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
        //System.out.println(" v value " + value);
        return value;
    }

    //////////////////////////////////////////////////////////
    //  Check for Complete Sequence
    //////////////////////////////////////////////////////////
    public static boolean complete_merge_sequence(String s1) {
        String[] result1 = s1.split(":");
        boolean outcome = true;
        //System.out.println(" complete merge sequences ");
        for (int x = 0; x < result1.length; x++) {
            if (result1[x].equals("0")) {
                //System.out.println(" complete merge sequences ");
                outcome = false;
                break;
            }
        }
        return outcome;
    }

}
