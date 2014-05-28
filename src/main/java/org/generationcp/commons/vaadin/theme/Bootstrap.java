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
package org.generationcp.commons.vaadin.theme;

import com.vaadin.ui.themes.Reindeer;

/**
 * Created with IntelliJ IDEA.
 * User: cyrus
 * Date: 11/16/13
 * Time: 2:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class Bootstrap {
    public enum Typography {
        H1, H2, H3, H4, H5, H6, BODYCOPY, LEAD, SMALL, TEXT_LEFT, TEXT_CENTER, TEXT_RIGHT
        , TEXT_MUTED, TEXT_PRIMARY, TEXT_WARNING, TEXT_DANGER, TEXT_SUCCESS, TEXT_INFO;

        public String styleName() {
            return this.toString().toLowerCase().replaceAll("_","-");
        }

    }

    public enum Tables {
        STRIPED, BORDERED, HOVER, CONDENSED;

        public String styleName() {
            return this.toString().toLowerCase().replaceAll("_","-");
        }
    }

    public enum Forms {
        FORM;

        public String styleName() {
            return this.toString().toLowerCase().replaceAll("_","-");
        }
    }

    public enum WINDOW {
        CONFIRM(Reindeer.WINDOW_LIGHT + " confirm");

        String styleName;

        WINDOW(String value) {
            this.styleName = value;
        }

        public String styleName() {
            return this.styleName.toLowerCase().replaceAll("_","-");
        }
    }

    public enum Buttons {
        DEFAULT, PRIMARY, INFO, SUCCESS, WARNING, DANGER, LINK, BORDERED;

        public String styleName() {
            return this.toString().toLowerCase().replaceAll("_","-");
        }
    }
}
