/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;


public class CuckooWithDimtrr {
    
      
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
    //static String next_seq="";
    static String cur_seq_long = "";
    //static String next_seq_long="";
    static String global_best_seq = "";
    static boolean complete = false;
    static int iteration = 50; 

    static int population_size = 50;
    static int dim = 10;
    static double x[][] = new double[population_size][dim];
    static double fitness[] = new double[population_size];
    static double best_value_array[] = new double[dim];
    static double poor_value_array[] = new double[dim];
    static double LB = -100;
    static double UB = 100;

//*****************************************************************
//                 GAMMA HELPER FUNCTION - LEVY FLIGHT
//*****************************************************************
    public static double logGamma(double x) {
        double tmp = (x - 0.5) * Math.log(x + 4.5) - (x + 4.5);
        double ser = 1.0 + 76.18009173 / (x + 0) - 86.50532033 / (x + 1)
                + 24.01409822 / (x + 2) - 1.231739516 / (x + 3)
                + 0.00120858003 / (x + 4) - 0.00000536382 / (x + 5);
        return tmp + Math.log(ser * Math.sqrt(2 * Math.PI));
    }

//*****************************************************************
//                 GAMMA FUNCTION - LEVY FLIGHT
//*****************************************************************
    public static double gamma(double x) {
        return Math.exp(logGamma(x));
    }

//*****************************************************************
//        LEVY FLIGHT - small unpredictable step+long jump
//*****************************************************************
    public static double LevyFlight() {
        double beta = 3 / 2;
        double sigma = Math.pow(gamma(1 + beta) * Math.sin(Math.PI * beta / 2) / (gamma((1 + beta) / 2) * beta * Math.pow(2, ((beta - 1) / 2))), (1 / beta));
        double u, v;
        Random randn = new Random();

        u = randn.nextGaussian() * sigma;
        v = randn.nextGaussian();
        double step = u / (Math.pow(Math.abs(v), (1 / beta)));

        return step;
    }

// generate random population
    public static void generate_random_population() {
        double p1 = 0.5;

        for (int i = 0; i < population_size; i++) {
            for (int j = 0; j < dim; j++) {
                Random r = new Random();
                double k = r.nextDouble();
                if (k > p1) {
                    x[i][j] = LB - ((UB - LB) * r.nextDouble());
                } else {
                    x[i][j] = LB + ((UB - LB) * r.nextDouble());
                }
                if (x[i][j] > UB) {
                    x[i][j] = LB;
                }
                if (x[i][j] < LB) {
                    x[i][j] = UB;
                }
            }
        }
    }

// Specify objective function
    public static double ObjectiveFunction(double x) {
        return ((x - 2) * (x - 4));
    }
    
        public static int find_best_value_index() {
        int index = 0;
        double f_best = ObjectiveFunction(x[0][0]); // big positive number for minimum
        double best_value = x[0][0];
        for (int i = 0; i < population_size; i++) {
            for (int j = 0; j < dim; j++) {
                double fx = ObjectiveFunction(x[i][j]);
                if (fx < f_best) {
                    f_best = fx;
                    best_value = x[i][j];
                    index=i;
                }
            }
        }
        return index;
    }

// the best solution in the population
    public static double find_best_value() {
        double index = 0;
        double f_best = ObjectiveFunction(x[0][0]); // big positive number for minimum
        double best_value = x[0][0];
        for (int i = 0; i < population_size; i++) {
            for (int j = 0; j < dim; j++) {
                double fx = ObjectiveFunction(x[i][j]);
                if (fx < f_best) {
                    f_best = fx;
                    best_value = x[i][j];
                    index=i;
                }
            }
        }
        return best_value;
    }

// the index of poor solution in the population
    public static int find_index_of_poor_value() {
        int index = 0;
        double f_poor = ObjectiveFunction(x[0][0]); // big positive number for minimum
        double poor_value = x[0][0];
        for (int i = 0; i < population_size; i++) {
            for (int j = 0; j < dim; j++) {
                double fx = ObjectiveFunction(x[i][j]);
                if (fx > f_poor) {
                    f_poor = fx;
                    poor_value = x[i][j];
                    index = i;
                }
            }
        }
        return index;
    }

    public static void fit_calculation() {

        double row_wise_fit[] = new double[dim];
        for (int i = 0; i < population_size; i++) {
            double sum = 0;
            for (int j = 0; j < dim; j++) {
                sum = sum + ObjectiveFunction(x[i][j]);
                row_wise_fit[j] = sum;
                sum = 0;
                System.out.println(" row wise " + row_wise_fit[j]);

            }

            double best_row_wiseFit = row_wise_fit[0];
            for (int m = 0; m < dim; m++) {
                if (best_row_wiseFit < row_wise_fit[m]) {
                    best_row_wiseFit = row_wise_fit[m];

                }
            }
            System.out.println(" best row wise fit " + best_row_wiseFit);
            fitness[i] = best_row_wiseFit;
        }
    }

    public static void main(String[] args) {
        double pa = 0.3;
        System.out.println("Algorithm: Cuckoo Search");
        generate_random_population();
        System.out.println(" fit value ");
        for (int iteration = 1; iteration < 100; iteration++) {
            for (int i = 0; i < population_size; i++) // iterate the population
            {
                for (int j = 0; j < dim; j++) {
                    Random r = new Random();
                    double x_new = x[i][j] + (double) (1 ^ Math.round(LevyFlight()));
                    double f_current = ObjectiveFunction(x[i][j]);
                    double f_new = ObjectiveFunction(x_new);

                    if (f_new < f_current) // new x is better than current x
                    {
                        x[i][j] = x_new;        // for minimum problem
                        //fitness[i]=f_new;
                    }
                }
            }

            // Elitism with pa probability
            for (int j = 0; j < Math.round(pa * population_size); j++) {

                int idx = find_index_of_poor_value();

                Random r = new Random();
                Double k = r.nextDouble();
                for (int m = 0; m < dim; m++) {
                    
                    double x_new;
                    // generate new random solution
                    if (k > 0.5) {
                        x_new = LB - ((UB - LB) * r.nextDouble());
                    } else {
                        x_new = LB + ((UB - LB) * r.nextDouble());
                    }

                    if (x_new > UB) {
                        x_new = LB;
                    }

                    if (x_new < LB) {
                        x_new = UB;
                    }
                    

                    double f_poor_value = ObjectiveFunction(x[idx][m]);
                    double f_new = ObjectiveFunction(x_new);
                    System.out.println("------------ELITISM-----------------------");
                    System.out.println("Current Solution = " + x[idx][m]);
                    System.out.println("Peer Solution = " + x_new);

                    if (f_new < f_poor_value) // new x is better than current x
                    {
                        x[idx][m] = x_new;        // for minimum problem
                        //fitness[idx]=f_new;
                    }
                }
            }
        }
        double best_solution = find_best_value();
        int val=(int)best_solution;
        System.out.println("Best solution = " + best_solution);
        for(int m=0;m<dim;m++){
        System.out.println(" "+x[val][m]);
        }
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
