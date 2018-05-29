import java.util.Hashtable;
import java.util.Vector;
import java.io.*;

public class MiniJavaToPiglet implements MiniJavaVisitor
{
	private StringBuffer PigletCode;
	private StringBuffer PigletProcedureInfo;
	private int localTemp;
	private int labelNumber;
	TypeTable tb;
	
	MiniJavaToPiglet(TypeTable t) throws IOException
	{
		tb = t;
		localTemp = 20;
		labelNumber = 1;
		PigletCode = new StringBuffer();
		PigletProcedureInfo = new StringBuffer();
	}
	
	public void showPigletCode()
	{
		System.out.print(PigletCode);
	}
	
	public void CreatePigletCode(String outputFileName) throws IOException
	{
		FileWriter output = new FileWriter(outputFileName);
		String result = PigletCode.toString();
		output.write(result);
		output.close();	
	}
	
	public void CreateTempNumber(String tempNumber) throws IOException
	{
		FileOutputStream file0=new FileOutputStream(tempNumber);
	    DataOutputStream outfile0=new DataOutputStream(file0);
	    outfile0.writeInt(localTemp);
	    outfile0.close();
	    file0.close();
	}
	
	public void CreatePigletProcedureInfo(String procedureInfoOutput) throws IOException
	{
		FileWriter output = new FileWriter(procedureInfoOutput);
		String result = PigletProcedureInfo.toString();
		output.write(result);
		output.close();	
	}
	
	public Object visit(SimpleNode node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTGoal node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTMainClass node, Object data)
	{
		String ClassID = ((ASTMyID)(node.jjtGetChild(0))).name;
		tb.CurrentClass = ClassID;
		tb.CurrentMethod = "main";
		tb.CurrentPlace = 2;
		
		PigletCode.append("MAIN \n");
		data = node.childrenAccept(this, data);
		PigletCode.append(" END \n");
		return data;
	}

	public Object visit(ASTTypeDeclaration node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTClassDeclaration node, Object data)
	{
		String ClassID = ((ASTMyID)(node.jjtGetChild(0))).name;
		tb.CurrentClass = ClassID;
		tb.CurrentPlace = 1;
		
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTClassExtendsDeclaration node, Object data)
	{
		String ClassID = ((ASTMyID)(node.jjtGetChild(0))).name;
		tb.CurrentClass = ClassID;
		tb.CurrentPlace = 1;

		
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTVarDeclaration node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}
    
	public Object visit(ASTMethodDeclaration node, Object data)
	{
		String ID = ((ASTMyID)(node.jjtGetChild(1))).name;
		tb.CurrentMethod = ID;
		tb.CurrentPlace = 2;
		
		int numberOfParameters = 0;
		numberOfParameters = ((tb.ClassTable.get(tb.CurrentClass)).MethodTable.get(tb.CurrentMethod)).ParamNum;
		numberOfParameters++;
		PigletCode.append(tb.CurrentClass);
		PigletCode.append("_");
		PigletCode.append(tb.CurrentMethod);
		PigletCode.append(" [");
		PigletCode.append(numberOfParameters);
		PigletCode.append("] \n BEGIN \n");
		
		PigletProcedureInfo.append(tb.CurrentClass+"_"+tb.CurrentMethod+" ");
					
		int startTempForMethod;
		int numberOfLocal;
		startTempForMethod = localTemp;
		((tb.ClassTable.get(tb.CurrentClass)).MethodTable.get(tb.CurrentMethod)).StartTemp = startTempForMethod;
		numberOfLocal = ((tb.ClassTable.get(tb.CurrentClass)).MethodTable.get(tb.CurrentMethod)).Local.size();
		localTemp = localTemp + numberOfLocal;
		
		PigletProcedureInfo.append(startTempForMethod+" "+numberOfLocal+"\n");
		
		int returnLocation;
		returnLocation = node.jjtGetNumChildren();
        
		int i;
		for (i = 0; i < returnLocation-1; i++)
		{
			data = node.jjtGetChild(i).jjtAccept(this,data);
		}

		PigletCode.append(" RETURN ");
		data = node.jjtGetChild(i).jjtAccept(this,data);
		PigletCode.append(" END \n");		
		return data;
	}

