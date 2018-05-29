import java.io.FileWriter;
import java.io.IOException;
import java.util.Queue;
import java.util.LinkedList;

public class SpigletToMIPS implements SpigletParserVisitor
{
	private StringBuffer MIPSCode;
	private int[] VeriableLiveness;
    private ProcedureInfo PI;
    private String currentProcedureName;
    private int[] register_t;
    private int[] register_s;
    private Queue<Integer> freeRegister_t;
    private Queue<Integer> freeRegister_s;
    private int[] offSet;
    private int localOffSetOnStack;
    private int temperaryOffSetOnStack;
       	
    SpigletToMIPS(ProcedureInfo P, int[] v, int[]offset)
	{
		MIPSCode = new StringBuffer();
		VeriableLiveness = new int[20001];
		VeriableLiveness = v;
		offSet = new int[20001];
		offSet = offset;
		PI = new ProcedureInfo();
	    PI = P;
	  
	    freeRegister_t = new LinkedList<Integer>();
	    freeRegister_s = new LinkedList<Integer>();
	   
	    register_t = new int[10];
	    register_s = new int[8];
	    localOffSetOnStack = 0;
	    temperaryOffSetOnStack = 0;

		int i;
		for (i = 0; i < 10; i++)
		{
			register_t[i] = -1;
			freeRegister_t.add(i);
		}
		for (i = 0; i < 8; i++)
		{
			register_s[i] = -1;
			freeRegister_s.add(i);
		}
	}
    
    public void clearRegisterRecord()
    {
    	freeRegister_t.clear();
    	freeRegister_s.clear();
    	int i;
    	for (i = 0; i < 10; i++)
		{
			register_t[i] = -1;
			freeRegister_t.add(i);
		}
		for (i = 0; i < 8; i++)
		{
			register_s[i] = -1;
			freeRegister_s.add(i);
		}
    }
    
    public int GetProcedureFrameSize(String ProcedureName)
    {
    	int frameSize = 0;
    	ProcedureNode info = new ProcedureNode();
    	int numberOfLocal;
    	int numberOfTemperaries;
    	int maxParameterNumber;
    	info = PI.ProcedureTable.get(ProcedureName);
    	numberOfLocal = info.numberOfLocal;
    	numberOfTemperaries = info.numberOfTemperaries;
    	maxParameterNumber = PI.maxParameterNumber;
    	frameSize = 4*(2+numberOfLocal+10+8+numberOfTemperaries+maxParameterNumber);
        	
    	return frameSize;
    }
    
    public int SpillToStack(int TempNumber)
    {
    	ProcedureNode info = new ProcedureNode();
    	int numberOfTemperaries;
    	int maxParameterNumber;
    	info = PI.ProcedureTable.get(currentProcedureName);
    	numberOfTemperaries = info.numberOfTemperaries;
    	maxParameterNumber = PI.maxParameterNumber;
    	
    	int result = 0;
    	if ((TempNumber <= 19)&&(TempNumber >= 0))
    	{
    		int frameSize = GetProcedureFrameSize(currentProcedureName);
    		result = 4*TempNumber+frameSize;
    		return result;
    	}
    	
		if (!currentProcedureName.equals("MAIN"))
		{
			ProcedureNode currentLocalInfo = new ProcedureNode();
		    int currentLocalStart;
		    int currentLocalNumber;
		    currentLocalInfo = PI.ProcedureTable.get(currentProcedureName);
		    currentLocalStart = currentLocalInfo.startTempNumber;
		    currentLocalNumber = currentLocalInfo.numberOfLocal;
		
		    if ((TempNumber >= currentLocalStart)&&(TempNumber < currentLocalStart+currentLocalNumber))
		    {
		    	if (offSet[TempNumber] == 0)
		    	{
		    		localOffSetOnStack++;
		    		offSet[TempNumber] = localOffSetOnStack;
		    	}
		    	
		    	result = 4*(offSet[TempNumber]-1+maxParameterNumber+numberOfTemperaries+10+8);
	    		return result;
		    }
		}
		
		if (offSet[TempNumber] == 0)
    	{
			temperaryOffSetOnStack++;
    		offSet[TempNumber] = temperaryOffSetOnStack;
    	}
    	
    	result = 4*(offSet[TempNumber]-1+maxParameterNumber);
		return result;
    }
	
