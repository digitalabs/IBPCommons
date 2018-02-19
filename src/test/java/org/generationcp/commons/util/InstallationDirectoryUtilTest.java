package org.generationcp.commons.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.junit.Assert;
import org.junit.Test;

public class InstallationDirectoryUtilTest {
	
	private static final String DUMMY_TOOL_TITLE = "DummyTitle";
	private static final String DUMMY_NATIVE_TOOL_PATH = "C:/Breeding Management System/tools/dummyTool/dummyTool.exe";
	private static final String DUMMY_PROJECT_NAME = "Maize Tutorial Program";
	
	private InstallationDirectoryUtil installationDirUtil = new InstallationDirectoryUtil();
	
	@Test
	public void testCreateWorkspaceDirectoriesForProject() {
		final Project project = ProjectTestDataInitializer.createProject();
		project.setProjectName(DUMMY_PROJECT_NAME);
		this.installationDirUtil.createWorkspaceDirectoriesForProject(project);

		final File projectWorkspaceDirectory =
				new File(InstallationDirectoryUtil.WORKSPACE_DIR + File.separator + project.getCropType().getCropName(), DUMMY_PROJECT_NAME);
		Assert.assertTrue(projectWorkspaceDirectory.exists());
		this.verifyProjectFolderSubdirectories(projectWorkspaceDirectory, true);
		
		this.deleteTestInstallationDirectory();
	}

	// Check the existence of "breeding_view" directory under program with "input" and "output" subdirectories
	private void verifyProjectFolderSubdirectories(final File projectWorkspaceDirectory, final boolean exists) {
		if (exists) {
			Assert.assertEquals(1, projectWorkspaceDirectory.list().length);
		}
		final File breedingViewDirectory = new File(projectWorkspaceDirectory, ToolName.BREEDING_VIEW.getName());
		Assert.assertEquals(exists, breedingViewDirectory.exists());
		final File bvInputDirectory = new File(breedingViewDirectory, InstallationDirectoryUtil.INPUT);
		Assert.assertEquals(exists, bvInputDirectory.exists());
		final File bvOutputDirectory = new File(breedingViewDirectory, InstallationDirectoryUtil.OUTPUT);
		Assert.assertEquals(exists, bvOutputDirectory.exists());
	}
	
	@Test
	public void testCreateWorkspaceDirectoriesForProjectWhenDirectoryAlreadyExists() {
		// Already create project directory. Test method should not continue with creating sub-contents
		final String cropName = "banana";
		final File projectWorkspaceDirectory =
				new File(InstallationDirectoryUtil.WORKSPACE_DIR + File.separator + cropName, DUMMY_PROJECT_NAME);
		projectWorkspaceDirectory.mkdirs();
		
		final Project project = ProjectTestDataInitializer.createProject();
		project.setProjectName(DUMMY_PROJECT_NAME);
		this.installationDirUtil.createWorkspaceDirectoriesForProject(project);
		
		Assert.assertTrue(projectWorkspaceDirectory.exists());
		// Check that "breeding_view" directory and sub-folders will not be created anymore
		this.verifyProjectFolderSubdirectories(projectWorkspaceDirectory, false);
		
		this.deleteTestInstallationDirectory();
	}
	
	@Test
	public void testRenameOldWorkspaceDirectoryWhenOldProgramFolderExists() {
		// Existing directory should be renamed to new program name
		final String oldProjectName = "Old Maize Program";
		final Project project = ProjectTestDataInitializer.createProject();
		final File oldProjectWorkspaceDirectory =
				new File(InstallationDirectoryUtil.WORKSPACE_DIR + File.separator + project.getCropType().getCropName(), oldProjectName);
		oldProjectWorkspaceDirectory.mkdirs();
		Assert.assertTrue(oldProjectWorkspaceDirectory.exists());
		
		project.setProjectName(DUMMY_PROJECT_NAME);
		this.installationDirUtil.renameOldWorkspaceDirectory(oldProjectName, project);
		// Folder for old project name should not exist anymore since it was renamed
		Assert.assertFalse(oldProjectWorkspaceDirectory.exists());
		final File newProjectWorkspaceDirectory = new File(
				InstallationDirectoryUtil.WORKSPACE_DIR + File.separator + project.getCropType().getCropName(), DUMMY_PROJECT_NAME);
		Assert.assertTrue(newProjectWorkspaceDirectory.exists());
		// Check that "breeding_view" directory and sub-folders will not be created anymore
		this.verifyProjectFolderSubdirectories(newProjectWorkspaceDirectory, false);
		
		this.deleteTestInstallationDirectory();
	}
	