	public Object visit(ASTFormalParameterList node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTFormalParameter node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTFormalParameterRest node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTType node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTArrayType node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTBooleanType node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTIntegerType node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTStatement node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTBlock node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTAssignmentStatement node, Object data)
	{
	
		String identifierID = (String)(node.jjtGetChild(0).jjtAccept(this,data));
		
		if ( ((tb.ClassTable.get(tb.CurrentClass)).MethodTable.get(tb.CurrentMethod)).LocalTable.containsKey(identifierID) )
		{
			int localStart;
			int localOffset = 0;
			int identifierTemp;
			int i, numberOfLocal;
			numberOfLocal = ((tb.ClassTable.get(tb.CurrentClass)).MethodTable.get(tb.CurrentMethod)).Local.size();
			
			for (i = 0; i < numberOfLocal; i++)
			{
				if (identifierID.equals( ((tb.ClassTable.get(tb.CurrentClass)).MethodTable.get(tb.CurrentMethod)).Local.elementAt(i)))
				{
					localOffset = i;
					break;
				}
			}
			localStart = ((tb.ClassTable.get(tb.CurrentClass)).MethodTable.get(tb.CurrentMethod)).StartTemp;
			identifierTemp = localStart + localOffset;
			PigletCode.append(" MOVE TEMP "+identifierTemp+" ");
			data = node.jjtGetChild(1).jjtAccept(this,data);
			return data;
		}
		else
		{
			if ( ((tb.ClassTable.get(tb.CurrentClass)).MethodTable.get(tb.CurrentMethod)).ParamTable.containsKey(identifierID) )
			{
				int paramOffset = 0;
				int numberOfParam;
				int paramTemp;
				int i;
				numberOfParam = ((tb.ClassTable.get(tb.CurrentClass)).MethodTable.get(tb.CurrentMethod)).Param.size();
				for (i = 0; i < numberOfParam; i++)
				{
					if (identifierID.equals(((tb.ClassTable.get(tb.CurrentClass)).MethodTable.get(tb.CurrentMethod)).Param.elementAt(i)))
					{
						paramOffset = i;
						break;
					}
				}
				paramTemp = paramOffset+1;
				PigletCode.append(" MOVE TEMP "+paramTemp+" ");
				data = node.jjtGetChild(1).jjtAccept(this,data);
				return data;
			}
			else
			{
				if ( (tb.ClassTable.get(tb.CurrentClass)).Field.contains(identifierID))
				{
					int fieldOffset = 0;
					int numberOfField;
					int i;
					numberOfField = (tb.ClassTable.get(tb.CurrentClass)).Field.size();
					for (i = numberOfField-1; i >= 0; i--)
					{
						if ( identifierID.equals( tb.ClassTable.get(tb.CurrentClass).Field.elementAt(i)) )
						{
							fieldOffset = i+1;
							break;
						}
					}

					fieldOffset = fieldOffset*4;
					PigletCode.append(" HSTORE TEMP "+0+" "+fieldOffset+" ");
					data = node.jjtGetChild(1).jjtAccept(this,data);
					return data;
				}
			}
		}
		
		return data;
	}

