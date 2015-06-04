
package org.generationcp.commons.vaadin.ui;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Table;

public class VaadinComponentsUtil {

	public static enum VaadinComponentFieldType {
		CAPTION, TABLE_CONTENT
	}

	private VaadinComponentsUtil() {
		// private constructor for utility class
	}

	public static boolean findComponent(Component root, VaadinComponentFieldType type, String text, String propertyName) {
		Deque<Component> stack = new ArrayDeque<Component>();
		stack.push(root);
		while (!stack.isEmpty()) {
			Component c = stack.pop();
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

	private static void pushComponents(Deque<Component> stack, Iterator<Component> componentIterator) {
		for (Iterator<Component> i = componentIterator; i.hasNext();) {
			stack.add(i.next());
		}
	}

	private static boolean findCaption(Component c, String text) {
		if (c.getCaption() != null && c.getCaption().equals(text)) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	private static boolean findItemInTable(Table table, String text, String propertyName) {
		if (table.getContainerDataSource() == null || table.getItemIds() == null) {
			return false;
		}
		for (Object itemId : table.getItemIds()) {
			if (table.getItem(itemId) instanceof BeanItem) {
				BeanItem beanItem = (BeanItem) table.getItem(itemId);
				Property property = beanItem.getItemProperty(propertyName);
				if (property != null && text.equals(property.toString())) {
					return true;
				}
			}
		}
		return false;
	}
}
