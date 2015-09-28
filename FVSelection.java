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
 */
public class FVSelection
{

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

    private String[] types;

    private int[] classMix;

    String directory;

    private int numberOfCols;

    private int[] perfectClassMix;
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

        Scanner in = new Scanner(System.in);
        System.out.print("Input the .csv file path name of your training database:");
        System.out.println();
        directory = in.nextLine();
        System.out.println();
        in.close();

        try
        {
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
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        numberOfSamples--;
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
        sortedDataset = new boolean[numberOfSamples]
        [numberOfFeatureVectors];
        for (int columnTracker = 0; columnTracker < numberOfFeatureVectors; columnTracker++) 
        {

            for(int rowTracker = 0; rowTracker < numberOfSamples; 
            rowTracker++)
            {
                boolean[] columnTrackerSamples = featureVectors[columnTracker].getData();
                sortedDataset[rowTracker][columnTracker] = columnTrackerSamples[rowTracker];
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
     * 
     * @return     an array that contains the number of perfect 
     *             samples for each FV
     */ 
    public void featureVectorAnalysis()
    {
        perfectClassMix = new int[classMix.length];

        //the number of samples that are deemed full for each FV
        int counter=0; 

        //the FV that is currently being looked at
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

        while(populatedColumns<(numberOfFeatureVectors))
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
                    if (classMix[rowTracker]==1) {
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

    public void fileData() /*throws IOException*/ {
        /*Scanner in = new Scanner(System.in);
        System.out.println("Input the file path for the file that you want the results to be written to. \n For best results, input a .csv file");
        System.out.println("Note: If there is no already existing file at this file path, one will be created.");
        System.out.print("If the file already exists, the results will be appended to the file.");
        System.out.println();
        String path = in.nextLine();
        System.out.println();
        in.close();*/
        
        //String path = directory + "results.csv";
        
        String path = (directory.substring(0,directory.length()-4) + "results.csv");

        try {
            WriteFile data = new WriteFile(path, true);
            data.writeToFile("Missing Data Analyzer Results: \n\n\n");
            data.writeToFile("Total number of samples: " + numberOfSamples);
            data.writeToFile("Total number of Feature Values(FV): " + numberOfFeatureVectors + "\n\n");
            data.writeToFile("FV sorted in terms of missing data (ascending): \n\n");
            data.writeToFile("Name of FV, Amount of empty data in the FV, % empty data in the FV");
            for (int i = 0; i < numberOfFeatureVectors; i++) {
                double percEmpt = (featureVectors[i].getEmptyData()/(numberOfSamples))*100;
                data.writeToFile(featureVectors[i].getName() + "," + Integer.toString(featureVectors[i].getEmptyData()) + "," + Double.toString(percEmpt) + "\n\n");
            }

            data.writeToFile("Data showing number of available samples with no missing data (class mix and total \n number of missing elements for chosen FV subset): ");
            data.writeToFile("Note: entry FV(i) denotes a subset of FV from 1-i \n\n");
            data.writeToFile("FV Subset Used, #samples with no missing data, #1s in Class mix, Total number of missing elements");
            for(int i = 0; i < numberOfFeatureVectors; i++) {
                data.writeToFile(featureVectors[0].getName() + " to " + featureVectors[i].getName() + ", " + Integer.toString(analysis[i]) + ", " + perfectClassMix[i] + ", " + Integer.toString(emptyDataSubset(i)));
            }

            data.writeToFile("\n\n\n\n\n");
            data.writeToFile("Once the user analyses the data above he/she can explore tradeoffs between choosing feature subset ");
            data.writeToFile("vs. available samples (with missing or no missing data) checking at the same time that class mix satisfies ");
            data.writeToFile("ML needs. Then user can extract desired samples using utility ExtractSamples. More specifically:");
            data.writeToFile("Based on desired number of  samples related number  of feature values FVi  as well as class mix for ");
            data.writeToFile("available samples (which may depend on specific ML algorithm) the user can chose the best “operating ");
            data.writeToFile("point” e.g. subset of feature values FVi  for which to extract samples for further processing. ");
            data.writeToFile("User then runs ExtractSamples utility which for a given subset of feature values FVi  (user input)  ");
            data.writeToFile("allows extraction of:");
            data.writeToFile("* Only samples with no missing values (option c) – number of these samples is less than original ");
            data.writeToFile("number of samples and is given in the data above ");
            data.writeToFile("* All samples (option m) in which case some will have missing values");
            data.writeToFile("ExtractSamples will then create news training DB in CSV format with each sample having original ID ");
            data.writeToFile("preserved followed by chosen subset of Feature Values FVi (with original names preserved) and ");
            data.writeToFile("corresponding (original) class label. Note that the order of samples and feature values will be different ");
            data.writeToFile("from the original database.  In case user chooses option “m” the actual missing value replacement is left ");
            data.writeToFile("to the user since it is highly application and data dependent.");
        }
        catch(IOException exception){
        }

    }

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

    public static void main(String [ ] args)
    {
        FVSelection a = new FVSelection();

        a.readFileAndPopulate();
        a.selectionSortEmptyDataAscending();
        a.featureVectorAnalysis();
        /*
        try {
            a.fileData();
        }
        catch(IOException exception){
        }
        */
        a.fileData();

    }
}

