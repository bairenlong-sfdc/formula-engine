/*
 * Copyright, 1999-2018, salesforce.com
 * All Rights Reserved
 * Company Confidential
 */
package com.force.formula.impl;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.force.formula.FormulaEngine;
import com.force.formula.impl.BaseCustomizableParserTest.FieldTestFormulaValidationHooks;
import com.force.formula.impl.sql.FormulaDefaultSqlStyle;
import com.force.formula.sql.TrinoContainerTester;

/**
 * Abstract class for testing formulas with trino (which is the successor to preso)
 * @author stamm
 * @since 0.3.0
 */
public abstract class FormulaPrestoTests extends FormulaGenericTests {

	// use a single DB with a docker container, and not three of them by sharing them.
	static DbTester SHARED_TESTER;
	static {
		try {
		    // Use the trino container (for now) because it's a little less fiddly than presto and is maintained
			SHARED_TESTER = new TrinoContainerTester();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
	
    public FormulaPrestoTests(String name) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
        super(name);
    }
    
	@Override
	protected BaseFormulaGenericTest createTestCase(boolean positive, String entity, FormulaTestCaseInfo testCase) {
		return new PrestoFormulaTest(testCase, testCase.getName(), positive, this);
	}

    @Override
    protected boolean filterTests(FormulaTestCaseInfo testCase) {
        if (testCase.getTestLabels().contains("ignore")) return false;
        return true;
    }

    @Override
    protected void setUpTest(BaseFormulaGenericTest test) {
        FormulaEngine.setHooks(new PrestoFormulaValidationHooks());
        FormulaEngine.setFactory(BaseFieldReferenceTest.TEST_FACTORY);
    }

	@Override
	protected boolean shouldTestSql() {
		return true;
	}

	@Override
	protected boolean shouldTestJavascript() {
		return false; // don't duplicate effort
	}

	@Override
	protected DbTester constructDbTester() throws IOException {
		return SHARED_TESTER;
	}
	
	protected static class PrestoFormulaTest extends FormulaGenericTest {
		
		public PrestoFormulaTest(FormulaTestCaseInfo testCase, String name, boolean positive,
				FormulaGenericTests suite) {
			super(testCase, name, positive, suite);
		}
	}

    protected static class PrestoFormulaValidationHooks extends FieldTestFormulaValidationHooks {
        @Override
		public FormulaSqlHooks getSqlStyle() {
        	return FormulaDefaultSqlStyle.PRESTO;
		}
    }
    
}