/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.core.git.command;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.inject.Inject;

import org.eclipse.dirigible.api.v3.auth.UserFacade;
import org.eclipse.dirigible.core.git.GitConnectorFactory;
import org.eclipse.dirigible.core.git.IGitConnector;
import org.eclipse.dirigible.core.git.project.ProjectMetadataManager;
import org.eclipse.dirigible.core.git.utils.GitFileUtils;
import org.eclipse.dirigible.core.git.utils.GitProjectProperties;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Push the changes of a project from the local repository to remote Git repository
 */
public class PushCommand {

	private static final String CHANGES_BRANCH = "changes_branch_"; //$NON-NLS-1$
	private static final String DOT_GIT = ".git"; //$NON-NLS-1$
	private static final String MASTER = "master"; //$NON-NLS-1$

	private static final Logger logger = LoggerFactory.getLogger(PushCommand.class);

	@Inject
	private ProjectMetadataManager projectMetadataManager;

	@Inject
	private GitProjectPropertyVerifier verifier;

	@Inject
	private GitFileUtils gitFileUtils;

	public void execute(final IWorkspace workspace, final IProject[] projects, final String commitMessage, final String username,
			final String password, final String email) {
		if (projects.length == 0) {
			logger.warn("No project is selected for the Push action");
		}

		for (IProject selectedProject : projects) {
			if (verifier.verify(workspace, selectedProject)) {
				logger.debug(String.format("Start pushing project [%s]...", selectedProject.getName()));
				pushProjectToGitRepository(workspace, selectedProject, commitMessage, username, password, email);
				logger.debug(String.format("Push of the project [%s] finished.", selectedProject.getName()));
			} else {
				logger.warn(String.format("Project [%s] is local only. Select a previousely clonned project for Push operation.", selectedProject));
			}
		}

	}

	private void pushProjectToGitRepository(final IWorkspace workspace, final IProject selectedProject, final String commitMessage,
			final String username, final String password, final String email) {

		final String errorMessage = String.format("Error occurred while pushing project [%s]. ", selectedProject.getName());
		GitProjectProperties gitProperties = null;
		try {
			gitProperties = gitFileUtils.getGitPropertiesForProject(workspace, selectedProject);
		} catch (IOException e) {
			logger.error(errorMessage + "Not a git project", e);
			return;
		}
		File tempGitDirectory = null;
		try {
			String gitRepositoryURI = gitProperties.getURL();

			String gitRepositoryBranch = MASTER;
			try {
				projectMetadataManager.ensureProjectMetadata(workspace, selectedProject.getName(), gitRepositoryURI, MASTER);
				gitRepositoryBranch = ProjectMetadataManager.getBranch(selectedProject);
			} catch (IOException e) {
				logger.error(errorMessage, e);
			}

			String repositoryName = gitRepositoryURI.substring(gitRepositoryURI.lastIndexOf("/") + 1, gitRepositoryURI.lastIndexOf(DOT_GIT));
			tempGitDirectory = GitFileUtils.createTempDirectory(GitFileUtils.TEMP_DIRECTORY_PREFIX + repositoryName);

			logger.debug(String.format("Cloning repository %s, with username %s for branch %s in the directory %s ...", gitRepositoryURI, "[nobody]",
					gitRepositoryBranch, tempGitDirectory.getCanonicalPath()));
			GitConnectorFactory.cloneRepository(tempGitDirectory.getCanonicalPath(), gitRepositoryURI, null, null, gitRepositoryBranch);
			logger.debug(String.format("Cloning repository %s finished.", gitRepositoryURI));

			IGitConnector gitConnector = GitConnectorFactory.getRepository(tempGitDirectory.getCanonicalPath());

			String lastSHA = gitProperties.getSHA();

			final String changesBranch = CHANGES_BRANCH + System.currentTimeMillis() + "_" + UserFacade.getName();
			gitConnector.checkout(lastSHA);
			gitConnector.createBranch(changesBranch, lastSHA);
			gitConnector.checkout(changesBranch);

			GitFileUtils.deleteProjectFolderFromDirectory(tempGitDirectory, selectedProject.getName());
			GitFileUtils.copyProjectToDirectory(selectedProject, tempGitDirectory);

			gitConnector.add(selectedProject.getName());
			gitConnector.commit(commitMessage, username, email, true);
			gitConnector.pull();
			int numberOfConflictingFiles = gitConnector.status().getConflicting().size();
			if (numberOfConflictingFiles == 0) {
				gitConnector.checkout(gitRepositoryBranch);
				gitConnector.rebase(changesBranch);
				gitConnector.push(username, password);

				String dirigibleUser = UserFacade.getName();

				gitFileUtils.deleteRepositoryProject(selectedProject);

				String workspacePath = GitProjectProperties.generateWorkspacePath(workspace, dirigibleUser);

				String newLastSHA = gitConnector.getLastSHAForBranch(gitRepositoryBranch);
				gitProperties.setSHA(newLastSHA);

				gitFileUtils.importProject(tempGitDirectory, workspacePath, dirigibleUser, workspace.getName(), gitProperties);

				logger.info(String.format("Project has been pushed to remote repository.", selectedProject.getName()));
			} else {
				gitConnector.hardReset();
				gitConnector.push(username, password);
				String statusLineMessage = String.format("Project has %d conflicting file(s).", numberOfConflictingFiles);
				logger.warn(statusLineMessage);
				String message = String.format(
						"Project has %d conflicting file(s). Pushed to remote branch [%s]. Please merge to [%s] and then continue working on project.",
						numberOfConflictingFiles, changesBranch, gitRepositoryBranch);
			}
		} catch (IOException e) {
			logger.error(errorMessage, e);
		} catch (InvalidRemoteException e) {
			logger.error(errorMessage, e);
		} catch (TransportException e) {
			logger.error(errorMessage, e);
			Throwable rootCause = e.getCause();
			if (rootCause != null) {
				rootCause = rootCause.getCause();
				if (rootCause instanceof UnknownHostException) {
					logger.error("Please check if proxy settings are set properly");
				} else {
					logger.error("Doublecheck the correctness of the [Username] and/or [Password] or [Git Repository URI]");
				}
			}
		} catch (GitAPIException e) {
			logger.error(errorMessage, e);
		} finally {
			GitFileUtils.deleteDirectory(tempGitDirectory);
		}
	}
}
