/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package babynames;

import org.apache.commons.csv.*;
import edu.duke.*;
import java.io.File;

/**
 *
 * @author tians
 */
public class BabyNames {
    
    public static void printNames(){
        FileResource fr = new FileResource();
        for(CSVRecord record : fr.getCSVParser(false)){
            System.out.println("Name: " + record.get(0) + ", " +
                    "Gender: "  + record.get(1) + ", " +
                    "Number Born: " + record.get(2) + ". ");
        }
    }
    
    public static void totalBirths(FileResource fr){
        int totalBirths = 0;
        int totalBirthsBoys = 0;
        int totalBirthsGirls = 0;
        int totalBoys = 0;
        int totalGirls = 0;
        
        for(CSVRecord record : fr.getCSVParser(false)){
            if(record.get(1).equals("F")) totalGirls++;
            else totalBoys++;
            
            int numBorn = Integer.parseInt(record.get(2));
            totalBirths += numBorn;
            
            if(record.get(1).equals("M")) totalBirthsBoys += numBorn;
            else totalBirthsGirls += numBorn;
        }
        System.out.println("Total no. of names: " + (totalGirls+totalBoys));
        System.out.println("Total no. of boynames: " + totalBoys);
        System.out.println("Total no. of girlnames: " + totalGirls);
        System.out.println("Total births: " + totalBirths);
        System.out.println("Total birth boys: " + totalBirthsBoys);
        System.out.println("Total birth girls: " + totalBirthsGirls);
    }
    public static void testTotalBirths(){
        //FileResource fr = new FileResource("data/example-small.csv");
        FileResource fr = new FileResource();
        totalBirths(fr);
    }
    
    /*
     * returns the rank of the name in the file for the given gender, where 
     * rank 1 is the name with the largest number of births
    */
    public static int getRank(int year, String name, String gender){
        int rank = -1;
        int girlCounter = 0;
        int boyCounter = 0;
        for(CSVRecord record : getParser(year)){
            // first count the girls and boys separately
            if(record.get(1).equals("F")){
                girlCounter++;
            }
            else{
                boyCounter++;
            }
            // check if the @param name is found with the @param gender in the correct gender category and set the return value accordingly
            if(record.get(0).equals(name) && record.get(1).equals(gender) && gender.equals("M")){
                rank = boyCounter;
            }
            if(record.get(0).equals(name) && record.get(1).equals(gender) && gender.equals("F")){
                rank = girlCounter;
            }
        }
        return rank;
    }
    public static void testGetRank(){
        int year = 1960;
        String name = "Emily";
        String gender = "F";
        System.out.println("Rank of the name " + name + " with the gender " + gender + " in the year " + year + " is: No. " + getRank(year, name, gender));
    }
    
    /*
     * returns the name of the person in the file at this rank, for the given gender
     * If the rank does not exist in the file, then “NO NAME” is returned.
    */
    public static String getName(int year, int rank, String gender){
        int girlCounter = 0;
        int boyCounter = 0;
        String name = "NO NAME";
        for(CSVRecord record : getParser(year)){
            // first count the girls and boys separately
            if(record.get(1).equals("F")){
                girlCounter++;
            }
            else{
                boyCounter++;
            }
            // check if the @param rank is reached within girls or boys and set return value accordingly
            if(girlCounter == rank && record.get(1).equals(gender)){
                name = record.get(0);
            }
            if(boyCounter == rank && record.get(1).equals(gender)){
                name = record.get(0);
            }
        }
        return name;
    }
    public static void testGetName(){
        int year = 1980;
        int rank = 350;
        String gender = "F";
        System.out.println("Name at the rank " + rank + " with the gender " + gender + " in the year " + year + " is: " + getName(year, rank, gender));
    }
    
