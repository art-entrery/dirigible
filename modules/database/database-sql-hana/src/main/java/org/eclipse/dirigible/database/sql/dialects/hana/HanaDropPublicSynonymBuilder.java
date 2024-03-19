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
package org.eclipse.dirigible.database.sql.dialects.hana;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.synonym.DropSynonymBuilder;

/**
 * The Class HanaDropPublicSynonymBuilder.
 */
public class HanaDropPublicSynonymBuilder extends DropSynonymBuilder {

    /**
     * Instantiates a new hana drop public synonym builder.
     *
     * @param dialect the dialect
     * @param synonym the synonym
     */
    public HanaDropPublicSynonymBuilder(ISqlDialect dialect, String synonym) {
        super(dialect, synonym);
    }

    /**
     * Generate synonym.
     *
     * @param sql the sql
     */
    @Override
    protected void generateSynonym(StringBuilder sql) {
        String synonymName = (isCaseSensitive()) ? encapsulate(this.getSynonym(), true) : this.getSynonym();
        sql.append(SPACE)
           .append(KEYWORD_PUBLIC)
           .append(SPACE)
           .append(KEYWORD_SYNONYM)
           .append(SPACE)
           .append(synonymName);
    }

}