	public Object visit(ASTArrayAssignmentStatement node, Object data)
	{
		String identifierID = (String)(node.jjtGetChild(0).jjtAccept(this,data));
		if ( ((tb.ClassTable.get(tb.CurrentClass)).MethodTable.get(tb.CurrentMethod)).LocalTable.containsKey(identifierID) )
		{
			int localStart;
			int localOffset = 0;
			int identifierTemp;
			int i, numberOfLocal;
			numberOfLocal = ((tb.ClassTable.get(tb.CurrentClass)).MethodTable.get(tb.CurrentMethod)).Local.size();
			for (i = 0; i < numberOfLocal; i++)
			{
				if (identifierID.equals( ((tb.ClassTable.get(tb.CurrentClass)).MethodTable.get(tb.CurrentMethod)).Local.elementAt(i)))
				{
					localOffset = i;
					break;
				}
			}
			localStart = ((tb.ClassTable.get(tb.CurrentClass)).MethodTable.get(tb.CurrentMethod)).StartTemp;
			identifierTemp = localStart + localOffset;
			
			int tempForArraylength = localTemp;
			localTemp++;
			int labelForJumpError1 = labelNumber;
			labelNumber++;
			int labelForJumpError2 = labelNumber;
			labelNumber++;
			PigletCode.append(" HLOAD TEMP "+tempForArraylength+" TEMP "+identifierTemp+" 0 \n");		
			PigletCode.append(" CJUMP LT MINUS TEMP "+tempForArraylength+" 1 ");
			data = node.jjtGetChild(1).jjtAccept(this,data);
			PigletCode.append(" L"+labelForJumpError1+" \n");
			PigletCode.append(" ERROR \n");
			PigletCode.append(" L"+labelForJumpError1+" \n");
			
			PigletCode.append(" CJUMP LT ");
			data = node.jjtGetChild(1).jjtAccept(this,data);
			PigletCode.append(" 0 ");
			PigletCode.append(" L"+labelForJumpError2+" \n");
			PigletCode.append(" ERROR \n");
			PigletCode.append(" L"+labelForJumpError2+" \n");
			
			PigletCode.append(" HSTORE PLUS TEMP "+identifierTemp+" TIMES 4 ");
			data = node.jjtGetChild(1).jjtAccept(this,data);
			PigletCode.append(" 4 ");
			data = node.jjtGetChild(2).jjtAccept(this,data);
			return data;
		}
		else
		{
			if ( ((tb.ClassTable.get(tb.CurrentClass)).MethodTable.get(tb.CurrentMethod)).ParamTable.containsKey(identifierID) )
			{
				int paramOffset = 0;
				int numberOfParam;
				int paramTemp;
				int i;
				numberOfParam = ((tb.ClassTable.get(tb.CurrentClass)).MethodTable.get(tb.CurrentMethod)).Param.size();
				for (i = 0; i < numberOfParam; i++)
				{
					if (identifierID.equals( ((tb.ClassTable.get(tb.CurrentClass)).MethodTable.get(tb.CurrentMethod)).Param.elementAt(i)))
					{
						paramOffset = i;
						break;
					}
				}
				paramTemp = paramOffset+1;
				
				int tempForArraylength = localTemp;
				localTemp++;
				int labelForJumpError1 = labelNumber;
				labelNumber++;
				int labelForJumpError2 = labelNumber;
				labelNumber++;
				PigletCode.append(" HLOAD TEMP "+tempForArraylength+" TEMP "+paramTemp+" 0 \n");		
				PigletCode.append(" CJUMP LT MINUS TEMP "+tempForArraylength+" 1 ");
				data = node.jjtGetChild(1).jjtAccept(this,data);
				PigletCode.append(" L"+labelForJumpError1+" \n");
				PigletCode.append(" ERROR \n");
				PigletCode.append(" L"+labelForJumpError1+" \n");
				
				PigletCode.append(" CJUMP LT ");
				data = node.jjtGetChild(1).jjtAccept(this,data);
				PigletCode.append(" 0 ");
				PigletCode.append(" L"+labelForJumpError2+" \n");
				PigletCode.append(" ERROR \n");
				PigletCode.append(" L"+labelForJumpError2+" \n");
				
				PigletCode.append(" HSTORE PLUS TEMP "+paramTemp+" TIMES 4 ");
				data = node.jjtGetChild(1).jjtAccept(this,data);
				PigletCode.append(" 4 ");
				data = node.jjtGetChild(2).jjtAccept(this,data);
				return data;
			}
			else
			{
				if ( (tb.ClassTable.get(tb.CurrentClass)).Field.contains(identifierID) )
				{
					int fieldOffset = 0;
					int numberOfField;
					int i;
					numberOfField = (tb.ClassTable.get(tb.CurrentClass)).Field.size();
					for (i = numberOfField-1; i >= 0; i--)
					{
						if ( identifierID.equals( tb.ClassTable.get(tb.CurrentClass).Field.elementAt(i) ))
						{
							fieldOffset = i+1;
							break;
						}
					}
					fieldOffset = fieldOffset*4;
					int tempForArrayPointer = localTemp;
					localTemp++;
					PigletCode.append(" HLOAD TEMP "+tempForArrayPointer+" TEMP 0 "+fieldOffset+" \n");
					
					int tempForArraylength = localTemp;
					localTemp++;
					int labelForJumpError1 = labelNumber;
					labelNumber++;
					int labelForJumpError2 = labelNumber;
					labelNumber++;
					PigletCode.append(" HLOAD TEMP "+tempForArraylength+" TEMP "+tempForArrayPointer+" 0 \n");		
					PigletCode.append(" CJUMP LT MINUS TEMP "+tempForArraylength+" 1 ");
					data = node.jjtGetChild(1).jjtAccept(this,data);
					PigletCode.append(" L"+labelForJumpError1+" \n");
					PigletCode.append(" ERROR \n");
					PigletCode.append(" L"+labelForJumpError1+" \n");
					
					PigletCode.append(" CJUMP LT ");
					data = node.jjtGetChild(1).jjtAccept(this,data);
					PigletCode.append(" 0 ");
					PigletCode.append(" L"+labelForJumpError2+" \n");
					PigletCode.append(" ERROR \n");
					PigletCode.append(" L"+labelForJumpError2+" \n");
					
					PigletCode.append(" HSTORE PLUS TEMP "+tempForArrayPointer+" TIMES 4 ");
					data = node.jjtGetChild(1).jjtAccept(this,data);
					PigletCode.append(" 4 ");
					data = node.jjtGetChild(2).jjtAccept(this,data);
					return data;
				}
			}
		}
		
		return data;
	}