	public void CreateMIPSCode(String outputFileName) throws IOException
	{
		FileWriter output = new FileWriter(outputFileName);
		String result = MIPSCode.toString();
		output.write(result);
		output.close();	
	}
	
	public String RegisterAllocate(int TempNumber)
	{
		String fail = "Fail";
		String success;
		int AllocatedRegister;
		if ((TempNumber >= 0)&&(TempNumber <= 19))
		{
			return fail;
		}
		
		if (!currentProcedureName.equals("MAIN"))
		{
			ProcedureNode currentLocalInfo = new ProcedureNode();
		    int currentLocalStart;
		    int currentLocalNumber;
		    currentLocalInfo = PI.ProcedureTable.get(currentProcedureName);
		    currentLocalStart = currentLocalInfo.startTempNumber;
		    currentLocalNumber = currentLocalInfo.numberOfLocal;
		
		    if ((TempNumber >= currentLocalStart)&&(TempNumber < currentLocalStart+currentLocalNumber))
		    {
		    	int i;
			    for (i = 0; i < 8; i++)
			    {
			    	if (register_s[i] == TempNumber)
				    {
			    		AllocatedRegister = i;
					    success = "s"+AllocatedRegister;
					    return success;
				    }
			    }
			    if (freeRegister_s.isEmpty())
			    {
			    	return fail;
			    }
			    else
			    {
			    	AllocatedRegister = freeRegister_s.poll();
				    register_s[AllocatedRegister] = TempNumber;
				    success = "s"+AllocatedRegister;
				    return success;
			    }
		    }
		}
		
		int i;
		for (i = 0; i < 10; i++)
		{
			if (register_t[i] == TempNumber)
			{
			    AllocatedRegister = i;
				success = "t"+AllocatedRegister;
				return success;
			}
		}
		
		for (i = 0; i < 10; i++)
		{
			if (register_t[i] != -1)
			{
				if ((VeriableLiveness[register_t[i]] == 0)&&(!freeRegister_t.contains(i)))
				{
					freeRegister_t.add(i);
				}
			}
		}

		if (freeRegister_t.isEmpty())
		{
			return fail;
		}
		else
		{
			AllocatedRegister = freeRegister_t.poll();
			register_t[AllocatedRegister] = TempNumber;
			success = "t"+AllocatedRegister;
			return success;
		}
	}
		
	

	public Object visit(SimpleNode node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTGoal node, Object data)
	{
		currentProcedureName = "MAIN";
		int frameSize = GetProcedureFrameSize("MAIN");
		
		MIPSCode.append(".text\n");
		MIPSCode.append(".globl main\n");
		MIPSCode.append("main:\n");
		
		//some stack work
		MIPSCode.append("move $fp, $sp\n");
		MIPSCode.append("subu $sp, $sp, ");
		MIPSCode.append(frameSize);
		MIPSCode.append("\n");
		MIPSCode.append("sw $ra, -4($fp)\n");
		
		data = node.jjtGetChild(0).jjtAccept(this,data);
		
		//some stack work
		MIPSCode.append("lw $ra, -4($fp)\n");
		MIPSCode.append("addu $sp, $sp, ");
		MIPSCode.append(frameSize);
		MIPSCode.append("\n");
		MIPSCode.append("j $ra\n");
		
		int i;
		for (i=1; i < node.jjtGetNumChildren(); i++)
		{
			data = node.jjtGetChild(i).jjtAccept(this,data);
		}
		
		//tiny procedure for hallocate
		MIPSCode.append(".text\n");
		MIPSCode.append(".globl _halloc\n");
		MIPSCode.append("_halloc:\n");
		MIPSCode.append("li $v0, 9\n");
		MIPSCode.append("syscall\n");
		MIPSCode.append("j $ra\n");
		
		//tiny procedure for print
		MIPSCode.append(".text\n");
		MIPSCode.append(".globl _print\n");
		MIPSCode.append("_print:\n");
		MIPSCode.append("li $v0, 1\n");
		MIPSCode.append("syscall\n");
		MIPSCode.append("la $a0, newl\n");
		MIPSCode.append("li $v0, 4\n");
		MIPSCode.append("syscall\n");
		MIPSCode.append("j $ra\n");
		
		//data for "\n"
		MIPSCode.append(".data\n");
		MIPSCode.append(".align 0\n");
		MIPSCode.append("newl:\n");
		MIPSCode.append(".asciiz \"\n\"\n");
		
		//data for ERROR
		MIPSCode.append(".data\n");
		MIPSCode.append(".align 0\n");
		MIPSCode.append("str_er:\n");
		MIPSCode.append(".asciiz \"Array Overflow!\"\n");
		
		return data;
	}

