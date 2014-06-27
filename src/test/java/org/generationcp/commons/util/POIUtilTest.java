/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.commons.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.generationcp.commons.util.PoiUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class POIUtilTest {
    
	private static String fileName = "C:/test.xls";
	private static Workbook workbook;
	private static Sheet sheet;
	
    @BeforeClass
    public static void setup() throws SQLException, ClassNotFoundException
            , InvalidFormatException, IOException {
    	 FileInputStream inp = new FileInputStream(fileName);
    	 workbook = WorkbookFactory.create(inp);
		 sheet = workbook.getSheetAt(workbook.getActiveSheetIndex());
    }

    @Test
    public void test() throws IOException, InterruptedException {    	

    	System.out.println( "rowIsEmpty:" + PoiUtil.rowIsEmpty(sheet, 1, 0, 6));
    	System.out.println( "rowHasEmpty:" + PoiUtil.rowHasEmpty(sheet, 1, 0, 0));
    	System.out.println( "columnIsEmpty:" + PoiUtil.columnIsEmpty(sheet, 0));
    	System.out.println( "columnHasEmpty:" + PoiUtil.columnHasEmpty(sheet, 0));
    	System.out.println( "columnArray:" );
    	String[] data = PoiUtil.asStringArrayColumn(sheet, 1);
    	for(int i=0; i<data.length; i++){
    		System.out.println( data[i] );
    	}

    	System.out.println( "rowIsEmpty:" + PoiUtil.rowIsEmpty(sheet, 1, 0, 5));
    	System.out.println( "rowHasEmpty:" + PoiUtil.rowHasEmpty(sheet, 1, 0, 5));
    	System.out.println( "rowAsString:" + PoiUtil.rowAsString(sheet, 2, 0, 5, ","));
    	System.out.println( "rowAsStringMax:" + PoiUtil.rowAsString(sheet, 2, 0, 5, ",", 2));
    	System.out.println( "rowAsStringArray:" + PoiUtil.rowAsStringArray(sheet, 2, 0, 5));
    	System.out.println( "columnIsEmpty:" + PoiUtil.columnIsEmpty(sheet, 0));
    	System.out.println( "columnHasEmpty:" + PoiUtil.columnHasEmpty(sheet,0));
    	System.out.println( "asStringArrayColumn:" + PoiUtil.asStringArrayColumn(sheet,0));

    }
    
    @AfterClass
    public static void destory(){    	
    	 workbook = null;
		 sheet = null;
    }    
   
}
