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
package org.generationcp.commons.breedingview.xml.test;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.generationcp.commons.breedingview.xml.Blocks;
import org.generationcp.commons.breedingview.xml.BreedingViewProject;
import org.generationcp.commons.breedingview.xml.BreedingViewProjectType;
import org.generationcp.commons.breedingview.xml.Fieldbook;
import org.generationcp.commons.breedingview.xml.Genotypes;
import org.generationcp.commons.breedingview.xml.Phenotypic;
import org.generationcp.commons.breedingview.xml.Replicates;
import org.generationcp.commons.breedingview.xml.Trait;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class TestBreedingViewProjectXml{
    
    private static BreedingViewProject project;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        //instantiate the objects for the xml
        List<Trait> traits = new ArrayList<Trait>();
        
        Trait trait1 = new Trait();
        trait1.setName("MAT50");
        trait1.setActive(true);
        traits.add(trait1);
        
        Trait trait2 = new Trait();
        trait2.setName("PODWT");
        trait2.setActive(true);
        traits.add(trait2);
        
        Trait trait3 = new Trait();
        trait3.setName("SEEDWT");
        trait3.setActive(true);
        traits.add(trait3);
        
        Genotypes genotypes = new Genotypes();
        genotypes.setName("Genotype");
        
        Blocks blocks = new Blocks();
        blocks.setName("BLOCK");
        
        Replicates replicates = new Replicates();
        replicates.setName("REP");
        
        Fieldbook fieldbook = new Fieldbook();
        fieldbook.setFile("c:/my documents/fieldbook.xls");
        
        Phenotypic phenotypic = new Phenotypic();
        phenotypic.setTraits(traits);
        phenotypic.setGenotypes(genotypes);
        phenotypic.setBlocks(blocks);
        phenotypic.setReplicates(replicates);
        phenotypic.setFieldbook(fieldbook);
        
        BreedingViewProjectType projectType = new BreedingViewProjectType();
        projectType.setDesign("Resolvable incomplete block design");
        projectType.setEnvname("Field Trial");
        projectType.setType("field trial");
        
        project = new BreedingViewProject();
        project.setName("MAT50");
        project.setVersion("1.01");
        project.setType(projectType);
        project.setPhenotypic(phenotypic);
    }

    @Test
    public void testWriting() throws Exception{
        JAXBContext context = JAXBContext.newInstance(BreedingViewProject.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(project, System.out);
    }
    
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

}
