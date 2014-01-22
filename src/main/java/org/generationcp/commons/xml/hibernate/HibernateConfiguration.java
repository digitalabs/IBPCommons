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
package org.generationcp.commons.xml.hibernate;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "hibernate-configuration")
public class HibernateConfiguration {

    private SessionFactory sessionFactory;
    
    @XmlElement(name="session-factory")
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    public boolean updateConnectionUrl(String connectionUrl) {
        return sessionFactory == null ? false : sessionFactory.updateConnectionUrl(connectionUrl);
    }
    
    public boolean updateUsername(String username) {
        return sessionFactory == null ? false : sessionFactory.updateUsername(username);
    }
    
    public boolean updatePassword(String password) {
        return sessionFactory == null ? false : sessionFactory.updatePassword(password);
    }
}