	public Object visit(ASTIfStatement node, Object data)
	{
		int tempLabel1, tempLabel2;
		tempLabel1 = labelNumber;
		labelNumber++;
		tempLabel2 = labelNumber;
		labelNumber++;
		PigletCode.append(" CJUMP ");
		data = node.jjtGetChild(0).jjtAccept(this,data);
		PigletCode.append(" L");
		PigletCode.append(tempLabel1);
		PigletCode.append("\n");
		data = node.jjtGetChild(1).jjtAccept(this,data);
		PigletCode.append(" JUMP L");
		PigletCode.append(tempLabel2);
		PigletCode.append("\n");
		PigletCode.append(" L");
		PigletCode.append(tempLabel1);
		PigletCode.append(" ");
		data = node.jjtGetChild(2).jjtAccept(this,data);
		PigletCode.append(" L");
		PigletCode.append(tempLabel2);
		PigletCode.append(" NOOP \n");
						
		return data;
	}

	public Object visit(ASTWhileStatement node, Object data)
	{
		
		int tempLabel1, tempLabel2;
		tempLabel1 = labelNumber;
		labelNumber++;
		tempLabel2 = labelNumber;
		labelNumber++;
		
		PigletCode.append(" L");
		PigletCode.append(tempLabel1);
		PigletCode.append(" CJUMP ");
		data = node.jjtGetChild(0).jjtAccept(this,data);
		PigletCode.append(" L");
		PigletCode.append(tempLabel2);
		PigletCode.append("\n");
		data = node.jjtGetChild(1).jjtAccept(this,data);
		PigletCode.append(" JUMP L");
		PigletCode.append(tempLabel1);
		PigletCode.append("\n");
		PigletCode.append(" L");
		PigletCode.append(tempLabel2);
		PigletCode.append(" NOOP \n");
				
		return data;
	}

	public Object visit(ASTPrintStatement node, Object data)
	{
		PigletCode.append(" PRINT \n");
		data = node.childrenAccept(this, data);
		return data;
	}


	//**************************************************************************************


	public Object visit(ASTExpression node, Object data)
	{
		data = node.childrenAccept(this, data); 
		return data;
	}

	public Object visit(ASTAndExpression node, Object data)
	{
		PigletCode.append(" TIMES");
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTCompareExpression node, Object data)
	{
		PigletCode.append(" LT");
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTPlusExpression node, Object data)
	{
		PigletCode.append(" PLUS");
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTMinusExpression node, Object data)
	{
		PigletCode.append(" MINUS");
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTTimesExpression node, Object data)
	{
		PigletCode.append(" TIMES");
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTArrayLookup node, Object data)
	{
		PigletCode.append(" BEGIN \n MOVE TEMP ");
		PigletCode.append(localTemp);
		int temp1 = localTemp;
		localTemp ++;
		node.jjtGetChild(0).jjtAccept(this,data);
		PigletCode.append("\n MOVE TEMP ");
		PigletCode.append(localTemp);
		int temp2 = localTemp;
		localTemp ++;
		node.jjtGetChild(1).jjtAccept(this,data);
		PigletCode.append("\n HLOAD TEMP ");
		PigletCode.append(localTemp);
		int temp3 = localTemp;
		localTemp ++;
		PigletCode.append(" TEMP ");
		PigletCode.append(temp1);
		PigletCode.append(" 0 \n CJUMP MINUS 1 LT TEMP ");
		PigletCode.append(temp2);
		PigletCode.append(" 0 L");
		PigletCode.append(labelNumber);
		int label2 = labelNumber;
		labelNumber ++;
		PigletCode.append("\n CJUMP LT MINUS TEMP ");
		PigletCode.append(temp3);
		PigletCode.append(" 1 TEMP ");
		PigletCode.append(temp2);
		PigletCode.append(" L");
		PigletCode.append(labelNumber);
		int label1 = labelNumber;
		labelNumber ++;
		PigletCode.append("\n L");
		PigletCode.append(label2);
		PigletCode.append(" ERROR \n L");
		PigletCode.append(label1);
		PigletCode.append("\n HLOAD TEMP ");
		PigletCode.append(localTemp);
		int temp4 = localTemp;
		localTemp ++;
		PigletCode.append(" PLUS TEMP ");
		PigletCode.append(temp1);
		PigletCode.append(" TIMES 4 PLUS TEMP ");
		PigletCode.append(temp2);
		PigletCode.append(" 1 0 \n RETURN TEMP ");
		PigletCode.append(temp4);
		PigletCode.append("\n END \n");

		return data;
	}

