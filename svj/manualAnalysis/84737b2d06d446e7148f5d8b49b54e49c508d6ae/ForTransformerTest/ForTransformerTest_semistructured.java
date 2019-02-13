package io.cloudslang.lang.compiler.modeller.transformers; 

import io.cloudslang.lang.compiler.validator.ExecutableValidator; 
import io.cloudslang.lang.compiler.validator.ExecutableValidatorImpl; 
import io.cloudslang.lang.compiler.validator.SystemPropertyValidator; 
import io.cloudslang.lang.compiler.validator.SystemPropertyValidatorImpl; 
import io.cloudslang.lang.entities.ListForLoopStatement; 
import io.cloudslang.lang.entities.LoopStatement; 
import io.cloudslang.lang.entities.MapForLoopStatement; 
import junit.framework.Assert; 
import org.junit.Rule; 
import org.junit.Test; 
import org.junit.rules.ExpectedException; 
import org.junit.runner.RunWith; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.context.annotation.Bean; 
import org.springframework.context.annotation.Configuration; 
import org.springframework.test.context.ContextConfiguration; 
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner; 

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ForTransformerTest.Config.class)
public  class  ForTransformerTest  extends TransformersTestParent {
	

    @Rule
    public ExpectedException exception = ExpectedException.none();
	

    @Autowired
    private ForTransformer transformer;
	

    public static ListForLoopStatement validateListForLoopStatement(LoopStatement statement) {
        Assert.assertEquals(true, statement instanceof ListForLoopStatement);
        return (ListForLoopStatement) statement;
    }
	

    public static MapForLoopStatement validateMapForLoopStatement(LoopStatement statement) {
        Assert.assertEquals(true, statement instanceof MapForLoopStatement);
        return (MapForLoopStatement) statement;
    }
	

    @Test
    public void testValidStatement() throws Exception {
        LoopStatement statement = transformer.transform("x in collection").getTransformedData();
        ListForLoopStatement listForLoopStatement = validateListForLoopStatement(statement);
        Assert.assertEquals("x", listForLoopStatement.getVarName());
        Assert.assertEquals("collection", listForLoopStatement.getExpression());
    }
	

    @Test
    public void testValidStatementWithSpaces() throws Exception {
        LoopStatement statement = transformer.transform("x in range(0, 9)").getTransformedData();
        ListForLoopStatement listForLoopStatement = validateListForLoopStatement(statement);
        Assert.assertEquals("x", listForLoopStatement.getVarName());
        Assert.assertEquals("range(0, 9)", listForLoopStatement.getExpression());
    }
	

    @Test
    public void testValidStatementAndTrim() throws Exception {
        LoopStatement statement = transformer.transform(" min   in  collection  ").getTransformedData();
        ListForLoopStatement listForLoopStatement = validateListForLoopStatement(statement);
        Assert.assertEquals("min", listForLoopStatement.getVarName());
        Assert.assertEquals("collection", listForLoopStatement.getExpression());
    }
	

    @Test
    public void testNoVarName() throws Exception {
        exception.expect(RuntimeException.class);
<<<<<<< C:\Users\GUILHE~1\AppData\Local\Temp\fstmerge_var1_3461526214240373248.java
        exception.expectMessage("Argument[] violates character rules");
        transformAndThrowFirstException(transformer, "  in  collection" );
=======
        exception.expectMessage("var name");
        transformAndThrowFirstException(transformer, "  in  collection");
>>>>>>> C:\Users\GUILHE~1\AppData\Local\Temp\fstmerge_var2_6416086617091978351.java
    }
	

    @Test
    public void testVarNameContainInvalidChars() throws Exception {
        exception.expect(RuntimeException.class);
        exception.expectMessage("Argument[x a] violates character rules.");
        transformAndThrowFirstException(transformer, "x a  in  collection");
    }
	

    @Test
    public void testNoCollectionExpression() throws Exception {
        exception.expect(RuntimeException.class);
        exception.expectMessage("expression");
        transformAndThrowFirstException(transformer, "x in  ");
    }
	

