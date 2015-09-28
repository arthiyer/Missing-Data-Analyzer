# Missing-Data-Analyzer
Purpose of the Program:

Missing values in  training databases pose severe problems in Machine Learning (ML)  analysis: a) Large majority of the main ML algorithms  do not deal well or not at all with missing data; b) eliminating samples with missing data further reduces sample sets in those training databases, thus  impeding effective  ML training; and c)  ML analyst would benefit from insights on what features need what level of imputing (e.g. replacement of missing values) and tradeoffs between using for example larger number of features with imputing vs. less features needing less imputing. 
Missing Data Analyzer program is intended to be run on the M L training database before the actual analysis and as an output it provides number of statistics that help ML analyst understand the following: amount of missing values for each feature; measure (chart) of how many training samples are available for various combinations of feature values, and for each provide resulting class mix (e.g. number of samples of class labeled 1 for binary class problem) and level of imputing needed (e.gh. total number of replacement needed). This helps in tradeoffs to decide what features and how much imputing to do.   Missing Data Analyzer does not provide actual imputing, since it is highly application dependent.

Applications:

This program is useful in offering information to machine learning analysts about the missing data in the training database before finalizing the analyzing process. The program provides only the analysis of missing data statistics, offers insight into tradeoffs between number of features and number of missing data to be used but does not perform an imputation (filling in of missing data), since this is application dependent.

Usage:

User prepares the training database, which is inputted into the program. 
Format of input training database file: The file must be a .csv file, in which every FV (feature vector) is a column with a title at the top. The last column MUST be a class mix classification using 1 and 0.  (examples are in the report reference below)

To run the program:

Step 1: Use the above "Usage" instructions to prepare your training database.
Step 2: Download the .jar file from the git page by viewing the raw version.
Step 3: Open a terminal window and navigate to your downloads directory.
Step 4: Use the command "java -jar MDA.jar" to start the program.
Step 5: Input the directory path name for the training database and press enter

User input: User inputs a file path name to the training database. Output will be written to the same directory as the training database with the word results appended to it. The output will always be a .csv file.

Format of the output results file: 

The output file illustrates a number of different statistics about the missing data in the training DB. It shows amounts, percentages, and subset data about class mix and perfect samples.

Contacts and References:

17arthii@students.harker.org

petkovic@sfsu.edu 
