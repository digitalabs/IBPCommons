package org.generationcp.commons.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectUtil<T> {

	public void serializeObject(T o, String filename){
		
	    // save the object to file
	    FileOutputStream fos = null;
	    ObjectOutputStream out = null;
	    try {
	      fos = new FileOutputStream(filename);
	      out = new ObjectOutputStream(fos);
	      out.writeObject(o);

	      out.close();
	    } catch (Exception ex) {
	      ex.printStackTrace();
	    }
		
	}
	
	public T deserializeFromFile(String filename){
		
		 FileInputStream fis = null;
		 ObjectInputStream in = null;
		 T object = null;
		 
		    try {
		      fis = new FileInputStream(filename);
		      in = new ObjectInputStream(fis);
		      object = (T) in.readObject();
		      in.close();
		    } catch (Exception ex) {
		      ex.printStackTrace();
		    }
		    
		return object;
	}

}
