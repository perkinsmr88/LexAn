import org.omg.DynamicAny._DynArrayStub;

import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * Created by michaelperkins on 4/4/14.
 */
public class SemAn
{
    ArrayList<Functions> functionList = new ArrayList<Functions>();
    String name = "";
    String type = "";
    Boolean isAr = false;
    ArrayList<String> ptype = new ArrayList<String>();
    ArrayList<String> pname = new ArrayList<String>();

    //---------------------------------------------------------------------------------------------------------------

    public ArrayList<Functions> run(ArrayDeque<imAtoken> x)
    {
        while(!x.isEmpty())
        {
            program(x);
        }

        return functionList;
    }

//---------------------------------------------------------------------------------------------------------------

    public void program(ArrayDeque<imAtoken> x)
    {
        declist(x);
    }

//---------------------------------------------------------------------------------------------------------------

    public void declist(ArrayDeque<imAtoken> x)
    {
        declaration(x);
        decloop(x);
    }

//---------------------------------------------------------------------------------------------------------------

    public void declaration(ArrayDeque<imAtoken> x)
    {
        if(!x.isEmpty())
        {
            typeSpec(x);

            imAtoken token = x.peek();

            if(token.type.equals("ID"))
            {
                //Capture the declarations ID
                name = token.name;

                //Remove the token
                x.pop();
            }
            else
            {
                System.out.println("Error1: expected an ID but got a " + token.type + " on line "  + token.line);
                System.exit(0);
            }

            decFollow(x);
        }

    }

//---------------------------------------------------------------------------------------------------------------

