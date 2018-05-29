//**************************************************************************************
//*                        Systematic Nonlinear Planner                                *
//*                                                                                    *
//*  Author: Yiwen Li                                                                  *
//*  CSE Division, EECS, University of Michigan                                        *
//*  Current Version: 1.0                                                              *
//*                                                                                    *
//*  All copyrights reserved                                                           *
//**************************************************************************************

import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

// Represents expressions such as A, Table, b, x, On(A, Table), Move(A, x, y)
interface Expression {
	// For a Sentence: return the function of the Sentence
	// For a Constant: return the value of the Constant
	// For a Variable: return name of the Variable
	public String Get_function();
    
	// For a Sentence: return the argument list of the Sentence
	// For a Constant: return null
	// For a Variable: return null
    public List<Expression> Get_arguments();
    
 	// For a Sentence: return the size of the argument list of the Sentence
 	// For a Constant: return 0
 	// For a Variable: return 0
    public int Get_arguments_size();
    
  	// For a Sentence: set the ith variable name in argument list to a given String
  	// For a Constant: do nothing
  	// For a Variable: set the variable name to a given String
    public void Set_variable_name(String s, int i);
}

// Represents constant expressions such as A, B, Table
class Constant implements Expression {
    private String value;
    
    public Constant(String value) {
        this.value = value;
    }
    
    @Override
    public boolean equals(Object that) {
        if (that instanceof Constant)
            return this.equals((Constant) that);
        else
            return false;
    }
    
    public boolean equals(Constant that) {
        return this.value.equals(that.value);
    }
    
    // Get the function of this sentence
    public String Get_function() {
    	return this.value;
    }
    
    // Get the arguments of this function
    public List<Expression> Get_arguments() {
    	return null;
    }
    
    // Return 0
    public int Get_arguments_size() {
    	return 0;
    }
    
    // Do nothing
    public void Set_variable_name(String s, int i) {
    	
    }
    
    @Override
    public int hashCode() {
        return value.hashCode();
    }
    
    @Override
    public String toString() {
        return value;
    }
}

// Represents variables such as b, x, y
class Variable implements Expression {
    private String name;
    
    public Variable(String name) {
        this.name = name;
    }
    
    // Get the function of this sentence
    public String Get_function() {
    	return this.name;
    }
    
    // Get the arguments of this function
    public List<Expression> Get_arguments() {
    	return null;
    }
    
    // Return 0
    public int Get_arguments_size() {
    	return 0;
    }
    
    // Set the variable name to a given String
    public void Set_variable_name(String s, int i) {
    	this.name = s;
    }
    
    @Override
    public boolean equals(Object that) {
        if (that instanceof Variable)
            return this.equals((Variable) that);
        else
            return false;
    }
    
    public boolean equals(Variable that) {
        return this.name.equals(that.name);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    @Override
    public String toString() {
        return name;
    }
}

// Represents statement expressions such as Clear(x), Move(b, C, D)
class Sentence implements Expression {
    private String function;
    private List<Expression> arguments;
    
    public Sentence(String function) {
        this(function, new ArrayList<Expression>(0));
    }
    
    public Sentence(String function, List<Expression> arguments) {
        this.function = function;
        this.arguments = new ArrayList<Expression>();
        int i;
        for (i = 0; i < arguments.size(); i++) {
        	if (arguments.get(i) instanceof Constant)
        		this.arguments.add(i, new Constant(arguments.get(i).Get_function()));
        	if (arguments.get(i) instanceof Variable) {
        		this.arguments.add(i, new Variable(arguments.get(i).Get_function()));
        	}
        }
    }
    
    public Sentence(String function, Expression... arguments) {
        this(function, Arrays.asList(arguments));
    }
    
    // Get the function of this sentence
    public String Get_function() {
    	return this.function;
    }
    
    // Get the arguments of this function
    public List<Expression> Get_arguments() {
    	return this.arguments;
    }
    
    // Get the size of arguments list
    public int Get_arguments_size () {
    	return this.arguments.size();
    }
    
    // Set the ith variable name in the argument list to a given string
    public void Set_variable_name(String s, int i) {
    	Variable v= new Variable(s);
    	this.arguments.set(i, v);
    }
    
    @Override
    public boolean equals(Object that) {
        if (that instanceof Sentence)
            return equals((Sentence) that);
        else
            return false;
    }
    
    public boolean equals(Sentence that) {
        if (!this.function.equals(that.function))
            return false;
        ListIterator<Expression> i = this.arguments.listIterator();
        ListIterator<Expression> j = that.arguments.listIterator();
        while (i.hasNext() && j.hasNext())
            if (!i.next().equals(j.next()))
                return false;
        return !i.hasNext() && !j.hasNext();
    }
    
    @Override
    public int hashCode() {
        int hash = function.hashCode();
        for (Expression a: arguments)
            hash += a.hashCode();
        return hash;
    }
    
    @Override
    public String toString() {
    	if (arguments.size() == 0)
    		return function;
        String s = function + "(";
        for (int i = 0; i < arguments.size(); i++) {
            if (i != 0)
                s += ",";
            s += arguments.get(i);
        }
        return s + ")";
    }
}

// Represents inequalities such as b != x, x != A
class Inequality {
    private Expression x, y;
    
    public Inequality(Expression x, Expression y) {
        this.x = x;
        this.y = y;
    }
    
    public Inequality(Inequality e) {
        if (e.Get_x() instanceof Constant)
        	this.x = new Constant(e.Get_x().Get_function());
        if (e.Get_x() instanceof Variable)
        	this.x = new Variable(e.Get_x().Get_function());
        if (e.Get_x() instanceof Sentence)
        	this.x = new Sentence(e.Get_x().Get_function(), e.Get_x().Get_arguments());
        if (e.Get_y() instanceof Constant)
        	this.y = new Constant(e.Get_y().Get_function());
        if (e.Get_y() instanceof Variable)
        	this.y = new Variable(e.Get_y().Get_function());
        if (e.Get_y() instanceof Sentence)
        	this.y = new Sentence(e.Get_y().Get_function(), e.Get_y().Get_arguments());
    }
    // Get Expression x
    public Expression Get_x() {
    	return this.x;
    }
    // Get Expression y
    public Expression Get_y() {
    	return this.y;
    }
    // Change Expression x
    public void Change_x(String s) {
    	this.x.Set_variable_name(s, 0);
    }
    // Change Expression y
    public void Change_y(String s) {
    	this.y.Set_variable_name(s, 0);
    }
    
    // Substitude this inequality using theta
    public void Substitude(Map<Variable, Expression> theta) {
    	if (theta.containsKey(this.x))
    		this.x = theta.get(this.x);
    	if (theta.containsKey(this.y))
    		this.y = theta.get(this.y);
    }
    
    // Check if there is conflict: C!=C, x!=x
    public boolean IsConflict() {
    	if (this.x.equals(this.y))
    		return true;
    	else return false;
    }
    
    @Override
    public boolean equals(Object that) {
        if (that instanceof Inequality)
            return equals((Inequality) that);
        else
            return false;
    }
    
    public boolean equals(Inequality that) {
        return (this.x.equals(that.x) && this.y.equals(that.y)) ||
        (this.x.equals(that.y) && this.y.equals(that.x));
    }
    
    @Override
    public int hashCode() {
        return x.hashCode() + y.hashCode();
    }
    
    @Override
    public String toString() {
        return x + "!=" + y;
    }
}

// Represents positive or negative expressions such as !Clear(x), On(A, B)
class Atom {
    private Expression expression;
    private boolean positive;
    private int belongToActionNumber;
    
