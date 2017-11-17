/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.persistence.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.eclipse.dirigible.database.persistence.IEntityManagerInterceptor;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.junit.Test;

/**
 * The Persistence Manager Interceptor Value Test.
 */
public class PersistenceManagerInterceptorValueTest extends AbstractPersistenceManagerTest {

	/**
	 * Ordered CRUD tests.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	@Test
	public void orderedCrudTests() throws SQLException {
		PersistenceManager<Task> persistenceManager = new PersistenceManager<Task>();
		persistenceManager.setEntityManagerInterceptor(new IEntityManagerInterceptor() {

			@Override
			public Object onGetValueBeforeUpdate(int index, String dataType, Object value) {
				return value;
			}

			@Override
			public Object onSetValueAfterQuery(Object pojo, Field field, Object value) {
				if ("subject".equals(field.getName())) {
					return "INTERCEPTED";
				}
				return value;
			}
		});
		Connection connection = null;
		try {
			connection = getDataSource().getConnection();
			// create table
			createTableForPojo(connection, persistenceManager);
			// check whether it is created successfully
			assertTrue(existsTable(connection, persistenceManager));
			// insert a record in the table for a pojo
			insertPojo(connection, persistenceManager);
			// get the list of all the records
			findAllPojo(connection, persistenceManager);
			// drop the table
			dropTableForPojo(connection, persistenceManager);
		} finally {
			connection.close();
		}
	}

	/**
	 * Creates the table for pojo.
	 *
	 * @param connection
	 *            the connection
	 * @param persistenceManager
	 *            the persistence manager
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void createTableForPojo(Connection connection, PersistenceManager<Task> persistenceManager) throws SQLException {
		persistenceManager.tableCreate(connection, Task.class);
	}

	/**
	 * Exists table.
	 *
	 * @param connection
	 *            the connection
	 * @param persistenceManager
	 *            the persistence manager
	 * @return true, if successful
	 * @throws SQLException
	 *             the SQL exception
	 */
	public boolean existsTable(Connection connection, PersistenceManager<Task> persistenceManager) throws SQLException {
		return persistenceManager.tableExists(connection, Task.class);
	}

	/**
	 * Insert pojo.
	 *
	 * @param connection
	 *            the connection
	 * @param persistenceManager
	 *            the persistence manager
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void insertPojo(Connection connection, PersistenceManager<Task> persistenceManager) throws SQLException {
		Task task = new Task();
		String uuid = UUID.randomUUID().toString();
		task.setId(uuid);
		task.setSubject("Subject 1");
		String uuidInserted = persistenceManager.insert(connection, task).toString();
		assertEquals(uuid, uuidInserted);
	}

	/**
	 * Find all pojo.
	 *
	 * @param connection
	 *            the connection
	 * @param persistenceManager
	 *            the persistence manager
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void findAllPojo(Connection connection, PersistenceManager<Task> persistenceManager) throws SQLException {
		List<Task> list = persistenceManager.findAll(connection, Task.class);

		assertNotNull(list);
		assertFalse(list.isEmpty());
		assertEquals(1, list.size());
		Task task = list.get(0);

		assertEquals("INTERCEPTED", task.getSubject());

		System.out.println(task.getId());

	}

	/**
	 * Drop table for pojo.
	 *
	 * @param connection
	 *            the connection
	 * @param persistenceManager
	 *            the persistence manager
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void dropTableForPojo(Connection connection, PersistenceManager<Task> persistenceManager) throws SQLException {
		persistenceManager.tableDrop(connection, Task.class);
	}

}
