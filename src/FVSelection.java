/**Copyright (c) <2016> <Arthi Iyer>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
and associated documentation files (the "Software"), to deal in the Software without restriction, 
including without limitation the rights to use, copy, modify, merge, publish, distribute, 
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is 
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial 
portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE 
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
import java.lang.Float;
import java.lang.String;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Arrays;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.*;
import java.lang.*;

/**
 * FVSelection is the main class that uses FeatureVector objects to 
 * implement the  algorithm created by the author and her mentor, 
 * Dr. Dragutin Petkovic. It entails sorting the data in ascending 
 * order by the amount of empty data in each feature vector and 
 * then figuring out how many cumulatively full samples there are
 * for each feature vector.
 * 
 * Method Descriptions:
 *      readFileAndPopulate():             reads the file and populates 
 *                                         multiple instance variables.
 *      
 *      selectionSortEmptyDataAscending(): sorts the data in an 
 *                                         ascending manner based on 
 *                                         empty data using a selection 
 *                                         sort
 *                                         
 *      printFeatureVectors():             pretty prints all the feature 
 *                                         vectors in the data
 *      
 *      printData():                       pretty prints all of the data 
 *                                         that was read
 *      
 *      printAnalysis():                   pretty prints the analysis 
 *                                         of the data
 *      
 *      featureVectorAnalysis():           analyses the data and 
 *                                         inputes the important 
 *                                         information into an array 
 *      
 * 
 * @author Arthi Iyer
 * mentors: Dr. Dragutin Petkovic (SFSU), Mr. Mike Wong (SFSU)
 * @version June 27, 2015
 * 
 * 
 */
public class FVSelection
{

    //PART 1 OF CODE

    /**
     *  # samples (rows) there are in the data
     */
    private int numberOfSamples = 0;

    /**
     * # feature vectors (columns) there are in the data
     */
    private int numberOfFeatureVectors = 0; 

    /**
     * The 2d array that stores the unsorted data. In other words, 
     * exactly what was found in the file.
     */
    private boolean[][] dataset; 

    /**
     * The 2d array that stores the sorted data.
     */
    private boolean[][] sortedDataset; 

    /**
     * the array that stores each feature vector. Every column in the 
     * data set is stored here in the form of an featureVectors.
     */
    private FeatureVector[] featureVectors;

    /**
     * The array that stores the end result of the class.
     */
    private int[] analysis;

    /**
     * The array that stores all of the class mix values for the entire DB
     */
    private int[] classMix;

    /**
     * The array that stores which samples have class mixes of 1
     */
    private int[] perfectClassMix;

    /**
     * The string storing the path to the training DB
     */
    String directory;

    /**
     * The number of columns in the DB
     */
    private int numberOfCols;

    /**
     * The string that stores the inputed name for the FV that the user wants the subset to go up to
     */
    private String subsetFVName;

    /**
     * The index that the subsetFV can be found at in the sorted FV array
     */
    private int subsetGreatestIndex;

    /**
     * The array that stores all that indices that should be included in the subset matrix
     */
    private ArrayList <Integer> idealIndexes;
    private ArrayList <String> idealIndexesNames;

    private Scanner in = new Scanner(System.in);

    private String date;
    private int[] cl;
    private ArrayList<Integer> filteredDBSamples = new ArrayList<Integer>();
    ArrayList<Integer> allSamples;
    /**
     * Empty constructor for the FVSelection class. The constructor 
     * does not need to do anything because this class employs 
     * only FeatureVector objects aside from primitive types and 
     * common classes such as String.
     */
    public FVSelection()
    {
    }

