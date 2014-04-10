/**
 * Created by Michael Perkins on 2/23/14.
 * Parser Class
 */

import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

import java.util.ArrayDeque;
import java.util.ArrayList;

public class Parser
{

    //stuff for func dec
    ArrayList<Functions> functionList = new ArrayList<Functions>();
    String name = "";
    String type = "";
    Boolean isAr = false;
    ArrayList<String> pname = new ArrayList<String>();

    //Holds the symbol tables which hold the declarations
    ArrayList<SymTable> decList = new ArrayList<SymTable>();
    ArrayList<String> ptype = new ArrayList<String>();
    String ID;
    int arraySize;
    boolean Array = true;
    String FunName;
    String FunType;
    int ifWhile = 0;


//---------------------------------------------------------------------------------------------------------------

    public String run(ArrayDeque<imAtoken> x, ArrayList<Functions> y, String conti)
    {
        while(!x.isEmpty())
        {
            //create global variable list
            SymTable symtab = new SymTable();
            decList.add(symtab);

            program(x,y);
        }

        conti = "The code can be parsed";

        return conti;
    }

//---------------------------------------------------------------------------------------------------------------

    public void program(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
        declist(x,y);
    }

//---------------------------------------------------------------------------------------------------------------

    public void declist(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
        declaration(x,y);
        decloop(x,y);
    }

//---------------------------------------------------------------------------------------------------------------

