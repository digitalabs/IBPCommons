package org.generationcp.commons.spring;

import java.util.Locale;

public interface SimpleResourceBundleMessageSourceListener {
    
    void localeChanged(Locale oldLocale, Locale newLocale);
}
