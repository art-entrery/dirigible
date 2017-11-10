/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.workspace.json;

import java.util.ArrayList;
import java.util.List;

public class WorkspaceDescriptor {

	private String name;

	private String path;

	private String type = "workspace";

	private List<ProjectDescriptor> projects = new ArrayList<ProjectDescriptor>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<ProjectDescriptor> getProjects() {
		return projects;
	}

	public void set(List<ProjectDescriptor> projects) {
		this.projects = projects;
	}

}
