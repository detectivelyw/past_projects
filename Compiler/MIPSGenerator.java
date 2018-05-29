import java.io.*;

public class MIPSGenerator {
  public static void main(String args[]) throws ParseException, IOException
  {
	  String input, output, pigletProcedureInfo;
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
	output = input.substring(0, input.length()-4);
	output = output+".s";
	File file = new File(input);
    FileInputStream fs= new FileInputStream(file);
    SpigletParser parser = new SpigletParser(fs);
    
    try 
    {
      String tempInfo;
      String[] splitInfo = new String[3];
      FileReader fr = new  FileReader(pigletProcedureInfo);   
      BufferedReader br = new  BufferedReader(fr); 
      ProcedureInfo PI = new ProcedureInfo();
      while ((tempInfo = br.readLine()) != null)
      {
    	  ProcedureNode tempNode = new ProcedureNode();
    	  splitInfo =  tempInfo.split(" ");
          tempNode.startTempNumber = Integer.parseInt(splitInfo[1]);
          tempNode.numberOfLocal = Integer.parseInt(splitInfo[2]);
          PI.ProcedureTable.put(splitInfo[0], tempNode);
      }

      ProcedureNode tempNode = new ProcedureNode();
      tempNode.startTempNumber = 0;
      tempNode.numberOfTemperaries = 0;
      tempNode.numberOfParameters = 0;
      tempNode.numberOfLocal = 0;
      PI.ProcedureTable.put("MAIN", tempNode);
      
      SimpleNode n = parser.Goal();

      SpigletParserVisitor v1 = new LivenessAnalysis();
      n.jjtAccept(v1,null);
 
      SpigletParserVisitor v2 = new GatherInfo(PI, ((LivenessAnalysis)v1).VeriableLiveness );
      n.jjtAccept(v2,null);
        
      SpigletParserVisitor v3 = new LivenessAnalysis();
      n.jjtAccept(v3,null);
         
      SpigletParserVisitor v4 = new SpigletToMIPS(((GatherInfo)v2).PI, ((LivenessAnalysis)v3).VeriableLiveness, ((GatherInfo)v2).offSetOfTemperary);
      n.jjtAccept(v4,null);
 
      ((SpigletToMIPS)v4).CreateMIPSCode(output);
            
      // System.out.println("Print the JJTree of the input program:");
      // n.dump("");
      
      System.out.println("Finally, we get the MIPS code!!!");
      System.out.println("The output MIPS code has been generated under the name: "+output);
      System.out.println("Thank you for being with us all the time!");
    	  
     }
    catch (Exception e)
    { 
      System.out.println("Oops.");
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }

}