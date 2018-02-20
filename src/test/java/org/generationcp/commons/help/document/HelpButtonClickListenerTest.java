
package org.generationcp.commons.help.document;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.generationcp.commons.tomcat.util.TomcatUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.exceptions.verification.TooLittleActualInvocations;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

public class HelpButtonClickListenerTest {

	private static final String INSTALLATION_PATH = "C:\\BMS 4";

	private HelpButtonClickListener listener;
	private HelpModule link;

	@Mock
	private TomcatUtil tomcatUtil;
	
	@Mock
	private Properties helpProperties;

	@Mock
	private Properties workbenchProperties;

	@Mock
	private ClickEvent event;
	@Mock
	private Component component;
	@Mock
	private Window window;
	@Mock
	private Component parentComponent;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.link = HelpModule.MANAGE_LIST;
		this.listener = Mockito.spy(new HelpButtonClickListener(this.link));
		this.listener.setHelpProperties(this.helpProperties);
		this.listener.setTomcatUtil(this.tomcatUtil);

		Mockito.when(this.event.getComponent()).thenReturn(this.component);
		Mockito.when(this.component.getWindow()).thenReturn(this.window);
		Mockito.when(this.component.getParent()).thenReturn(this.parentComponent);
		Mockito.when(this.parentComponent.getWindow()).thenReturn(this.window);
		Mockito.when(this.workbenchProperties.getProperty("workbench.version", "")).thenReturn("4.0.0");
	}

	@Test
	public void testButtonClick_ForOnlineLink() throws MalformedURLException {
		String onlineLink = "www.google.com";
		Mockito.doReturn(onlineLink).when(this.helpProperties).getProperty(this.link.getPropertyName());
		Mockito.when(this.listener.getOnlineLink(this.link)).thenReturn(onlineLink);

		URL onlineURL = new URL(HelpDocumentUtil.getOnLineLink(onlineLink));
		ExternalResource tutorialLink = new ExternalResource(onlineURL);
		Mockito.doReturn(tutorialLink).when(this.listener).getTutorialLink(this.link, this.window, true);

		this.listener.buttonClick(this.event);

		try {
			Mockito.verify(this.window, Mockito.times(1)).open(tutorialLink, " _BLANK");
		} catch (TooLittleActualInvocations e) {
			Assert.fail("Expecting to call the method that opens an online link.");
		}
	}

	@Test
	public void testButtonClick_ForOfflineLinkWithEmptyLink() throws MalformedURLException {
		String offlineLink = "www.offline.com";
		URL offlineURL = new URL(HelpDocumentUtil.getOnLineLink(offlineLink));

		Mockito.doReturn(offlineLink).when(this.helpProperties).getProperty(this.link.getPropertyName());
		Mockito.when(this.listener.getOfflineLink(this.link, offlineURL)).thenReturn(offlineLink);

		ExternalResource tutorialLink = new ExternalResource(offlineURL);
		Mockito.doReturn(tutorialLink).when(this.listener).getTutorialLink(this.link, this.window, false);

		this.listener.buttonClick(this.event);

		try {
			Mockito.verify(this.parentComponent, Mockito.times(1)).getWindow();
		} catch (TooLittleActualInvocations e) {
			Assert.fail("Expecting to call the method that opens an help pop up but didn't.");
		}
	}

	@Test
	public void testButtonClick_ForOfflineLinkWithNonEmptyLink() throws MalformedURLException, MiddlewareQueryException {
		String offlineLink = "www.offline.com";
		URL offlineURL = new URL(HelpDocumentUtil.getOnLineLink(offlineLink));

		Mockito.doReturn(offlineLink).when(this.helpProperties).getProperty(this.link.getPropertyName());
		Mockito.when(this.listener.getOfflineLink(this.link, offlineURL)).thenReturn(offlineLink);

		ExternalResource tutorialLink = new ExternalResource(offlineURL);
		Mockito.doReturn(tutorialLink).when(this.listener).getTutorialLink(this.link, this.window, false);

		this.listener.buttonClick(this.event);

		if (new File(INSTALLATION_PATH.concat("\\Documents")).exists()) {
			try {
				Mockito.verify(this.window, Mockito.times(1)).open(tutorialLink, " _BLANK");
			} catch (TooLittleActualInvocations e) {
				Assert.fail("Expecting to call the method that opens an online link.");
			}
		} else {
			try {
				Mockito.verify(this.parentComponent, Mockito.times(1)).getWindow();
			} catch (TooLittleActualInvocations e) {
				Assert.fail("Expecting to call the method that opens an help pop up but didn't.");
			}
		}

	}

	@Test
	public void testGetTutorialLink_ForOnlineLink() {
		Window currentWindow = Mockito.mock(Window.class);
		String onlineLink = "www.onlinelink.com";
		Mockito.doReturn(onlineLink).when(this.helpProperties).getProperty(this.link.getPropertyName());
		Mockito.when(this.listener.getOnlineLink(this.link)).thenReturn(onlineLink);

		ExternalResource actualResult = this.listener.getTutorialLink(this.link, currentWindow, true);

		Assert.assertNotNull("Expecting to return an ExternalResource link but didn't.", actualResult);
		Assert.assertTrue("Expecting the online link is contained inside the generated tutorial links.",
				actualResult.getURL().contains(onlineLink));
		Assert.assertTrue("Expecting the online ", actualResult.getURL().startsWith("https://"));
	}

	@Test
	public void testGetTutorialLink_ForOfflineLink() throws MalformedURLException {
		URL currentURL = new URL("http:\\localhost:8080\\www.google.com");
		Window currentWindow = Mockito.mock(Window.class);
		Mockito.when(currentWindow.getURL()).thenReturn(currentURL);

		String offlineLink = "www.offlinelink.com";
		Mockito.doReturn(offlineLink).when(this.helpProperties).getProperty(this.link.getPropertyName());

		Mockito.when(this.listener.getOfflineLink(this.link, currentURL)).thenReturn(offlineLink);

		ExternalResource actualResult = this.listener.getTutorialLink(this.link, currentWindow, false);

		Assert.assertNotNull("Expecting to return an ExternalResource link but didn't.", actualResult);
		Assert.assertTrue("Expecting the offline link is containeds inside the generated tutorial links.",
				actualResult.getURL().contains(offlineLink));
		Assert.assertTrue("Expecting the offline ", actualResult.getURL().startsWith("http://"));
	}
}
