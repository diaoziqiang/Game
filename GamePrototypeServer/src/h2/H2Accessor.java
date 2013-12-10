package h2;

/**
 * @author DZQ
 * H2 accessor.
 */

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import org.h2.jdbcx.JdbcConnectionPool;

public class H2Accessor {

	public static int rowNum = 100; // row >= 1
	public static int columnNum = 100; // column >= 2
	public static String tableName = new String("TEST");

	private static JdbcConnectionPool cp = null;// must be static

	//instantiated by game server to generate a connection pool
	public H2Accessor(JdbcConnectionPool cp) {
		H2Accessor.cp = cp;
	}
	
	//instantiated by ValidRequestHandler, when JdbcConnectionPool already exists
	public H2Accessor() {
		// TODO Auto-generated constructor stub
	}

	// create a table
	public boolean createTable() throws Exception {
		if (columnNum < 2) {
			System.err.println("Please do not set columnNum less than two!");
			return false;
		} else {
			Connection conn = cp.getConnection();
			Statement stmt = conn.createStatement();
			try {
				String order = "DROP TABLE IF EXISTS " + tableName
						+ "; CREATE TABLE " + tableName
						+ "(ID INT PRIMARY KEY, NAME VARCHAR(255)";
				for (int i = 1; i < columnNum - 1; i++) {
					order = order + ", COLUMN" + i + " VARCHAR(255)";
				}
				order = order + ");";

				// System.out.println(order);

				stmt.execute(order);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			} finally {// no matter whether there is an Exception or not,
						// statement in finally will be execute once before
						// return
				stmt.close();
				conn.close();
			}
		}

	}

	// insert simulation values into the table
	public boolean insertValues() throws Exception {

		Connection conn = cp.getConnection();
		Statement stmt = conn.createStatement();

		try {
			for (int i = 1; i <= rowNum; i++) {

				String order = "";

				order = order + "INSERT INTO " + tableName + " VALUES (";

				for (int j = 1; j <= columnNum; j++) {
					if (j == 1) {
						order = order + i + "";
					} else {
						order = order + ",'" + (100000 + i) + "'";
					}

				}
				order = order + ");";

				// System.out.println(order);

				stmt.execute(order);
			}
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			stmt.close();
			conn.close();
		}
	}

	// insert one row into H2 with a specified row id  --used by start
	public boolean addSpecifiedRow(String rowId) throws Exception {
		Connection conn = cp.getConnection();
		Statement stmt = conn.createStatement();

		// check if this row already exists
		if (selectOneRow(rowId, false)) {
			System.out.println("The row " + rowId + " already exists in the table!");
			return true;
		} else {
			try {

				String order = "";

				order = order + "INSERT INTO " + tableName + " VALUES (";

				/* if rowId is not number, here will be a problem!!!!!! */
				for (int i = 1; i <= columnNum; i++) {
					if (i == 1) {
						order = order + rowId + "";
					} else {
						order = order + ",'" + rowId + "'";
					}

				}
				order = order + ");";
				
//				System.out.println(order);
				
				stmt.execute(order);

				System.out.println("A new row with ID " + rowId
						+ " is inserted.");
				return true;

			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("There is an exception while inserting " + rowId + " into the table.");
//				addSpecifiedRow(rowId);
				return false;
			} finally {
				stmt.close();
				conn.close();
			}
		}
	}

	// update one column in a row  --used by sell
	public boolean updateColumn(String rowId, String columnName, String newValue)
			throws Exception {
		Connection conn = cp.getConnection();
		Statement stmt = conn.createStatement();

		// make sure that this row exists.
		selectOneColumn(rowId, columnName, false);

		try {
			String queryOrder = "";
			queryOrder = "UPDATE " + tableName + " SET " + columnName + " = '"
					+ newValue + "' WHERE ID = " + rowId + ";";

			// System.out.println(queryOrder);

			if (stmt.executeUpdate(queryOrder) == 0) {
				System.out.println("There is no such a row." + "rowId = "
						+ rowId);
				return false;
			} else {
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			stmt.close();
			conn.close();
		}
	}

	// select one row
	public boolean selectOneRow(String rowId, boolean printResult)
			throws Exception {

		return select("SELECT * FROM " + tableName + " WHERE ID = " + rowId
				+ ";", printResult);

	}

	// select all from table
	public boolean selectAll() throws Exception {

		return select("SELECT * FROM " + tableName + ";", true);

	}

	// select one column of a row --used by "get"
	public boolean selectOneColumn(String rowId, String rowName,
			boolean printResult) throws Exception {
		// if there is no such a row, insert values, and then query again.
		if (select("SELECT " + rowName + " FROM " + tableName + " WHERE ID = "
				+ rowId + ";", printResult)) {
			return true;
		} else {
			System.out.println("The row with ID " + rowId
					+ " does not exist, so I have to insert it firstly.");
			new H2Accessor(cp).addSpecifiedRow(rowId);
			return selectOneColumn(rowId, rowName, printResult);
		}

	}

	// select several columns of all row
	public boolean selectMultiColumns(String rowNames) throws Exception {

		return select("SELECT " + rowNames + " FROM " + tableName + " ;", true);

	}

	// select value from table
	public boolean select(String queryOrder, boolean printResult)
			throws Exception {

		Connection conn = cp.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(queryOrder);


			// check if the row exists
			boolean hasResult = true;
			hasResult = rs.next();
			if (!hasResult) {
				return false;
			} else if (printResult) {

				// print the name of each column
				ResultSetMetaData md = rs.getMetaData();
				for (int j = 1; j <= md.getColumnCount(); j++) {
					System.out.print("'" + md.getColumnLabel(j) + "' ");
				}

				System.out.println();
				
				// print the result
				do {
					for (int i = 1; i <= md.getColumnCount(); i++) {
						System.out.print(rs.getNString(i) + " ");
					}
					System.out.println();
				} while (rs.next());
				return true;
			} else {
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			rs.close();
			stmt.close();
			conn.close();
		}
	}
}
