import java.util.Hashtable;

public class ProcedureInfo
{
  Hashtable<String,ProcedureNode> ProcedureTable;
  int maxParameterNumber;
  ProcedureInfo()
  {
	  ProcedureTable = new Hashtable<String,ProcedureNode>();
	  maxParameterNumber = 0;
  }
}

class ProcedureNode
{
  int startTempNumber;
  int numberOfLocal;
  int numberOfTemperaries;
  int numberOfParameters;
}
