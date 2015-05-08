package org.generationcp.commons.constant;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.generationcp.commons.util.StringUtil;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.OntologyDataManager;

public enum ColumnLabels {


	 AVAILABLE_INVENTORY(TermId.AVAILABLE_INVENTORY, "AVAIL INV")
    , BREEDING_METHOD_NAME(TermId.BREEDING_METHOD_NAME, "METHOD NAME")
    , BREEDING_METHOD_ABBREVIATION(TermId.BREEDING_METHOD_ABBREVIATION, "METHOD ABBREV")
    , BREEDING_METHOD_NUMBER(TermId.BREEDING_METHOD_NUMBER, "METHOD NUMBER")
    , BREEDING_METHOD_GROUP(TermId.BREEDING_METHOD_GROUP, "METHOD GROUP")
    , CROSS_FEMALE_GID(TermId.CROSS_FEMALE_GID, "CROSS-FEMALE GID")
    , CROSS_FEMALE_PREFERRED_NAME(TermId.CROSS_FEMALE_PREFERRED_NAME, "CROSS-FEMALE PREFERRED NAME")
    , CROSS_MALE_GID(TermId.CROSS_MALE_GID, "CROSS-MALE GID")
    , CROSS_MALE_PREFERRED_NAME(TermId.CROSS_MALE_PREFERRED_NAME, "CROSS-MALE PREFERRED NAME")
    , DESIGNATION(TermId.DESIG, "DESIGNATION")
    , ENTRY_CODE(TermId.ENTRY_CODE, "ENTRY CODE")
    , ENTRY_ID(TermId.ENTRY_NO, "ENTRY_ID")
    , FEMALE_PARENT(TermId.FEMALE_PARENT, "Female Parent")
    , FGID(TermId.FGID, "FGID")
    , GERMPLASM_DATE(TermId.GERMPLASM_DATE, "GERMPLASM DATE")
    , GERMPLASM_LOCATION(TermId.GERMPLASM_LOCATION, "LOCATIONS")
    , GID(TermId.GID, "GID")
    , MALE_PARENT(TermId.MALE_PARENT, "Male Parent")
    , MGID(TermId.MGID, "MGID")
    , PARENTAGE(TermId.CROSS, "PARENTAGE")
    , PREFERRED_ID(TermId.PREFERRED_ID, "PREFERRED ID")
    , PREFERRED_NAME(TermId.PREFERRED_NAME, "PREFERRED NAME")
    , SEED_RESERVATION(TermId.SEED_RESERVATION, "SEED RES")
    , SEED_SOURCE(TermId.SEED_SOURCE, "SEED SOURCE")
    , STOCKID(TermId.STOCKID, "STOCKID")
    , TAG(null, "TAG")
    
    //INVENTORY TABLE COLUMNS
    , AMOUNT(TermId.AMOUNT_INVENTORY, "AMOUNT")
    , COMMENT(TermId.COMMENT_INVENTORY, "COMMENT")
    , LOT_ID(TermId.LOT_ID_INVENTORY,"LOT_ID")
    , LOT_LOCATION(TermId.LOT_LOCATION_INVENTORY,"LOCATION")
    , NEWLY_RESERVED(TermId.NEW_RESERVED_INVENTORY, "NEW_RES")
    , RESERVED(TermId.RESERVED_INVENTORY, "RES")
    , SCALE(TermId.SCALE_INVENTORY, "SCALE")
    , TOTAL(TermId.TOTAL_INVENTORY, "TOTAL")
    , UNITS(TermId.UNITS_INVENTORY, "UNITS")
    
    //GERMPLASM CHECK
    , ENTRY_TYPE(TermId.ENTRY_TYPE, "CHECK TYPE");
	 
	private TermId termId;
	private String name;
	private static final Map<String, ColumnLabels> lookup = new HashMap<>();
	
	static {
	      for(ColumnLabels cl : EnumSet.allOf(ColumnLabels.class))
	           lookup.put(cl.getName(), cl);
	 }
	
	private ColumnLabels(TermId termId, String name){
			this.name = name;
			this.termId = termId;
	}
	
	public String getName(){
		return this.name;
	}
	
	public TermId getTermId(){
		return this.termId;
		
	}
	
	public String getTermNameFromOntology(OntologyDataManager ontologyDataManager){
    	try{
    		if (termId != null){
    			Term term = ontologyDataManager.getTermById(termId.getId());
        		if (term != null && !StringUtil.isEmpty(term.getName())){
        			return term.getName();
        		}else{
        			return this.name;
        		}
    		}else{
    			return this.name;
    		}
    		
    	} catch(MiddlewareQueryException ex){
            return this.name;
        }
    }
	
	public static ColumnLabels get(String name) { 
        return lookup.get(name); 
    } 

}
