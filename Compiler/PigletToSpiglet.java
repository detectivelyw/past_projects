import java.io.FileWriter;
import java.io.IOException;

public class PigletToSpiglet implements PigletParserVisitor
{
	StringBuffer SpigletCode;
	int localTemp;

    PigletToSpiglet(int temp)
	{
		localTemp = temp;
		SpigletCode = new StringBuffer();
	}
	
	public void CreateSpigletCode(String outputFileName) throws IOException
	{
		FileWriter output = new FileWriter(outputFileName);
		String result = SpigletCode.toString();
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
		SpigletCode.append("MAIN\n");
		data = node.jjtGetChild(0).jjtAccept(this,data);
		SpigletCode.append("END\n");
		int i;
		for (i=1; i < node.jjtGetNumChildren(); i++)
		{
			data = node.jjtGetChild(i).jjtAccept(this,data);
		}
		return data;
	}

	public Object visit(ASTStmtList node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTProcedure node, Object data)
	{
		data = node.jjtGetChild(0).jjtAccept(this,data);
		SpigletCode.append("[ ");
		data = node.jjtGetChild(1).jjtAccept(this,data);
		SpigletCode.append(" ]\n");
		SpigletCode.append("BEGIN\n");
		data = node.jjtGetChild(2).jjtGetChild(0).jjtAccept(this,data);
		if(node.jjtGetChild(2).jjtGetChild(1).jjtGetChild(0).toString().equals("Temp")
			|| node.jjtGetChild(2).jjtGetChild(1).jjtGetChild(0).toString().equals("MyInt"))
		{
			SpigletCode.append("RETURN ");
			data = node.jjtGetChild(2).jjtGetChild(1).jjtGetChild(0).jjtAccept(this,data);
			SpigletCode.append("\nEND\n");
		}
		else
		{
			int temp = localTemp;
			localTemp ++;
			data = node.jjtGetChild(2).jjtGetChild(1).jjtAccept(this,temp);
			SpigletCode.append("RETURN TEMP ");
			SpigletCode.append(temp);
			SpigletCode.append("\nEND\n");
		}
		return data;
	}

	public Object visit(ASTStmt node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTNoOpStmt node, Object data)
	{
		SpigletCode.append("NOOP\n");
		return data;
	}

	public Object visit(ASTErrorStmt node, Object data)
	{
		SpigletCode.append("ERROR\n");
		return data;
	}

	public Object visit(ASTCJumpStmt node, Object data)
	{
		if(node.jjtGetChild(0).jjtGetChild(0).toString().equals("Temp"))
		{
			SpigletCode.append("CJUMP ");
			data = node.jjtGetChild(0).jjtGetChild(0).jjtAccept(this,data);
			SpigletCode.append(" ");
			data = node.jjtGetChild(1).jjtAccept(this,data);
			SpigletCode.append("\n");
		}
		else
		{
			int temp = localTemp;
			localTemp ++;
			data = node.jjtGetChild(0).jjtAccept(this,temp);
			SpigletCode.append("CJUMP TEMP ");
			SpigletCode.append(temp);
			SpigletCode.append(" ");
			data = node.jjtGetChild(1).jjtAccept(this,data);
			SpigletCode.append("\n");
		}
		return data;
	}

	public Object visit(ASTJumpStmt node, Object data)
	{
		SpigletCode.append("JUMP ");
		data = node.childrenAccept(this, data);
		SpigletCode.append("\n");
		return data;
	}

