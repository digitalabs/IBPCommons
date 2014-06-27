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
package org.generationcp.commons.hibernate.util;

import junit.framework.Assert;

import org.generationcp.commons.hibernate.DefaultManagerFactoryProvider;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionPerThreadProvider;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.hibernate.HibernateUtil;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.WorkbenchDataManagerImpl;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.InputStream;
import java.util.Properties;

@Ignore
public class TestManagerFactoryProvider{

    private static WorkbenchDataManager manager;
    private static HibernateUtil hibernateUtil;

    @BeforeClass
    public static void setUp() throws Exception {
        Properties props = new Properties();
        InputStream propStream = ClassLoader.getSystemResourceAsStream("test.properties");
        props.load(propStream);
        propStream.close();

        String hostName = props.getProperty("workbench.host");
        String portNumber = props.getProperty("workbench.port");
        String dbName = props.getProperty("workbench.dbname");
        String dbUserName = props.getProperty("workbench.username");
        String dbPassword = props.getProperty("workbench.password");

        hibernateUtil = new HibernateUtil(hostName, portNumber, dbName, dbUserName, dbPassword);
        HibernateSessionProvider sessionProvider = 
                new HibernateSessionPerThreadProvider(hibernateUtil.getSessionFactory());
        manager = new WorkbenchDataManagerImpl(sessionProvider);
    }

    @Test
    public void testManagerFactoryByCropType() {
        ManagerFactoryProvider provider = new DefaultManagerFactoryProvider();
        ManagerFactory factory = null;

        try {
            factory = provider.getManagerFactoryForCropType(
                    manager.getCropTypeByName(CropType.CASSAVA));
            Assert.assertNotNull(factory);

            factory = provider.getManagerFactoryForCropType(
                    manager.getCropTypeByName(CropType.CHICKPEA));
            Assert.assertNotNull(factory);

            Project project = new Project();
            project.setProjectId(1L);
            project.setCropType(manager.getCropTypeByName(CropType.CHICKPEA));
            factory = provider.getManagerFactoryForProject(project);
            Assert.assertNotNull(factory);

        } catch (MiddlewareQueryException e) {
            System.out.println("Error in testManagerFactoryByCropType(): " + e.getMessage());
            e.printStackTrace();
        }
        provider.close();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        hibernateUtil.shutdown();
    }
}
