import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Solver {
    static DecimalFormat decimalFormat = new DecimalFormat("#.######");
    static ArrayList<Integer> rows;

    public static void main(String[] args) throws FileNotFoundException{
        Scanner reader = new Scanner(System.in);

        int order;
        String option;
        double[][] equations;

        while(true){
            order = -1;

            while(order <= 0 || order > 10){
                System.out.print("Enter the number of linear equations to solve (10 or less): ");
                order = reader.nextInt();
            }

            System.out.print("1) Enter equation coefficients\n" +
                             "2) Read equations from a file\n" +
                             "3) Exit program\n" +
                             ">");

            reader.nextLine();
            option = reader.nextLine();

            equations = new double[order][order + 1];
            rows = new ArrayList<>();

            if(option.matches("1")){
                userInput(order, equations, reader);
                display(order, equations);
                solve(order, equations);
            }else if(option.matches("2")){
                fileInput(order, equations, reader);
                display(order, equations);
                solve(order, equations);
            }else if(option.matches("3")){
                System.out.println("Program exited.");
                break;
            }else
                System.out.println("No option selected.");

            System.out.print("Select new system of equations? (y/n): ");
            option = reader.nextLine();

            if(option.matches("N|n")){
                System.out.println("Program exited.");
                break;
            }
        }

        reader.close();
    }

    public static void userInput(int n, double[][] eq, Scanner in){

        for(int i = 0; i < n; ++i){
            for(int j = 0; j <= n; ++j){
                if(j == n){
                    System.out.print("Enter the right hand side for equation " + (i + 1) + " : ");
                }else
                    System.out.print("Enter coefficient c" + (i + 1) + "" + (j + 1) + ": ");

                eq[i][j] = in.nextDouble();
            }
        }
    }

    public static void fileInput(int n, double[][] eq, Scanner in) throws FileNotFoundException{
        String fileName;

        in = new Scanner(System.in);

        System.out.print("Provide a valid file name: ");
        fileName = in.nextLine();

        File inputFile = new File(fileName);

        in = new Scanner(inputFile);

        for(int i = 0; i < n; ++i){
            for(int j = 0; j <= n; ++j){
                eq[i][j] = in.nextDouble();
            }
        }
    }

    public static void solve(int n, double[][] eq){
        //Visited keeps track of rows already used as the pivot
        //Max vector stores max coefficient in each equation (filled once)
        //Scale vector is calculated n - 1 times; used to determine next pivot row
        //backSequence is used to determine the order of back substitution
        boolean[] visited = new boolean[n];
        double[] maxVector = new double[n];
        double[] scaleVector;
        ArrayList<Integer> backSequence = new ArrayList<>();

        int pivotRow, scaleIndex;

        //Filling the max vector array
        for(int i = 0; i < n; ++i){

            for(int j = 0; j < n; ++j){
                if(eq[i][j] > maxVector[i])
                    maxVector[i] = eq[i][j];
            }

            rows.add(i);
        }

        //Determining the pivot row based on scale vector values
        //Performing Gaussian elimination at each iteration
        for(int i = 0; i < n - 1; ++i){
            pivotRow = 0;
            scaleIndex = 0;
            scaleVector = new double[n - i];

            for(int j = 0; j < n; ++j){
                if(!visited[j]){
                    scaleVector[scaleIndex] = Math.abs(eq[j][i] / maxVector[j]);

                    if(scaleVector[scaleIndex++] > scaleVector[pivotRow])
                        pivotRow = j;
                }
            }

            visited[pivotRow] = true;
            backSequence.add(pivotRow);
            rows.remove(rows.indexOf(pivotRow));

            elimination(i, n, pivotRow, eq, visited);
        }

        System.out.println();

        backSequence.add(rows.get(0));
        Collections.reverse(backSequence);

        //Begin back substitution
        backSubstitution(n, eq, backSequence);
    }

    //Use the pivot row to eliminate columns in all other rows
    public static void elimination(int start, int n, int pivot, double[][] eq, boolean[] v){
        double temp;

        for(int i = 0; i < n; ++i) {
            if(!v[i]) {
                temp = eq[i][start];

                for (int j = start; j <= n; ++j)
                    eq[i][j] = eq[i][j] - (eq[pivot][j] * (temp / eq[pivot][start]));
            }
        }
    }

    //Performing back substitution to solve for variables row by row
    public static void backSubstitution(int n, double[][] eq, ArrayList<Integer> sequence){
        double[] solutionVector = new double[n];

        for(int i = 0; i < n; ++i){
            solutionVector[n - (i + 1)] = eq[sequence.get(0)][n];

            for(int j = n - 1; j >= n - (i + 1); --j){
                if(j == n - (i + 1)){
                    solutionVector[n - (i + 1)] /= eq[sequence.get(0)][j];
                }else
                    solutionVector[n - (i + 1)] += -1 * (eq[sequence.get(0)][j] * solutionVector[j]);
            }

            sequence.remove(0);
        }

        for(int i = 0; i < n; ++i)
            System.out.println("X" + (i + 1) + " = " + decimalFormat.format(solutionVector[i]));
    }

    //Displaying the formatted system of equations
    public static void display(int n, double[][] eq){
        for(int i = 0; i < n; ++i){
            for(int j = 0; j <= n; ++j){
                if(j == 0){
                    System.out.print(decimalFormat.format(eq[i][j]) + "X[" + (j + 1) + "]");
                }else if(j == n){
                    System.out.print(" = " + decimalFormat.format(eq[i][j]));
                }else if(eq[i][j] >= 0){
                    System.out.print(" + " + decimalFormat.format(eq[i][j]) + "X[" + (j + 1) + "]");
                }else
                    System.out.print(" - " + decimalFormat.format(Math.abs(eq[i][j])) + "X[" + (j + 1) + "]");
            }

            System.out.println();
        }
    }
}