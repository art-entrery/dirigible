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
package org.eclipse.dirigible.core.git.command;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import org.eclipse.dirigible.core.git.GitConnectorException;
import org.eclipse.dirigible.core.git.GitConnectorFactory;
import org.eclipse.dirigible.core.git.IGitConnector;
import org.eclipse.dirigible.core.git.project.ProjectPropertiesVerifier;
import org.eclipse.dirigible.core.git.utils.GitFileUtils;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Push the changes of a project from the local repository to remote Git repository.
 */
public class CommitCommand {

	private static final Logger logger = LoggerFactory.getLogger(CommitCommand.class);

	/** The verifier. */
	private ProjectPropertiesVerifier verifier = new ProjectPropertiesVerifier();

	/**
	 * Execute the Commit command.
	 *
	 * @param workspace
	 *            the workspace
	 * @param repositories
	 *            the projects
	 * @param commitMessage
	 *            the commit message
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @param email
	 *            the email
	 * @param branch
	 *            the branch
	 * @param add
	 *            the add
	 * @throws GitConnectorException 
	 */
	public void execute(final IWorkspace workspace, List<String> repositories, final String commitMessage, final String username,
			final String password, final String email, final String branch, boolean add) throws GitConnectorException {
		if (repositories.size() == 0) {
			logger.warn("No repository is selected for the Commit action");
		}
		for (String repositoryName : repositories) {
			if (verifier.verify(workspace.getName(), repositoryName)) {
				logger.debug(String.format("Start committing repository [%s]...", repositoryName));
				commitProjectToGitRepository(workspace, repositoryName, commitMessage, username, password, email, branch, add);
				logger.debug(String.format("Commit of the repository [%s] finished.", repositoryName));
			} else {
				logger.warn(String.format("Project [%s] is local only. Select a previously clonned project for Commit operation.", repositoryName));
			}
		}

	}

	/**
	 * Commit project to git repository by executing several low level Git commands.
	 *
	 * @param workspace
	 *            the workspace
	 * @param repositoryName
	 *            the selected project
	 * @param commitMessage
	 *            the commit message
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @param email
	 *            the email
	 * @throws GitConnectorException 
	 */
	private void commitProjectToGitRepository(final IWorkspace workspace, String repositoryName, final String commitMessage,
			final String username, final String password, final String email, final String branch,  boolean add) throws GitConnectorException {

		String errorMessage = String.format("Error occurred while committing repository [%s]. ", repositoryName);

		try {
			File gitDirectory = GitFileUtils.getGitDirectoryByRepositoryName(workspace.getName(), repositoryName);
			IGitConnector gitConnector = GitConnectorFactory.getConnector(gitDirectory.getCanonicalPath());

			if (add) {
				List<String> projects = GitFileUtils.getGitRepositoryProjects(workspace.getName(), repositoryName);
				for (String projectName : projects) {
					gitConnector.add(projectName);
				}
			}
			gitConnector.commit(commitMessage, username, email, add);
		} catch (IOException | GitAPIException | GitConnectorException e) {
			Throwable rootCause = e.getCause();
			if (rootCause != null) {
				rootCause = rootCause.getCause();
				if (rootCause instanceof UnknownHostException) {
					errorMessage += " Please check your network, or if proxy settings are set properly";
				} else {
					errorMessage += " Doublecheck the correctness of the [Username] and/or [Password] or [Git Repository URI]";
				}
			} else {
				errorMessage += " " + e.getMessage();
			}
			logger.error(errorMessage);
			throw new GitConnectorException(errorMessage, e);
		}
	}
}