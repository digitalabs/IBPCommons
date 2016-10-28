package org.generationcp.commons.vaadin.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.manager.api.UserProgramStateDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Tree;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.Window;

/**
 * Created by Daniel Villafuerte on 6/2/2015.
 */
@Configurable
public class SaveTreeStateListener implements Window.CloseListener {

	private TreeTable sourceTable;
    private Tree sourceTree;
    private String treeType;
    private String rootItem;

    @Autowired
    private ContextUtil contextUtil;

    @Autowired
    private UserProgramStateDataManager userStateManager;

    public SaveTreeStateListener(TreeTable sourceTable, String treeType, String rootItem) {
        this.sourceTable = sourceTable;
        this.treeType = treeType;
        this.rootItem = rootItem;
    }


    public SaveTreeStateListener(Tree sourceTree, String treeType, String rootItem) {
        this.sourceTree = sourceTree;
        this.treeType = treeType;
        this.rootItem = rootItem;
    }

    @Override
    public void windowClose(Window.CloseEvent e) {
        List<String> itemIds = getExpandedIds();
        this.userStateManager.saveOrUpdateUserProgramTreeState(contextUtil.getCurrentUserLocalId(), contextUtil.getCurrentProgramUUID(), treeType, itemIds);
    }

    protected List<String> getExpandedIds() {
        return sourceTable != null ? getTableItemIds(new TreeTableFunction()) : sourceTree != null ? getTableItemIds(new TreeFunction()) : null;
    }

    protected List<String> getTableItemIds(TableFunction function)  {

        List<String> returnVal = new ArrayList<>();

        recurseSaveOpenNodes(rootItem, function, returnVal);
        return returnVal;
    }

    public void recurseSaveOpenNodes(Object itemId, TableFunction tableFunction, List<String> openNodes) {
        if (!tableFunction.isExpanded(itemId)) {
            return;
        }

        openNodes.add(itemId.toString());
        Collection children = tableFunction.getChildren(itemId);
        if (children != null && !children.isEmpty()) {
            for (Object child : children) {
                recurseSaveOpenNodes(child, tableFunction, openNodes);
            }
        }
    }

    public interface TableFunction  {
        public boolean isExpanded(Object itemId);
        public Collection getChildren(Object itemId);
    }

    class TreeTableFunction implements  TableFunction {
        @Override
        public Collection getChildren(Object itemId) {
            return sourceTable.getChildren(itemId);
        }

        @Override
        public boolean isExpanded(Object itemId) {
            return !sourceTable.isCollapsed(itemId);
        }
    }

    class TreeFunction implements TableFunction {
        @Override
        public Collection getChildren(Object itemId) {
            return sourceTree.getChildren(itemId);
        }

        @Override
        public boolean isExpanded(Object itemId) {
            return sourceTree.isExpanded(itemId);
        }
    }

}
