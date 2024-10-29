import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
		// Oracle : create or replace table 없음.
		// Oracle : create table if not exists 없음.
		query = "create sequence idSEQ";
		try {
			stmt.executeUpdate(query);
		} catch(SQLException e) {
			System.out.println("시퀀스생성오류 : "+e.getMessage());
		}
		query = "create table 임시주문 (" +
				"주문번호 NUMBER(10)," +
				"주문자 VARCHAR2(20)," +
				"구매상품 VARCHAR2(20)," +
				"기록시작일 DATE," +     //오라클은 Time type이 없음
				"주문일시 TIMESTAMP )";
		try {
			stmt.executeUpdate(query);
			stmt.close();
		} catch(SQLException e) {
			System.out.println("테이블생성오류 : "+e.getMessage());
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
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while( rs.next( )) {
				System.out.print("\t" + rs.getInt(1) );
				System.out.print("\t" + rs.getString(2) );
				System.out.print("\t" + rs.getString(3) );
				System.out.print("\t" + rs.getDate(4).toLocalDate() );
				//System.out.print("\t" + rs.getTime(5).toLocalDateTime().format(formatter) );
				System.out.print("\t" + rs.getTimestamp(5).toLocalDateTime().format(formatter) + "\n" );
				System.out.print("\t요일 : " + rs.getTimestamp(5).toLocalDateTime().getDayOfWeek());  //월요일이 1
				int year = rs.getDate(4).toLocalDate().getYear();
				System.out.print("\t시작년도 : " + year + "\n");
			}
			stmt.close();
		} catch(SQLException e) {
			System.out.println("데이터검색오류 : "+e.getMessage());
		}
	}
}
