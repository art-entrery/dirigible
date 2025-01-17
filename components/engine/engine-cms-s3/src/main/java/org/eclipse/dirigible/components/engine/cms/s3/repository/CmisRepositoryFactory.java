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
package org.eclipse.dirigible.components.engine.cms.s3.repository;

import org.eclipse.dirigible.components.api.s3.S3Facade;

import java.io.IOException;

/**
 * A factory for creating CmisRepository objects.
 */
public class CmisRepositoryFactory {

    /**
     * Creates a new CmisRepository object.
     *
     * @param root the root
     * @return the cmis repository
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static CmisRepository createCmisRepository(String root) throws IOException {
        return new CmisS3Repository(root);
    }

}