	public Object visit(ASTArrayLength node, Object data)
	{
		PigletCode.append(" BEGIN \n HLOAD TEMP ");
		PigletCode.append(localTemp);
		int temp = localTemp;
		localTemp ++;
		node.jjtGetChild(0).jjtAccept(this,data);
		PigletCode.append(" 0 \n RETURN TEMP ");
		PigletCode.append(temp);
		PigletCode.append("\n END \n");

		return data;
	}

	public Object visit(ASTMessageSend node, Object data)
	{
		MyCheckTypeVisitor tv = new MyCheckTypeVisitor(tb);
		String Type = (String)(node.jjtGetChild(0).jjtAccept(tv,data));
		String Func = (String)(node.jjtGetChild(1).jjtAccept(this,data));
		int offset = (tb.ClassTable.get(Type)).Method.indexOf(Func);
		PigletCode.append("\n CALL \n BEGIN \n MOVE TEMP ");
		PigletCode.append(localTemp);
		int temp1 = localTemp;
		localTemp ++;
		node.jjtGetChild(0).jjtAccept(this,data);
		PigletCode.append("\n HLOAD TEMP ");
		PigletCode.append(localTemp);
		int temp2 = localTemp;
		localTemp ++;
		PigletCode.append(" TEMP ");
		PigletCode.append(temp1);
		PigletCode.append(" 0 \n HLOAD TEMP ");
		PigletCode.append(localTemp);
		int temp3 = localTemp;
		localTemp ++;
		PigletCode.append(" TEMP ");
		PigletCode.append(temp2);
		PigletCode.append(" ");
		PigletCode.append(offset * 4);
		PigletCode.append("\n RETURN TEMP ");
		PigletCode.append(temp3);
		PigletCode.append(" END ( TEMP ");
		PigletCode.append(temp1);
		if(node.jjtGetNumChildren() == 3)
		{
			node.jjtGetChild(2).jjtAccept(this,data);
		}
		PigletCode.append(" )\n");
		
		return data;
	}

	public Object visit(ASTExpressionList node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTExpressionRest node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTPrimaryExpression node, Object data)
	{
		if(node.jjtGetChild(0).toString().equals("MyID"))
		{
			String IDName = (String)(node.jjtGetChild(0).jjtAccept(this,data));
			if(((tb.ClassTable.get(tb.CurrentClass)).MethodTable.get(tb.CurrentMethod)).LocalTable.containsKey(IDName))
		    {
				int tempNum = ((tb.ClassTable.get(tb.CurrentClass)).MethodTable.get(tb.CurrentMethod)).StartTemp;
				tempNum += ((tb.ClassTable.get(tb.CurrentClass)).MethodTable.get(tb.CurrentMethod)).Local.indexOf(IDName);
				PigletCode.append(" TEMP ");
				PigletCode.append(tempNum);
		    }
			
			else if(((tb.ClassTable.get(tb.CurrentClass)).MethodTable.get(tb.CurrentMethod)).ParamTable.containsKey(IDName))
		    {
		    	int paramNum = ((tb.ClassTable.get(tb.CurrentClass)).MethodTable.get(tb.CurrentMethod)).Param.indexOf(IDName) + 1;
		    	PigletCode.append(" TEMP ");
				PigletCode.append(paramNum);
		    }
			
			else
			{
				int size = (tb.ClassTable.get(tb.CurrentClass)).Field.size();
				int i;
				for(i=size-1;i>=0;i--)
				{
					if(((tb.ClassTable.get(tb.CurrentClass)).Field.elementAt(i)).equals(IDName))
						break;
				}
				i=(i+1)*4;
		    	PigletCode.append("\n BEGIN \n HLOAD TEMP ");
				PigletCode.append(localTemp);
				int temp = localTemp;
				localTemp ++;
				PigletCode.append(" TEMP 0 ");
				PigletCode.append(i);
				PigletCode.append("\n RETURN TEMP ");
				PigletCode.append(temp);
				PigletCode.append("\n END \n");
			}
		}
		
		else
		{
			data = node.childrenAccept(this, data);
		}

		return data;
	}