	public Object visit(ASTHStoreStmt node, Object data)
	{
		if(node.jjtGetChild(0).jjtGetChild(0).toString().equals("Temp")
			&& node.jjtGetChild(2).jjtGetChild(0).toString().equals("Temp"))
		{
			SpigletCode.append("HSTORE ");
			data = node.jjtGetChild(0).jjtGetChild(0).jjtAccept(this,data);
			SpigletCode.append(" ");
			data = node.jjtGetChild(1).jjtAccept(this,data);
			SpigletCode.append(" ");
			data = node.jjtGetChild(2).jjtGetChild(0).jjtAccept(this,data);
			SpigletCode.append("\n");
		}
		else if(node.jjtGetChild(0).jjtGetChild(0).toString().equals("Temp")
			&& !(node.jjtGetChild(2).jjtGetChild(0).toString().equals("Temp")))
		{
			int temp2 = localTemp;
			localTemp ++;
			data = node.jjtGetChild(2).jjtAccept(this,temp2);
			SpigletCode.append("HSTORE ");
			data = node.jjtGetChild(0).jjtGetChild(0).jjtAccept(this,data);
			SpigletCode.append(" ");
			data = node.jjtGetChild(1).jjtAccept(this,data);
			SpigletCode.append(" TEMP ");
			SpigletCode.append(temp2);
			SpigletCode.append("\n");
		}
		else if(!(node.jjtGetChild(0).jjtGetChild(0).toString().equals("Temp"))
			&& node.jjtGetChild(2).jjtGetChild(0).toString().equals("Temp"))
		{
			int temp1 = localTemp;
			localTemp ++;
			data = node.jjtGetChild(0).jjtAccept(this,temp1);
			SpigletCode.append("HSTORE TEMP ");
			SpigletCode.append(temp1);
			SpigletCode.append(" ");
			data = node.jjtGetChild(1).jjtAccept(this,data);
			SpigletCode.append(" ");
			data = node.jjtGetChild(2).jjtGetChild(0).jjtAccept(this,data);
			SpigletCode.append("\n");
		}
		else
		{
			int temp1 = localTemp;
			localTemp ++;
			int temp2 = localTemp;
			localTemp ++;
			data = node.jjtGetChild(0).jjtAccept(this,temp1);
			data = node.jjtGetChild(2).jjtAccept(this,temp2);
			SpigletCode.append("HSTORE TEMP ");
			SpigletCode.append(temp1);
			SpigletCode.append(" ");
			data = node.jjtGetChild(1).jjtAccept(this,data);
			SpigletCode.append(" TEMP ");
			SpigletCode.append(temp2);
			SpigletCode.append("\n");
		}
		return data;
	}

	public Object visit(ASTHLoadStmt node, Object data)
	{
		if(node.jjtGetChild(1).jjtGetChild(0).toString().equals("Temp"))
		{
			SpigletCode.append("HLOAD ");
			data = node.jjtGetChild(0).jjtAccept(this,data);
			SpigletCode.append(" ");
			data = node.jjtGetChild(1).jjtGetChild(0).jjtAccept(this,data);
			SpigletCode.append(" ");
			data = node.jjtGetChild(2).jjtAccept(this,data);
			SpigletCode.append("\n");
		}
		else
		{
			int temp = localTemp;
			localTemp ++;
			data = node.jjtGetChild(1).jjtAccept(this,temp);
			SpigletCode.append("HLOAD ");
			data = node.jjtGetChild(0).jjtAccept(this,data);
			SpigletCode.append(" TEMP ");
			SpigletCode.append(temp);
			SpigletCode.append(" ");
			data = node.jjtGetChild(2).jjtAccept(this,data);
			SpigletCode.append("\n");
		}
		return data;
	}

	public Object visit(ASTMoveStmt node, Object data)
	{
		if(node.jjtGetChild(1).jjtGetChild(0).toString().equals("Temp"))
		{
			SpigletCode.append("MOVE ");
			data = node.jjtGetChild(0).jjtAccept(this,data);
			SpigletCode.append(" ");
			data = node.jjtGetChild(1).jjtGetChild(0).jjtAccept(this,data);
			SpigletCode.append("\n");
		}
		else
		{
			int temp = localTemp;
			localTemp ++;
			data = node.jjtGetChild(1).jjtAccept(this,temp);
			SpigletCode.append("MOVE ");
			data = node.jjtGetChild(0).jjtAccept(this,data);
			SpigletCode.append(" TEMP ");
			SpigletCode.append(temp);
			SpigletCode.append("\n");
		}
		return data;
	}