    // Generate an Atom representing Action X
    public Atom(Action X) {
        this.expression = X.Get_expression();
        this.positive = true;
    }
    
    // Copy a Atom
    public Atom(Atom Y) {
    	if (Y.Get_expression() instanceof Constant)
    		this.expression = new Constant(Y.Get_expression().Get_function());
    	if (Y.Get_expression() instanceof Variable)
    		this.expression = new Variable(Y.Get_expression().Get_function());
    	if (Y.Get_expression() instanceof Sentence)
    		this.expression = new Sentence(Y.Get_expression().Get_function(), Y.Get_expression().Get_arguments());
    	
    	this.positive = Y.positive;
    }
    
    public Atom(Expression expression) {
        this(expression, true);
    }
    
    public Atom(Expression expression, boolean positive) {
        this.expression = expression;
        this.positive = positive;
    }
    
    // Get the expression of this Atom
    public Expression Get_expression() {
    	return this.expression;
    }
    
    // Get positive
    public boolean Get_positive() {
    	return this.positive;
    }
    
    // Get belongToActionNumber
    public int Get_belongToActionNumber() {
    	return this.belongToActionNumber;
    }
    
    // Set the variable name to a given string
    public void Set_variable_name(String s, int i) {
    	this.expression.Set_variable_name(s, i);
    }
    
    // Set belongToActionNumber
    public void Set_belongToActionNumber(int number) {
    	this.belongToActionNumber = number;
    }
    
    @Override
    public boolean equals(Object that) {
        if (that instanceof Atom)
            return equals((Atom) that);
        else
            return false;
    }
    
    public boolean equals(Atom that) {
        return this.positive == that.positive &&
        this.expression.equals(that.expression);
    }
    
    @Override
    public int hashCode() {
        return expression.hashCode() + (positive ? 0 : 1);
    }
    
    @Override
    public String toString() {
        return (positive ? "" : "!") + expression;
    }
    
    // Negate this Atom
    public Atom negate(Atom x) {
    	if (x.positive == true) {
    		return new Atom(x.expression, false);
    	}
    	else return new Atom(x.expression, true);
    }
}

// Represents actions such as Move(b,x,y) with its preconditions, inequalities,
// and effects.
class Action {
    private Expression expression;
    // Each of these lists represents a conjunction of items
    private List<Atom> preconditions;
    private List<Inequality> inequalities;
    private List<Atom> effects;
    
    public Action(Expression expression, List<Atom> preconditions,
                  List<Inequality> inequalities, List<Atom> effects) {
        this.expression = expression;
        this.preconditions = preconditions;
        this.inequalities = inequalities;
        this.effects = effects;
    }
    
    public Action(Expression expression, Atom[] preconditions,
                  Inequality[] inequalities, Atom[] effects) {
        this(expression, Arrays.asList(preconditions),
             Arrays.asList(inequalities), Arrays.asList(effects));
    }
    
    // Copy an action
    public Action(Action A){
    	int i;
    	this.expression = new Sentence(A.expression.Get_function(), A.expression.Get_arguments());
    	this.preconditions = new ArrayList<Atom>();
    	for (i = 0; i < A.preconditions.size(); i++) {
    		this.preconditions.add(new Atom(A.preconditions.get(i)));
    	}
    	
    	this.effects = new ArrayList<Atom>();
    	for (i = 0; i < A.effects.size(); i++) {
    		this.effects.add(new Atom(A.effects.get(i)));
    	}
    	
    	this.inequalities = new ArrayList<Inequality>();
    	for (i = 0; i < A.inequalities.size(); i++) {
    		this.inequalities.add(new Inequality(A.inequalities.get(i)));
    	}
    }
    
    public Action(Atom A) {
    	this.expression = new Sentence(A.toString());
    }
    
    // Get preconditions
    public List<Atom> Get_Preconditions() {
    	return this.preconditions;
    }
    
    // Get effects
    public List<Atom> Get_Effects() {
    	return this.effects;
    }
    
    // Get expression
    public Expression Get_expression() {
    	return this.expression;
    }
    
    // Get inequalities
    public List<Inequality> Get_Inequalities() {
    	return this.inequalities;
    }
    
    // Check if this action can achieve effect B(Atom)
    public boolean IsInEffects(Atom B) {
    	int i;
    	// Search for Atom B in this action's effects list
    	for (i = 0; i < this.effects.size(); i++) {
    		if (this.effects.get(i).equals(B))
    			return true;
    	}
    	return false;
    }
    
    // Check if this action has pre-condition B(Atom)
    public boolean IsInPreconditions(Atom B) {
    	int i;
    	// Search for Atom B in this action's pre-condition list
    	for (i = 0; i < this.preconditions.size(); i++) {
    		if (this.preconditions.get(i).equals(B))
    			return true;
    	}
    	return false;
    }
    