	@Test
	public void testRenameOldWorkspaceDirectoryWhenOldProgramFolderDoesNotExist() {
		final String oldProjectName = "Old Maize Program";
		final Project project = ProjectTestDataInitializer.createProject();
		final File oldProjectWorkspaceDirectory =
				new File(InstallationDirectoryUtil.WORKSPACE_DIR + File.separator + project.getCropType().getCropName(), oldProjectName);
		Assert.assertFalse(oldProjectWorkspaceDirectory.exists());
		
		project.setProjectName(DUMMY_PROJECT_NAME);
		this.installationDirUtil.renameOldWorkspaceDirectory(oldProjectName, project);
		// Folder for old project name should still not exist
		Assert.assertFalse(oldProjectWorkspaceDirectory.exists());
		final File newProjectWorkspaceDirectory = new File(
				InstallationDirectoryUtil.WORKSPACE_DIR + File.separator + project.getCropType().getCropName(), DUMMY_PROJECT_NAME);
		// Folder for new project name should now exist
		Assert.assertTrue(newProjectWorkspaceDirectory.exists());
		this.verifyProjectFolderSubdirectories(newProjectWorkspaceDirectory, true);
		
		this.deleteTestInstallationDirectory();
	}
	
	@Test
	public void testResetWorkspaceDirectoryForCropWhenOldProgramFoldersExists() {
		// Create crop workspace directory with existing programs
		final String oldProjectName1 = "Old Maize Program 1";
		final Project project = ProjectTestDataInitializer.createProject();
		final CropType cropType = project.getCropType();
		final File oldProjectWorkspaceDirectory1 =
				new File(InstallationDirectoryUtil.WORKSPACE_DIR + File.separator + cropType.getCropName(), oldProjectName1);
		oldProjectWorkspaceDirectory1.mkdirs();
		Assert.assertTrue(oldProjectWorkspaceDirectory1.exists());
		final String oldProjectName2 = "Old Maize Program 2";
		final File oldProjectWorkspaceDirectory2 =
				new File(InstallationDirectoryUtil.WORKSPACE_DIR + File.separator + cropType.getCropName(), oldProjectName2);
		oldProjectWorkspaceDirectory2.mkdirs();
		Assert.assertTrue(oldProjectWorkspaceDirectory2.exists());
		
		project.setProjectName(DUMMY_PROJECT_NAME + "1");
		final Project project2 = ProjectTestDataInitializer.createProject();
		project2.setProjectName(DUMMY_PROJECT_NAME + "2");
		final Project project3 = ProjectTestDataInitializer.createProject();
		project3.setProjectName(DUMMY_PROJECT_NAME + "3");
		final List<Project> projects = Arrays.asList(project, project2, project3);
		this.installationDirUtil.resetWorkspaceDirectoryForCrop(cropType, projects);
		
		// Folder for old project names should not exist anymore and folders for new programs should have been generated
		Assert.assertFalse(oldProjectWorkspaceDirectory1.exists());
		Assert.assertFalse(oldProjectWorkspaceDirectory2.exists());
		final File cropWorkspaceDirectory = new File(InstallationDirectoryUtil.WORKSPACE_DIR + File.separator + cropType.getCropName());
		Assert.assertTrue(cropWorkspaceDirectory.exists());
		Assert.assertEquals(projects.size(), cropWorkspaceDirectory.list().length);
		for (final Project newProject : projects) {
			final File newProjectWorkspaceDirectory = new File(
					InstallationDirectoryUtil.WORKSPACE_DIR + File.separator + cropType.getCropName(), newProject.getProjectName());
			Assert.assertTrue(newProjectWorkspaceDirectory.exists());
			this.verifyProjectFolderSubdirectories(newProjectWorkspaceDirectory, true);
		}
		
		this.deleteTestInstallationDirectory();
	}
	