	public Object visit(ASTTrueLiteral node, Object data)
	{
		PigletCode.append(" 1");
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTFalseLiteral node, Object data)
	{
		PigletCode.append(" 0");
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTThisExpression node, Object data)
	{
		PigletCode.append(" TEMP 0");
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTArrayAllocationExpression node, Object data)
	{
		PigletCode.append(" BEGIN \n MOVE TEMP ");
		PigletCode.append(localTemp);
		int temp1 = localTemp;
		localTemp ++;
		PigletCode.append(" HALLOCATE TIMES 4 PLUS 1 \n BEGIN \n MOVE TEMP ");
		PigletCode.append(localTemp);
		int temp2 = localTemp;
		localTemp ++;
		node.jjtGetChild(0).jjtAccept(this,data);
		PigletCode.append("\n RETURN TEMP ");
		PigletCode.append(temp2);
		PigletCode.append("\n END \n CJUMP LT TEMP ");
		PigletCode.append(temp2);
		PigletCode.append(" 0 L");
		PigletCode.append(labelNumber);
		int label1 = labelNumber;
		labelNumber ++;
		PigletCode.append("\n ERROR \n L");
		PigletCode.append(label1);
		PigletCode.append("\n HSTORE TEMP ");
		PigletCode.append(temp1);
		PigletCode.append(" 0 TEMP ");
		PigletCode.append(temp2);
		PigletCode.append("\n RETURN TEMP ");
		PigletCode.append(temp1);
		PigletCode.append("\n END \n");

		return data;
	}

	public Object visit(ASTAllocationExpression node, Object data)
	{
		String ClassID = ((ASTMyID)(node.jjtGetChild(0))).name;
		int size1 = ((tb.ClassTable.get(ClassID)).Field.size() + 1) * 4;
		int size2 = ((tb.ClassTable.get(ClassID)).Method.size()) * 4;
		PigletCode.append(" BEGIN \n MOVE TEMP ");
		PigletCode.append(localTemp);
		int temp1 = localTemp;
		localTemp ++;
		PigletCode.append(" HALLOCATE ");
		PigletCode.append(size1);
		PigletCode.append("\n MOVE TEMP ");
		PigletCode.append(localTemp);
		int temp2 = localTemp;
		localTemp ++;
		PigletCode.append(" HALLOCATE ");
		PigletCode.append(size2);
		PigletCode.append("\n");
		int i;
		String Extends;
		String Func;
		for(i=0; i<size2; i+=4)
		{
			PigletCode.append(" HSTORE TEMP ");
			PigletCode.append(temp2);
			PigletCode.append(" ");
			PigletCode.append(i);
			Extends = ClassID;
			Func = (tb.ClassTable.get(ClassID)).Method.elementAt(i/4);
			while(!(tb.ClassTable.get(Extends)).MethodTable.containsKey(Func))
			{
				Extends = (tb.ClassTable.get(Extends)).Extends;
			}
			PigletCode.append(" ");
			PigletCode.append(Extends);
			PigletCode.append("_");
			PigletCode.append(Func);
			PigletCode.append("\n");
		}
		
		for(i=4;i<size1;i+=4)
		{
			PigletCode.append(" HSTORE TEMP ");
			PigletCode.append(temp1);
			PigletCode.append(" ");
			PigletCode.append(i);
			PigletCode.append(" 0 \n");
		}
		
		PigletCode.append(" HSTORE TEMP ");
		PigletCode.append(temp1);
		PigletCode.append(" 0 TEMP ");
		PigletCode.append(temp2);
		PigletCode.append("\n RETURN TEMP ");
		PigletCode.append(temp1);
		PigletCode.append("\n END \n");

		return data;
	}

	public Object visit(ASTNotExpression node, Object data)
	{
		PigletCode.append(" MINUS 1");
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTBracketExpression node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTMyInt node, Object data)
	{
		int val = ((ASTMyInt)node).val;
		PigletCode.append(" ");
		PigletCode.append(val);
		return data;
	}

	public Object visit(ASTMyID node, Object data)
	{
		return ((ASTMyID)node).name;  
	}
		
}