	public Object visit(ASTStmtList node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTProcedure node, Object data)
	{
		clearRegisterRecord();
		
		currentProcedureName = ((ASTMyLabel)(node.jjtGetChild(0))).name;
		int frameSize = GetProcedureFrameSize(currentProcedureName);
		
		localOffSetOnStack = 0;
	    temperaryOffSetOnStack = 0;
		
		MIPSCode.append(".text\n");
		MIPSCode.append(".globl " +  currentProcedureName + "\n");
		MIPSCode.append(currentProcedureName + ":\n");
		
		//some stack work
		MIPSCode.append("sw $fp, -8($sp)\n");
		MIPSCode.append("move $fp, $sp\n");
		MIPSCode.append("subu $sp, $sp, ");
		MIPSCode.append(frameSize);
		MIPSCode.append("\n");
		MIPSCode.append("sw $ra, -4($fp)\n");
		
		ProcedureNode info = new ProcedureNode();
    	int numberOfTemperaries;
    	int maxParameterNumber;
    	info = PI.ProcedureTable.get(currentProcedureName);
    	numberOfTemperaries = info.numberOfTemperaries;
    	maxParameterNumber = PI.maxParameterNumber;
    	int Register_s_offset;
    	Register_s_offset = 4*(maxParameterNumber+numberOfTemperaries+10);
		
      	int m;
    	for(m = 0; m <= 7; m++)
    	{
    		MIPSCode.append("sw $s");
    		MIPSCode.append(m);
    		MIPSCode.append(", ");
    		MIPSCode.append(Register_s_offset);
    		MIPSCode.append("($sp)\n");
    		Register_s_offset += 4;
    	}
		
		data = node.jjtGetChild(2).jjtAccept(this,data);
		
		//some stack work
		Register_s_offset = 4*(maxParameterNumber+numberOfTemperaries+10);
    	for(m = 0; m <= 7; m++)
    	{
    		MIPSCode.append("lw $s");
    		MIPSCode.append(m);
    		MIPSCode.append(", ");
    		MIPSCode.append(Register_s_offset);
    		MIPSCode.append("($sp)\n");
    		Register_s_offset += 4;
    	}
    	MIPSCode.append("lw $ra, -4($fp)\n");
		MIPSCode.append("lw $fp, -8($fp)\n");
		MIPSCode.append("addu $sp, $sp, ");
		MIPSCode.append(frameSize);
		MIPSCode.append("\n");
    	
		MIPSCode.append("j $ra\n");
		
		return data;
	}

	public Object visit(ASTStmt node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTNoOpStmt node, Object data)
	{
		MIPSCode.append("nop\n");
		return data;
	}

	public Object visit(ASTErrorStmt node, Object data)
	{
		MIPSCode.append("li $v0, 4\n");
		MIPSCode.append("la $a0, str_er\n");
		MIPSCode.append("syscall\n");
		MIPSCode.append("li $v0, 10\n");
		MIPSCode.append("syscall\n");
		return data;
	}

