import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;


/**
 * Created with IntelliJ IDEA.
 * User: Michael Perkins
 * Date: 1/12/14
 * Time: 6:37 PM
 */

public class Compiler
{
    public static void main(String[] args)
    {
        ArrayDeque<imAtoken> parseMe = new ArrayDeque();// collection of analyzed pieces to be parsed
        ArrayList<Functions> functionList = new ArrayList<Functions>();
        String conti   = ""; //Can it be parsed?
        int[] x = new int[10];

        Lex lex = new Lex(); //Lexical Analyzer creation
        parseMe = lex.run(args); //Create the string from the lexical analyzer

        //Prepare the Function Declaration Table-------------------------
        SemAn semAn = new SemAn();
        functionList = semAn.run(parseMe);

        //Call the Parser------------------------------------------------

        Parser parser = new Parser();  //Parser creation
        conti = parser.run(parseMe, functionList, conti);   //run through parser

        //Print Successful Result-----------------------------------------

        System.out.println(conti);
    }
}
