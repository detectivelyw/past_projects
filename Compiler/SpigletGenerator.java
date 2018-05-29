import java.io.*;

public class SpigletGenerator {
  public static void main(String args[]) throws ParseException, IOException
  {
	  String input, output;
	  String tempNumber = "../tempNumber.txt";
	  input = "";
	 
	  int currentTempNumber;
	  FileInputStream file=new FileInputStream(tempNumber);
      DataInputStream infile=new DataInputStream(file);
      currentTempNumber = infile.readInt();
      infile.close();
	  file.close();
	  
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
	output = input.substring(0, input.length()-3);
	output = output+".spg";
	File file2 = new File(input);
    FileInputStream fs= new FileInputStream(file2);
    PigletParser parser = new PigletParser(fs);
    
    try 
    {
      SimpleNode n = parser.Goal();
      PigletParserVisitor v = new PigletToSpiglet(currentTempNumber);
      n.jjtAccept(v,null);
      ((PigletToSpiglet)v).CreateSpigletCode(output);
      System.out.println("The input Piglet code has been transformed to ");
      System.out.println("Spiglet code under the name: "+output);
    }
    catch (Exception e)
    { 
      System.out.println("Oops.");
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }

}