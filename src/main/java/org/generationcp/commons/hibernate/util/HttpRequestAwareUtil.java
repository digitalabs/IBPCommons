package org.generationcp.commons.hibernate.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.generationcp.commons.hibernate.HttpRequestAware;
import org.springframework.context.ApplicationContext;

public abstract class HttpRequestAwareUtil {
    
    public static void onRequestStart(ApplicationContext applicationContext, HttpServletRequest request, HttpServletResponse response) {
        // notify all HttpRequestAware beans that an HTTP request has been started
        Map<String, HttpRequestAware> beans = applicationContext.getBeansOfType(HttpRequestAware.class, false, false);
        for (String beanName : beans.keySet()) {
            HttpRequestAware bean = applicationContext.getBean(beanName, HttpRequestAware.class);
            bean.onRequestStarted(request, response);
        }
    }
    
    public static void onRequestEnd(ApplicationContext applicationContext, HttpServletRequest request, HttpServletResponse response) {
        // notify all HttpRequestAware beans that an HTTP request has been ended
        Map<String, HttpRequestAware> beans = applicationContext.getBeansOfType(HttpRequestAware.class, false, false);
        for (String beanName : beans.keySet()) {
            HttpRequestAware bean = applicationContext.getBean(beanName, HttpRequestAware.class);
            bean.onRequestEnded(request, response);
        }
    }
}
