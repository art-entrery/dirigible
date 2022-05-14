/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
exports.getEditor = function () {
	return {
		"id": "roles",
		"name": "Roles",
		"factory": "frame",
		"region": "center-top",
		"label": "Roles",
		"link": "../ide-security/editors/roles/editor.html",
		"contentTypes": ["application/json+roles"]
	};
};