	public Object visit(ASTPrintStmt node, Object data)
	{
		if(node.jjtGetChild(0).jjtGetChild(0).toString().equals("Temp")
			|| node.jjtGetChild(0).jjtGetChild(0).toString().equals("MyInt"))
		{
			SpigletCode.append("PRINT ");
			data = node.jjtGetChild(0).jjtGetChild(0).jjtAccept(this,data);
			SpigletCode.append("\n");
		}
		else
		{
			int temp = localTemp;
			localTemp ++;
			data = node.jjtGetChild(0).jjtAccept(this,temp);
			SpigletCode.append("PRINT TEMP ");
			SpigletCode.append(temp);
			SpigletCode.append("\n");
		}
		return data;
	}

	public Object visit(ASTExp node, Object temp0)
	{
		Object data = null;
		if(node.jjtGetChild(0).toString().equals("Temp")
			|| node.jjtGetChild(0).toString().equals("MyInt")
			|| node.jjtGetChild(0).toString().equals("MyLabel"))
		{
			int temp = (Integer)temp0;
			SpigletCode.append("MOVE TEMP ");
			SpigletCode.append(temp);
			SpigletCode.append(" ");
			data = node.jjtGetChild(0).jjtAccept(this,data);
			SpigletCode.append("\n");
		}
		else
		{
			data = node.jjtGetChild(0).jjtAccept(this,temp0);
		}
		return data;
	}

	public Object visit(ASTStmtExp node, Object temp0)
	{
		Object data = null;
		data = node.jjtGetChild(0).jjtAccept(this,data);
		if(node.jjtGetChild(1).jjtGetChild(0).toString().equals("Temp")
			|| node.jjtGetChild(1).jjtGetChild(0).toString().equals("MyInt"))
		{
			int temp = (Integer)temp0;
			SpigletCode.append("MOVE TEMP ");
			SpigletCode.append(temp);
			SpigletCode.append(" ");
			data = node.jjtGetChild(1).jjtGetChild(0).jjtAccept(this,data);
			SpigletCode.append("\n");
		}
		else
		{
			data = node.jjtGetChild(1).jjtAccept(this,temp0);
		}
		return data;
	}

	public Object visit(ASTCall node, Object temp0)
	{
		Object data = null;
		int Len = node.jjtGetNumChildren();
		int[] temp = new int[Len];
		int i;
		for(i=0; i<Len; i++)
		{
			if(node.jjtGetChild(i).jjtGetChild(0).toString().equals("Temp"))
			{
				temp[i] = 0;
			}
			else
			{
				temp[i] = localTemp;
				localTemp ++;
				data = node.jjtGetChild(i).jjtAccept(this,temp[i]);
			}
		}
		SpigletCode.append("MOVE TEMP ");
		SpigletCode.append((Integer)temp0);
		if(temp[0] == 0)
		{
			SpigletCode.append(" CALL ");
			data = node.jjtGetChild(0).jjtGetChild(0).jjtAccept(this,data);
		}
		else
		{
			SpigletCode.append(" CALL TEMP ");
			SpigletCode.append(temp[0]);
		}
		SpigletCode.append(" (");
		for(i=1; i<Len; i++)
		{
			if(temp[i] == 0)
			{
				SpigletCode.append(" ");
				data = node.jjtGetChild(i).jjtGetChild(0).jjtAccept(this,data);
			}
			else
			{
				SpigletCode.append(" TEMP ");
				SpigletCode.append(temp[i]);
			}
		}
		SpigletCode.append(" )\n");
		return data;
	}

	public Object visit(ASTHAllocate node, Object temp0)
	{
		Object data = null;
		if(node.jjtGetChild(0).jjtGetChild(0).toString().equals("Temp")
			|| node.jjtGetChild(0).jjtGetChild(0).toString().equals("MyInt"))
		{
			SpigletCode.append("MOVE TEMP ");
			SpigletCode.append((Integer)temp0);
			SpigletCode.append(" HALLOCATE ");
			data = node.jjtGetChild(0).jjtGetChild(0).jjtAccept(this,data);
			SpigletCode.append("\n");
		}
		else
		{
			int temp = localTemp;
			localTemp ++;
			data = node.jjtGetChild(0).jjtAccept(this,temp);
			SpigletCode.append("MOVE TEMP ");
			SpigletCode.append((Integer)temp0);
			SpigletCode.append(" HALLOCATE TEMP ");
			SpigletCode.append(temp);
			SpigletCode.append("\n");
		}
		return data;
	}

