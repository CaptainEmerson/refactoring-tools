package edu.pdx.cs.multiview.smelldetector.detectors.duplicateCode;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.junit.Before;
import org.junit.Test;

import edu.pdx.cs.multiview.smelldetector.BaseSmellDetectorTest;

public class DuplicateCodeInstaceITTest {
	private BaseSmellDetectorTest baseSmellDetectorTest;
	private DuplicateCodeMetadataCreator duplicateCodeMetadataCreator;
	final DuplicateCodeMetadataCollector collectorMock = mock(DuplicateCodeMetadataCollector.class);

	private DuplicateCodeInstace duplicateCodeInstace;
	
	@Before
	public void setup() throws Exception {
		String path = System.getProperty("user.dir")
				+ "/src_tests/edu/pdx/cs/multiview/smelldetector/detectors/duplicateCode/duplicate_method_class.java_file";
		System.out.println("File Path: " + path);
		baseSmellDetectorTest = new BaseSmellDetectorTest(path);
		duplicateCodeMetadataCreator = new DuplicateCodeMetadataCreator(baseSmellDetectorTest.getTestProject().getJavaProject());
		duplicateCodeMetadataCreator.createSmellMetaData(getTestMethod());
		List<IMethod> visibleMethods = new ArrayList<IMethod>();
		visibleMethods.add(getTestMethod());
		duplicateCodeInstace = new DuplicateCodeInstace(visibleMethods);
	}
	
	@Test
	public void shouldReturnCorrectNumberOfDuplicateLines() throws Exception {
		assertEquals(5, duplicateCodeInstace.getNumberOfDuplicateLinesForMethod(getTestMethod()));
	}
	
	private IMethod getTestMethod() throws Exception {
		String methodSignature = "testMethod";
		return baseSmellDetectorTest.getMethod(methodSignature);
	}
}
