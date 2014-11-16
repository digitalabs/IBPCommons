package org.generationcp.commons.exceptions;

public class GermplasmListExporterException extends Exception{

    private static final long serialVersionUID = -1639961960516233500L;

    public GermplasmListExporterException(){
    	super("Error with exporting germplasm list.");
    }
    
    public GermplasmListExporterException(String message) {
        super(message);
    }
    
    public GermplasmListExporterException(String message, Throwable cause) {
        super(message, cause);
    }
}

