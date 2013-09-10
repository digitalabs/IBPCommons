package org.generationcp.commons.util.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.generationcp.commons.util.MySQLUtil;
import org.generationcp.commons.util.PoiUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class POIUtilTest {
    
	private static String fileName = "C:/test.xls";
	private static Workbook workbook;
	private static Sheet sheet;
	
    @BeforeClass
    public static void setup() throws SQLException, ClassNotFoundException, InvalidFormatException, IOException {
    	 FileInputStream inp = new FileInputStream(fileName);
    	 workbook = WorkbookFactory.create(inp);
		 sheet = workbook.getSheetAt(workbook.getActiveSheetIndex());
    }

    @Test
    public void test() throws IOException, InterruptedException {    	
    	System.out.println( "rowIsEmpty:" + PoiUtil.rowIsEmpty(sheet, 1, 0, 5));
    	System.out.println( "rowHasEmpty:" + PoiUtil.rowHasEmpty(sheet, 1, 0, 5));
    	System.out.println( "rowAsString:" + PoiUtil.rowAsString(sheet, 2, 0, 5, ","));
    	System.out.println( "rowAsStringMax:" + PoiUtil.rowAsString(sheet, 2, 0, 5, ",", 2));
    }
    
    @AfterClass
    public static void destory(){
    	
    	 workbook = null;
		 sheet = null;
    }
    
   
}