    public void decloop(ArrayDeque<imAtoken> x)
    {
        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if( token.name.equals("int") || token.name.equals("void") || token.name.equals("float") )
            {
                declaration(x);
                decloop(x);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void typeSpec(ArrayDeque<imAtoken> x)
    {
        imAtoken token = x.peek();

        if( token.name.equals("int") || token.name.equals("void") || token.name.equals("float") )
        {
            //Capture the declarations type
           type = token.name;

            //Remove the token from the stack
            x.pop();
        }
        else
        {
            System.out.println("Error2: expected an int, void, or float but found " + token.name + " on line "  + token.line);
            System.exit(0);
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void decFollow(ArrayDeque<imAtoken> x)
    {
        //decfollow -> (params) compound | X

        boolean duplicate = true;

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("("))
            {
                x.pop();

                params(x);

                if(!x.isEmpty())
                {
                    token = x.peek();

                    if(token.name.equals(")"))
                    {
                        if(functionList.size() != 0)
                        {
                            //check for duplicate function
                            for(int t = 0; t <= (functionList.size()-1); t++)
                            {
                                if(name.equals(functionList.get(t).name))
                                {
                                    for(int s = 0; s < (functionList.get(s).pType.size()-1); s++)
                                    {
                                        if(!ptype.get(s).equals(functionList.get(s).pType.get(s)))
                                        {
                                            duplicate = false;
                                        }
                                    }
                                }
                                else if (t == functionList.size()-1)
                                {
                                    duplicate = false;
                                }
                            }
                        }
                        else
                        {
                            duplicate = false;
                        }

                        if(!duplicate)
                        {
                            //create a Function
                            Functions func = new Functions(name, type, ptype, pname, isAr);

                            //Send function to Function list
                            functionList.add(func);

                            System.out.println("function " + type + " " + name + " found");

                            //reset functions
                            name = "";
                            type = "";
                            ptype.clear();
                            pname.clear();
                            isAr = false;

                        }
                        else
                        {
                            System.out.println("error function " + type + " " + name + " already defined");
                            System.exit(0);
                        }

                        //remove end token
                        x.pop();

                        compound(x);
                    }
                }
            }
            else if(token.name.equals(";") || token.name.equals("["))
            {
                X(x);
            }
            else
            {
                System.out.println("Error3: Expected \"(\" or \";\" or \"[\" but found " + token + " on line "  + token.line);
                System.exit(0);
            }
        }
        else
        {
            System.out.println("Error: Out of Tokens");
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void params(ArrayDeque<imAtoken> x)
    {
        //params-> int ID paramtype parLoop | float ID paramtype parLoop | void parameter

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("int") || token.name.equals("float"))
            {
                //capture parameter type
                ptype.add(token.name);

                //remove the token
                x.pop();

                if(!x.isEmpty())
                {
                    token = x.peek();

                    if(token.type.equals("ID"))
                    {
                        //capture param name
                        pname.add(token.name);

                        //remove the token
                        x.pop();

                        paramType(x);

                        parLoop(x);
                    }
                }
                else
                {
                    System.out.println("Error: Out of Tokens");
                    System.exit(0);
                }
            }
            else
            {
                if(token.name.equals("void"))
                {
                    //capture parameter type
                    ptype.add(token.name);
                    pname.add("");

                    //remove token
                    x.pop();

                    parameter(x);
                }
                else
                {
                    System.out.println("Error4: Expected int, float, or void but found " + token + " on line "  + token.line);
                    System.exit(0);
                }
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void compound(ArrayDeque<imAtoken> x)
    {
        //compound-> { localDecs statementList }
        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("{"))
            {
                x.pop();

                localDecs(x);

                statementList(x);

                if(!x.isEmpty())
                {
                    token = x.peek();

                    if(token.name.equals("}"))
                    {
                        x.pop();
                    }
                    else
                    {
                        System.out.println("Error5: expected \"}\" but found " + token.name + " on line "  + token.line);
                        System.exit(0);
                    }
                }
                else
                {
                    System.out.println("Error: Out of Tokens");
                    System.exit(0);
                }
            }
            else
            {
                System.out.println("Error6: expected \"{\" but found " + token.name + " on line " + token.line);
                System.exit(0);
            }
        }
        else
        {
            System.out.println("Error: Out of Tokens");
        }

    }

//---------------------------------------------------------------------------------------------------------------

    public void X(ArrayDeque<imAtoken> x)
    {
        //X-> ; | [NUM] ;

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals(";"))
            {
                x.pop();
            }
            else
            {
                if(token.name.equals("["))
                {
                    x.pop();

                    NUM(x);

                    token = x.peek();

                    if(token.name.equals("]"))
                    {
                        x.pop();

                        token = x.peek();

                        if(token.name.equals(";"))
                        {
                            x.pop();
                        }
                        else
                        {
                            System.out.println("Error7: expected \";\" but found " + token.name + " on line "  + token.line);
                            System.exit(0);
                        }
                    }
                    else
                    {
                        System.out.println("Error8: expected \"]\" but found " + token.name + " on line "  + token.line);
                        System.exit(0);
                    }
                }
                else
                {
                    System.out.println("Error9: expected \"[\" but found " + token.name + " on line "  + token.line);
                    System.exit(0);
                }
            }
        }
        else
        {
            System.out.println("Error: out of Tokens");
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void NUM(ArrayDeque<imAtoken> x)
    {
        if(!x.isEmpty())
        {

            imAtoken token = x.peek();

            if(token.type.equals("Float") ||  token.type.equals("Int"))
            {
                x.pop();
            }
            else
            {
                System.out.println("Error10: expected an Int or a Float but found a " + token.type + " on line "  + token.line);
                System.exit(0);
            }
        }
        else
        {
            System.out.println("Error: Out of Tokens");
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void localDecs(ArrayDeque<imAtoken> x)
    {
        //localDecs-> typeSpec ID X localDecs | empty

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("int") || token.name.equals("void") || token.name.equals("float"))
            {
                typeSpec(x);

                token = x.peek();

                if(token.type.equals( "ID"))
                {
                    x.pop();

                    X(x);

                    localDecs(x);
                }
                else
                {
                    System.out.println("Error: Expected an ID found " + token + " on line "  + token.line);
                    System.exit(0);
                }
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void statementList(ArrayDeque<imAtoken> x)
    {
        //statementList-> statement statementList | empty
        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("{") || token.name.equals("(") || token.name.equals("if") || token.name.equals("while") || token.name.equals("return") || token.type.equals("ID") || token.type.equals("Int") || token.type.equals("Float") || token.name.equals(";"))
            {
                statement(x);

                statementList(x);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void statement(ArrayDeque<imAtoken> x)
    {
        //statement-> expressionSt | compound | selectionSt | iterationSt | returns
        // ( || { || if || while || return

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if (token.name.equals("(") || token.type.equals("Int") || token.type.equals("Float") || token.type.equals("ID") || token.type.equals(";"))
            {
                expressionSt(x);
            }
            else if (token.name.equals("{"))
            {
                compound(x);
            }
            else if (token.name.equals("if"))
            {
                selectionSt(x);
            }
            else if (token.name.equals("while"))
            {
                iterationSt(x);
            }
            else if (token.name.equals("return"))
            {
                returnSt(x);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void param(ArrayDeque<imAtoken> x)
    {
        // param-> typeSpec ID paramType

        typeSpec(x);

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.type.equals("ID"))
            {
                x.pop();

                paramType(x);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void paramType(ArrayDeque<imAtoken> x)
    {
        //paramType-> [ ] | empty

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("["))
            {
                //mark as array
                isAr = true;

                //remove token
                x.pop();

                if(!x.isEmpty())
                {
                    token = x.peek();

                    if(token.name.equals("]"))
                    {
                        x.pop();
                    }
                    else
                    {
                        System.out.println("Error11: expected \"]\" but found " + token.name + " on line "  + token.line);
                        System.exit(0);
                    }
                }
                else
                {
                    System.out.print("Error: Out of Tokens");
                    System.exit(0);
                }
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void parLoop(ArrayDeque<imAtoken> x)
    {
        //parLoop-> , param parLoop | empty

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals(","))
            {
                x.pop();

                param(x);

                parLoop(x);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void parameter(ArrayDeque<imAtoken> x)
    {
        //parameter-> ID paramtype parLoop | empty

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.type.equals("ID"))
            {
                x.pop();

                paramType(x);

                parLoop(x);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void expressionSt(ArrayDeque<imAtoken> x)
    {
        //expressionSt-> expression ; | ;

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals(";"))
            {
                x.pop();
            }
            else
            {
                expression(x);

                if(!x.isEmpty())
                {
                    token = x.peek();

                    if(token.name.equals(";"))
                    {


                        x.pop();
                    }
                    else
                    {
                        System.out.println("Error12: expected \";\" but found " + token.name + " on line "  + token.line);
                        System.exit(0);
                    }
                }
                else
                {
                    System.out.println("Error13: Out of Tokens");
                    System.exit(0);
                }
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void selectionSt(ArrayDeque<imAtoken> x)
    {
        //selectionSt-> if ( expression ) statement selectFollow

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("if"))
            {
                x.pop();

                if(!x.isEmpty())
                {
                    token = x.peek();

                    if(token.name.equals("("))
                    {
                        x.pop();

                        expression(x);

                        if(!x.isEmpty())
                        {
                            token = x.peek();

                            if(token.name.equals(")"))
                            {


                                x.pop();

                                statement(x);

                                selectFollow(x);
                            }
                            else
                            {
                                System.out.println("Error14: expected \")\" but got " + token.name + " on line "  + token.line);
                                System.exit(0);
                            }
                        }
                        else
                        {
                            System.out.println("Error15: out of tokens");
                            System.exit(0);
                        }
                    }
                }
                else
                {
                    System.out.println("Error16: out of tokens");
                    System.exit(0);
                }
            }
            else
            {
                System.out.println("Error17: expected \"if\" but found " + token.name + " on line "  + token.line);
                System.exit(0);
            }
        }
        else
        {
            System.out.println("Error18: out of tokens");
            System.exit(0);
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void iterationSt(ArrayDeque<imAtoken> x)
    {
        //iterationSt-> while ( expression ) statement

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("while"))
            {
                x.pop();

                if(!x.isEmpty())
                {
                    token = x.peek();

                    if(token.name.equals("("))
                    {
                        x.pop();

                        expression(x);

                        if(!x.isEmpty())
                        {
                            token = x.peek();

                            if(token.name.equals(")"))
                            {
                                x.pop();

                                statement(x);
                            }
                            else
                            {
                                System.out.println("Error19: expected \")\" but found " + token.name + " on line "  + token.line);
                                System.exit(0);
                            }
                        }
                    }
                    else
                    {
                        System.out.println("Error20: expected \"(\" but found " + token.name + " on line "  + token.line);
                        System.exit(0);
                    }
                }
                else
                {
                    System.out.println("Error21: out of tokens");
                    System.exit(0);
                }
            }
            else
            {
                System.out.println("Error22: expected \"while\" but found " + token.name + " on line "  + token.line);
                System.exit(0);
            }
        }
        else
        {
            System.out.println("Error23: out of tokens");
            System.exit(0);
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void returnSt(ArrayDeque<imAtoken> x)
    {
        //returnSt-> return retFollow

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("return"))
            {
                x.pop();

                retFollow(x);
            }
            else
            {
                System.out.println("Error24: expected return but found " + token.name + " on line "  + token.line);
                System.exit(0);
            }
        }
        else
        {
            System.out.println("Error25: out of tokens");
            System.exit(0);
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void expression(ArrayDeque<imAtoken> x)
    {
        //expression-> ( expression ) expFollow | NUM expFollow | ID idFollow

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("("))
            {
                x.pop();

                expression(x);

                if(!x.isEmpty())
                {
                    token = x.peek();

                    if(token.name.equals(")"))
                    {
                        x.pop();

                        expFollow(x);
                    }
                    else
                    {
                        System.out.println("Error26: expected ) but found " + token.name + " on line "  + token.line);
                        System.exit(0);
                    }
                }
                else
                {
                    System.out.println("Error27: out of tokens");
                    System.exit(0);
                }
            }
            else if(token.type.equals("Int") || token.type.equals("Float"))
            {
                NUM(x);

                expFollow(x);
            }
            else if(token.type.equals("ID"))
            {
                x.pop();

                idFollow(x);
            }
            else
            {
                System.out.println("Error: expected stuff but found " + token.name + " on line "  + token.line);
                System.exit(0);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void selectFollow(ArrayDeque<imAtoken> x)
    {
        //selectFollow-> else statement | empty

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("else"))
            {
                x.pop();

                statement(x);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void retFollow(ArrayDeque<imAtoken> x)
    {
        //retFollow-> ; | expression ;
        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals(";"))
            {
                x.pop();
            }
            else
            {
                expression(x);

                if(!x.isEmpty())
                {
                    token = x.peek();

                    if(token.name.equals(";"))
                    {


                        x.pop();
                    }
                    else
                    {
                        System.out.println("Error28: expected ; but found " + token.name + " on line "  + token.line);
                        System.exit(0);
                    }
                }
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void expFollow(ArrayDeque<imAtoken> x)
    {
        //expFollow-> termloop addExpLoop C

        termLoop(x);

        addExpLoop(x);

        C(x);
    }

//---------------------------------------------------------------------------------------------------------------

    public void termLoop(ArrayDeque<imAtoken> x)
    {
        //termLoop-> mulop factor termLoop | empty

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("*") || token.name.equals("/"))
            {
                mulop(x);

                factor(x);

                termLoop(x);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void C(ArrayDeque<imAtoken> x)
    {
        //C-> relop addExp | empty
        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("<=") || token.name.equals("<") || token.name.equals(">") || token.name.equals(">=") || token.name.equals("==") || token.name.equals("!="))
            {
                relop(x);

                addExp(x);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void relop(ArrayDeque<imAtoken> x)
    {
        //relop->  <= | <  | > | >= | == | !=

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("<=") || token.name.equals("<") || token.name.equals(">") || token.name.equals(">=") || token.name.equals("==") || token.name.equals("!="))
            {
                x.pop();
            }
            else
            {
                System.out.println("Error29: expected ; but found " + token.name + " on line "  + token.line);
                System.exit(0);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void addExp(ArrayDeque<imAtoken> x)
    {
        //addExp-> term addExpLoop

        term(x);

        addExpLoop(x);
    }

//---------------------------------------------------------------------------------------------------------------

    public void term(ArrayDeque<imAtoken> x)
    {
        factor(x);

        termLoop(x);
    }

//---------------------------------------------------------------------------------------------------------------

    public void addExpLoop(ArrayDeque<imAtoken> x)
    {
        //addExpLoop-> addop term addExpLoop | empty

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("+") || token.name.equals("-"))
            {
                x.pop();

                term(x);

                addExpLoop(x);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void mulop(ArrayDeque<imAtoken> x)
    {
        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("*") || token.name.equals("/"))
            {
                x.pop();
            }
            else
            {
                System.out.println("Error: looking for * or / and found " + token + " on line "  + token.line);
                System.exit(0);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void factor(ArrayDeque<imAtoken> x)
    {
        //factor-> (expression) | NUM | ID factorFollow
        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("("))
            {
                x.pop();

                expression(x);

                if(!x.isEmpty())
                {
                    token = x.peek();

                    if(token.name.equals(")"))
                    {
                        x.pop();
                    }
                    else
                    {
                        System.out.println("Error30: expected ; but found " + token.name + " on line "  + token.line);
                        System.exit(0);
                    }
                }
                else
                {
                    System.out.println("Error31: out of tokens");
                    System.exit(0);
                }
            }
            else if(token.type.equals("Int") || token.type.equals("Float"))
            {
                NUM(x);
            }
            else if(token.type.equals("ID"))
            {
                x.pop();

                factorFollow(x);
            }
            else
            {
                System.out.println("Error32: expected ( or an int or a float or an ID but found " + token.name + " on line "  + token.line);
                System.exit(0);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void factorFollow(ArrayDeque<imAtoken> x)
    {
        //factorFollow-> B | ( args)
        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("("))
            {
                x.pop();

                args(x);

                if(!x.isEmpty())
                {
                    token = x.peek();

                    if(token.name.equals(")"))
                    {

                        x.pop();
                    }
                }
            }
            else
            {
                B(x);
            }

        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void idFollow(ArrayDeque<imAtoken> x)
    {
        //idFollow-> BM | ( args ) expFollow

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("("))
            {
                x.pop();

                args(x);

                if(!x.isEmpty())
                {
                    token = x.peek();

                    if(token.name.equals(")"))
                    {
                        x.pop();

                        expFollow(x);
                    }
                    else
                    {
                        System.out.println("Error33: expected ) but found " + token.name + " on line "  + token.line);
                        System.exit(0);
                    }
                }
                else
                {
                    System.out.println("Error34: out of tokens");
                    System.exit(0);
                }
            }
            else
            {
                B(x);

                M(x);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void args(ArrayDeque<imAtoken> x)
    {
        //args-> argList | empty

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if (token.name.equals("(") || token.type.equals("Int") || token.type.equals("Float") || token.type.equals("ID"))
            {
                argList(x);
            }

        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void argList(ArrayDeque<imAtoken> x)
    {
        //argList-> expression argListLoop

        expression(x);

        argsListLoop(x);
    }

//---------------------------------------------------------------------------------------------------------------

    public void argsListLoop(ArrayDeque<imAtoken> x)
    {
        //argListLoop-> , expression argListLoop | empty

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals(","))
            {
                x.pop();

                expression(x);

                argsListLoop(x);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void B(ArrayDeque<imAtoken> x)
    {
        //B-> [ expression ] | empty

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("["))
            {


                x.pop();

                expression(x);

                if(!x.isEmpty())
                {
                    token = x.peek();

                    if(token.name.equals("]"))
                    {


                        x.pop();
                    }
                    else
                    {
                        System.out.println("Error35: expected ] but found " + token.name + " on line "  + token.line);
                        System.exit(0);
                    }
                }
                else
                {
                    System.out.println("Error36: out of tokens");
                    System.exit(0);
                }
            }
        }

    }

//---------------------------------------------------------------------------------------------------------------

    public void M(ArrayDeque<imAtoken> x)
    {
        //M-> = expression | expFollow

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("="))
            {

                x.pop();

                expression(x);
            }
            else
            {
                expFollow(x);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------
}
