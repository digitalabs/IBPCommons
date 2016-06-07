/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 * @author Kevin L. Manansala
 *
 *         This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of
 *         Part F of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 **************************************************************/

package org.generationcp.commons.breedingview.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class BreedingViewProjectXmlTest {

	private static BreedingViewProject project;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// instantiate the objects for the xml
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

		EnvironmentLabel envLabel = new EnvironmentLabel();
		envLabel.setName("HN96b");
		envLabel.setTrial("HN96b");
		envLabel.setSubset(true);

		Environment environments = new Environment();
		environments.setName("E");
		environments.setLabel(envLabel);

		Genotypes genotypes = new Genotypes();
		genotypes.setName("Genotype");

		Blocks blocks = new Blocks();
		blocks.setName("BLOCK");

		Replicates replicates = new Replicates();
		replicates.setName("REP");

		Rows rows = new Rows();
		rows.setName("ROWS");

		Columns columns = new Columns();
		columns.setName("COLUMNS");

		Data fieldbook = new Data();
		fieldbook.setFieldBookFile("c:/my documents/fieldbook.xls");

		Phenotypic phenotypic = new Phenotypic();
		phenotypic.setTraits(traits);
		phenotypic.setEnvironments(environments);
		phenotypic.setGenotypes(genotypes);
		phenotypic.setBlocks(blocks);
		phenotypic.setReplicates(replicates);
		phenotypic.setRows(rows);
		phenotypic.setColumns(columns);
		phenotypic.setFieldbook(fieldbook);

		BreedingViewProjectType projectType = new BreedingViewProjectType();
		projectType.setDesign("Resolvable incomplete block design");
		projectType.setEnvname("Field Trial");
		projectType.setType("field trial");

		BreedingViewProjectXmlTest.project = new BreedingViewProject();
		BreedingViewProjectXmlTest.project.setName("MAT50");
		BreedingViewProjectXmlTest.project.setVersion("1.01");
		BreedingViewProjectXmlTest.project.setType(projectType);
		BreedingViewProjectXmlTest.project.setPhenotypic(phenotypic);
	}

	@Test
	public void testWriting() throws Exception {
		JAXBContext context = JAXBContext.newInstance(BreedingViewProject.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.marshal(BreedingViewProjectXmlTest.project, System.out);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

}
