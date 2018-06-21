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
		final List<Trait> traits = new ArrayList<Trait>();

		final Trait trait1 = new Trait();
		trait1.setName("MAT50");
		trait1.setActive(true);
		traits.add(trait1);

		final Trait trait2 = new Trait();
		trait2.setName("PODWT");
		trait2.setActive(true);
		traits.add(trait2);

		final Trait trait3 = new Trait();
		trait3.setName("SEEDWT");
		trait3.setActive(true);
		traits.add(trait3);

		final EnvironmentLabel envLabel = new EnvironmentLabel();
		envLabel.setName("HN96b");
		envLabel.setTrial("HN96b");
		envLabel.setSubset(true);

		final Environment environments = new Environment();
		environments.setName("E");
		environments.setLabel(envLabel);

		final Genotypes genotypes = new Genotypes();
		genotypes.setName("Genotype");

		final Blocks blocks = new Blocks();
		blocks.setName("BLOCK");

		final Replicates replicates = new Replicates();
		replicates.setName("REP");

		final Rows rows = new Rows();
		rows.setName("ROWS");

		final Columns columns = new Columns();
		columns.setName("COLUMNS");

		final Data fieldbook = new Data();
		fieldbook.setFieldBookFile("c:/my documents/fieldbook.xls");

		final Phenotypic phenotypic = new Phenotypic();
		phenotypic.setTraits(traits);
		phenotypic.setEnvironments(environments);
		phenotypic.setGenotypes(genotypes);
		phenotypic.setBlocks(blocks);
		phenotypic.setReplicates(replicates);
		phenotypic.setRows(rows);
		phenotypic.setColumns(columns);
		phenotypic.setFieldbook(fieldbook);

		final BreedingViewProjectType projectType = new BreedingViewProjectType();
		projectType.setDesign("Resolvable incomplete block design");
		projectType.setEnvname("Field Study");
		projectType.setType("field study");

		BreedingViewProjectXmlTest.project = new BreedingViewProject();
		BreedingViewProjectXmlTest.project.setName("MAT50");
		BreedingViewProjectXmlTest.project.setVersion("1.01");
		BreedingViewProjectXmlTest.project.setType(projectType);
		BreedingViewProjectXmlTest.project.setPhenotypic(phenotypic);
	}

	@Test
	public void testWriting() throws Exception {
		final JAXBContext context = JAXBContext.newInstance(BreedingViewProject.class);
		final Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.marshal(BreedingViewProjectXmlTest.project, System.out);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

}
