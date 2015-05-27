package org.generationcp.commons.vaadin.spring;

import com.google.common.io.CharStreams;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by cyrus on 5/27/15.
 */
public class ZoomFixForRequestWrapper extends HttpServletRequestWrapper {
	public static final String GROUP_SEPARATOR = "\u001d";
	public static final String RECORD_SEPARATOR = "\u001e";
	public static final String UNIT_SEPARATOR = "\u001f";

	private final String body;

	public static String truncateFloatingPointValuesInIntegerFields(String string)
	{
		String regex = "([" + GROUP_SEPARATOR + RECORD_SEPARATOR + "][0-9]*)\\.[0-9]*(" + UNIT_SEPARATOR + "\\w+" + UNIT_SEPARATOR + "\\w+" + UNIT_SEPARATOR + "i)";
		return string.replaceAll(
				regex,
				"$1$2"
		);
	}

	public ZoomFixForRequestWrapper(HttpServletRequest request) throws IOException
	{
		super(request);
		final InputStreamReader inr = new InputStreamReader(request.getInputStream(), request.getCharacterEncoding());
		body = truncateFloatingPointValuesInIntegerFields(CharStreams.toString(inr));
	}

	@Override
	public ServletInputStream getInputStream() throws IOException
	{
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes(getCharacterEncoding()));
		ServletInputStream servletInputStream = new ServletInputStream()
		{
			public int read() throws IOException
			{
				return byteArrayInputStream.read();
			}
		};
		return servletInputStream;
	}

	@Override
	public BufferedReader getReader() throws IOException
	{
		return new BufferedReader(new InputStreamReader(this.getInputStream()));
	}

	public String getBody()
	{
		return this.body;
	}

}