	@Test
	public void testResetWorkspaceDirectoryForCropWhenCropDirectoryDoesNotExist() {
		// Create crop workspace directory with existing programs
		final Project project = ProjectTestDataInitializer.createProject();
		final CropType cropType = project.getCropType();
		final File cropWorkspaceDirectory = new File(InstallationDirectoryUtil.WORKSPACE_DIR + File.separator + cropType.getCropName());
		Assert.assertFalse(cropWorkspaceDirectory.exists());
		
		project.setProjectName(DUMMY_PROJECT_NAME + "1");
		final Project project2 = ProjectTestDataInitializer.createProject();
		project2.setProjectName(DUMMY_PROJECT_NAME + "2");
		final Project project3 = ProjectTestDataInitializer.createProject();
		project3.setProjectName(DUMMY_PROJECT_NAME + "3");
		final List<Project> projects = Arrays.asList(project, project2, project3);
		this.installationDirUtil.resetWorkspaceDirectoryForCrop(cropType, projects);
		
		// Folders for crop and new programs should have been generated
		Assert.assertTrue(cropWorkspaceDirectory.exists());
		for (final Project newProject : projects) {
			final File newProjectWorkspaceDirectory = new File(
					InstallationDirectoryUtil.WORKSPACE_DIR + File.separator + cropType.getCropName(), newProject.getProjectName());
			Assert.assertTrue(newProjectWorkspaceDirectory.exists());
			this.verifyProjectFolderSubdirectories(newProjectWorkspaceDirectory, true);
		}
		
		this.deleteTestInstallationDirectory();
	}

	private void deleteTestInstallationDirectory() {
		// Delete test installation directory and its contents as part of cleanup
		final File testInstallationDirectory = new File(InstallationDirectoryUtil.WORKSPACE_DIR);
		this.installationDirUtil.recursiveFileDelete(testInstallationDirectory);
	}
	
	@Test
	public void testGetInputDirectoryForTool() {
		final Project project = ProjectTestDataInitializer.createProject();
		project.setProjectName(DUMMY_PROJECT_NAME);
		try {
			String inputDirectory = this.installationDirUtil.getInputDirectoryForProjectAndTool(project, ToolName.BREEDING_VIEW);
			Assert.assertNotNull(inputDirectory);
			Assert.assertEquals(new File(InstallationDirectoryUtil.WORKSPACE_DIR + File.separator
					+ project.getCropType().getCropName() + File.separator + DUMMY_PROJECT_NAME + File.separator + ToolName.BREEDING_VIEW.getName() + File.separator
					+ InstallationDirectoryUtil.INPUT).getAbsolutePath(), new File(inputDirectory).getAbsolutePath());
			
			this.deleteTestInstallationDirectory();
		} catch (IllegalStateException e) {
			Assert.fail("There should be no exception thrown");
		}
	}
	
	@Test
	public void testGetOutputDirectoryForTool() {
		final Project project = ProjectTestDataInitializer.createProject();
		project.setProjectName(DUMMY_PROJECT_NAME);
		Tool tool = this.constructDummyNativeTool();
		tool.setGroupName("GROUPNAME");
		try {
			String inputDirectory = this.installationDirUtil.getOutputDirectoryForProjectAndTool(project, ToolName.BREEDING_VIEW);
			Assert.assertNotNull(inputDirectory);
			Assert.assertEquals(new File(InstallationDirectoryUtil.WORKSPACE_DIR + File.separator
					+ project.getCropType().getCropName() + File.separator + DUMMY_PROJECT_NAME + ToolName.BREEDING_VIEW.getName() + File.separator
					+ InstallationDirectoryUtil.OUTPUT).getAbsolutePath(), new File(inputDirectory).getAbsolutePath());
			
			this.deleteTestInstallationDirectory();
		} catch (IllegalStateException e) {
			Assert.fail("There should be no exception thrown");
		}
	}
	
	private Tool constructDummyNativeTool() {
		return new Tool(ToolName.GDMS.name(), InstallationDirectoryUtilTest.DUMMY_TOOL_TITLE, InstallationDirectoryUtilTest.DUMMY_NATIVE_TOOL_PATH);
	}

}