    /**
     * First, read the data file and populate the featureVectors array 
     * and dataset 2d array. 
     * This method both reads the file and populates featureVectors and 
     * dataset. 
     * It figures out numberOfFeatureVectors and numberOfSamples, niether 
     * of which will be modified at any later point in the code.
     */
    public void readFileAndPopulate() 
    {

        System.out.println("Missing Data Analyser");
        System.out.println();
        System.out.println();
        System.out.println();

        int i = 0;
        while (true) {
            System.out.println("What is the date that you would like to be reflected in the program's output?");
            date = in.nextLine();

            if (i==0) {
                System.out.println("Would you like to input the .csv file path name of your training database?");
                System.out.print("If not, type no now. Otherwise, input the path name now.");
                i++;
            }
            System.out.println();
            directory = in.nextLine();

            try
            {

                if (directory.equalsIgnoreCase("no")) {
                    System.exit(-1);
                }

                Scanner scanner = new Scanner(new File(directory));
                while(scanner.hasNextLine())
                {
                    String sample = scanner.nextLine();
                    if (numberOfSamples==0) 
                    {

                        String[] instances = sample.split(",");
                        numberOfCols = instances.length;
                        numberOfFeatureVectors = numberOfCols-1;
                    }

                    numberOfSamples++;
                }

                scanner.close();
                break;
            }
            catch (FileNotFoundException e)
            {
                System.out.println("Invalid input: '" + directory + "'. Would you like to try again?");
                System.out.println("If not, type 'NO' now. Otherwise, try again.");
            }
        }

        numberOfSamples--;
        if (numberOfSamples==0) {
            System.out.println("The training dataset has no samples. Please run a dataset with atleast one sample.");
            System.exit(0);
        }
        classMix = new int[numberOfSamples];
        int lineTracker = 0;
        try
        {
            Scanner scanner = new Scanner(new File(directory));
            dataset = new boolean[numberOfSamples][numberOfFeatureVectors];
            featureVectors = new FeatureVector[numberOfFeatureVectors];
            while(scanner.hasNextLine())
            {
                String sample = scanner.nextLine();

                String[] instances = sample.split(",", numberOfCols);
                if (lineTracker == 0) 
                {

                    for (int k = 0; k < numberOfFeatureVectors; k++) 
                    {
                        featureVectors[k] = new FeatureVector();
                        featureVectors[k].setName(instances[k]);
                        featureVectors[k].setPosition(k);
                    }
                }

                else {

                    for(int c = 0; c < numberOfFeatureVectors; c++) 
                    {
                        if ((instances[c].equals(""))) 
                        {
                            dataset[lineTracker-1][c] = false; 
                        }
                        else {
                            dataset[lineTracker-1][c] = true;
                        }

                    }

                    if(instances[instances.length-1].equals("")){
                        classMix[lineTracker-1] = Integer.MAX_VALUE;
                    }
                    else{
                        if ((instances[instances.length-1].equals("1"))||(instances[instances.length-1].equals("0"))) {
                            classMix[lineTracker-1] = Integer.parseInt(instances[instances.length-1]);
                        }
                        else{
                            System.out.println("Program has been terminated...");
                            System.out.println("The class mix for the training set has not been properly initialized. Please review the instructions in the Readme file.");
                            System.exit(0);
                        }
                    }

                }
                lineTracker++;
            }

            scanner.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        for(int columnTracker = 0; columnTracker < 
        numberOfFeatureVectors; columnTracker++) 
        {
            boolean[] sampleTemporary = new boolean[lineTracker-1]; 
            for(int rowTracker = 0; rowTracker < lineTracker-1; 
            rowTracker++)
            {
                sampleTemporary[rowTracker] = dataset[rowTracker]
                [columnTracker];

            }
            featureVectors[columnTracker].setSamples(sampleTemporary);
        }
    }

    //PART 2 OF CODE

    /**
     * Sorts the featureVectors ascendingly based on how many
     * empty instances they have. A selection sort traverses the 
     * array backwards, switching values when necessary. It keeps
     * track of two indices consistently, which are always 
     * consecutive integers. When the value at the greater index
     * is less than that of the lesser index, the objects in the 
     * array are switched. Only objects are swapped, not the
     * actual samples.
     * 
     * @return a FeatureVector[] that is the new featureVectors array.
     */
    public FeatureVector[] selectionSortEmptyDataAscending()
    {
        /**
         * except for featureVectors, all variable names
         * in this method are purely local. Their only
         * purpose is to aid the sort.
         */ 
        for(int greaterIndex = numberOfFeatureVectors - 1; 
        greaterIndex >= 0; greaterIndex--) 
        {
            int highestIndex = greaterIndex; 
            for(int lesserIndex = greaterIndex; lesserIndex >= 0; 
            lesserIndex--)
            {

                if(featureVectors[lesserIndex].getEmptyData() > 
                featureVectors[highestIndex].getEmptyData())
                    highestIndex = lesserIndex;
            }

            FeatureVector temporaryStorage = featureVectors[greaterIndex];
            featureVectors[greaterIndex] = featureVectors[highestIndex];
            featureVectors[highestIndex] = temporaryStorage;
        }

        /**
         * populates a new 2d array (sortedDataset) with the sorted data.
         * This way, both arrays can be accessed to see if the sort was 
         * correct
         */ 
        sortedDataset = new boolean[numberOfSamples][numberOfFeatureVectors];
        for (int columnTracker = 0; columnTracker < numberOfFeatureVectors; columnTracker++) 
        {

            for(int rowTracker = 0; rowTracker < numberOfSamples; 
            rowTracker++)
            {
                boolean[] columnTrackerSamples = featureVectors[columnTracker].getData();
                sortedDataset[rowTracker][columnTracker] = columnTrackerSamples[rowTracker];
                featureVectors[columnTracker].setSortedPosition(columnTracker);
            }

        }
        return featureVectors;
    }

    /**
     * prints all the featureVector names for the data. It keeps a 
     * good amount of space in between each line so the user can read 
     * the array easily and prints an empty line after it to keep the 
     * terminal window organized.
     */
    public void printFeatureVectors() 
    {

        for(int tracker = 0; tracker < featureVectors.length; tracker++) 
        {
            System.out.print(featureVectors[tracker].getName() 
                + "      ");
        }
        System.out.println();
        System.out.println();
    }

    /**
     * prints the data in the specified 2d array. If the instance
     * is empty, which is represented by Float.MAX_VALUE, the word 
     * "empty" is printed. There is a good amount of space left between 
     * each element so that the user can view the data more easily. The 
     * parameter used here serves the purpose of giving the user the 
     * option of whether of not the sorted data or the unsorted data 
     * should be printed.
     * 
     * @param   sorted a boolean that specifies whether or not the sorted 
     *          data should be printed.
     */
    public void printData(boolean sorted) 
    {
        for(int rowTracker = 0; rowTracker < numberOfSamples;
        rowTracker++) 
        {

            for (int columnTracker = 0; columnTracker < 
            numberOfFeatureVectors; 
            columnTracker++) 
            {
                boolean currentVal = sorted ? 
                        sortedDataset[rowTracker][columnTracker] : 
                    dataset[rowTracker][columnTracker]; 

                if(currentVal==false) 
                {
                    System.out.print("empty      "); 
                }
                else
                {
                    System.out.print("data      ");
                }

            }
            System.out.println();
            System.out.println();
        }
    }

    /**
     * prints the analysis array. The word "analysis" is printed 
     * a line before the data so that the terminal window stays 
     * organized. A good amount of space is left between each 
     * element to make viewing the data easier on the user.
     */
    public void printAnalysis() 
    {
        System.out.println("analysis: ");
        for(int tracker = 0; tracker < featureVectors.length; tracker++) 
        {
            System.out.print(analysis[tracker] + "      ");
        }
        System.out.println();
        System.out.println();
    }

    /**
     * This method implements the algorithm that this class is 
     * centered around. It cumulatively calculates how many 
     * full samples there are for each feature vector in the 
     * sorted array. The algorithm here basically asks this question:
     * In the sorted data, for each FV, how many samples are still 
     * perfectly full? It traverses the 2d array from left to 
     * right and every time it gets to a new FV, it checks to 
     * see how many samples still have no empty instances
     * 
     */ 
    public void featureVectorAnalysis()
    {
        classMix();
        perfectClassMix = new int[numberOfFeatureVectors];

        //the number of samples that are deemed full for each FV
        int counter=0; 

        //the number of samples that are deemed full for each FV
        int populatedColumns = 0; 

        //a boolean array that shows when a sample is no longer full
        boolean[] validSamples = new boolean[numberOfSamples]; 

        // sets all the samples to be valid in the beginning.
        for(int b  = 0; b < numberOfSamples; b++) 
        {
            validSamples[b] = true;
        }

        /**
         * the local variable that will later be set as a global 
         * instance variable (analysis). It stores the end result 
         * of the algorithm described in the comment section of 
         * featureVectorAnalysis().
         */ 
        int[] featureVectorAnalysis = new int[numberOfFeatureVectors]; 

        while(populatedColumns+1 < (numberOfFeatureVectors))
        {
            /**
             * Counter needs to be set to 0 every time the loop moves on 
             * to a new FV because otherwise the featureVectorAnalysis[] 
             * will contain a fibonacci sequence of sorts.
             */
            counter = 0; 
            for(int rowTracker = 0; rowTracker < numberOfSamples; rowTracker++) 
            {

                if ((sortedDataset[rowTracker][populatedColumns] != 
                    false) && (validSamples[rowTracker] == 
                    true)) 

                {
                    counter++;
                    if (cl[rowTracker]==1 && populatedColumns <= numberOfFeatureVectors) {
                        perfectClassMix[populatedColumns]++;
                    }
                }
                else 
                {
                    validSamples[rowTracker] = false; 
                }

            }

            featureVectorAnalysis[populatedColumns] = counter;
            populatedColumns++;
        }

        analysis = featureVectorAnalysis;

    }

    public int[] classMix(){
        try{
            Scanner scanner = new Scanner(new File(directory));
            cl = new int [numberOfSamples];
            int i = 0;

            while(scanner.hasNextLine()){ 
                if (i==0){
                    String[] instances = scanner.nextLine().split(",");
                }
                i++;
                if (i>0 /**&& samplesCounter<numberOfSamples*/) {
                    for(int a = 0; a< numberOfSamples; a++){
                        String[] instances = scanner.nextLine().split(",");
                        cl[a] = Integer.parseInt(instances[instances.length - 1]);

                    }
                }

            }
        }
        catch(IOException exception){

        }
        return cl;
    }

    //PART 3 OF CODE

    /**
     * Creates the first output file for this program
     */
    public void populateFirstFileOutput() /*throws IOException*/ {
        String path = (directory.substring(0,directory.length()-4) + "_results.csv");

        try {
            Scanner scanner = new Scanner(new File(directory));
            WriteFile data = new WriteFile(path, true);
            data.writeToFile("Missing Data Analyzer Results: \n\n\n");
            data.writeToFile("Name of this results file: " + path);
            data.writeToFile("Name of the input file: " + directory + "\n\n");
            data.writeToFile("Total number of samples: " + numberOfSamples);
            data.writeToFile("Total number of Feature Values(FV): " + numberOfFeatureVectors + "\n\n");
            data.writeToFile("date: " + date + "\n\n");
            data.writeToFile("FV sorted in terms of missing data (ascending): \n\n");

            for (int i = 0; i < numberOfFeatureVectors; i++) {
                double percEmpt = ((float)featureVectors[i].getEmptyData()/(numberOfSamples))*100;
                data.writeToFile(featureVectors[i].getName() + "," + Integer.toString(featureVectors[i].getEmptyData()) + "," + Double.toString(percEmpt));

            }
            data.writeToFile("\n Data showing number of available samples with no missing data (class mix and total number ");
            data.writeToFile("of missing elements for chosen FV subset): ");
            data.writeToFile("e.g. FV5 denotes subset of feature vectors with amount of empty data <= FV5.");
            data.writeToFile("Number of training samples with no missing values and its related class mix using ALL features FVi is given ");
            data.writeToFile("as the last entry in the list below \n");
            data.writeToFile("The class mix can be defined as the number of ones in the chosen subset of training samples. \n\n");
            data.writeToFile("FV Subset Used, #samples with no missing data, Class Mix");

            for(int i = 0; i < numberOfFeatureVectors; i++) {
                data.writeToFile(featureVectors[0].getName() + " to " + featureVectors[i].getName() + ", " + Integer.toString(analysis[i]) + ", " + perfectClassMix[i]);
            }

            data.writeToFile("The FV subset in the list above (first column) is denoted for brevity ");
            data.writeToFile("by only showing first and last item in the FV subset where the subset contains FV with least number ");
            data.writeToFile("of empty data as the first element (top one on the list of sorted FVi in the first results list above) ");
            data.writeToFile("then grows by adding next FV from that sorted list of FV with missing data top to bottom one by one. \n\n");

            data.writeToFile("\n\n\n");
            data.writeToFile("Once the user reviews  the results above he/she can select which subset of training data with no");
            data.writeToFile("missing values to extract. This is done by choosing the desired subset of FVi  with related number of");
            data.writeToFile("training samples with no missing data and class mix from the results list above. The user then follows");
            data.writeToFile("the dialog in the original MDA program and enters the desired FVi subset");
            data.writeToFile("MDA then extracts the data into a filtered training databases with file generated");
            data.writeToFile("name as (DDD explain how is name derived) comprising of  chosen subset of FVi and training samples");
            data.writeToFile("with their class labels where extracted training samples have no missing values");

        }
        catch(IOException exception){

        }
    }

    /**
     * calculates how much empty data is in a subset
     * œœœ∑
     * @param lastFV the last FV that is used for the subset
     * 
     * @return the amount of empty data fields are in the subset
     */
    private int emptyDataSubset(int lastFV) {
        int[] empties = new int[lastFV];
        for (int i = 0; i < lastFV; i++) {
            empties[i] = featureVectors[i].getEmptyData();
        }
        int answer = 0;
        for (int j = 0; j < lastFV; j++) {
            answer+=empties[j];
        }
        return answer;
    }

    //PART 4 OF CODE

    /**
     * takes the second input from the user to be used for the subset portion of the program.
     */
    private void takeSecondInput() {

        boolean trueFV = false;

        System.out.println("A results file containing statistics on your TB can now be found at: ");
        System.out.println();
        System.out.println((directory.substring(0,directory.length()-4) + "_results.csv"));
        System.out.println();
        System.out.println();
        System.out.println("If you would like to quit now, type 'quit'. ");
        System.out.println();
        System.out.println("Otherwise, type the name of the FV that your subset should go up to.");
        System.out.println("To figure out which FV to use, please refer to results file. ");
        System.out.println("After you input the FV, the program will create a .csv file containing ");
        System.out.println("a subset of the training database. The subset will involve no imputing.");
        System.out.println();

        while (!trueFV) {
            String nextLine = in.nextLine();

            if (nextLine.equalsIgnoreCase("quit")) {
                System.exit(0);
            }
            else {
                for (int i = 0; i < featureVectors.length; i++) {
                    if (nextLine.equals(featureVectors[i].getName())) {
                        subsetFVName = nextLine;
                        trueFV = true;
                        break;
                    }

                }
                if (trueFV == false) {
                    System.out.println();
                    System.out.println("'" + nextLine + "' does not appear legitimate. Please try agan if you want.");
                    System.out.println("Otherwise, type quit");

                }
            }
        }
    }

    /**
     * Constructs an array of just the indices that will be used in the subset DB
     */
    private void constructIdealIdexesArray() {

        for (int i = 0; i < featureVectors.length; i++) {
            if (featureVectors[i].getName().equals(subsetFVName)) {
                subsetGreatestIndex = featureVectors[i].getSortedPosition();
            }
        }

        idealIndexes = new ArrayList<Integer>();
        idealIndexesNames = new ArrayList<String>();
        int idealIndexesIndex = -1;

        for(int i = 0; i < featureVectors.length; i++) {
            if ((featureVectors[i].getSortedPosition() <= subsetGreatestIndex)){
                idealIndexesIndex++;
                idealIndexes.add(featureVectors[i].getPosition());
                idealIndexesNames.add(featureVectors[i].getName());
            }
        }

        ArrayList<Integer> allSamples = new ArrayList<Integer>();
        for (int i = 0; i < numberOfSamples; i++){
            allSamples.add(i);
        }

        //         for (int i = 0; i <  idealIndexes.size(); i++){
        //             System.out.print(idealIndexes.get(i) + "   ");
        //         }

        for (int i = 0; i < idealIndexes.size(); i++){
            ArrayList<Integer> temp = new ArrayList<Integer>(); /*= featureVectors[idealIndexes.get(i)].getFullIndexes();*/
            ArrayList<Boolean> FVList = new ArrayList<Boolean>();
            FVList = createFVArrayList(i);
            for (int a = 0; a < numberOfSamples; a++) {
                if (FVList.get(a) == true) {
                    temp.add(a);
                }
            }

            
            //int t = allSamples.size();
            for (int x = 0; x < allSamples.size(); x++){
                if(!temp.contains(allSamples.get(x))) {
                    allSamples.remove(x);
                    x--;
                }
            }
        }
        //printData(true);
        filteredDBSamples = allSamples;

    }

    private ArrayList<Boolean> createFVArrayList(int index) {
        ArrayList<Boolean> FVArrayList = new ArrayList<Boolean>();
        for (int rowTracker = 0; rowTracker < numberOfSamples; rowTracker++) {
            FVArrayList.add(sortedDataset[rowTracker][index]);
        }
        return FVArrayList;
    }

    /**
     * creates the subset matrix and writes it to a .csv file
     */
    public void dealWithSubsetDataSet(){

        String path = (directory.substring(0,directory.length()-4) + "_subset_database_" + subsetFVName + ".csv");
        int lineTracker = 0;
        try
        {
            WriteFile data = new WriteFile(path, true);
            Scanner scanner = new Scanner(new File(directory));
            int rowTracker = -1;
            while(scanner.hasNextLine())
            {
                String[] instances = scanner.nextLine().split(",");
                String writeThisToFile = ""; 

                for (int i = 0; i < idealIndexes.size(); i++) {
                    for (int k = 0; k < instances.length; k++) {
                        if ((idealIndexes.get(i) == k)) {
                            if((filteredDBSamples.contains(rowTracker)) || (rowTracker==-1)){
                                writeThisToFile += instances[k] + ",";
                            }
                        }
                    }
                }
                if (!writeThisToFile.equals("") ){
                    writeThisToFile += instances[instances.length - 1];
                    data.writeToFile(writeThisToFile);
                }
                writeThisToFile = "";
                rowTracker++;
            }

            scanner.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (java.io.IOException i){}

        System.out.println();
        System.out.println("A database containing only the subset of data you requested can now be found at: ");
        System.out.println();
        System.out.println((directory.substring(0,directory.length()-4) + "_subset_database_" + subsetFVName + ".csv"));
    }

    public static void main(String [ ] args)
    {
        FVSelection a = new FVSelection();

        a.readFileAndPopulate();
        a.classMix();

        //System.out.println("main1");
        a.selectionSortEmptyDataAscending();
        //a.classMixArray();
        //System.out.println("main2");
        a.featureVectorAnalysis();
        //System.out.println("main3");
        a.populateFirstFileOutput();
        //System.out.println("main4");
        a.takeSecondInput();
        //System.out.println("main5");
        a.constructIdealIdexesArray();
        //System.out.println("main6");
        a.dealWithSubsetDataSet();
        //System.out.println("main7");

    }
}