    /*
     * determines what name would have been named if they were born in a different year, 
     * based on the same popularity. That is, determine the rank of name 
     * in the year they were born, and then print the name born in newYear that is at the same rank and same gender
    */
    public static String whatIsNameInYear(String name, int year, int newYear, String gender){
        int originalRank = getRank(year, name, gender);
        if(originalRank == -1) return "NO NAME";
        return getName(newYear, originalRank, gender);
    }
    public static void testWhatIsNameInYear(){
        String name = "Susan";
        int year = 1972;
        int newYear = 2014;
        String gender = "F";
        System.out.println(name + " born in " + year + " would be " + whatIsNameInYear(name, year, newYear, gender) + " if he/she was born in " + newYear);
    }
    
    /*
     * This method selects a range of files to process and returns an integer, 
     * the year with the highest rank for the name and gender.
     * If the name and gender are not in any of the selected files, it should return -1.
    */
    public static int yearOfHighestRank(String name, String gender){
        int yearOfHighestRank = -1;
        int highestRank = Integer.MAX_VALUE;
        DirectoryResource dir = new DirectoryResource();
        for(File f : dir.selectedFiles()){
            String filename = f.getPath().substring(60, 64);
            int yearOfCurrentFile = Integer.parseInt(filename);
            int currentRank = getRank(yearOfCurrentFile, name, gender);
            if(currentRank == -1) continue;
            if(currentRank < highestRank){
                yearOfHighestRank = yearOfCurrentFile;
                highestRank = currentRank;
            }
        }
        return yearOfHighestRank;
    }
    public static void testYearOfHighestRank(){
        String name = "Mich";
        String gender = "M";
        System.out.println(name + " was highest ranked in the year of: " + yearOfHighestRank(name, gender));
    }
    
    /* 
     * returns a double representing the average rank of the name and gender over the selected files.
     * It returns -1.0 if the name is not ranked in any of the selected files
    */
    public static double getAverageRank(String name, String gender){
        double sumOfRanks = 0.0;
        StorageResource fileNameList = new StorageResource();
        DirectoryResource dir = new DirectoryResource();
        for(File f : dir.selectedFiles()){
            String filename = f.getPath().substring(60, 64);
            fileNameList.add(filename);
            int yearOfCurrentFile = Integer.parseInt(filename);
            int currentRank = getRank(yearOfCurrentFile, name, gender);
            sumOfRanks += (double) currentRank;
        }
        return sumOfRanks / (double) fileNameList.size();
    }
    public static void testGetAverageRank(){
        String name = "Susan";
        String gender = "F";
        System.out.println(name + " has the average rank in the selected years of: " + getAverageRank(name, gender));
    }
    
    /*
     * returns an integer, the total number of births of those names with the 
     * same gender and same year who are ranked higher than name
    */
    public static int getTotalBirthsRankedHigher(int year, String name, String gender){
        int total = 0;
        int rankOfName = getRank(year, name, gender);
        int birthNumOfName = 0;
        for(CSVRecord record : getParser(year)){
            if(record.get(0).equals(name) && record.get(1).equals(gender)){
                birthNumOfName = Integer.parseInt(record.get(2));
            }
        }
        for(CSVRecord record : getParser(year)){
            int birthNumOfCurrent = Integer.parseInt(record.get(2));
            if(record.get(1).equals(gender) && birthNumOfCurrent > birthNumOfName){
                total += Integer.parseInt(record.get(2));
            }
        }
        return total;
    }
    public static void testGetTotalBirthsRankedHigher(){
        int year = 1990;
        String name = "Drew";
        String gender = "M";
        System.out.println("The total births of those ranked higher then " + name + ", is: " + getTotalBirthsRankedHigher(year, name, gender));
    }
    
    /*
     * helper method for creating a CSVParser from a FileResource according to the @param year
    */
    private static CSVParser getParser(int year){
        FileResource fr = new FileResource("data/yob" + year + ".csv"); //TODO: Kivenni a short -ot a névből!!!
        CSVParser parser = fr.getCSVParser(false);
        return parser;
    }
    
    
}
