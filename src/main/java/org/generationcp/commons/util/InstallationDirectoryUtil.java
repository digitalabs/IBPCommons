package org.generationcp.commons.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ToolName;

public class InstallationDirectoryUtil {
	
	public static final String WORKSPACE_DIR = "workspace";
	public static final String INPUT = "input";
	public static final String OUTPUT = "output";
	
	public void createWorkspaceDirectoriesForProject(final Project project) {

		// create the directory for the project
		final File projectDir = this.getFileForWorkspaceProjectDirectory(project);
		if (projectDir.exists()) {
			return;
		}
		projectDir.mkdirs();

		// create the directory only for breeding_view tool
		final List<String> toolList = Collections.singletonList(ToolName.BREEDING_VIEW.getName());
		for (final String toolName : toolList) {
			final File toolDir = new File(projectDir, toolName);
			toolDir.mkdirs();

			// create the input and output directories
			new File(toolDir, InstallationDirectoryUtil.INPUT).mkdirs();
			new File(toolDir, InstallationDirectoryUtil.OUTPUT).mkdirs();
		}
	}

	private File getFileForWorkspaceProjectDirectory(final Project project) {
		return this.getFileForWorkspaceProjectDirectory(project, project.getProjectName());
	}
	
	private File getFileForWorkspaceProjectDirectory(final Project project, final String projectName) {
		return new File(this.buildWorkspaceCropDirectoryPath(project), projectName);
	}

	private String buildWorkspaceCropDirectoryPath(final Project project) {
		final String cropName = project.getCropType().getCropName();
		return this.buildWorkspaceCropDirectoryPath(cropName);
	}
	
	private String buildWorkspaceCropDirectoryPath(final String cropName) {
		return InstallationDirectoryUtil.WORKSPACE_DIR + File.separator + cropName;
	}

	public void renameOldWorkspaceDirectory(final String oldProjectName, final Project project) {
		final File oldDir = this.getFileForWorkspaceProjectDirectory(project, oldProjectName);

		// Rename old project name folder if found, otherwise create folder for latest project name
		if (oldDir.exists()) {
			oldDir.renameTo(this.getFileForWorkspaceProjectDirectory(project));
		} else {
			this.createWorkspaceDirectoriesForProject(project);
		}
	}
	
	public void resetWorkspaceDirectoryForCrop(final CropType cropType, final List<Project> projects) {
		final String cropName = cropType.getCropName();
		final File cropDirectory = new File(this.buildWorkspaceCropDirectoryPath(cropName));
		// Delete all contents of given crop directory and recreate folders for each of the project names
		if (cropDirectory.exists()) {
			this.recursiveFileDelete(cropDirectory);
		}
		for (final Project project : projects) {
			this.createWorkspaceDirectoriesForProject(project);
		}
	}

	public String getInputDirectoryForProjectAndTool(final Project project, final ToolName tool) {
		final File toolDir = this.getToolDirectoryForProject(project, tool);
		return new File(toolDir, InstallationDirectoryUtil.INPUT).getAbsolutePath();
	}
	
	public String getOutputDirectoryForProjectAndTool(final Project project, final ToolName tool) {
		final File toolDir = this.getToolDirectoryForProject(project, tool);
		return new File(toolDir, InstallationDirectoryUtil.OUTPUT).getAbsolutePath();
	}
	
	public String getTempFileInOutputDirectoryForProjectAndTool(final String fileName, final String extension, final Project project,
			final ToolName tool) throws IOException {
		final File outputDir = new File(this.getOutputDirectoryForProjectAndTool(project, tool));
		// Create temporary file under output directory of project and tool
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
		// FIXME - perform checking that fileName is > 3 characters, else temp file will throw "Prefix too short" error
		return File.createTempFile(fileName, extension).getAbsolutePath();
	}
	
	public String getFileInTemporaryDirectoryForProjectAndTool(final String fileName, final Project project, final ToolName tool)
			throws IOException {
		final File toolDirectory = this.getToolDirectoryForProject(project, tool);
		if (!toolDirectory.exists()) {
			toolDirectory.mkdirs();
		}
		final Path tempDirectory = Files.createTempDirectory(FileSystems.getDefault().getPath(toolDirectory.getPath()), InstallationDirectoryUtil.OUTPUT);
		return new File(tempDirectory.toFile(), fileName).getAbsolutePath();
	}
	
	private File getToolDirectoryForProject(final Project project, final ToolName tool) {
		final File projectDir = this.getFileForWorkspaceProjectDirectory(project);
		return new File(projectDir, tool.getName());
	}
	
	void recursiveFileDelete(File file) {
        //to end the recursive loop
        if (!file.exists()){
        	return;
        }
         
        //if directory, go inside and call recursively
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                //call recursively
                recursiveFileDelete(f);
            }
        }
        //call delete to delete files and empty directory
        file.delete();
    }


}
