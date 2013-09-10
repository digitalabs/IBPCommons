package org.generationcp.commons.util.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.generationcp.commons.util.SheetUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SheetUtilTest {
    
	private static String fileName = "C:/Book2.xls";
	private static Workbook workbook;
	
	
    @BeforeClass
    public static void setup() throws SQLException, ClassNotFoundException, InvalidFormatException, IOException {
    	 FileInputStream inp = new FileInputStream(fileName);
    	 workbook = WorkbookFactory.create(inp);
		
    }

    @Test
    public void test() throws IOException, InterruptedException {    	
    	
    	String[] data = SheetUtil.sheetsToArray(workbook);
    	for(int i=0; i<data.length; i++)
    	{
    		System.out.println( data[i] );
    	}
    }
    
    @AfterClass
    public static void destory(){
    	
    	 workbook = null;
		
    }
    
   
}
