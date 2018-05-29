import java.io.*;

public class MiniJavaCompiler {
  public static void main(String args[]) throws ParseException, IOException
  {
	  String input, output, tempNumber, pigletProcedureInfo;
	  tempNumber = "../tempNumber.txt";
	  pigletProcedureInfo = "../procedureInfo.txt";
	  input = "";
	  if (args.length == 1) 
	  {
		 try
		 {
			 input = args[0];
		 }
		 catch (Exception e)
		 {
			System.out.println("Input File Error! Cannot proceed!");
		 } finally {
    		}
	  }
	  
	  else
	  {
		  BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
		  input = stdin.readLine();
	  }

    input = "../"+input;
	output = input.substring(0, input.length()-5);
	output = output+".pg";
	File file = new File(input);
    FileInputStream fs= new FileInputStream(file);
    MiniJava parser = new MiniJava(fs);
    
    try 
    {
      SimpleNode n = parser.Goal();
      // System.out.println("Print the JJTree of the input program:");
      // n.dump("");
      System.out.println("Lexical && Syntax Analysis: OK.");

      MiniJavaVisitor v1 = new MyMakeTableVisitor();
      n.jjtAccept(v1,null);
      if(!((MyMakeTableVisitor)v1).isRight)
      {
        System.out.println("Build Symbol Tables: Failed... ");
        return;
      }
      System.out.println("Build Symbol Tables: OK.");

      MiniJavaVisitor v2 = new MyCheckTypeVisitor(((MyMakeTableVisitor)v1).tb);
      n.jjtAccept(v2,null);
      if(!((MyCheckTypeVisitor)v2).isRight)
      {
        System.out.println("Type Checking: FAILED...");
        return;
      }
      System.out.println("Type Checking: OK.");
      System.out.println("The Semantic Analysis has completed successfully.");
         
      MiniJavaVisitor v3 = new MiniJavaToPiglet(((MyMakeTableVisitor)v1).tb);
      n.jjtAccept(v3,null);
      ((MiniJavaToPiglet)v3).CreatePigletCode(output);
      ((MiniJavaToPiglet)v3).CreateTempNumber(tempNumber);
      ((MiniJavaToPiglet)v3).CreatePigletProcedureInfo(pigletProcedureInfo);
      System.out.println("The input Minijava program has been compiled successfully!");
      System.out.println("The output Piglet code has been generated under the name:"+output);
      System.out.println("Thank you for using our MiniJava Compiler!");
    	  
     }
    catch (Exception e)
    { 
      System.out.println("Oops.");
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }

}