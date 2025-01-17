/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.base.spring;

import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * The Class BeanProvider.
 */
@Component
public class BeanProvider implements ApplicationContextAware {

    /** The context. */
    private static ApplicationContext context;

    /**
     * Sets the application context.
     *
     * @param argApplicationContext the new application context
     * @throws BeansException the beans exception
     */
    @Override
    public void setApplicationContext(ApplicationContext argApplicationContext) throws BeansException {
        context = argApplicationContext;
    }

    /**
     * Gets the bean.
     *
     * @param <T> the generic type
     * @param clazz the clazz
     * @return the bean
     */
    public static <T> T getBean(Class<T> clazz) {
        if (!isInitialzed()) {
            throw new IllegalStateException("Spring is not initialized yet.");
        }
        return context.getBean(clazz);
    }

    /**
     * Gets the tenant context.
     *
     * @return the tenant context
     */
    public static TenantContext getTenantContext() {
        return getBean(TenantContext.class);
    }

    /**
     * Checks if is initialzed.
     *
     * @return true, if is initialzed
     */
    public static boolean isInitialzed() {
        return context != null;
    }

}
