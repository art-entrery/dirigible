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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.junit.Test;

public class PersistenceManagerEnumTest extends AbstractPersistenceManagerTest {

	@Test
	public void orderedCrudTests() throws SQLException {
		PersistenceManager<Process> persistenceManager = new PersistenceManager<Process>();
		Connection connection = getDataSrouce().getConnection();
		try {
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

	public void createTableForPojo(Connection connection, PersistenceManager<Process> persistenceManager) throws SQLException {
		persistenceManager.tableCreate(connection, Process.class);
	}

	public boolean existsTable(Connection connection, PersistenceManager<Process> persistenceManager) throws SQLException {
		return persistenceManager.tableExists(connection, Process.class);
	}

	public void insertPojo(Connection connection, PersistenceManager<Process> persistenceManager) throws SQLException {
		Process process = new Process();
		process.setName("Process1");
		process.setTypeAsInt(Process.ProcessType.STARTED);
		process.setTypeAsString(Process.ProcessType.STARTED);
		persistenceManager.insert(connection, process);

		PreparedStatement preparedStatement = connection.prepareStatement("select * from PROCESSES");
		try {
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				assertTrue(Process.ProcessType.STARTED.name().equals(resultSet.getString("PROCESS_TYPE_AS_STRING")));
				assertTrue(Process.ProcessType.STARTED.ordinal() == resultSet.getInt("PROCESS_TYPE_AS_INT"));
			}
		} finally {
			preparedStatement.close();
		}
	}

	public void findAllPojo(Connection connection, PersistenceManager<Process> persistenceManager) throws SQLException {
		List<Process> list = persistenceManager.findAll(connection, Process.class);

		assertNotNull(list);
		assertFalse(list.isEmpty());
		assertEquals(1, list.size());
		Process pojo = list.get(0);
		Process order = pojo;
		assertEquals(Process.ProcessType.STARTED, order.getTypeAsInt());
		assertEquals(Process.ProcessType.STARTED, order.getTypeAsString());

		System.out.println(order.getId());

	}

	public void dropTableForPojo(Connection connection, PersistenceManager<Process> persistenceManager) throws SQLException {
		persistenceManager.tableDrop(connection, Process.class);
	}

}
