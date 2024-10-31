import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// DateType.java
public class DateType {
	Connection con = null;
	Statement stmt = null;
	PreparedStatement pstmt = null;

	public DateType(Connection con) {
		this.con = con;
	}
	public void create() {
		String query = "drop table 임시주문";
		try {
			stmt = con.createStatement();
			stmt.executeUpdate(query);
		} catch(SQLException e) {
			System.out.println("테이블삭제오류 : "+e.getMessage());
		}
		query = "drop sequence idSEQ";
		try {
			stmt.executeUpdate(query);
		} catch(SQLException e) {
			System.out.println("시퀀스삭제오류 : "+e.getMessage());
		}
		query = "create sequence idSEQ";
		try {
			stmt.executeUpdate(query);
		} catch(SQLException e) {
			System.out.println("시퀀스생성오류 : "+e.getMessage());
		}
		try {
			DatabaseMetaData dbMetaData = con.getMetaData();
			String tableName = "임시주문";
			ResultSet tables = dbMetaData.getTables(null, null, tableName.toUpperCase(), new String[] {"TABLE"});
				if (tables.next()) {		System.out.println("Table " + tableName + " exists."); }
				else {
					query = "create table 임시주문 (" +
							"주문번호 NUMBER(10)," +
							"주문자 VARCHAR2(20)," +
							"구매상품 VARCHAR2(20)," +
							"기록시작일 DATE," +     //오라클은 Time type이 없음
							"주문일시 TIMESTAMP )";
					stmt.executeUpdate(query);
					stmt.close();
				}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public void insert(String 주문자,  String 주문상품) {
		long milli = System.currentTimeMillis();
		java.util.Date date1 = new java.util.Date(milli);
		//System.out.println(date1); // sql.Date는 util.Date의 sub
		LocalDate 기록시작일 = LocalDate.of(2019, 8, 20);
		LocalDateTime 주문일시 = LocalDateTime.now();

		String query = "insert into 임시주문 values(idSeq.NEXTVAL,?,?,?,?)";
		try {
			pstmt = con.prepareStatement(query);
			pstmt.setString(1,  주문자);
			pstmt.setString(2,  주문상품);
			//pstmt.setDate(3,  new java.sql.Date(milli));  //현재날짜
			pstmt.setDate(3,  java.sql.Date.valueOf(기록시작일));
			// 특정 날짜 입력하고 싶을 때 valueOf( ) : static function
			pstmt.setTimestamp(4, java.sql.Timestamp.valueOf(주문일시));
			pstmt.executeUpdate();
			pstmt.close();
		} catch(SQLException e) {
			System.out.println("데이터삽입오류 : "+e.getMessage());
		}
	}
	public void select() {
		String query = "SELECT * from 임시주문";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		//DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while( rs.next( )) {
				System.out.print("\t" + rs.getInt(1) );
				System.out.print("\t" + rs.getString(2) );
				System.out.print("\t" + rs.getString(3) );
				System.out.print("\t" + rs.getDate(4).toLocalDate() );
				//System.out.print("\t" + rs.getTime(5).toLocalDateTime().format(formatter) );
				System.out.print("\t" + rs.getTimestamp(5).toLocalDateTime().format(formatter) );
				System.out.println("\t요일 : " + rs.getTimestamp(5).toLocalDateTime().getDayOfWeek());

				LocalDate 오늘 = LocalDate.now();
				System.out.print("\t\t" + 오늘);
				System.out.print("\t년도 : 윤년여부 ==> " + 오늘.getYear() + " : "  + 오늘.isLeapYear());

				LocalDate 백일뒤 = 오늘.plusDays(100);
				System.out.println("\t백일뒤 : " + 백일뒤+ "\n");
			}
			stmt.close();
		} catch(SQLException e) {
			System.out.println("데이터검색오류 : "+e.getMessage());
		}
	}
}
