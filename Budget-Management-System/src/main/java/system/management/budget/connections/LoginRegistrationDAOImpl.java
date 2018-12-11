package system.management.budget.connections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import system.management.budget.BudgetPortal;
import system.management.budget.valueObjects.UserRegistrationVO;


public class LoginRegistrationDAOImpl {
	
	Connection con = null;
	
	DatabaseConnect jdbcObj = new DatabaseConnect();
	
	DataSource dataSource = null;
	
	public int createConnection(String username,String pass){
		
    	try {
    		
    		dataSource = jdbcObj.setUpPool();
    		
    		con = dataSource.getConnection();
    		
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(jdbcObj.accSel);
			while(rs.next()) {
				String name = rs.getString("email");
				if (username.equals(name)) {
					String password = rs.getString("password");
					if(pass.equals(password)) 
					{	
						int currentAccountId = rs.getInt("account_id");
						return currentAccountId;
						
					}
						
				}
				
			}
			stmt.close();
			
    	} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(con!=null) {
					con.close();
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return 0;
    }
	
	public boolean registrationDbConnection(UserRegistrationVO userDetails){

		try {
			dataSource = jdbcObj.setUpPool();
			
			con = dataSource.getConnection();
			
			//PreparedStatement stmt = con.prepareStatement(jdbcObj.accountAdd);
			PreparedStatement stmt = con.prepareStatement(jdbcObj.accountAdd);
			stmt.setString(1, userDetails.getEmailID());
			stmt.setString(2, userDetails.getFirstName());
			stmt.setString(3, userDetails.getLastName());
			stmt.setString(4, userDetails.getGender());
			stmt.setString(5, userDetails.getPassword());
			stmt.setString(6, userDetails.getRecoveryAnswer());
			stmt.execute();
			stmt.close();
			return true;

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(con!=null) {
					con.close();
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	public boolean forgotPasswordDbConnection(String email,String recoveryAnswer) {
		
		try {
			String password;
			String reenteredPassword;
			Scanner scanner = new Scanner(System.in);
			
			dataSource = jdbcObj.setUpPool();
			
			con = dataSource.getConnection();
			
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(jdbcObj.forgotPasswordCheck);
			
			while(rs.next()){
				String emailId= rs.getString("email");
				if(emailId.equals(email)) {
					String securityAnswer= rs.getString("recovery_answer");
					if(securityAnswer.equals(recoveryAnswer)) {
						do {		
							System.out.println("\nPlease enter your new password : ");
							password=scanner.nextLine();
							BudgetPortal.goBackToPortal(password);
							System.out.println("\nPlease reenter your password : ");
							reenteredPassword=scanner.nextLine();
							if(!password.equals(reenteredPassword)) {
								System.out.println("\nYour password doesn't match. Please enter again. \n");
							}
						}
						while(!password.equals(reenteredPassword));
						
							Statement updatestmt = con.createStatement();
							updatestmt.execute("UPDATE Account SET password = '"+reenteredPassword+"' WHERE email='"+emailId+"'");
							System.out.println("\nYour password has been updated , Please Login again.\n");
							updatestmt.close();
						
					
						return true;
					}
					else {
						System.out.println("\nYour answer doesn't match. Please enter again. \n");
						return false;
					}
				}
					
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(con!=null) {
					con.close();
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