	public Object visit(ASTCJumpStmt node, Object data)
	{
		int temp = ((ASTMyInt)(node.jjtGetChild(0).jjtGetChild(0))).val;
		String label = ((ASTMyLabel)(node.jjtGetChild(1))).name;
		String reg = RegisterAllocate(temp);
		
		if(reg.equals("Fail"))
		{
			int off = SpillToStack(temp);
			MIPSCode.append("lw $v1, ");
			MIPSCode.append(off);
			MIPSCode.append("($sp)\n");
			reg = "v1";
		}
		
		MIPSCode.append("beqz $" + reg + ", " + label + "\n");

		//release reg
		VeriableLiveness[temp]--;
		
		return data;
	}

	public Object visit(ASTJumpStmt node, Object data)
	{
		String label = ((ASTMyLabel)(node.jjtGetChild(0))).name;
		MIPSCode.append("j " + label + "\n");
		return data;
	}

	public Object visit(ASTHStoreStmt node, Object data)
	{
		int temp1 = ((ASTMyInt)(node.jjtGetChild(0).jjtGetChild(0))).val;
		int temp2 = ((ASTMyInt)(node.jjtGetChild(2).jjtGetChild(0))).val;
		int offset = ((ASTMyInt)(node.jjtGetChild(1))).val;
		String reg1 = RegisterAllocate(temp1);
		String reg2 = RegisterAllocate(temp2);

		if(reg1.equals("Fail"))
		{
			int off1 = SpillToStack(temp1);
			MIPSCode.append("lw $v0, ");
			MIPSCode.append(off1);
			MIPSCode.append("($sp)\n");
			reg1 = "v0";
		}
		
		if(reg2.equals("Fail"))
		{
			int off2 = SpillToStack(temp2);
			MIPSCode.append("lw $v1, ");
			MIPSCode.append(off2);
			MIPSCode.append("($sp)\n");
			reg2 = "v1";
		}
		
		MIPSCode.append("sw $" + reg2 + ", ");
		MIPSCode.append(offset);
		MIPSCode.append("($" + reg1 + ")\n");
		
		//release 2 regs
		VeriableLiveness[temp1]--;
		VeriableLiveness[temp2]--;
		
		return data;
	}

	public Object visit(ASTHLoadStmt node, Object data)
	{
		int temp1 = ((ASTMyInt)(node.jjtGetChild(0).jjtGetChild(0))).val;
		int temp2 = ((ASTMyInt)(node.jjtGetChild(1).jjtGetChild(0))).val;
		int offset = ((ASTMyInt)(node.jjtGetChild(2))).val;
		String reg1 = RegisterAllocate(temp1);
		String reg2 = RegisterAllocate(temp2);
		boolean judge = false;
		
		if(reg1.equals("Fail"))
		{
			judge = true;
			reg1 = "v0";
		}
		
		if(reg2.equals("Fail"))
		{
			int off2 = SpillToStack(temp2);
			MIPSCode.append("lw $v1, ");
			MIPSCode.append(off2);
			MIPSCode.append("($sp)\n");
			reg2 = "v1";
		}
		
		MIPSCode.append("lw $" + reg1 + ", ");
		MIPSCode.append(offset);
		MIPSCode.append("($" + reg2 + ")\n");

		if(judge)
		{
			int off1 = SpillToStack(temp1);
			MIPSCode.append("sw $v0, ");
			MIPSCode.append(off1);
			MIPSCode.append("($sp)\n");
		}

		//release 2 regs
		VeriableLiveness[temp1]--;
		VeriableLiveness[temp2]--;
		
		return data;
	}

