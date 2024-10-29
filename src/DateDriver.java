import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//DateDriver.java
class DBConn {
	public Connection connect() {
		Connection con = null;
		String url = "jdbc:oracle:thin:@localhost:1521:XE";
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection(url, "hmart", "1234");
		} catch (ClassNotFoundException e) {
			System.out.println("a");
		}
		catch (SQLException e) {
			System.out.println("b");
		}
		return con;
	}
}
public class DateDriver {
	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		Connection con = null;
		DBConn db = new DBConn();
		con = db.connect();
		if( con == null ) return;
		DateType dateType = new DateType(con);
		dateType.create();
		dateType.insert("apple","p06");
		dateType.insert("banana", "p01");
		dateType.select();
		con.close();
	}
}
