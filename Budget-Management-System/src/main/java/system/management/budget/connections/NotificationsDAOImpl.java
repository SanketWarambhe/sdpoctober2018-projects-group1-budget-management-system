package system.management.budget.connections;
import java.util.*;
import java.sql.*;
import system.management.budget.valueObjects.*;

public class NotificationsDAOImpl {
	static DatabaseConnect db = new DatabaseConnect();
	static Connection con = db.dbConnect();
	
	public static boolean getNotifications (int currentAccountId) {
		DashboardVO trans_row;
		List <DashboardVO> foundTransactions = new ArrayList<DashboardVO>();
		try {
			Statement s = con.createStatement();
			s.execute("Select DISTINCT t.transaction_date, t.transaction_name, t.transaction_amount, t.currency FROM Transactions  t INNER JOIN (SELECT a.transaction_date, a.account_id FROM Transactions a WHERE account_id='"+ currentAccountId +"')AS T2 ON t.account_id = T2.account_id ORDER BY t.transaction_date DESC LIMIT 3 ");
			ResultSet rs = s.getResultSet();
			
				while(rs.next()) 
				{
					trans_row = new DashboardVO(rs.getDate("transaction_date"),rs.getString("transaction_name"),rs.getDouble("transaction_amount"),rs.getString("currency"));
					foundTransactions.add(trans_row);	
				}
			
			return showNotifications(foundTransactions);
			
		}catch(Exception e){
			System.out.println("Error" + e);
		}
		
		return false;	
	}
	
	public static boolean showNotifications(List <DashboardVO> foundTransactions) {
		try {			
			for (int i = 0; i < foundTransactions.size() ; i++ ) {
				System.out.println("----------------------------------------------------------------------------------------------------------");
		        System.out.println("Your recently bought : " + foundTransactions.get(i).getTransaction_name() +" on the:" + foundTransactions.get(i).getTransaction_date() + " and the total amount spent was : " + foundTransactions.get(i).getTransaction_amount() + foundTransactions.get(i).getCurrency());
				System.out.println("----------------------------------------------------------------------------------------------------------");
			}
		}
		catch(Exception e) {
			System.out.println("Error" + e);
		}
	  return false;
		
	}

}
