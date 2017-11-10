/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.engine.api.script;

import static java.text.MessageFormat.format;

import java.util.Map;

import org.eclipse.dirigible.commons.api.scripting.ScriptingException;

public class ScriptEngineExecutorsManager {

	public static Object executeServiceModule(String engineType, String module, Map<Object, Object> executionContext) throws ScriptingException {
		IScriptEngineExecutor scriptEngineExecutor = ScriptEngineExecutorFactory.getScriptEngineExecutor(engineType);
		if (scriptEngineExecutor != null) {
			return scriptEngineExecutor.executeServiceModule(module, executionContext);
		}

		throw new ScriptingException(
				format("Script Executor of Type [{0}] does not exist, hence the Module [{1}] cannot be processed", engineType, module));
	}

	public static Object executeServiceCode(String engineType, String code, Map<Object, Object> executionContext) throws ScriptingException {
		IScriptEngineExecutor scriptEngineExecutor = ScriptEngineExecutorFactory.getScriptEngineExecutor(engineType);
		if (scriptEngineExecutor != null) {
			return scriptEngineExecutor.executeServiceCode(code, executionContext);
		}

		throw new ScriptingException(
				format("Script Executor of Type [{0}] does not exist, hence the code [{1}] cannot be processed", engineType, code));
	}

}