    public void declaration(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {       
        if(!x.isEmpty())
        {
            typeSpec(x,y);

            imAtoken token = x.peek();

            if(token.type.equals("ID"))
            {
                for(int i = decList.size() - 1; i > 0; i--)
                {
                    for(int v = 0; v < decList.get(i).stacker.size() - 1; v++)
                    {
                        if (decList.get(i).lookUp(v).ID.equals(token.name))
                        {
                            ptype.add(decList.get(i).lookUp(i).type);
                            System.out.println("Variable " + token.name + " has been defined before");
                        }
                        else if (i == 0)
                        {
                            System.out.println("variable " + token.name + " is not defined");
                            System.exit(0);
                        }
                    }
                }

                //Capture declaration ID
                ID = token.name;

                //remove token
                x.pop();
            }
            else
            {
                System.out.println("Error1: expected an ID but got a " + token.type + " on line "  + token.line);
                System.exit(0);
            }

            decFollow(x,y);
        }

    }

//---------------------------------------------------------------------------------------------------------------

    public void decloop(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if( token.name.equals("int") || token.name.equals("void") || token.name.equals("float") )
            {
                declaration(x,y);
                decloop(x,y);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void typeSpec(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
        imAtoken token = x.peek();

      if( token.name.equals("int") || token.name.equals("void") || token.name.equals("float") )
      {
          //capture declaration type
          type = token.name;

          //remove token
          x.pop();
      }
      else
      {
          System.out.println("Error2: expected an int, void, or float but found " + token.name + " on line "  + token.line);
          System.exit(0);
      }
    }

//---------------------------------------------------------------------------------------------------------------

    public void decFollow(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
        //decfollow -> (params) compound | X

        boolean duplicate = true;

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("("))
            {
                x.pop();

                params(x,y);

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
                            Functions func = new Functions(ID, type, ptype, pname, isAr);

                            //Send function to Function list
                            functionList.add(func);

                            System.out.println("function " + type + " " + ID + " found");

                            //reset functions
                            name = "";
                            type = "";
                            ptype.clear();
                            pname.clear();
                            isAr = false;

                        }
                        else
                        {
                            System.out.println("error function " + type + " " + ID + " already defined");
                            System.exit(0);
                        }

                        x.pop();

                        compound(x,y);
                    }
                }
            }
            else if(token.name.equals(";") || token.name.equals("["))
            {
                X(x,y);
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

    public void params(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
        //params-> int ID paramtype parLoop | float ID paramtype parLoop | void parameter

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("int") || token.name.equals("float"))
            {
                //capture parameter type
                ptype.add(token.name);

                //remove token
                x.pop();

                if(!x.isEmpty())
                {
                    token = x.peek();
System.out.println("you shouldnt see this -> " + token.name);
                    if(token.type.equals("ID"))
                    {
                        //capture param name
                        pname.add(token.name);

                        x.pop();

                        paramType(x,y);

                        parLoop(x,y);
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

                    x.pop();

                    parameter(x,y);
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

    public void compound(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
        //compound-> { localDecs statementList }
        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("{"))
            {
                if(ifWhile == 0)
                {
                    //create a new symbol table
                    SymTable symTab = new SymTable();

                    //add table to List
                    decList.add(symTab);
                }
                    //remove token
                    x.pop();

                    localDecs(x, y);

                    statementList(x, y);

                    if (!x.isEmpty()) {
                        token = x.peek();

                        if (token.name.equals("}"))
                        {
                            if(ifWhile > 0)
                            {
                                ifWhile--;
                            }
                            else if(ifWhile == 0)
                            {
                                //drop current scope
                                decList.remove(decList.size() - 1);
                            }

                            //remove token
                            x.pop();
                        }
                        else
                        {
                            System.out.println("Error5: expected \"}\" but found " + token.name + " on line " + token.line);
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

    public void X(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
        //X-> ; | [NUM] ;
        Array = false;

        boolean duplicate = true;

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals(";"))
            {
                if(decList.get(decList.size()-1).stacker.size() != 0)
                {
                    //check for duplicate variable
                    for(int t = 0; t <= decList.get(decList.size()-1).stacker.size()-1; t++)
                    {
                        if(!ID.equals(decList.get(decList.size()-1).lookUp(t).ID))
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
                    Declaration dec = new Declaration(ID, type, arraySize, Array);

                    //add declaration to table
                    decList.get(decList.size()-1).insert(dec);

                    System.out.println(dec.type + " " + dec.ID + " found");
                }
                else
                {
                    System.out.println("Error1:" + ID + " already declared in this scope");
                    System.exit(0);
                }

                //remove token
                x.pop();
            }
            else
            {
                if(token.name.equals("["))
                {

                    //declare it an array
                    Array = true;

                    x.pop();

                    NUM(x,y);

                    if(!x.isEmpty())
                    {
                        token = x.peek();

                        if (token.name.equals("]"))
                        {
                            x.pop();

                            if (!x.isEmpty()) {

                                token = x.peek();

                                if (token.name.equals(";"))
                                {
                                    if(((decList.get(decList.size()-1).stacker.size()))!= 0)
                                    {
                                        //check for duplicate variable
                                        for(int t = 0; t <= decList.get(decList.size()-1).stacker.size()-1; t++)
                                        {
                                            if(!ID.equals(decList.get(decList.size()-1).lookUp(t).ID))
                                            {
                                                duplicate = false;
                                            }
                                            else if(t == decList.get(decList.size()-1).stacker.size()-1 )
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
                                        Declaration dec = new Declaration(ID, type, arraySize, Array);

                                        //add dec to table
                                        decList.get(decList.size()-1).insert(dec);
                                        System.out.println(dec.type + "[" + arraySize + "] " + dec.ID + " found");
                                    }
                                    else
                                    {
                                        System.out.println("Error2:" + ID + " already declared in this scope");
                                        System.exit(0);
                                    }

                                    //remove token
                                    x.pop();

                                }
                                else
                                {
                                    System.out.println("Error7: expected \";\" but found " + token.name + " on line " + token.line);
                                    System.exit(0);
                                }
                            }
                            else
                            {
                                System.out.println("Error: Out of Tokens");
                            }
                        }
                        else
                        {
                            System.out.println("Error8: expected \"]\" but found " + token.name + " on line " + token.line);
                            System.exit(0);
                        }
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

    public void NUM(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

             if(token.type.equals("float") ||  token.type.equals("int"))
             {
                 if(Array && token.type.equals("int"))
                 {
                     //capture length of array
                     arraySize = Integer.parseInt(token.name);
                 }
                 else if(Array && token.type.equals("float"))
                 {
                     System.out.println("Arrays can not be of size \"float\"");
                     System.exit(0);
                 }

                     //remove token
                     x.pop();
             }
            else
             {
                 System.out.println("Error10: expected an int or a float but found a " + token.type + " on line "  + token.line);
                 System.exit(0);
             }
        }
        else
        {
            System.out.println("Error: Out of Tokens");
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void localDecs(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
        //localDecs-> typeSpec ID X localDecs | empty

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("int") || token.name.equals("void") || token.name.equals("float"))
            {
                typeSpec(x,y);

                token = x.peek();

                if(token.type.equals( "ID"))
                {
                    if(type.equals("void"))
                    {
                        System.out.println("Error: variables can not be of type void");
                        System.exit(0);
                    }
                    else
                    {
                        //capture the ID
                        ID = token.name;
                    }

                    //remove the token
                    x.pop();

                     X(x,y);

                    localDecs(x,y);
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

    public void statementList(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
        //statementList-> statement statementList | empty
        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("{") || token.name.equals("(") || token.name.equals("if") || token.name.equals("while") || token.name.equals("return") || token.type.equals("ID") || token.type.equals("int") || token.type.equals("float") || token.name.equals(";"))
            {
                statement(x,y);

                statementList(x,y);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void statement(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
        //statement-> expressionSt | compound | selectionSt | iterationSt | returns
                    // ( || { || if || while || return

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if (token.name.equals("(") || token.type.equals("int") || token.type.equals("float") || token.type.equals("ID") || token.type.equals(";"))
            {
                expressionSt(x,y);
            }
            else if (token.name.equals("{"))
            {
                compound(x,y);
            }
            else if (token.name.equals("if"))
            {
                selectionSt(x,y);
            }
            else if (token.name.equals("while"))
            {
                iterationSt(x, y);
            }
            else if (token.name.equals("return"))
            {
                returnSt(x,y);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void param(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
       // param-> typeSpec ID paramType

        typeSpec(x,y);

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.type.equals("ID"))
            {
                for(int i = decList.size() - 1; i > 0; i--)
                {
                    if(decList.get(i).lookUp(i).ID.equals(token.name))
                    {
                        ptype.add(decList.get(i).lookUp(i).type);
                        System.out.println("variable " + token.name + " has been defined before");
                    }
                    else if (i == 0)
                    {
                        System.out.println("variable " + token.name + " is not defined");
                        System.exit(0);
                    }
                }

                x.pop();

                paramType(x,y);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void paramType(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
        //paramType-> [ ] | empty

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("["))
            {
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

    public void parLoop(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
        //parLoop-> , param parLoop | empty

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals(","))
            {
                x.pop();

                param(x,y);

                parLoop(x,y);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void parameter(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
        //parameter-> ID paramtype parLoop | empty

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.type.equals("ID"))
            {
                for(int i = decList.size() - 1; i > 0; i--)
                {
                    for(int v = 0; v < decList.get(i).stacker.size()-1; v++)
                    {
                        if (decList.get(i).lookUp(v).ID.equals(token.name))
                        {
                            ptype.add(decList.get(i).lookUp(i).type);
                            System.out.println("Variable " + token.name + " has been defined before");
                        }
                        else if (i == 0)
                        {
                            System.out.println("variable " + token.name + " is not defined");
                            System.exit(0);
                        }
                    }
                }

                x.pop();

                paramType(x,y);

                parLoop(x,y);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void expressionSt(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
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
                expression(x,y);

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

    public void selectionSt(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
        //selectionSt-> if ( expression ) statement selectFollow

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("if"))
            {
                //deny symbol table construction
                ifWhile++;

                x.pop();

                if(!x.isEmpty())
                {
                    token = x.peek();

                    if(token.name.equals("("))
                    {
                        x.pop();

                        expression(x,y);

                        if(!x.isEmpty())
                        {
                            token = x.peek();

                            if(token.name.equals(")"))
                            {
                                x.pop();

                                statement(x,y);

                                selectFollow(x,y);
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

    public void iterationSt(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
        //iterationSt-> while ( expression ) statement

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("while"))
            {
                //deny symbol table construction
                ifWhile++;

                x.pop();

                if(!x.isEmpty())
                {
                    token = x.peek();

                    if(token.name.equals("("))
                    {
                        x.pop();

                        expression(x,y);

                        if(!x.isEmpty())
                        {
                            token = x.peek();

                            if(token.name.equals(")"))
                            {
                                x.pop();

                                statement(x,y);
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

    public void returnSt(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
        //returnSt-> return retFollow

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("return"))
            {
                x.pop();

                retFollow(x,y);
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

    public void expression(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
        //expression-> ( expression ) expFollow | NUM expFollow | ID idFollow

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("("))
            {                
                x.pop();

                expression(x,y);

                if(!x.isEmpty())
                {
                    token = x.peek();

                    if(token.name.equals(")"))
                    {
                        x.pop();

                        expFollow(x,y);
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
            else if(token.type.equals("int") || token.type.equals("float"))
            {
                NUM(x,y);

                expFollow(x,y);
            }
            else if(token.type.equals("ID"))
            {
                //You need to be coding here
                for(int i = decList.size() - 1; i > 0; i--)
                {
                    for(int v = 0; v < decList.get(i).stacker.size() - 1; v++)
                    {
                        if (decList.get(i).lookUp(v).ID.equals(token.name))
                        {
                            ptype.add(decList.get(i).lookUp(i).type);
                            System.out.println("Variable " + token.name + " has been defined before");
                        }
                        else if (i == 0)
                        {
                            System.out.println("variable " + token.name + " is not defined");
                            System.exit(0);
                        }
                    }
                }

                //capture ID for checking
                FunName = token.name;

                //remove token
                x.pop();

                idFollow(x,y);
            }
            else
            {
                System.out.println("Error: expected stuff but found " + token.name + " on line "  + token.line);
                System.exit(0);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void selectFollow(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
        //selectFollow-> else statement | empty

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("else"))
            {
                x.pop();

                statement(x,y);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void retFollow(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
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
                expression(x,y);

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

    public void expFollow(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
        //expFollow-> termloop addExpLoop C

        termLoop(x,y);

        addExpLoop(x,y);

        C(x,y);
    }

//---------------------------------------------------------------------------------------------------------------

    public void termLoop(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
        //termLoop-> mulop factor termLoop | empty

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("*") || token.name.equals("/"))
            {
                mulop(x,y);

                factor(x,y);

                termLoop(x,y);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void C(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
        //C-> relop addExp | empty
        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("<=") || token.name.equals("<") || token.name.equals(">") || token.name.equals(">=") || token.name.equals("==") || token.name.equals("!="))
            {
                relop(x,y);

                addExp(x,y);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void relop(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
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

    public void addExp(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
        //addExp-> term addExpLoop

        term(x,y);

        addExpLoop(x,y);
    }

//---------------------------------------------------------------------------------------------------------------

    public void term(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
        factor(x,y);

        termLoop(x,y);
    }

//---------------------------------------------------------------------------------------------------------------

    public void addExpLoop(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
        //addExpLoop-> addop term addExpLoop | empty

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("+") || token.name.equals("-"))
            {
                x.pop();

                term(x,y);

                addExpLoop(x,y);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void mulop(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
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

    public void factor(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
        //factor-> (expression) | NUM | ID factorFollow
        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("("))
            {
                x.pop();

                expression(x,y);

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
            else if(token.type.equals("int") || token.type.equals("float"))
            {
                if(type.equals("int") || type.equals("float"))


                NUM(x,y);
            }
            else if(token.type.equals("ID"))
            {
                for(int i = decList.size() - 1; i > 0; i--)
                {
                    for(int v = 0; v < decList.get(i).stacker.size()-1; v++)
                    {
                        if (decList.get(i).lookUp(v).ID.equals(token.name))
                        {
                            ptype.add(decList.get(i).lookUp(i).type);
                            System.out.println("Variable " + token.name + " has been defined before");
                        }
                        else if (i == 0)
                        {
                            System.out.println("variable " + token.name + " is not defined");
                            System.exit(0);
                        }
                    }
                }

                x.pop();

                factorFollow(x,y);
            }
            else
            {
                System.out.println("Error32: expected ( or an int or a float or an ID but found " + token.name + " on line "  + token.line);
                System.exit(0);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void factorFollow(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
        //factorFollow-> B | ( args)
        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

             if(token.name.equals("("))
            {
                x.pop();

                args(x,y);

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
                B(x,y);
            }

        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void idFollow(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
        //idFollow-> BM | ( args ) expFollow

        Boolean corCall = true;

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("("))
            {
                x.pop();

                args(x,y);

                if(!x.isEmpty())
                {
                    token = x.peek();

                    if(token.name.equals(")"))
                    {
                        if(functionList.size() > 1)
                        {
                            for (int i = 0; i < functionList.size() - 1; i++)
                            {
                                if (FunName.equals(functionList.get(i).name))
                                {
                                    if (!ptype.equals(functionList.get(i).pType))
                                    {
                                        System.out.println("function " + FunName + " not defined");
                                        System.exit(0);
                                    }
                                    else
                                    {
                                        System.out.println("function " + FunName + " successfully called");
                                    }
                                }
                                else if (i == ptype.size() - 1)
                                {
                                    System.out.println("function " + FunName + " not defined");
                                    System.exit(0);
                                }
                            }
                        }
                        else
                        {
                            for(int r = 0; r <= 1; r++)
                            {
                                if (FunName.equals(functionList.get(r).name))
                                {
                                    if (!ptype.equals(functionList.get(r).pType))
                                    {
                                        System.out.println("function " + FunName + " not defined");
                                        System.exit(0);
                                    }
                                    else
                                    {
                                        System.out.println(" function " + FunName + " successfully called");
                                    }
                                }
                                else
                                {
                                    System.out.println("function " + FunName + " not defined");
                                    System.exit(0);
                                }
                            }
                        }
                        x.pop();

                        expFollow(x,y);
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
                B(x, y);

                M(x, y);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void args(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
        //args-> argList | empty

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if (token.name.equals("(") || token.type.equals("int") || token.type.equals("float") || token.type.equals("ID"))
            {
                //capture paramtype
                if(token.type.equals("ID"))
                {
                    for(int i = decList.size() - 1; i > 0; i--)
                    {
                        for(int v = 0; v < decList.get(i).stacker.size()-1; v++)
                        {
                            if (decList.get(i).lookUp(v).ID.equals(token.name))
                            {
                                ptype.add(decList.get(i).lookUp(i).type);
                                System.out.println("Variable " + token.name + " has been defined before");
                            }
                            else if (i == 0)
                            {
                                System.out.println("variable " + token.name + " is not defined");
                                System.exit(0);
                            }
                        }
                    }

                }
                else if(!token.name.equals("("))
                {
                    ptype.add(token.type);
                }

                argList(x, y);
            }

        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void argList(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
        //argList-> expression argListLoop

        expression(x,y);

        argsListLoop(x,y);
    }

//---------------------------------------------------------------------------------------------------------------

    public void argsListLoop(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
        //argListLoop-> , expression argListLoop | empty

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals(","))
            {
                x.pop();

                expression(x,y);

                argsListLoop(x,y);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void B(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
        //B-> [ expression ] | empty

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("["))
            {
                //flag variable as an array
                Array = true;

                for(int i = decList.size() - 1; i > 0; i--)
                {
                    if(decList.get(i).lookUp(i).ID.equals(token.name))
                    {
                        for(int v = 0; v < decList.get(i).stacker.size()-1; v++)
                        {
                            if(decList.get(i).stacker.get(v).Array == Array && decList.get(i).stacker.get(v).ID.equals(name))
                            {
                                ptype.add(decList.get(i).lookUp(v).type);
                                System.out.println("variable " + name + "is secure");
                            }
                        }
                    }
                    else if (i == 0)
                    {
                        System.out.println("variable " + name + " is not defined");
                        System.exit(0);
                    }
                }

                x.pop();

                expression(x,y);

                if(!x.isEmpty())
                {
                    token = x.peek();

                    if(token.name.equals("]"))
                    {
                        //reset array parameter
                        Array = false;

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

    public void M(ArrayDeque<imAtoken> x, ArrayList<Functions> y)
    {
        //M-> = expression | expFollow

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("="))
            {
                //need to compare each side
                x.pop();

                expression(x,y);
            }
            else
            {
                expFollow(x,y);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

}
