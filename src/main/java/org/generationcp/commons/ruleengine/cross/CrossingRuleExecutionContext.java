package org.generationcp.commons.ruleengine.cross;

import org.generationcp.commons.parsing.pojo.ImportedCrosses;
import org.generationcp.commons.ruleengine.OrderedRuleExecutionContext;
import org.generationcp.commons.settings.CrossSetting;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.PedigreeDataManager;

import java.util.List;

/**
 * Created by Daniel Villafuerte on 6/6/2015.
 */
public class CrossingRuleExecutionContext extends OrderedRuleExecutionContext{

    private GermplasmDataManager germplasmDataManager;
    private PedigreeDataManager pedigreeDataManager;

    private Integer maleGid;
    private Integer femaleGid;
    private String currentCrossName;
    private CrossSetting crossSetting;

    public CrossingRuleExecutionContext(List<String> executionOrder, CrossSetting crossSetting, Integer maleGid,Integer femaleGid,
                                        GermplasmDataManager germplasmDataManager, PedigreeDataManager pedigreeDataManager) {
        super(executionOrder);
        this.crossSetting = crossSetting;
        this.maleGid = maleGid;
        this.germplasmDataManager = germplasmDataManager;
        this.pedigreeDataManager = pedigreeDataManager;
        this.femaleGid = femaleGid;
        this.currentCrossName = "";
    }

    public CrossingRuleExecutionContext(List<String> executionOrder) {
        super(executionOrder);
    }

    @Override
    public Object getRuleExecutionOutput() {
        return null;
    }

    public Integer getFemaleGid() {
        return femaleGid;
    }

    public void setFemaleGid(Integer femaleGid) {
        this.femaleGid = femaleGid;
    }

    public Integer getMaleGid() {
        return maleGid;
    }

    public void setMaleGid(Integer maleGid) {
        this.maleGid = maleGid;
    }

    public String getCurrentCrossName() {
        return currentCrossName;
    }

    public void setCurrentCrossName(String currentCrossName) {
        this.currentCrossName = currentCrossName;
    }

    public GermplasmDataManager getGermplasmDataManager() {
        return germplasmDataManager;
    }

    public void setGermplasmDataManager(GermplasmDataManager germplasmDataManager) {
        this.germplasmDataManager = germplasmDataManager;
    }

    public CrossSetting getCrossSetting() {
        return crossSetting;
    }

    public void setCrossSetting(CrossSetting crossSetting) {
        this.crossSetting = crossSetting;
    }

    public PedigreeDataManager getPedigreeDataManager() {
        return pedigreeDataManager;
    }

    public void setPedigreeDataManager(PedigreeDataManager pedigreeDataManager) {
        this.pedigreeDataManager = pedigreeDataManager;
    }
}
