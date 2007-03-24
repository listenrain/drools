package org.drools.clp;

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.drools.Person;
import org.drools.clp.functions.AddFunction;
import org.drools.clp.functions.BindFunction;
import org.drools.clp.functions.ModifyFunction;

public class BlockExecutionTest extends TestCase {
    
    FunctionRegistry registry;
    
    public void setUp() {
        this.registry = new FunctionRegistry( BuiltinFunctions.getInstance() );
    }    
    
    public void testAddWithModify() {
        BlockExecutionEngine engine = new BlockExecutionEngine();        
        ExecutionBuildContext build = new ExecutionBuildContext(engine, this.registry );                
        
        FunctionCaller addCaller = new FunctionCaller( new AddFunction() );
        addCaller.addParameter( new ObjectValueHandler( new BigDecimal( 20) ) );
        addCaller.addParameter( new LongValueHandler( "11" ) );
                
        FunctionCaller bindCaller = new FunctionCaller( new BindFunction() );
        bindCaller.addParameter( build.createLocalVariable( "?x" ) );
        bindCaller.addParameter( addCaller );
        
        engine.addFunction( bindCaller );
        
        
        FunctionCaller modifyCaller = new FunctionCaller( new ModifyFunction() );        
        build.createLocalVariable( "?p" );        
        modifyCaller.addParameter( build.getVariableValueHandler( "?p" ) );
        
        ListValueHandler list = new ListValueHandler();
        list.add( new ObjectValueHandler( "age") );
        list.add( build.getVariableValueHandler( "?x" ) );
        modifyCaller.addParameter( list );
        
        ExecutionContext context = new ExecutionContext(null, null, 2);
        Person p = new Person("mark");
        context.setLocalVariable( 1, new ObjectValueHandler( p ) );
        
        engine.addFunction( modifyCaller );
        
        engine.execute( context );
        
        assertEquals( 31, p.getAge() );
        
        
    }
}
