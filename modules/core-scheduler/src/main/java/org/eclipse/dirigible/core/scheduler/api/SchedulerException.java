/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.scheduler.api;

public class SchedulerException extends Exception {

	private static final long serialVersionUID = 5800180600419241248L;

	public SchedulerException() {
		super();
	}

	public SchedulerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SchedulerException(String message, Throwable cause) {
		super(message, cause);
	}

	public SchedulerException(String message) {
		super(message);
	}

	public SchedulerException(Throwable cause) {
		super(cause);
	}

}
