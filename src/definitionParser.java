import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Parse the definition file into definitions that are used to instantiate
 * an semantic graph.
 *
 * @author Eric Andersson
 */

public class definitionParser {
    private final File definitions;
    private String variableString = "";
    public definitionParser(File definitions){
        this.definitions = definitions;
    }

    public ArrayList<definitionPair> parseDefinitions(){
        String currentLine;
        String[] splitLine, arguments;
        String currentVariable;
        ArrayList<definitionPair> defPairs = new ArrayList<>();
        try{
            Scanner scan = new Scanner(definitions);
            while (scan.hasNextLine()){
                currentLine = scan.nextLine();
                splitLine = currentLine.trim().split("=");

                if(splitLine.length != 2){
                    throw new IllegalArgumentException();
                }
                currentVariable = splitLine[0].trim();
                arguments = splitLine[1].trim().split(" ");
                variableString += currentVariable;
                defPairs.add(new definitionPair(currentVariable, arguments));
            }

            return defPairs;

        }catch(FileNotFoundException e){
            System.err.println("definition file not found");
        }catch (IllegalArgumentException e){
            System.err.println("None or too many \"=\" ");
        }

        return null;
    }

    //TODO USE TO CHECK IF VARIABLES ARE VALID
    public String getVariableString() {
        return variableString;
    }
}