    // Change an action with fresh variables
    // Note: x_number, y_number and b_number will increase globally to avoid conflict number assignment
    public Action Fresh_variables(Action old, int x_number, int y_number, int b_number) {
    	String refreshed_x, refreshed_y, refreshed_b;
    	refreshed_x = "x" + x_number;
    	refreshed_y = "y" + y_number;
    	refreshed_b = "b" + b_number;
    	Action refreshed = new Action(old);
        // change variables in expression
    	if (old.expression instanceof Variable) {
    		if (old.expression.toString().startsWith("x"))
    			refreshed.expression.Set_variable_name(refreshed_x, 0);
    		if (old.expression.toString().startsWith("y"))
    			refreshed.expression.Set_variable_name(refreshed_y, 0);
    		if (old.expression.toString().startsWith("b"))
    			refreshed.expression.Set_variable_name(refreshed_b, 0);
    	}
    	if (old.expression instanceof Sentence) {
    		for (int i = 0; i < old.expression.Get_arguments_size(); i++) {
    			if (old.expression.Get_arguments().get(i).toString().startsWith("x"))
    				refreshed.expression.Set_variable_name(refreshed_x, i);
    			if (old.expression.Get_arguments().get(i).toString().startsWith("y"))
    				refreshed.expression.Set_variable_name(refreshed_y, i);
    			if (old.expression.Get_arguments().get(i).toString().startsWith("b"))
    				refreshed.expression.Set_variable_name(refreshed_b, i);
    		}
    	}
    	// change variables in preconditions
    	for (int i = 0; i < old.preconditions.size(); i++) {
    		if (old.preconditions.get(i).Get_expression() instanceof Variable) {
        		if (old.preconditions.get(i).Get_expression().toString().startsWith("x"))
        			refreshed.preconditions.get(i).Set_variable_name(refreshed_x, 0);
        		if (old.preconditions.get(i).Get_expression().toString().startsWith("y"))
        			refreshed.preconditions.get(i).Set_variable_name(refreshed_y, 0);
        		if (old.preconditions.get(i).Get_expression().toString().startsWith("b"))
        			refreshed.preconditions.get(i).Set_variable_name(refreshed_b, 0);
        	}
        	if (old.preconditions.get(i).Get_expression() instanceof Sentence) {
        		for (int j = 0; j < old.preconditions.get(i).Get_expression().Get_arguments_size(); j++) {
        			if (old.preconditions.get(i).Get_expression().Get_arguments().get(j).toString().startsWith("x"))
            			refreshed.preconditions.get(i).Set_variable_name(refreshed_x, j);
            		if (old.preconditions.get(i).Get_expression().Get_arguments().get(j).toString().startsWith("y"))
            			refreshed.preconditions.get(i).Set_variable_name(refreshed_y, j);
            		if (old.preconditions.get(i).Get_expression().Get_arguments().get(j).toString().startsWith("b"))
            			refreshed.preconditions.get(i).Set_variable_name(refreshed_b, j);
        		}
        	}
    	}
    	// change variables in effects
    	for (int i = 0; i < old.effects.size(); i++) {
    		if (old.effects.get(i).Get_expression() instanceof Variable) {
        		if (old.effects.get(i).Get_expression().toString().startsWith("x"))
        			refreshed.effects.get(i).Set_variable_name(refreshed_x, 0);
        		if (old.effects.get(i).Get_expression().toString().startsWith("y"))
        			refreshed.effects.get(i).Set_variable_name(refreshed_y, 0);
        		if (old.effects.get(i).Get_expression().toString().startsWith("b"))
        			refreshed.effects.get(i).Set_variable_name(refreshed_b, 0);
        	}
        	if (old.effects.get(i).Get_expression() instanceof Sentence) {
        		for (int j = 0; j < old.effects.get(i).Get_expression().Get_arguments_size(); j++) {
        			if (old.effects.get(i).Get_expression().Get_arguments().get(j).toString().startsWith("x"))
            			refreshed.effects.get(i).Set_variable_name(refreshed_x, j);
            		if (old.effects.get(i).Get_expression().Get_arguments().get(j).toString().startsWith("y"))
            			refreshed.effects.get(i).Set_variable_name(refreshed_y, j);
            		if (old.effects.get(i).Get_expression().Get_arguments().get(j).toString().startsWith("b"))
            			refreshed.effects.get(i).Set_variable_name(refreshed_b, j);
        		}
        	}
    	}
    	// change variables in inequalities
    	for (int i = 0; i < old.inequalities.size(); i++) {
    		if (old.inequalities.get(i).Get_x().toString().startsWith("x"))
    			refreshed.inequalities.get(i).Change_x(refreshed_x);
    		if (old.inequalities.get(i).Get_x().toString().startsWith("y"))
    			refreshed.inequalities.get(i).Change_x(refreshed_y);
    		if (old.inequalities.get(i).Get_x().toString().startsWith("b"))
    			refreshed.inequalities.get(i).Change_x(refreshed_b);
    		if (old.inequalities.get(i).Get_y().toString().startsWith("x"))
    			refreshed.inequalities.get(i).Change_y(refreshed_x);
    		if (old.inequalities.get(i).Get_y().toString().startsWith("y"))
    			refreshed.inequalities.get(i).Change_y(refreshed_y);
    		if (old.inequalities.get(i).Get_y().toString().startsWith("b"))
    			refreshed.inequalities.get(i).Change_y(refreshed_b);
    	}
    	return refreshed;
    }
    
    // Use theta to substitude all expression in this action
    public Action Substitude(Action old, Map<Variable, Expression> theta) {
    	Action Substituded = new Action(old);
    	Unifier unifier = new Unifier();
    	// Substitude expression
    	Atom expression = new Atom(Substituded.expression);
    	if (unifier.substitude(expression, theta) != null)
    		Substituded.expression = unifier.substitude(expression, theta).Get_expression();
        
        // Substitude preconditions
        int i;
        for (i = 0; i < old.preconditions.size(); i++) {
        	if (unifier.substitude(Substituded.preconditions.get(i), theta) != null)
        		Substituded.preconditions.set(i, unifier.substitude(Substituded.preconditions.get(i), theta));
        }
        
        // Substitude effects
        for (i = 0; i < old.effects.size(); i++) {
        	if (unifier.substitude(Substituded.effects.get(i), theta) != null)
        		Substituded.effects.set(i, unifier.substitude(Substituded.effects.get(i), theta));
        }
        
        // Substitude inequalities
        for (i = 0; i < old.inequalities.size(); i++) {
        	Expression x, y;
        	x = old.inequalities.get(i).Get_x();
        	y = old.inequalities.get(i).Get_y();
        	if (theta.containsKey(x)) {
        		x = theta.get(x);
        	}
        	if (theta.containsKey(y)) {
        		y = theta.get(y);
        	}
        	Substituded.inequalities.set(i, new Inequality(x, y));
        }
        return Substituded;
    }
    
    // for test purpose
    public void print() {
    	System.out.print(this.expression.toString()+"\n");
    	int i;
    	System.out.print("The Preconditions: \n");
    	for (i = 0; i < this.preconditions.size(); i++) {
    		System.out.print(this.preconditions.get(i).toString()+" ");
    	}
    	System.out.print("\n");
    	
    	System.out.print("The Inequalities: \n");
    	for (i = 0; i < this.inequalities.size(); i++) {
    		System.out.print(this.inequalities.get(i).toString()+" ");
    	}
    	System.out.print("\n");
    	
    	System.out.print("The Effects: \n");
    	for (i = 0; i < this.effects.size(); i++) {
    		System.out.print(this.effects.get(i).toString()+" ");
    	}
    	System.out.print("\n");
    }
    @Override
    public String toString() {
        return expression.toString();
    }
}

// Represents ordering constraints, meaning x < y;
class Ordering_Constraints {
	private Action A;
    private Action B;
    
    public Ordering_Constraints(Action A, Action B) {
    	this.A = new Action(A);
        this.B = new Action(B);
    }
    
    public Ordering_Constraints(Ordering_Constraints O) {
    	this.A = new Action(O.A);
        this.B = new Action(O.B);
    }
    
    public String toString() {
    	return A.toString()+"<"+B.toString();
    }
    
    public boolean EqualsTo(Ordering_Constraints O) {
    	if (this.toString().equals(O.toString()))
    		return true;
    	return false;
    }
    
    public String getA() {
    	return this.A.toString();
    }
    
    public String getB() {
    	return this.B.toString();
    }
    
    // check if there is any Ordering conflict: u>v && u<v (u = this)
    public boolean IsConflict(Ordering_Constraints v) {
    	if ((this.A.equals(v.B))&&(this.B.equals(v.A)))
    		return true;
    	return false;
    }
    // This function is to assist Print function. We don't print ordering constraints containing Start or Finish
    public boolean isFact() {
    	if ((this.A.Get_expression().Get_function().equals("Start"))||(this.A.Get_expression().Get_function().equals("Finish"))
            ||(this.B.Get_expression().Get_function().equals("Start"))||(this.B.Get_expression().Get_function().equals("Finish")))
    		return true;
    	else return false;
    }
    
    // This is to substitude the ordering string
    public void Substitude(Map<Variable, Expression> theta) {
    	this.A = this.A.Substitude(this.A, theta);
    	this.B = this.B.Substitude(this.B, theta);
    }
}

// Represents causal links, meaning A achieves p for B;
class Causal_Links {
	private Atom A;
    private Atom p;
    private Atom B;
    
    public Causal_Links(Atom A, Atom p, Atom B) {
    	this.A = A;
        this.p = p;
        this.B = B;
    }
    
    public Causal_Links(Causal_Links e) {
    	this.A = new Atom(e.A);
        this.p = new Atom(e.p);
        this.B = new Atom(e.B);
    }
    
    // Check if action v is a threat to this causal link
    public boolean IsThreat(Action v) {
    	if ((!this.A.toString().equals(v.toString()))&&(!this.B.toString().equals(v.toString()))&&
            ((v.IsInEffects(this.p))||(v.IsInEffects(this.p.negate(this.p))))) {
    		return true;
    	}
    	else return false;
    }
    
