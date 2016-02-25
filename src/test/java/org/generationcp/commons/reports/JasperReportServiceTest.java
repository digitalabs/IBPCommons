package org.generationcp.commons.reports;

import junit.framework.Assert;
import org.generationcp.commons.constant.ToolEnum;
import org.generationcp.commons.constant.ToolSection;
import org.generationcp.commons.pojo.CustomReportType;
import org.generationcp.commons.reports.service.JasperReportService;
import org.generationcp.commons.reports.service.impl.JasperReportServiceImpl;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.presets.StandardPreset;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class JasperReportServiceTest {

    public static final String TEST_CROP_NAME = "Test";
    public static final Long TEST_TOOL_ID = 1L;
    public static final String TEST_PROFILE = "cimmyt";
    @Mock
    private CrossExpansionProperties crossExpansionProperties;

    @Mock
    private ContextUtil contextUtil;

    @Mock
    private WorkbenchDataManager workbenchDataManager;

    @InjectMocks
    private JasperReportService unitUnderTest = new JasperReportServiceImpl();

    @Test
    public void testGetCustomReportTypes() throws MiddlewareQueryException {
        Mockito.when(crossExpansionProperties.getProfile()).thenReturn(TEST_PROFILE);

        List<StandardPreset> standardPresets = new ArrayList<StandardPreset>();
        StandardPreset preset = new StandardPreset();
        preset.setConfiguration("<reports><profile>cimmyt</profile><report><code>WLBL05</code><name>labels without design, wheat</name></report><report><code>WLBL21</code><name>labels with design, wheat</name></report></reports>");
        standardPresets.add(preset);

        Project project = new Project();
        CropType cropType = new CropType();
        cropType.setCropName(TEST_CROP_NAME);
        project.setCropType(cropType);
        Mockito.when(contextUtil.getProjectInContext()).thenReturn(project);

        Tool fbTool = new Tool();
        fbTool.setToolId(TEST_TOOL_ID);

        Mockito.when(this.workbenchDataManager.getToolWithName(ToolEnum.FIELDBOOK_WEB.getToolName())).thenReturn(fbTool);

        Mockito.when(workbenchDataManager.getStandardPresetFromCropAndTool(TEST_CROP_NAME.toLowerCase(), TEST_TOOL_ID.intValue(), ToolSection.FB_TRIAL_MGR_CUSTOM_REPORT.name())).thenReturn(standardPresets);


        List<CustomReportType> presets = unitUnderTest.getCustomReportTypes(ToolSection.FB_TRIAL_MGR_CUSTOM_REPORT.name(), ToolEnum.FIELDBOOK_WEB.getToolName());
        Assert.assertEquals("Should return 2 presets since there is a study", 2, presets.size());
    }
}