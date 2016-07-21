
package org.generationcp.commons.vaadin.ui;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.UserDefinedField;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Select;
import com.vaadin.ui.Table;

public class VaadinComponentsUtil {

	public static enum VaadinComponentFieldType {
		CAPTION, TABLE_CONTENT
	}

	private VaadinComponentsUtil() {
		// private constructor for utility class
	}

	public static boolean findComponent(final Component root, final VaadinComponentFieldType type, final String text,
			final String propertyName) {
		final Deque<Component> stack = new ArrayDeque<Component>();
		stack.push(root);
		while (!stack.isEmpty()) {
			final Component c = stack.pop();
			if (c instanceof ComponentContainer) {
				VaadinComponentsUtil.pushComponents(stack, ((ComponentContainer) c).getComponentIterator());
			} else if (c instanceof Table && type == VaadinComponentFieldType.TABLE_CONTENT
					&& VaadinComponentsUtil.findItemInTable((Table) c, text, propertyName)) {
				return true;
			} else if (type == VaadinComponentFieldType.CAPTION && VaadinComponentsUtil.findCaption(c, text)) {
				return true;
			}
		}
		return false;
	}

	private static void pushComponents(final Deque<Component> stack, final Iterator<Component> componentIterator) {
		for (final Iterator<Component> i = componentIterator; i.hasNext();) {
			stack.add(i.next());
		}
	}

	private static boolean findCaption(final Component c, final String text) {
		if (c.getCaption() != null && c.getCaption().equals(text)) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	private static boolean findItemInTable(final Table table, final String text, final String propertyName) {
		if (table.getContainerDataSource() == null || table.getItemIds() == null) {
			return false;
		}
		for (final Object itemId : table.getItemIds()) {
			if (table.getItem(itemId) instanceof BeanItem) {
				final BeanItem beanItem = (BeanItem) table.getItem(itemId);
				final Property property = beanItem.getItemProperty(propertyName);
				if (property != null && text.equals(property.toString())) {
					return true;
				}
			}
		}
		return false;
	}

	public static void populateSelectType(final Select selectType, final List<UserDefinedField> listTypes) throws MiddlewareQueryException {
		for (final UserDefinedField listType : listTypes) {
			final String typeCode = listType.getFcode();
			selectType.addItem(typeCode);
			selectType.setItemCaption(typeCode, listType.getFname());
			// set "GERMPLASMLISTS" as the default value
			if ("LST".equals(typeCode)) {
				selectType.setValue(typeCode);
			}
		}
	}
}