    public String toString() {
    	return "Add action "+A+" to achieve "+p+" for action "+B;
    }
    
    public Atom returnA() {
    	return A;
    }
    
    public Atom returnB() {
    	return B;
    }
}

// Implementation of the Unification
class Unifier {
	public Unifier() {
	}
    
	public Map<Variable, Expression> unify(Expression x, Expression y,
                                           Map<Variable, Expression> theta) {
		
		if (((x.Get_function().equals("A"))||(x.Get_function().equals("B"))||(x.Get_function().equals("C"))||(x.Get_function().equals("D"))||
             (x.Get_function().equals("E"))||(x.Get_function().equals("F"))||(x.Get_function().equals("G"))||(x.Get_function().equals("H"))||
             (x.Get_function().equals("I"))||(x.Get_function().equals("Table")))&&((y.Get_function().equals("A"))||(y.Get_function().equals("B"))||(y.Get_function().equals("C"))
                                                                                   ||(y.Get_function().equals("D"))||(y.Get_function().equals("E"))||(y.Get_function().equals("F"))||(y.Get_function().equals("G"))
                                                                                   ||(y.Get_function().equals("H"))||(y.Get_function().equals("I"))||(y.Get_function().equals("Table")))
            &&(!x.Get_function().equals(y.Get_function())))
			return null;
		// if theta = failure then return failure
		if (theta == null) {
			return null;
		} else if (x.equals(y)) {
			// else if x = y then return theta
			return theta;
		} else if (x instanceof Variable) {
			// else if VARIABLE?(x) then return UNIVY-VAR(x, y, theta)
			return unifyVar((Variable)x, y, theta);
		} else if (y instanceof Variable) {
			// else if VARIABLE?(y) then return UNIFY-VAR(y, x, theta)
			return unifyVar((Variable)y, x, theta);
		} else if (x instanceof Sentence && y instanceof Sentence) {
			// deal with the function
			if (!x.Get_function().equals(y.Get_function())||(x.Get_arguments_size()!=y.Get_arguments_size()))
				return null;
			else {
				for (int i = 0; i < x.Get_arguments().size(); i++) {
					theta = unify(x.Get_arguments().get(i), y.Get_arguments().get(i), theta);
				}
				return theta;
			}
		} else {
			// else return failure
			return null;
		}
	}
	
	public Map<Variable, Expression> unify(Atom x, Atom y,
                                           Map<Variable, Expression> theta) {
		if (((x.Get_positive() == true)&&(y.Get_positive() == false))
			||((x.Get_positive() == false)&&(y.Get_positive() == true)))
			return null;
		else return unify(x.Get_expression(), y.Get_expression(), theta);
	}
	
	public Map<Variable, Expression> unify(Expression x, Expression y) {
		return unify(x, y, new LinkedHashMap<Variable, Expression>());
	}
	
	public Map<Variable, Expression> unify(Atom x, Atom y) {
		if (((x.Get_positive() == true)&&(y.Get_positive() == false))||((x.Get_positive() == false)&&(y.Get_positive() == true)))
			return null;
		else return unify(x, y, new LinkedHashMap<Variable, Expression>());
	}
	
	private Map<Variable, Expression> unifyVar(Variable var, Expression x,
                                               Map<Variable, Expression> theta) {
        if (theta.keySet().contains(var)) {
			// if {var/val} E theta then return UNIFY(val, x, theta)
			return unify(theta.get(var), x, theta);
		} else if (theta.keySet().contains(x)) {
			// else if {x/val} E theta then return UNIFY(var, val, theta)
			return unify(var, theta.get(x), theta);
		}  else {
			// else return add {var/x} to theta
			theta.put(var, x);
			return theta;
		}
	}
	
	// Use a Unifier theta to change a expression into a substitude one
	public Atom substitude (Atom expression, Map<Variable, Expression> theta) {
		if (expression.Get_expression() instanceof Constant)
			return null;
		else if ((expression.Get_expression() instanceof Variable)&&(theta.containsKey(expression.Get_expression()))) {
			Atom changed_expression = new Atom(expression);
			changed_expression.Set_variable_name(theta.get(expression.Get_expression()).toString(), 0);
			return changed_expression;
		}
		else if (expression.Get_expression() instanceof Sentence) {
			Atom changed_expression = new Atom(expression);
			for (int i = 0; i < expression.Get_expression().Get_arguments_size(); i++) {
				if (expression.Get_expression().Get_arguments().get(i) instanceof Variable) {
					if (theta.containsKey((Variable)expression.Get_expression().Get_arguments().get(i))) {
						changed_expression.Set_variable_name(theta.get(expression.Get_expression().Get_arguments().get(i)).toString(), i);
					}
				}
			}
			return changed_expression;
		}
		else return null;
	}
	
	// Print out the unifier
	public void print (Map<Variable, Expression> theta) {
		if (theta == null) {
			System.out.print("There is no unifier \n");
		}
		else {
			System.out.print("The Unifer is: \n");
			Iterator<Map.Entry<Variable, Expression>> it = theta.entrySet().iterator();
			while (it.hasNext()) {
                Map.Entry<Variable, Expression> entry = (Map.Entry<Variable, Expression>) it.next();
                System.out.print(entry.getKey()+"/"+entry.getValue()+" ");
			}
			System.out.print("\n");
		}
	}
}

// Represents a PartialOrderPlan
class PartialOrderPlan {
	public List<Action> actions;
	public List<Action> available_actions;
	public List<Ordering_Constraints> ordering_constraints;
	public List<Causal_Links> causal_links;
	public List<Atom> open_preconditions;
	public Causal_Links plan_transformation;
	public Action[] total_order_plan;
	public static int x_number, y_number, b_number, debug;
	
	// Initialize a Partial Order Plan with the provided Action Set.
	public PartialOrderPlan(Action... actions) {
		this.actions = new ArrayList<Action>();
		this.actions.add(actions[0]);
		this.actions.add(actions[1]);
		this.available_actions = new ArrayList<Action>();
		int i;
		for (i = 2; i < actions.length; i++) {
			available_actions.add(actions[i]);
		}
        this.ordering_constraints = new ArrayList<Ordering_Constraints>();
        this.ordering_constraints.add(new Ordering_Constraints(actions[0], actions[1]));
        this.causal_links = new ArrayList<Causal_Links>();
        this.open_preconditions = new ArrayList<Atom>();
        for (i = 0; i < actions[1].Get_Preconditions().size(); i++) {
        	this.open_preconditions.add(actions[1].Get_Preconditions().get(i));
        	this.open_preconditions.get(i).Set_belongToActionNumber(1);
        }
        this.plan_transformation = null;
        this.x_number = 0; this.y_number = 0; this.b_number = 0;
        this.debug = 0;
    }
	
	public PartialOrderPlan(List<Action> actions, List<Action> available_actions, List<Ordering_Constraints> ordering_constraints,
                            List<Causal_Links> causal_links, List<Atom> open_preconditions) {
		this.actions = actions;
		this.available_actions = available_actions;
        this.ordering_constraints = ordering_constraints;
        this.causal_links = causal_links;
        this.open_preconditions = open_preconditions;
        this.plan_transformation = null;
    }
    
    public PartialOrderPlan(Action[] actions, Action[] available_actions, Ordering_Constraints[] ordering_constraints,
                            Causal_Links[] causal_links, Atom[] open_preconditions) {
    	this(Arrays.asList(actions), Arrays.asList(available_actions), Arrays.asList(ordering_constraints),
             Arrays.asList(causal_links), Arrays.asList(open_preconditions));
    }
    
