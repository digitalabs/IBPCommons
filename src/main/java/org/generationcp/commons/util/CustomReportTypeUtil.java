package org.generationcp.commons.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.generationcp.commons.pojo.CustomReportList;
import org.generationcp.commons.pojo.CustomReportType;
import org.generationcp.middleware.pojos.presets.StandardPreset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomReportTypeUtil {
	private static final Logger LOG = LoggerFactory.getLogger(CustomReportTypeUtil.class);
	
	private CustomReportTypeUtil(){
		//do nothing, this is to hide piblic constructor
	}
	
	public static List<CustomReportType> readReportConfiguration(StandardPreset standardPreset){
		List<CustomReportType> customReportTypes = new ArrayList<CustomReportType>();
		if(standardPreset != null && standardPreset.getConfiguration() != null){
			try {
			  final Unmarshaller parseXML = JAXBContext.newInstance(CustomReportList.class).createUnmarshaller();			
			  CustomReportList customReportList = (CustomReportList) parseXML
						.unmarshal(new StringReader(standardPreset.getConfiguration()));
			  return customReportList.getCustomReportType();
			}catch(JAXBException e){
				LOG.error(e.getMessage(), e);
			}
		}
		return customReportTypes;
	}
}
