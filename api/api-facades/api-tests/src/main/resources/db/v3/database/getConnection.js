/* eslint-env node, dirigible */

var database = require('db/v3/database');

database.update("CREATE TABLE T (A INT, B VARCHAR(10))");
database.update("INSERT INTO T VALUES (1, 'ABC')");
database.update("INSERT INTO T VALUES (2, 'DEF')");

var sql = "SELECT * FROM T WHERE A = ?";

var value;
var connection = database.getConnection();
try {
	var statement = connection.prepareStatement(sql);
	try {
		statement.setInt(1, 2);
		var resultset = statement.executeQuery();
		try {
			while (resultset.next()) {
				var value = resultset.getString('B');
				console.log('B: ' + value);
			}
		} finally {
			resultset.close();
		}
	} finally {
		statement.close();
	}
} finally {
	connection.close();
}

database.update("DROP TABLE T");

value == 'DEF';