    public PartialOrderPlan(PartialOrderPlan plan) {
    	this.actions = new ArrayList<Action>();
    	this.available_actions = new ArrayList<Action>();
        this.ordering_constraints = new ArrayList<Ordering_Constraints>();
        this.causal_links = new ArrayList<Causal_Links>();
        this.open_preconditions = new ArrayList<Atom>();
        this.plan_transformation = null;
        this.debug = plan.debug;
    	int i;
    	for (i = 0; i < plan.actions.size(); i++)
    		this.actions.add(new Action(plan.actions.get(i)));
    	for (i = 0; i < plan.available_actions.size(); i++)
    		this.available_actions.add(new Action(plan.available_actions.get(i)));
    	for (i = 0; i < plan.ordering_constraints.size(); i++)
    		this.ordering_constraints.add(new Ordering_Constraints(plan.ordering_constraints.get(i)));
    	for (i = 0; i < plan.causal_links.size(); i++)
    		this.causal_links.add(new Causal_Links(plan.causal_links.get(i)));
    	for (i = 0; i < plan.open_preconditions.size(); i++) {
    		this.open_preconditions.add(new Atom(plan.open_preconditions.get(i)));
    		this.open_preconditions.get(i).Set_belongToActionNumber(plan.open_preconditions.get(i).Get_belongToActionNumber());
    	}
    }
    
    // Check if the current Partial Order Plan is the final solution
    public boolean Goal_Check() {
    	return this.open_preconditions.isEmpty();
    }
    
    // Get a feasible total order plan(action sequence) from a given partial order plan, store it in this.total_order_plan
    public void Total_Order_Plan() {
    	int number_of_actions = this.actions.size();
    	this.total_order_plan = new Action[number_of_actions];
    	// The first action of the total order plan is Start and the last action is Finish
    	this.total_order_plan[0] = this.actions.get(0);
    	this.total_order_plan[number_of_actions-1] = this.actions.get(1);
    	int position_in_sequence, candidate_action, existing_action, number_of_ordering_constraints;
    	Action current_action = null;
    	// For each position between Start and Finish in the action sequence, choose a appropriate action to fill it
    	// This loop is for the position between Start and Finish
    	for (position_in_sequence = 1; position_in_sequence < number_of_actions-1; position_in_sequence++) {
    		// This loop is for the candidate action
    		for (candidate_action = 2; candidate_action < number_of_actions; candidate_action++) {
    			// can_select marks if this action can fill the current position in the action sequence
    			boolean can_select = true;
    			// Select a candidate action, but it shouldn't be Start or Finish
        		if (current_action == null)
        			current_action = new Action(this.actions.get(candidate_action));
        		else current_action = this.actions.get(candidate_action);
        		// Firstly, check if the candidate_action has already been selected
        		for (existing_action = 0; existing_action < position_in_sequence; existing_action++) {
        			if (this.total_order_plan[existing_action].equals(current_action)) {
        				can_select = false;
        				break;
        			}
        		}
                
        		if (can_select == false)
        			continue;
        		// Secondly, check if the candidate action appear in the right position of any ordering constraints
        		for (number_of_ordering_constraints = 0; number_of_ordering_constraints < this.ordering_constraints.size();
                     number_of_ordering_constraints++) {
        			// If the candidate action appear in the right position of any ordering constraints, then it cannot be
        			// selected, unless all the actions in the left position of those ordering constraints have already been selected
        			if (this.ordering_constraints.get(number_of_ordering_constraints).getB().equals(current_action.toString())) {
        				can_select = false;
        				for (existing_action = 0; existing_action < position_in_sequence; existing_action++) {
                			if (this.total_order_plan[existing_action].toString().equals(this.ordering_constraints.get(number_of_ordering_constraints).getA())) {
                				can_select = true;
                				break;
                			}
        				}
        				if (can_select == false)
        					break;
        			}
        		}
        		// If both of the above checks have been passed, then the candidate action can be selected,
        		// and we can move to the next position in the action sequence
        		if (can_select == true) {
        			this.total_order_plan[position_in_sequence] = current_action;
        			break;
        		}
    		}
    	}
    }
    
    // Switch 2 open-preconditons in the list: provided for Sort_Open_preconditons()
    public void Switch_Preconditions(int i, int j) {
    	Atom temp = new Atom(this.open_preconditions.get(i));
    	this.open_preconditions.set(i, this.open_preconditions.get(j));
    	this.open_preconditions.set(j, temp);
    }
    
    // Check if this open-precondition is in the effect list of Start, provided for Sort_Open_preconditons()
    public boolean Is_from_Start(Atom chosen_open_precondition) {
    	boolean Is_from_Start = false;
    	Unifier unifier = new Unifier();
		Action start = this.actions.get(0);
		
		// See if there is an effect in Action Start that can unify with p
		for (int check_effect = 0; check_effect < start.Get_Effects().size(); check_effect++) {
			Map<Variable, Expression> theta = new HashMap<Variable, Expression>();
			theta = unifier.unify(start.Get_Effects().get(check_effect),
                                  chosen_open_precondition);
			// If this effect cannot unify with p, try next
			if (theta != null) {
				Is_from_Start = true;
				break;
			}
		}
		return Is_from_Start;
    }
    
    // Sort the open-precondition list to choose open-preconditions which can be deleted by Start first
    public void Sort_Open_preconditions() {
    	int i, j;
    	for (i = 0; i < this.open_preconditions.size(); i++) {
    		if (!this.Is_from_Start(this.open_preconditions.get(i))) {
    			for (j = i+1; j < this.open_preconditions.size(); j++) {
    				if (this.Is_from_Start(this.open_preconditions.get(j))) {
    					this.Switch_Preconditions(i, j);
    					break;
    				}
    			}
    		}
    	}
    }
    
