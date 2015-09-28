
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;


public class WriteFile {
    private String path;
    private boolean appendToFile = false;

    public WriteFile(String filePath) {
        path = filePath;
    }

    public WriteFile (String filePath, boolean appendValue) {
        path = filePath;
        appendToFile = appendValue;
    }

    public void writeToFile( String textLine ) throws IOException {
        FileWriter write = new FileWriter( path , appendToFile);
        PrintWriter print_line = new PrintWriter( write );
        print_line.printf("%s" + "%n", textLine);
        print_line.close();
    }

}