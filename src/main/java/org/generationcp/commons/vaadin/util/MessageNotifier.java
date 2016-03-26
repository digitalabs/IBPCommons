/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.commons.vaadin.util;

import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

/**
 * The Class MessageNotifier. This is used in showing UI message notification in the application.
 *
 * @author Joyce Avestro
 *
 */
public class MessageNotifier {

	private static final int POSITION = Notification.POSITION_TOP_RIGHT;
	private static final int LONG_DELEY = 6000; // Lets the notification window stay until the user clicks it

	private static final int DEFAULT_MESSAGE_DELAY = 3000;

	/**
	 * Show warning.
	 *
	 * @param window the window
	 * @param caption the caption
	 * @param description the description
	 */
	public static void showWarning(Window window, String caption, String description) {
		Notification notification = new Notification(caption, Window.Notification.TYPE_WARNING_MESSAGE);
		notification.setDescription(description == null ? "" : "</br>" + description);
		notification.setDelayMsec(MessageNotifier.LONG_DELEY);
		notification.setPosition(MessageNotifier.POSITION);
		window.showNotification(notification);
	}

	@Deprecated
	public static void showWarning(Window window, String caption, String description, int position) {
		MessageNotifier.showWarning(window, caption, description);
	}

	/**
	 * Show message.
	 *
	 * @param window the window
	 * @param caption the caption
	 * @param description the description
	 */
	public static void showMessage(Window window, String caption, String description) {
		MessageNotifier.showMessage(window, caption, description, MessageNotifier.DEFAULT_MESSAGE_DELAY);
	}

	@Deprecated
	public static void showMessage(Window window, String caption, String description, int delayMs, int position) {
		MessageNotifier.showMessage(window, caption, description, delayMs);
	}

	/**
	 * Show message.
	 *
	 * @param window the window
	 * @param caption the caption
	 * @param description the description
	 */
	public static void showMessage(Window window, String caption, String description, int delayMs) {
		Notification notification = new Notification(caption, Notification.TYPE_HUMANIZED_MESSAGE);
		notification.setDescription(description == null ? "" : "</br>" + description);
		notification.setDelayMsec(delayMs);
		notification.setPosition(MessageNotifier.POSITION);
		window.showNotification(notification);
	}

	/**
	 * Show error.
	 *
	 * @param window the window
	 * @param caption the caption
	 * @param description the description
	 */
	public static void showError(Window window, String caption, String description) {
		Notification notification = new Notification(caption, description, Notification.TYPE_WARNING_MESSAGE);
		notification.setDescription(description == null ? "" : "</br>" + description);
		notification.setDelayMsec(MessageNotifier.LONG_DELEY);
		notification.setStyleName("error");
		notification.setPosition(MessageNotifier.POSITION);
		window.showNotification(notification);
	}

	/**
	 * Show error for required fields.
	 *
	 * @param window the window
	 * @param description the description
	 */
	public static void showRequiredFieldError(Window window, String description) {
		MessageNotifier.showError(window, "Invalid Input", description);
	}

	@Deprecated
	public static void showError(Window window, String caption, String description, int position) {
		MessageNotifier.showError(window, caption, description);
	}

}
