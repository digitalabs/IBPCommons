package org.generationcp.commons.vaadin.util;

import com.vaadin.ui.Tree;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.Window;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.UserProgramStateDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Daniel Villafuerte on 6/2/2015.
 */
@Configurable
public class SaveTreeStateListener implements Window.CloseListener {

    private TreeTable sourceTable;
    private Tree sourceTree;
    private String treeType;

    @Autowired
    private ContextUtil contextUtil;

    @Autowired
    private UserProgramStateDataManager userStateManager;

    public SaveTreeStateListener(TreeTable sourceTable, String treeType) {
        this.sourceTable = sourceTable;
        this.treeType = treeType;
    }


    public SaveTreeStateListener(Tree sourceTree, String treeType) {
        this.sourceTree = sourceTree;
        this.treeType = treeType;
    }

    @Override
    public void windowClose(Window.CloseEvent e) {
        List<String> itemIds = getExpandedIds();

        try {
            userStateManager.saveOrUpdateUserProgramTreeState(contextUtil.getCurrentWorkbenchUserId(), contextUtil.getCurrentProgramUUID(), treeType, itemIds);
        } catch (MiddlewareQueryException e1) {
            e1.printStackTrace();
        }
    }

    protected List<String> getExpandedIds() {
        return sourceTable != null ? getTableItemIds() : sourceTree != null ? getTreeItemIds() : null;
    }

    protected List<String> getTableItemIds()  {
        Collection itemIds = sourceTable.getItemIds();

        List<String> returnVal = new ArrayList<>();
        for (Object itemId : itemIds) {
            if (!sourceTable.isCollapsed(itemId)) {
                returnVal.add(itemId.toString());
            }
        }

        return returnVal;
    }

    protected List<String> getTreeItemIds() {
        Collection itemIds = sourceTree.getItemIds();

        List<String> returnVal = new ArrayList<>();
        for (Object itemId : itemIds) {
            if (sourceTree.isExpanded(itemId)) {
                returnVal.add(itemId.toString());
            }
        }

        return returnVal;
    }

}
