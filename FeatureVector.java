import java.lang.Float;
import java.lang.String;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Arrays;
/**
 * Creates a FeatureVector and provides the necessary methods for 
 * accessing and maintaining saidFeatureVector object from 
 * the FVSelection class
 * 
 * @author Arthi Iyer, Dr. Dragutin Petkovic, Mr. Mike Wong
 * @version June 27, 2015
 */
public class FeatureVector
{
    private String name;
    private boolean[] samples;
    private int numberOfEmptyData;

    /**
     * Constructor for objects of class FeatureVector
     */
    public FeatureVector()
    {
    }

    /**
     * @return the name of the FeatureVector
     */
    public String getName() 
    {
        return name;
    }

    /**
     * @param       instances the values that are supposed to be set as 
     *              the samples for the FeatureVector
     */
    public void setSamples(boolean[] instances) 
    {
        samples = new boolean[instances.length];
        for(int tracker = 0; tracker < instances.length; tracker++) 
        {
            samples[tracker] = instances[tracker];

        }
    }

    /**
     * @return the samples for the FeatureVector
     */
    public boolean[] getData() 
    {
        return samples;
    }

    /**
     * @param nam the String that is supposed to be set as the 
     *            name of the FeatureVector
     */
    public void setName(String nam) 
    {
        name = nam;
    }

    /**
     * @return the amount of empty instances the attribute has in 
     *         its instances
     */
    public int getEmptyData() 
    {
        int counter = 0;
        for (int tracker = 0; tracker < samples.length; tracker++) 
        {
            
            if (samples[tracker] == false) 
            {
                counter++;
            }

        }
        numberOfEmptyData = counter;
        return numberOfEmptyData;
    }
}
