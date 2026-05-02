/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mizan
 */
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Spliterator;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

public class treductJayawithdim {

    static String filename = "data7.txt";
    static String totalreqs = "";
    static String totaltests = "";
    static String test_entry = "";
    static ArrayList<String> testcase_list = new ArrayList<String>();
    static int[] prioritization = new int[100];
    static ArrayList<String> priority_list_for_seq = new ArrayList<String>();
    static ArrayList<String> final_priority_list_for_seq = new ArrayList<String>();
    static ArrayList<Integer> list_of_objective_value = new ArrayList<Integer>();

    static String cur_seq = "";
    //static String next_seq="";
    static String cur_seq_long = "";
    //static String next_seq_long="";
    static String global_best_seq = "";
    static boolean complete = false;
    static int iteration_max = 50; // default iteration

    static int lb = 0;
    static int ub = 29;
    static int population_size = 50;
    static int dim = 30;
    static int x[][] = new int[population_size][dim];
    static Integer[] x_single = new Integer[dim];
    static ArrayList<ArrayList<Integer>> fitness = new ArrayList<>();
    //static int fitness[][] = new int[population_size][dim];
    static int fitness_seq_count[] = new int[population_size];
    static int arr[] = new int[dim];
    static ArrayList<Integer> index1 = new ArrayList<Integer>(population_size);

// generate random population
    public static void population_generation() {
        for (int i = 0; i < dim; i++) {
            x_single[i] = i;
        }
        String seq = "";
        for (int i = 0; i < population_size; i++) {
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
            fitness.add(new ArrayList<Integer>());
            //System.out.println(" seq " + seq);
            String s = objective_function(seq);
            //System.out.println(" s " + s);
            String[] arr = s.split(",");
            for (int m = 0; m < arr.length; m++) {
                fitness.get(i).add(m, (Integer.parseInt(arr[m])));
                //System.out.println(" mizan");
            }
            fitness_seq_count[i] = arr.length;
            //fitness.get(0).get(0);
        }
    }

// Specify objective function
    public static double ObjectiveFunction(double x) {
        return ((x - 2) * (x - 4));
    }

// the best solution in the population
    public static int find_best_value() {
        int index;
        int f_best = fitness_seq_count[0]; // big positive number for minimum
        for (int i = 0; i < population_size; i++) {

            if (fitness_seq_count[i] < f_best) {
                f_best = fitness_seq_count[i];
                index = i;
                //System.out.println(" true form best value");

            }

        }
        return f_best;
    }

// find poor solution in the population
    public static int find_poor_value() {
// big positive number for minimum
        int poor_value = fitness_seq_count[0];
        for (int i = 0; i < population_size; i++) {
            if (poor_value < fitness_seq_count[i]) {
                poor_value = fitness_seq_count[i];
            }
        }
        return poor_value;
    }

    public static int find_best_index() {
        int index = 0;
        double f_best = fitness_seq_count[0]; // big positive number for minimum

        for (int i = 0; i < population_size; i++) {
            if (f_best > fitness_seq_count[i]) {
                //best_value = x[i][j];
                index = i;
                f_best = fitness_seq_count[i];
                //System.out.println(" true from mizan value ");
            }

        }
        return index;
    }

    public static String Bounds(int index_value) {
        List<Integer> list = new ArrayList<>();
        //System.out.println(" bound check ");

        
        for (int i = 0; i < dim; i++) {
            //System.out.print(" " + x[index_value][i]);
            list.add(x_single[i]);
        }
        List result = list.stream().filter(s -> s > ub).collect(Collectors.toList());
        List result1 = list.stream().filter(s -> s < lb).collect(Collectors.toList());
        //System.out.println(" result 1" + result);
        //System.out.println(" result 2" + result1);
        //System.out.println(" list " + list);
        missingValue(list, lb, ub);
        //System.out.println(" list " + list);
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

    public static void main(String[] args) throws IOException {
        filename = filename.trim();
        process_input_file(filename, testcase_list, prioritization);
        int average_value_limit = 10;
        double avg_percent[] = new double[average_value_limit];
        int output_limit = 30;

        double temp_percent[] = new double[output_limit];
        //System.out.println(" fit ness value");
        // Spliterator<ArrayList<Integer>> m = fitness.spliterator();
        long lStartTime = System.currentTimeMillis();
        for (int tt = 0; tt < output_limit; tt++) {
            population_generation();

            //System.out.println("Algorithm: Jaya ");

            for (int iteration = 1; iteration < iteration_max; iteration++) {
                for (int i = 0; i < population_size; i++) // iterate the population
                {
                    Random r = new Random();
                    for (int j = 0; j < dim; j++) {
                        double r1 = r.nextDouble();
                        double r2 = r.nextDouble();
                        //System.out.println(" r1 " + r1 + " r2 " + r2);
                        x_single[j] = (int) (x[i][j] + (r1 * (find_best_value() - x[i][j])) - (r2 * (x[i][j] - find_poor_value())));
                        //System.out.print(" " + x_single[j]);
                    }
                    String seq;
                    seq = Bounds(i);
                    //System.out.println(" ");
                    //System.out.println(" seq send" + seq);
                    String s = objective_function(seq);
                    //System.out.println(" new seq "+s);
                    String[] arr = s.split(",");

                    if (fitness_seq_count[i] > arr.length) {
                        //fitness.set(i, null);
                        for (int n = 0; n < arr.length; n++) {
                            fitness.get(i).add(n, (Integer.parseInt(arr[n])));
                        }
                        fitness_seq_count[i] = arr.length;
                    }
                }
            }

            int best_solution = find_best_index();
            String seq = "";
            //System.out.println(" best solution index" + best_solution);
            for (int i = 0; i < fitness_seq_count[best_solution]; i++) {
                if (i == 0) {
                    seq = String.valueOf(fitness.get(best_solution).get(i));
                } else {
                    seq = seq + "," + fitness.get(best_solution).get(i);
                }
                //System.out.print(" " + fitness.get(best_solution).get(i));
            }
            //System.out.println(" seq " + seq);
            double percent_saving = (double) (100 * (dim - objective_value(seq)) / dim);
            temp_percent[tt] = percent_saving;
            //System.out.println("[Percent reduction] = " + percent_saving + "%");
            //display_test_cases(seq);
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
                    //iteration = Integer.parseInt(s.nextToken().trim());
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
        /*int[] ar = new int[5];
        int d, tmp;
        Random generator = new Random();

        for (int i=0;i<5;i++)
             ar[i]=i;

        for (int i=0;i<5;i++)
            System.out.println (ar[i]);


        System.out.println ("++++++AFTER++++++++++");

        for (int i=0;i<5;i++)
        {
          d=i+(generator.nextInt()&(4-i));
          tmp=ar[i];
          ar[i]=ar[d];
          ar[d]=tmp;
        }

        for (int i=0;i<5;i++)
            System.out.println (ar[i]);*/

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
        int count = 0;
        String[] plist = sequence.split(","); //split sequence
        String answer = "";
        String cur_seq = ""; // track of current seq to break early
        for (int j = 0; j < plist.length; j++) {
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