	public Object visit(ASTBinOp node, Object temp0)
	{
		Object data = null;
		if(node.jjtGetChild(1).jjtGetChild(0).toString().equals("Temp")
			&& (node.jjtGetChild(2).jjtGetChild(0).toString().equals("Temp")
				|| node.jjtGetChild(2).jjtGetChild(0).toString().equals("MyInt")))
		{
			SpigletCode.append("MOVE TEMP ");
			SpigletCode.append((Integer)temp0);
			SpigletCode.append(" ");
			data = node.jjtGetChild(0).jjtAccept(this,data);
			SpigletCode.append(" ");
			data = node.jjtGetChild(1).jjtGetChild(0).jjtAccept(this,data);
			SpigletCode.append(" ");
			data = node.jjtGetChild(2).jjtGetChild(0).jjtAccept(this,data);
			SpigletCode.append("\n");
		}
		else if(node.jjtGetChild(1).jjtGetChild(0).toString().equals("Temp")
			&& !(node.jjtGetChild(2).jjtGetChild(0).toString().equals("Temp")
				|| node.jjtGetChild(2).jjtGetChild(0).toString().equals("MyInt")))
		{
			int temp2 = localTemp;
			localTemp ++;
			data = node.jjtGetChild(2).jjtAccept(this,temp2);
			SpigletCode.append("MOVE TEMP ");
			SpigletCode.append((Integer)temp0);
			SpigletCode.append(" ");
			data = node.jjtGetChild(0).jjtAccept(this,data);
			SpigletCode.append(" ");
			data = node.jjtGetChild(1).jjtGetChild(0).jjtAccept(this,data);
			SpigletCode.append(" TEMP ");
			SpigletCode.append(temp2);
			SpigletCode.append("\n");
		}
		else if(!(node.jjtGetChild(1).jjtGetChild(0).toString().equals("Temp"))
			&& (node.jjtGetChild(2).jjtGetChild(0).toString().equals("Temp")
				|| node.jjtGetChild(2).jjtGetChild(0).toString().equals("MyInt")))
		{
			int temp1 = localTemp;
			localTemp ++;
			data = node.jjtGetChild(1).jjtAccept(this,temp1);
			SpigletCode.append("MOVE TEMP ");
			SpigletCode.append((Integer)temp0);
			SpigletCode.append(" ");
			data = node.jjtGetChild(0).jjtAccept(this,data);
			SpigletCode.append(" TEMP ");
			SpigletCode.append(temp1);
			SpigletCode.append(" ");
			data = node.jjtGetChild(2).jjtGetChild(0).jjtAccept(this,data);
			SpigletCode.append("\n");
		}
		else
		{
			int temp1 = localTemp;
			localTemp ++;
			int temp2 = localTemp;
			localTemp ++;
			data = node.jjtGetChild(1).jjtAccept(this,temp1);
			data = node.jjtGetChild(2).jjtAccept(this,temp2);
			SpigletCode.append("MOVE TEMP ");
			SpigletCode.append((Integer)temp0);
			SpigletCode.append(" ");
			data = node.jjtGetChild(0).jjtAccept(this,data);
			SpigletCode.append(" TEMP ");
			SpigletCode.append(temp1);
			SpigletCode.append(" TEMP ");
			SpigletCode.append(temp2);
			SpigletCode.append("\n");
		}
		return data;
	}

	public Object visit(ASTMyOp node, Object data)
	{
		SpigletCode.append(node.name);
		return data;
	}

	public Object visit(ASTTemp node, Object data)
	{
		SpigletCode.append("TEMP ");
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTMyInt node, Object data)
	{
		SpigletCode.append(node.val);
		return data;
	}

	public Object visit(ASTMyLabel node, Object data)
	{
		SpigletCode.append(node.name + " ");
		return data;
	}
}
