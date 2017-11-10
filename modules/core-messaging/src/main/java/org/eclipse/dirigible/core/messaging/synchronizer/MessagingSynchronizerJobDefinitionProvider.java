/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.messaging.synchronizer;

import org.eclipse.dirigible.core.scheduler.api.IJobDefinitionProvider;
import org.eclipse.dirigible.core.scheduler.service.definition.JobDefinition;

public class MessagingSynchronizerJobDefinitionProvider implements IJobDefinitionProvider {

	@Override
	public JobDefinition getJobDefinition() {
		JobDefinition jobDefinition = new JobDefinition();
		jobDefinition.setName("dirigible-internal-messaging-synchronizer-job");
		jobDefinition.setGroup("dirigible-internal");
		jobDefinition.setClazz(MessagingSynchronizerJob.class.getCanonicalName());
		jobDefinition.setDescription("Messaging Synchronizer Job");
		jobDefinition.setExpression("0/20 * * * * ?");
		jobDefinition.setSingleton(true);
		return jobDefinition;
	}

}