    @Test
    public void testMultipleInsAreTrimmed() throws Exception {
        LoopStatement statement = transformer.transform(" in   in in ").getTransformedData();
        ListForLoopStatement listForLoopStatement = validateListForLoopStatement(statement);
        Assert.assertEquals("in", listForLoopStatement.getExpression());
    }
	

    @Test
    public void testEmptyValue() throws Exception {
        LoopStatement statement = transformer.transform("").getTransformedData();
        Assert.assertNull(statement);
    }
	

    @Test
    public void testValidMapStatement() throws Exception {
        LoopStatement statement = transformer.transform("k, v in collection").getTransformedData();
        MapForLoopStatement mapForLoopStatement = validateMapForLoopStatement(statement);
        Assert.assertEquals("k", mapForLoopStatement.getKeyName());
        Assert.assertEquals("v", mapForLoopStatement.getValueName());
        Assert.assertEquals("collection", statement.getExpression());
    }
	

    @Test
    public void testValidMapStatementSpaceBeforeComma() throws Exception {
        LoopStatement statement = transformer.transform("k ,v in collection").getTransformedData();
        MapForLoopStatement mapForLoopStatement = validateMapForLoopStatement(statement);
        Assert.assertEquals("k", mapForLoopStatement.getKeyName());
        Assert.assertEquals("v", mapForLoopStatement.getValueName());
        Assert.assertEquals("collection", statement.getExpression());
    }
	

    @Test
    public void testValidMapStatementWithoutSpaceAfterComma() throws Exception {
        LoopStatement statement = transformer.transform("k,v in collection").getTransformedData();
        MapForLoopStatement mapForLoopStatement = validateMapForLoopStatement(statement);
        Assert.assertEquals("k", mapForLoopStatement.getKeyName());
        Assert.assertEquals("v", mapForLoopStatement.getValueName());
        Assert.assertEquals("collection", statement.getExpression());
    }
	

    @Test
    public void testValidMapStatementAndTrim() throws Exception {
        LoopStatement statement = transformer.transform(" k, v   in  collection  ").getTransformedData();
        MapForLoopStatement mapForLoopStatement = validateMapForLoopStatement(statement);
        Assert.assertEquals("k", mapForLoopStatement.getKeyName());
        Assert.assertEquals("v", mapForLoopStatement.getValueName());
        Assert.assertEquals("collection", statement.getExpression());
    }
	

    @Test
    public void testValidMapStatementAndTrimMultipleWhitSpaces() throws Exception {
        LoopStatement statement = transformer.transform("   k,    v     in  collection  ").getTransformedData();
        MapForLoopStatement mapForLoopStatement = validateMapForLoopStatement(statement);
        Assert.assertEquals("k", mapForLoopStatement.getKeyName());
        Assert.assertEquals("v", mapForLoopStatement.getValueName());
        Assert.assertEquals("collection", statement.getExpression());
    }
	

    @Test
    public void testMapVarNameContainInvalidChars() throws Exception {
        exception.expect(RuntimeException.class);
<<<<<<< C:\Users\GUILHE~1\AppData\Local\Temp\fstmerge_var1_7172690534733497259.java
        exception.expectMessage("Argument[(k v m)] violates character rules.");
        transformAndThrowFirstException(transformer, "(k v m)  in  collection" );
=======
        exception.expectMessage("var name");
        exception.expectMessage("invalid");
        transformAndThrowFirstException(transformer, "(k v m)  in  collection");
>>>>>>> C:\Users\GUILHE~1\AppData\Local\Temp\fstmerge_var2_6910673679649299455.java
    }
	

    @Test
    public void testMapNoCollectionExpression() throws Exception {
        exception.expect(RuntimeException.class);
        exception.expectMessage("expression");
        transformAndThrowFirstException(transformer, "k, v in  ");
    }
	

    @Configuration
    public static  class  Config {
		
        @Bean
        public ForTransformer forTransformer() {
            return new ForTransformer();
        }
		
        @Bean
        public ExecutableValidator executableValidator() {
            return new ExecutableValidatorImpl();
        }
		
        @Bean
        public SystemPropertyValidator systemPropertyValidator() {
            return new SystemPropertyValidatorImpl();
        }

	}

}

