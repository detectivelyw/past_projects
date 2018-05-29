

public class LivenessAnalysis implements SpigletParserVisitor
{
	public int[] VeriableLiveness;
    	
	LivenessAnalysis()
	{
		VeriableLiveness = new int[20001];
		int i;
		for (i = 0; i <= 20000; i++)
		{
			VeriableLiveness[i] = 0;
		}
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

	public Object visit(ASTStmtList node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTProcedure node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTStmt node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTNoOpStmt node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTErrorStmt node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTCJumpStmt node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTJumpStmt node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTHStoreStmt node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTHLoadStmt node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTMoveStmt node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTPrintStmt node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTExp node, Object data) 
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTStmtExp node, Object data) 
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTCall node, Object data) 
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTHAllocate node, Object data) 
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTBinOp node, Object data) 
	{
		data = node.childrenAccept(this, data);
		return data;
	}


	public Object visit(ASTMyOp node, Object data) 
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTSimpleExp node, Object data) 
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTTemp node, Object data) 
	{
		int number = 0;
		number = ((ASTMyInt)(node.jjtGetChild(0))).val;
		VeriableLiveness[number]++;
		return data;
	}

	public Object visit(ASTMyInt node, Object data) 
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTMyLabel node, Object data) 
	{
		data = node.childrenAccept(this, data);
		return data;
	}
}