    // Search for the solution of POP(Partial Order Plan)
	public boolean SNLP() {
		this.Print();
		// For debug purpose only
		/*
         debug = debug+1;
         if (debug >= 16)
         return false;
         System.out.print("No."+debug+": \n");
         this.Print();
         */
		// Check if the current POP is inconsistent.
		int i, j;
		for (i = 0; i < this.ordering_constraints.size(); i++) {
			for (j = 0; j < this.ordering_constraints.size(); j++) {
				if ((i!=j)&&(this.ordering_constraints.get(i).IsConflict(this.ordering_constraints.get(j)))) {
					return false;
				}
			}
		}
		
		// Check if the current Partial Order Plan is the solution
		if (Goal_Check()) {
			// System.out.print("We have found the solution successfully.\n");
			// The function Total_Order_Plan() must be run before the function Print()
			// Because the Print() will print the total order action sequence generated by Total_Order_Plan()
			this.Total_Order_Plan();
			this.Print();
			
			System.out.print("The Linearized Plan:\n");
	    	if (this.total_order_plan.length != 0) {
	    		for (i = 0; i < this.total_order_plan.length-1; i++) {
	        		System.out.print(this.total_order_plan[i].toString()+"->");
	        	}
	    	}
	    	System.out.print(this.total_order_plan[i].toString()+"\n");
	    	
			return true;
		}
		
		// Add ordering constraints to resolve potential conflicts
		for (i = 0; i < this.actions.size(); i++) {
			Action x = new Action(this.actions.get(i));
			if ((x.toString().equals("Start"))||(x.toString().equals("Finish")))
				continue;
			for (int x_effect_i = 0; x_effect_i < x.Get_Effects().size(); x_effect_i++) {
				Atom x_effect = new Atom(x.Get_Effects().get(x_effect_i));
				for (j = i+1; j < this.actions.size(); j++) {
					Action y = new Action(this.actions.get(j));
					if ((y.toString().equals("Start"))||(y.toString().equals("Finish")))
						continue;
					for (int y_precondition_i = 0; y_precondition_i < y.Get_Preconditions().size(); y_precondition_i++) {
						Atom y_precondition = new Atom(y.Get_Preconditions().get(y_precondition_i));
						if (x_effect.negate(x_effect).toString().equals(y_precondition.toString())) {
							Ordering_Constraints add_ordering_constraint = new Ordering_Constraints(y, x);
							boolean can_add = true;
							for (int check = 0; check < this.ordering_constraints.size(); check++) {
								if (this.ordering_constraints.get(check).EqualsTo(add_ordering_constraint)) {
									can_add = false;
								}
							}
							if (can_add)
								this.ordering_constraints.add(add_ordering_constraint);
						}
					}
				}
			}
			
			for (int x_precondition_i = 0; x_precondition_i < x.Get_Preconditions().size(); x_precondition_i++) {
				Atom x_precondition = new Atom(x.Get_Preconditions().get(x_precondition_i));
				for (j = i+1; j < this.actions.size(); j++) {
					Action y = new Action(this.actions.get(j));
					for (int y_effect_i = 0; y_effect_i < y.Get_Effects().size(); y_effect_i++) {
						Atom y_effect = new Atom(y.Get_Effects().get(y_effect_i));
						if (x_precondition.negate(x_precondition).toString().equals(y_effect.toString())) {
							Ordering_Constraints add_ordering_constraint = new Ordering_Constraints(x, y);
							boolean can_add = true;
							for (int check = 0; check < this.ordering_constraints.size(); check++) {
								if (this.ordering_constraints.get(check).EqualsTo(add_ordering_constraint)) {
									can_add = false;
								}
							}
							if (can_add)
								this.ordering_constraints.add(add_ordering_constraint);
						}
					}
				}
			}
		}
		
		/*
         // Search for any existing threats and deal with it
         for (i = 0; i < this.causal_links.size(); i++) {
         for (j = 0; j < this.actions.size(); j++) {
         if (this.causal_links.get(i).IsThreat(this.actions.get(j))) {
         Ordering_Constraints add_constraint1, add_constraint2;
         add_constraint1 = new Ordering_Constraints(this.actions.get(j), new Action(this.causal_links.get(i).returnA()));
         add_constraint2 = new Ordering_Constraints(new Action(this.causal_links.get(i).returnB()), this.actions.get(j));
         if ((!this.ordering_constraints.contains(add_constraint1))&&(!this.ordering_constraints.contains(add_constraint2))) {
         PartialOrderPlan plan1 = new PartialOrderPlan(this);
         plan1.ordering_constraints.add(add_constraint1);
         if (plan1.SNLP())
         return true;
         
         PartialOrderPlan plan2 = new PartialOrderPlan(this);
         plan2.ordering_constraints.add(add_constraint2);
         plan2.SNLP();
         if (plan2.SNLP())
         return true;
         }
         }
         }
         }
         */
		
		// Pick an open-condition to execute the algorithm
		int s, p, w;
		Atom chosen_open_precondition;
		
		this.Sort_Open_preconditions();
		// Pick an open-precondition
		for (p = 0; p < this.open_preconditions.size(); p++) {
			chosen_open_precondition = this.open_preconditions.get(p);
			// Search for an action in current POP that has p as a pre-condition
			for (w = 0; w < this.actions.size(); w++) {
				if (this.actions.get(w).IsInPreconditions(chosen_open_precondition)) {
					// Search for an applicable action s from the existing actions in the current POP to achieve p for w
		    		for (s = 0; s < 2; s++) {
		    			int check_effect;
		    			Unifier unifier = new Unifier();
		    			Action try_action = new Action(this.actions.get(s));
		    			
		    			// See if there is an effect in Action try_action that can unify with p
		    			boolean action_can_choose = false;
		    			Map<Variable, Expression> final_theta = new HashMap<Variable, Expression>();
		    			for (check_effect = 0; check_effect < try_action.Get_Effects().size(); check_effect++) {
		    				Map<Variable, Expression> theta = new HashMap<Variable, Expression>();
		    				theta = unifier.unify(chosen_open_precondition, try_action.Get_Effects().get(check_effect));
		    				// If this effect cannot unify with p, try next
		    				if (theta == null)
		    					continue;
		    				// If the effect can unify with p, then check to see if can satisfy all inequalities constraints
		    				Inequality inequality_check;
		    				int inequality_number;
		    				boolean pass_check = true;
		    				// If any substitution in theta conflicts with any inequality constraint, fail
		    				for (inequality_number = 0; inequality_number < try_action.Get_Inequalities().size(); inequality_number++) {
		    					inequality_check = try_action.Get_Inequalities().get(inequality_number);
		    					if (theta.containsKey(inequality_check.Get_x())) {
		    						if (theta.get(inequality_check.Get_x()).toString().equals(inequality_check.Get_y().toString())) {
		    							pass_check = false;
	    	    						break;
		    						}
		    					}
		    					if (theta.containsKey(inequality_check.Get_y())) {
		    						if (theta.get(inequality_check.Get_y()).toString().equals(inequality_check.Get_x().toString())) {
		    							pass_check = false;
	    	    						break;
		    						}
		    					}
		    				}
		    				
		    				if (pass_check == false)
		    					continue;
		    				else {
		    					action_can_choose = true;
		    				}
		    				if (action_can_choose == true) {
		    					final_theta = theta;
		    					break;
		    				}
	 	    			}
		    			// If the inequality check is passed, the Action try_action should be substitude using theta
	    				// Also: chosen_open_precondition should be removed from the open_preconditions list
	    				// A new causal_link should be added into the PoP's causal_links list and transformation
		    			// A new ordering constraint about Actions should be added into the PoP
		    			// All the preconditions of the action should be added into the PoP's open-precondition list
		    			if (action_can_choose == true) {
		    				// For debug
		    				/*
                             debug = debug+1;
                             if (debug >= 3)
                             return false;
                             System.out.print("No."+debug+"\n");
                             System.out.print("We are in the actions list! \n");
                             System.out.print("The chosen action is: \n");
                             try_action.print();
                             */
		    				
		    				// Unify the action whose pre-condition will be reached and removed by choosing this action
		    				int action_number = chosen_open_precondition.Get_belongToActionNumber();
		    				Action temp_action = this.actions.get(action_number);
		    				this.actions.set(action_number, temp_action.Substitude(temp_action, final_theta));
		    				// Unify all the existing open-preconditions who belong to this action
		    				int open_precondition_i;
		    				for (open_precondition_i = 0; open_precondition_i < this.open_preconditions.size(); open_precondition_i++) {
		    					if ((open_precondition_i != p)&&
                                    (this.open_preconditions.get(open_precondition_i).Get_belongToActionNumber() == action_number)) {
		    						Atom temp_open_preconsition = new Atom(this.open_preconditions.get(open_precondition_i));
		    						temp_open_preconsition = unifier.substitude(temp_open_preconsition, final_theta);
		    						this.open_preconditions.set(open_precondition_i, temp_open_preconsition);
		    					}
		    				}
		    				// Substitude the ordering_constraints
		    				int order_i;
		    				for (order_i = 0; order_i < this.ordering_constraints.size(); order_i++) {
		    					this.ordering_constraints.get(order_i).Substitude(final_theta);
		    				}
                            
		    				// check if there is any conflict inequality constraint
		    				int temp;
		    				boolean has_conflict = false;
		    				for (temp = 0; temp < try_action.Get_Inequalities().size(); temp++) {
		    					if (try_action.Get_Inequalities().get(temp).IsConflict()) {
		    						has_conflict = true;
		    						break;
		    					}
                                
		    				}
		    				if (has_conflict)
		    					continue;
		    				
		    				this.actions.set(s, try_action);
		    				Causal_Links add_causal_link = new Causal_Links(new Atom(try_action), chosen_open_precondition,
                                                                            new Atom(this.actions.get(w)));
		    				if (!this.causal_links.contains(add_causal_link)) {
		    					PartialOrderPlan newPlan = new PartialOrderPlan(this);
		    			    	newPlan.causal_links.add(add_causal_link);
		    			    	newPlan.plan_transformation = new Causal_Links(add_causal_link);
		    			    	newPlan.open_preconditions.remove(chosen_open_precondition);
		    			    	
		    			    	Ordering_Constraints add_ordering_constraint_1 = new Ordering_Constraints(try_action,
                                                                                                          this.actions.get(w));
		    					
		    					if (!newPlan.causal_links.contains(add_causal_link))
		    						newPlan.causal_links.add(add_causal_link);
		    					if (!newPlan.ordering_constraints.contains(add_ordering_constraint_1))
		    						newPlan.ordering_constraints.add(add_ordering_constraint_1);
                                
		    			    	int k;
		    			    	for (k = 0; k < try_action.Get_Preconditions().size(); k++) {
		    			    		newPlan.open_preconditions.add(try_action.Get_Preconditions().get(k));
		    			    	}
		    			    	// For debug
		    			    	/*
                                 System.out.print("The substituded action is: \n");
                                 try_action.print();
                                 
                                 newPlan.Print();
                                 */
		    					
		    			    	if (newPlan.SNLP())
		    			    		return true;
		    				}
		    			}
		    			
		    			/*
                         if (this.actions.get(s).IsInEffects(chosen_open_precondition)) {
                         Causal_Links add_causal_link = new Causal_Links(new Atom(this.actions.get(s)), chosen_open_precondition,
                         new Atom(this.actions.get(w)));
                         if (!this.causal_links.contains(add_causal_link)) {
                         PartialOrderPlan newPlan = new PartialOrderPlan(this);
                         Ordering_Constraints add_ordering_constraint_1 = new Ordering_Constraints(this.actions.get(s).toString(),
                         this.actions.get(w).toString());
                         
                         if (!newPlan.causal_links.contains(add_causal_link)) {
                         newPlan.causal_links.add(add_causal_link);
                         newPlan.plan_transformation = new Causal_Links(add_causal_link);
                         }
                         if (!newPlan.ordering_constraints.contains(add_ordering_constraint_1))
                         newPlan.ordering_constraints.add(add_ordering_constraint_1);
                         
                         newPlan.open_preconditions.remove(chosen_open_precondition);
                         int k;
                         for (k = 0; k < this.actions.get(s).Get_Preconditions().size(); k++) {
                         newPlan.open_preconditions.add(this.actions.get(s).Get_Preconditions().get(k));
                         }
                         if (newPlan.SNLP())
                         return true;
                         }
                         }
                         */
		    		}
		    		
		    		// Search for an applicable action s from the available actions in the current POP to achieve p for w
		    		for (s = this.available_actions.size()-1; s >= 0; s--) {
		    			int check_effect;
		    			Unifier unifier = new Unifier();
		    			// Firstly, get a copy of the selected action with fresh variables
		    			Action try_action = new Action(this.available_actions.get(s));
		    			try_action = try_action.Fresh_variables(try_action, x_number, y_number, b_number);
		    			x_number = x_number+1;
		    			y_number = y_number+1;
		    			b_number = b_number+1;
		    			
		    			// Secondly, see if there is an effect in Action try_action that can unify with p
		    			boolean action_can_choose = false;
		    			Map<Variable, Expression> final_theta = new HashMap<Variable, Expression>();
		    			for (check_effect = 0; check_effect < try_action.Get_Effects().size(); check_effect++) {
		    				Map<Variable, Expression> theta = new HashMap<Variable, Expression>();
		    				theta = unifier.unify(try_action.Get_Effects().get(check_effect),
                                                  chosen_open_precondition);
		    				// If this effect cannot unify with p, try next
		    				if (theta == null)
		    					continue;
		    				// If the effect can unify with p, then check to see if can satisfy all inequalities constraints
		    				Inequality inequality_check;
		    				int inequality_number;
		    				boolean pass_check = true;
		    				// If any substitution in theta conflicts with any inequality constraint, fail
		    				for (inequality_number = 0; inequality_number < try_action.Get_Inequalities().size(); inequality_number++) {
		    					inequality_check = try_action.Get_Inequalities().get(inequality_number);
		    					if (theta.containsKey(inequality_check.Get_x())) {
		    						if (theta.get(inequality_check.Get_x()).toString().equals(inequality_check.Get_y().toString())) {
		    							pass_check = false;
	    	    						break;
		    						}
		    					}
		    				}
		    				if (pass_check == false)
		    					continue;
		    				else {
		    					action_can_choose = true;
		    				}
		    				if (action_can_choose == true) {
		    					final_theta = theta;
		    					break;
		    				}
	 	    			}
		    			
		    			// If the inequality check is passed, the Action try_action can be substitude using theta and add into the action list
	    				// Also: chosen_open_precondition should be removed from the open_preconditions list
	    				// A new causal_link should be added into the PoP's causal_links list and transformation
		    			// A new ordering constraint about Actions should be added into the PoP
		    			// The new substituded action should be added into the action list of the PoP
		    			// All the preconditions of the new substituded action should be added into the PoP's open-precondition list
		    			if (action_can_choose == true) {
		    				try_action = try_action.Substitude(try_action, final_theta);
		    				// check if there is any conflict inequality constraint
		    				int temp;
		    				boolean has_conflict = false;
		    				for (temp = 0; temp < try_action.Get_Inequalities().size(); temp++) {
		    					if (try_action.Get_Inequalities().get(temp).IsConflict()) {
		    						has_conflict = true;
		    						break;
		    					}
                                
		    				}
		    				if (has_conflict)
		    					continue;
		    				
		    				Causal_Links add_causal_link = new Causal_Links(new Atom(try_action), chosen_open_precondition,
                                                                            new Atom(this.actions.get(w)));
		    				if (!this.causal_links.contains(add_causal_link)) {
		    					PartialOrderPlan newPlan = new PartialOrderPlan(this);
		    			    	newPlan.causal_links.add(add_causal_link);
		    			    	newPlan.plan_transformation = new Causal_Links(add_causal_link);
		    			    	newPlan.open_preconditions.remove(chosen_open_precondition);
		    			    	Ordering_Constraints add_ordering_constraint_1 = new Ordering_Constraints(try_action,
                                                                                                          this.actions.get(w));
		    					
		    					if (!newPlan.causal_links.contains(add_causal_link))
		    						newPlan.causal_links.add(add_causal_link);
		    					if (!newPlan.ordering_constraints.contains(add_ordering_constraint_1))
		    						newPlan.ordering_constraints.add(add_ordering_constraint_1);
		    					
		    			    	int k;
		    			    	int open_precondition_start = newPlan.open_preconditions.size();
		    			    	int action_number = newPlan.actions.size();
		    			    	for (k = 0; k < try_action.Get_Preconditions().size(); k++) {
		    			    		newPlan.open_preconditions.add(try_action.Get_Preconditions().get(k));
		    			    		// Record which action this open-precondition belongs to
		    			    		newPlan.open_preconditions.get(open_precondition_start).Set_belongToActionNumber(action_number);
		    			    		open_precondition_start++;
		    			    	}
		    			    	
		    			    	/*
                                 debug = debug+1;
                                 if (debug >= 3)
                                 return false;
                                 System.out.print("No."+debug+"\n");
                                 System.out.print("We are in the available actions list! \n");
                                 System.out.print("The chosen action is: \n");
                                 this.available_actions.get(s).print();
                                 System.out.print("The substituded action is: \n");
                                 try_action.print();
                                 */
		    					// newPlan.Print();
		    					// For debug
		    					/*
                                 int test;
                                 System.out.print("Show the action list: \n");
                                 for (test = 0; test < newPlan.actions.size()-1; test++) {
                                 System.out.print(newPlan.actions.get(test).toString()+", ");
                                 }
                                 System.out.print(newPlan.actions.get(test).toString()+"\n");
                                 
                                 System.out.print("Show the available action list: \n");
                                 for (test = 0; test < newPlan.available_actions.size()-1; test++) {
                                 System.out.print(newPlan.available_actions.get(test).toString()+", ");
                                 }
                                 System.out.print(newPlan.available_actions.get(test).toString()+"\n");
                                 System.out.print("Action added! \n");
                                 */
		    					newPlan.actions.add(try_action);
		    					// For debug
		    					// newPlan.Print();
		    					/*
                                 System.out.print("Show the action list: \n");
                                 for (test = 0; test < newPlan.actions.size()-1; test++) {
                                 System.out.print(newPlan.actions.get(test).toString()+", ");
                                 }
                                 System.out.print(newPlan.actions.get(test).toString()+"\n");
                                 
                                 System.out.print("Show the available action list: \n");
                                 for (test = 0; test < newPlan.available_actions.size()-1; test++) {
                                 System.out.print(newPlan.available_actions.get(test).toString()+", ");
                                 }
                                 System.out.print(newPlan.available_actions.get(test).toString()+"\n");
                                 */
		    			    	//newPlan.Print();
                                
		    			    	if (newPlan.SNLP())
		    			    		return true;
		    				}
		    			}
                        
		    			/*
                         if (this.available_actions.get(s).IsInEffects(chosen_open_precondition)) {
                         Causal_Links add_causal_link = new Causal_Links(new Atom(this.available_actions.get(s)), chosen_open_precondition,
                         new Atom(this.actions.get(w)));
                         if (!this.causal_links.contains(add_causal_link)) {
                         PartialOrderPlan newPlan = new PartialOrderPlan(this);
                         newPlan.causal_links.add(add_causal_link);
                         newPlan.plan_transformation = new Causal_Links(add_causal_link);
                         newPlan.open_preconditions.remove(chosen_open_precondition);
                         Action action_taken = this.available_actions.get(s);
                         Ordering_Constraints add_ordering_constraint_1 = new Ordering_Constraints(this.available_actions.get(s).toString(),
                         this.actions.get(w).toString());
                         
                         if (!newPlan.causal_links.contains(add_causal_link))
                         newPlan.causal_links.add(add_causal_link);
                         if (!newPlan.ordering_constraints.contains(add_ordering_constraint_1))
                         newPlan.ordering_constraints.add(add_ordering_constraint_1);
                         
                         newPlan.actions.add(action_taken);
                         newPlan.available_actions.remove(action_taken);
                         int k;
                         for (k = 0; k < this.available_actions.get(s).Get_Preconditions().size(); k++) {
                         newPlan.open_preconditions.add(this.available_actions.get(s).Get_Preconditions().get(k));
                         }
                         if (newPlan.SNLP())
                         return true;
                         }
                         } */
		    		}
				}
			}
		}
		
		return false;
	}
    
