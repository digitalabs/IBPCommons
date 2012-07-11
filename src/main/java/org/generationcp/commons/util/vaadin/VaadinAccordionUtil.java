/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * @author Kevin L. Manansala
 * 
 * This software is licensed for use under the terms of the 
 * GNU General Public License (http://bit.ly/8Ztv8M) and the 
 * provisions of Part F of the Generation Challenge Programme 
 * Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 **************************************************************/
package org.generationcp.commons.util.vaadin;

import com.vaadin.ui.Accordion;
import com.vaadin.ui.TabSheet.Tab;


public class VaadinAccordionUtil{
    
    public static boolean isAccordionDatasetExist(Accordion accordion, String accordionCaption) {
        int countAccordionTab = accordion.getComponentCount();

        for (int i = 0; i < countAccordionTab; i++) {
            Tab tab = accordion.getTab(i);
            if (tab.getCaption().equals(accordionCaption)) {
                return true;
            }
        }

        return false;
    }

}