	public Object visit(ASTMoveStmt node, Object data)
	{
		//Must consider Label!
		int temp = ((ASTMyInt)(node.jjtGetChild(0).jjtGetChild(0))).val;
		String str = node.jjtGetChild(1).jjtGetChild(0).toString();
		if(str.equals("SimpleExp"))
		{
			if(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).toString().equals("MyInt"))
			{
				String reg = RegisterAllocate(temp);
				int val = ((ASTMyInt)(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0))).val;
				boolean judge = false;
				
				if(reg.equals("Fail"))
				{
					judge = true;
					reg = "v1";
				}

				MIPSCode.append("li $" + reg + ", ");
				MIPSCode.append(val);
				MIPSCode.append("\n");

				if(judge)
				{
					int off = SpillToStack(temp);
					MIPSCode.append("sw $v1, ");
					MIPSCode.append(off);
					MIPSCode.append("($sp)\n");
				}
				
				//release reg
				VeriableLiveness[temp]--;
				
			}
			
			else if(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).toString().equals("MyLabel"))
			{
				String reg = RegisterAllocate(temp);
				String label = ((ASTMyLabel)(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0))).name;
				boolean judge = false;
				
				if(reg.equals("Fail"))
				{
					judge = true;
					reg = "v1";
				}
				
				MIPSCode.append("la $" + reg + ", " + label + "\n");

				if(judge)
				{
					int off = SpillToStack(temp);
					MIPSCode.append("sw $v1, ");
					MIPSCode.append(off);
					MIPSCode.append("($sp)\n");
				}

				//release reg
				VeriableLiveness[temp]--;
			}
			
			else
			{
				int temp2 = ((ASTMyInt)(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0))).val;
				String reg1 = RegisterAllocate(temp);
				String reg2 = RegisterAllocate(temp2);
				boolean judge = false;
				
				if(reg1.equals("Fail"))
				{
					judge = true;
					reg1 = "v0";
				}
				
				if(reg2.equals("Fail"))
				{
					int off2 = SpillToStack(temp2);
					MIPSCode.append("lw $v1, ");
					MIPSCode.append(off2);
					MIPSCode.append("($sp)\n");
					reg2 = "v1";
				}
				
				MIPSCode.append("move $" + reg1 + ", $" + reg2 + "\n");

				if(judge)
				{
					int off = SpillToStack(temp);
					MIPSCode.append("sw $v0, ");
					MIPSCode.append(off);
					MIPSCode.append("($sp)\n");
				}
				
				//release 2 regs
				VeriableLiveness[temp]--;
				VeriableLiveness[temp2]--;
			}
		}
		
		else
		{
			node.jjtGetChild(1).jjtGetChild(0).jjtAccept(this,temp);
		}
		
		return data;
	}

	public Object visit(ASTPrintStmt node, Object data)
	{
		if(node.jjtGetChild(0).jjtGetChild(0).toString().equals("MyInt"))
		{
			int val = ((ASTMyInt)(node.jjtGetChild(0).jjtGetChild(0))).val;
			MIPSCode.append("li $a0, ");
			MIPSCode.append(val);
			MIPSCode.append("\n");
			MIPSCode.append("jal _print\n");
		}
		else
		{
			int temp = ((ASTMyInt)(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0))).val;
			String reg = RegisterAllocate(temp);
			
			if(reg.equals("Fail"))
			{
				int off = SpillToStack(temp);
				MIPSCode.append("lw $v1, ");
				MIPSCode.append(off);
				MIPSCode.append("($sp)\n");
				reg = "v1";
			}
			
			MIPSCode.append("move $a0, $" + reg + "\n");
			MIPSCode.append("jal _print\n");
			
			//release reg
			VeriableLiveness[temp]--;
		}
		
		return data;
	}

	public Object visit(ASTExp node, Object data)
	{
		return data;
	}

	public Object visit(ASTStmtExp node, Object data)
	{
		data = node.jjtGetChild(0).jjtAccept(this,data);
		if(node.jjtGetChild(1).jjtGetChild(0).toString().equals("MyInt"))
		{
			int val = ((ASTMyInt)(node.jjtGetChild(1).jjtGetChild(0))).val;
			MIPSCode.append("li $v0, ");
			MIPSCode.append(val);
			MIPSCode.append("\n");
		}
		else
		{
			int temp = ((ASTMyInt)(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0))).val;
			String reg = RegisterAllocate(temp);
			
			if(reg.equals("Fail"))
			{
				int off = SpillToStack(temp);
				MIPSCode.append("lw $v1, ");
				MIPSCode.append(off);
				MIPSCode.append("($sp)\n");
				reg = "v1";
			}

			MIPSCode.append("move $v0, $" + reg + "\n");

			//release reg
			VeriableLiveness[temp]--;
		}
		
		return data;
	}

	public Object visit(ASTCall node, Object temp0)
	{
		Object data = null;
		int temp_v0 = (Integer)temp0;
		//some stack work
		ProcedureNode info = new ProcedureNode();
    	int numberOfTemperaries;
    	int maxParameterNumber;
    	info = PI.ProcedureTable.get(currentProcedureName);
    	numberOfTemperaries = info.numberOfTemperaries;
    	maxParameterNumber = PI.maxParameterNumber;
    	int Register_t_offset;
    	Register_t_offset = 4*(maxParameterNumber+numberOfTemperaries);
    	int m;
    	for(m = 0; m <= 9; m++)
    	{
    		MIPSCode.append("sw $t");
    		MIPSCode.append(m);
    		MIPSCode.append(", ");
    		MIPSCode.append(Register_t_offset);
    		MIPSCode.append("($sp)\n");
    		Register_t_offset += 4;
    	}
    	
    	int Register_a_offset = 0;
    	String reg_a;
    	int temp_a;
    	int off_a;
    	for(m = 1; m < node.jjtGetNumChildren(); m++)
    	{
    		temp_a = ((ASTMyInt)(node.jjtGetChild(m).jjtGetChild(0))).val;
    		reg_a = RegisterAllocate(temp_a);
    		if(reg_a.equals("Fail"))
    		{
				off_a = SpillToStack(temp_a);
				MIPSCode.append("lw $v1, ");
				MIPSCode.append(off_a);
				MIPSCode.append("($sp)\n");
				reg_a = "v1";
    		}
    		MIPSCode.append("sw $" + reg_a + ", ");
    		MIPSCode.append(Register_a_offset);
    		MIPSCode.append("($sp)\n");
    		Register_a_offset += 4;
    	}
		
		if(node.jjtGetChild(0).jjtGetChild(0).toString().equals("MyInt"))
		{
			int val = ((ASTMyInt)(node.jjtGetChild(0).jjtGetChild(0))).val;
			MIPSCode.append("jal ");
			MIPSCode.append(val);
			MIPSCode.append("\n");
		}
		else
		{
			int temp_aim = ((ASTMyInt)(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0))).val;
			String reg_aim = RegisterAllocate(temp_aim);
			
			if(reg_aim.equals("Fail"))
			{
				int off_aim = SpillToStack(temp_aim);
				MIPSCode.append("lw $v1, ");
				MIPSCode.append(off_aim);
				MIPSCode.append("($sp)\n");
				reg_aim = "v1";
			}
			
			MIPSCode.append("jalr $" + reg_aim + "\n");
		}
		
		//some stack work
		//NOTE: mustn't use v0 here! because v0 is the return value!
    	Register_t_offset = 4*(maxParameterNumber+numberOfTemperaries);
    	for(m = 0; m <= 8; m++)
    	{
    		MIPSCode.append("lw $t");
    		MIPSCode.append(m);
    		MIPSCode.append(", ");
    		MIPSCode.append(Register_t_offset);
    		MIPSCode.append("($sp)\n");
    		Register_t_offset += 4;
    	}
		
		String reg_v0 = RegisterAllocate(temp_v0);
		boolean judge = false;
		
		if(reg_v0.equals("Fail"))
		{
			judge = true;
			reg_v0 = "v1";
		}

		MIPSCode.append("move $" + reg_v0 + ", $v0\n");

		if(judge)
		{
			int off_v0 = SpillToStack(temp_v0);
			MIPSCode.append("sw $v1, ");
			MIPSCode.append(off_v0);
			MIPSCode.append("($sp)\n");
		}
		
		//release many many regs here
		int numberOfRegsToRelease;
		int tempToRelease;
		numberOfRegsToRelease = node.jjtGetNumChildren();
		
		VeriableLiveness[temp_v0]--;		
		if(node.jjtGetChild(0).jjtGetChild(0).toString().equals("Temp"))
		{
			tempToRelease = ((ASTMyInt)(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0))).val;
			VeriableLiveness[tempToRelease]--;
		}
		int j;
		for (j = 1; j < numberOfRegsToRelease; j++)
		{
			tempToRelease = ((ASTMyInt)(node.jjtGetChild(j).jjtGetChild(0))).val;
			VeriableLiveness[tempToRelease]--;
		}
		
		return data;
	}

	public Object visit(ASTHAllocate node, Object temp0)
	{
		Object data = null;
		int temp = (Integer)temp0;
		if(node.jjtGetChild(0).jjtGetChild(0).toString().equals("MyInt"))
		{
			int val = ((ASTMyInt)(node.jjtGetChild(0).jjtGetChild(0))).val;
			String reg = RegisterAllocate(temp);
			boolean judge = false;
			
			if(reg.equals("Fail"))
			{
				judge = true;
				reg = "v1";
			}
			
			MIPSCode.append("li $a0, ");
			MIPSCode.append(val);
			MIPSCode.append("\n");
			MIPSCode.append("jal _halloc\n");
			MIPSCode.append("move $" + reg +", $v0\n");

			if(judge)
			{
				int off = SpillToStack(temp);
				MIPSCode.append("sw $v1, ");
				MIPSCode.append(off);
				MIPSCode.append("($sp)\n");
			}
			//release reg
			VeriableLiveness[temp]--;
		}
		
		else
		{
			int temp2 = ((ASTMyInt)(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0))).val;
			String reg1 = RegisterAllocate(temp);
			String reg2 = RegisterAllocate(temp2);
			boolean judge = false;
			
			if(reg1.equals("Fail"))
			{
				judge = true;
				reg1 = "v1";
			}
			
			if(reg2.equals("Fail"))
			{
				int off2 = SpillToStack(temp2);
				MIPSCode.append("lw $v0, ");
				MIPSCode.append(off2);
				MIPSCode.append("($sp)\n");
				reg2 = "v0";
			}
			
			MIPSCode.append("move $a0, $" + reg2 + "\n");
			MIPSCode.append("jal _halloc\n");
			MIPSCode.append("move $" + reg1 +", $v0\n");

			if(judge)
			{
				int off = SpillToStack(temp);
				MIPSCode.append("sw $v1, ");
				MIPSCode.append(off);
				MIPSCode.append("($sp)\n");
			}
			
			//release 2 regs
			VeriableLiveness[temp]--;
			VeriableLiveness[temp2]--;
		}
		
		return data;
	}

	public Object visit(ASTBinOp node, Object temp0)
	{
		Object data = null;
		int temp1 = (Integer)temp0;
		int temp2 = ((ASTMyInt)(node.jjtGetChild(1).jjtGetChild(0))).val;
		if(node.jjtGetChild(2).jjtGetChild(0).toString().equals("MyInt"))
		{
			String reg1 = RegisterAllocate(temp1);
			String reg2 = RegisterAllocate(temp2);
			int val = ((ASTMyInt)(node.jjtGetChild(2).jjtGetChild(0))).val;
			boolean judge = false;
			
			if(reg1.equals("Fail"))
			{
				judge = true;
				reg1 = "v0";
			}
			
			if(reg2.equals("Fail"))
			{
				int off2 = SpillToStack(temp2);
				MIPSCode.append("lw $v1, ");
				MIPSCode.append(off2);
				MIPSCode.append("($sp)\n");
				reg2 = "v1";
			}
			
			if(((ASTMyOp)(node.jjtGetChild(0))).name.equals("LT"))
			{
				MIPSCode.append("slt $" + reg1 + ", $" + reg2 + ", ");
				MIPSCode.append(val);
				MIPSCode.append("\n");
			}
			else if(((ASTMyOp)(node.jjtGetChild(0))).name.equals("PLUS"))
			{
				MIPSCode.append("add $" + reg1 + ", $" + reg2 + ", ");
				MIPSCode.append(val);
				MIPSCode.append("\n");
			}
			else if(((ASTMyOp)(node.jjtGetChild(0))).name.equals("MINUS"))
			{
				MIPSCode.append("sub $" + reg1 + ", $" + reg2 + ", ");
				MIPSCode.append(val);
				MIPSCode.append("\n");
			}
			else
			{
				MIPSCode.append("mul $" + reg1 + ", $" + reg2 + ", ");
				MIPSCode.append(val);
				MIPSCode.append("\n");
			}

			if(judge)
			{
				int off1 = SpillToStack(temp1);
				MIPSCode.append("sw $v0, ");
				MIPSCode.append(off1);
				MIPSCode.append("($sp)\n");
			}
			
			//release 2 regs
			VeriableLiveness[temp1]--;
			VeriableLiveness[temp2]--;
		}
		
		else
		{
			int temp3 = ((ASTMyInt)(node.jjtGetChild(2).jjtGetChild(0).jjtGetChild(0))).val;
			String reg1 = RegisterAllocate(temp1);
			String reg2 = RegisterAllocate(temp2);
			String reg3 = RegisterAllocate(temp3);
			boolean judge = false;
			
			if(reg1.equals("Fail"))
			{
				judge = true;
				reg1 = "v0";
			}
			
			if(reg2.equals("Fail"))
			{
				int off2 = SpillToStack(temp2);
				MIPSCode.append("lw $v0, ");
				MIPSCode.append(off2);
				MIPSCode.append("($sp)\n");
				reg2 = "v0";
			}
			
			if(reg3.equals("Fail"))
			{
				int off3 = SpillToStack(temp3);
				MIPSCode.append("lw $v1, ");
				MIPSCode.append(off3);
				MIPSCode.append("($sp)\n");
				reg3 = "v1";
			}
			
			if(((ASTMyOp)(node.jjtGetChild(0))).name.equals("LT"))
			{
				MIPSCode.append("slt $" + reg1 + ", $" + reg2 + ", $" + reg3 + "\n");
			}
			else if(((ASTMyOp)(node.jjtGetChild(0))).name.equals("PLUS"))
			{
				MIPSCode.append("add $" + reg1 + ", $" + reg2 + ", $" + reg3 + "\n");
			}
			else if(((ASTMyOp)(node.jjtGetChild(0))).name.equals("MINUS"))
			{
				MIPSCode.append("sub $" + reg1 + ", $" + reg2 + ", $" + reg3 + "\n");
			}
			else
			{
				MIPSCode.append("mul $" + reg1 + ", $" + reg2 + ", $" + reg3 + "\n");
			}

			if(judge)
			{
				int off1 = SpillToStack(temp1);
				MIPSCode.append("sw $v0, ");
				MIPSCode.append(off1);
				MIPSCode.append("($sp)\n");
			}
			
			//release 3 regs
			VeriableLiveness[temp1]--;
			VeriableLiveness[temp2]--;
			VeriableLiveness[temp3]--;
		}
		
		return data;
	}

	public Object visit(ASTMyOp node, Object data)
	{
		return data;
	}

	public Object visit(ASTSimpleExp node, Object data)
	{
		return data;
	}

	public Object visit(ASTTemp node, Object data)
	{
		return data;
	}

	public Object visit(ASTMyInt node, Object data)
	{
		return data;
	}

	public Object visit(ASTMyLabel node, Object data)
	{
		String label = node.name;
		MIPSCode.append(label + ":\n");
		return data;
	}
}