	// Print the current Partial Order Plan
    public void Print() {
    	System.out.print("Partial Order Plan:\n");
    	if (this.plan_transformation == null)
    		System.out.print("Initial State: \n");
    	else {
    		System.out.print("Plan Transformation: \n");
    		System.out.print(this.plan_transformation.toString()+"\n");
    	}
        
    	System.out.print("Actions: ");
    	int i;
    	for (i = 0; i < this.actions.size()-1; i++) {
    		System.out.print(this.actions.get(i).toString()+", ");
    	}
    	System.out.print(this.actions.get(i).toString()+"\n");
    	for (i = 0; i < this.actions.size(); i++) {
    		System.out.print("Action "+i+": ");
    		this.actions.get(i).print();
    		System.out.print("\n");
    	}
    	System.out.print("\n");
    	
    	System.out.print("Orderings: ");
    	for (i = 0; i < this.ordering_constraints.size()-1; i++) {
    		if (!this.ordering_constraints.get(i).isFact())
    			System.out.print(this.ordering_constraints.get(i).toString()+", ");
    	}
    	if (!this.ordering_constraints.get(i).isFact())
    		System.out.print(this.ordering_constraints.get(i).toString());
    	System.out.print("\n");
    	
    	System.out.print("Causal Links: ");
    	if (this.causal_links.size() != 0) {
    		for (i = 0; i < this.causal_links.size()-1; i++) {
        		System.out.print(this.causal_links.get(i).toString()+", ");
        	}
        	System.out.print(this.causal_links.get(i).toString());
    	}
    	System.out.print("\n");
    	
    	System.out.print("Open Preconditions: ");
    	if (this.open_preconditions.size() != 0) {
    		for (i = 0; i < this.open_preconditions.size()-1; i++) {
        		System.out.print(this.open_preconditions.get(i).toString()+", ");
        	}
        	System.out.print(this.open_preconditions.get(i).toString());  	
    	}
    	System.out.print("\n");  	
    }
}

// The problem files will call A2Main.solve to find the solution to the problem
public class A2Main {
    public static void solve(Action... actions) {
        // The first action (start) has the initial state as effects
        // The second action (finish) has the goal state as preconditions
        // Use SNLP to find a solution to the planning problem
    	
    	// Initialize a Partial Order Plan, and use SNLP to find a solution
    	PartialOrderPlan plan = new PartialOrderPlan(actions);
    	plan.SNLP();
        
    	// Test for the Unification
    	/*
         Atom x,y;
         x = actions[2].Get_Preconditions().get(1);
         y = actions[2].Get_Effects().get(1);
         Map<Variable, Expression> theta = new HashMap<Variable, Expression>();
         System.out.print(x.toString()+"\n");
         System.out.print(y.toString()+"\n");
         Unifier test = new Unifier();
         theta = test.unify(x.Get_expression(), y.Get_expression(), theta);
         test.print(theta);
         Atom p,q;
         p = test.substitude(x, theta);
         q = test.substitude(y, theta);
         System.out.print(p.toString()+"\n");
         System.out.print(q.toString()+"\n");
         */
    	
    	// Test for Substitution
    	/*
         int x = 13, y = 13, b = 13;
         Action refreshed_1;
         actions[3].print();
         refreshed_1 = actions[3].Fresh_variables(actions[3],x , y, b);
         refreshed_1.print();
         */
    }
}

