

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.text.*;


// import au.com.bytecode.opencsv.CSVReader;


public class Administrator{
	Connection conn = null;
	Statement stmt = null;
	
	public Administrator(Connection myConn, Statement myStmt) throws Exception{
		 	
	        stmt = myStmt;
	        conn = myConn;
	}
	
	public void menu() throws Exception{
		while (true){  
            System.out.println("Administrator, what would you like to do?");
            System.out.println("1. Create tables");
            System.out.println("2. Delete tables");
            System.out.println("3. Load data");
            System.out.println("4. Check data");
            System.out.println("5. Go back");
            System.out.println("Please enter [1-5].");

            int input;
            while (true){   // get valid input
                try{
                	Scanner scan = new Scanner(System.in);
                	
                    input = scan.nextInt();
                    if (input < 6 && input > 0)
                        break;
                    System.out.println("[ERROR]: Invalid input.\nPlease enter [1-5].");
                } catch (InputMismatchException e){
                    System.out.println("[ERROR]: Invalid input.\nPlease enter [1-5].");
                }
            }
            switch (input){
                case 1:
                    createtable();
                    break;
                case 2:
                    deletetable();
                    break;
                case 3:
                    loaddata();
                    break;
                case 4:
                    checkdata();
                    break;
                case 5:
                	return;
                default:    break;
            }
        }
	}
	
	public boolean tableexist(String tableName) throws Exception{
		boolean tExists = false;
	    try (ResultSet rs = conn.getMetaData().getTables(null, null, tableName, null)) {
	        while (rs.next()) { 
	            String tName = rs.getString("TABLE_NAME");
	            if (tName != null && tName.equals(tableName)) {
	                tExists = true;
	                break;
	            }
	        }
	    }
	    return tExists;
	}
	
	public void createtable() throws Exception{
		System.out.print("Processing...");
    	
    	//create tables
    	if(tableexist("Company")) {
    		System.out.println("[ERROR]: table Company already exists");
    		return;
    	}   
    	
    	String sql = "create table Company(Company char(30), Size int, Founded int, primary key(Company))";
    	stmt.executeUpdate(sql);
    	
    	
    	if(tableexist("Employee")) {
    		System.out.println("[ERROR]: table Employee already exists");
    		return;
    	}
    	
    	sql = "create table Employee(Employee_ID char(6), Name char(30), Expected_Salary int, Experience int, Skills char(50), primary key(Employee_ID))";
    	stmt.executeUpdate(sql);
    	
    	
    	if(tableexist("Employer")) {
    		System.out.println("[ERROR]: table Employer already exists");
    		return;
    	}
    	
    	sql = "create table Employer(Employer_ID char(6), Name char(30), Company char(30) NOT NULL, primary key(Employer_ID), foreign key (Company) references Company(Company) )";
    	stmt.executeUpdate(sql);
    	
    	
    	if(tableexist("Positions")) {
    		System.out.println("[ERROR]: table Positions already exists");
    		return;
    	}
    	
    	sql = "create table Positions(Position_ID char(6), Position_Title char(30), Salary int, Experience int, Employer_ID char(6) NOT NULL, Status boolean, primary key(Position_ID), foreign key (Employer_ID) references Employer(Employer_ID) )";
    	stmt.executeUpdate(sql);
    	
    	
    	if(tableexist("Employment_History")) {
    		System.out.println("[ERROR]: table Employment_History already exists");
    		return;
    	}
    	
    	sql = "create table Employment_History(Employee_ID char(6), Company char(30), Position_ID char(6), Start date, End date, primary key(Position_ID), foreign key (Employee_ID) references Employee(Employee_ID), foreign key (Company) references Company(Company))";
    	stmt.executeUpdate(sql);
    	
    	
    	if(tableexist("Marked")) {
    		System.out.println("[ERROR]: table Marked already exists");
    		return;
    	}
    	
    	sql = "create table Marked(Position_ID char(6), Employee_ID char(6), Status boolean, primary key(Position_ID, Employee_ID) )";
    	stmt.executeUpdate(sql);
    	
    	System.out.println("Done! Tables are created!");
		
	}
	
	public void deletetable() throws Exception{
		System.out.print("Processing...");
		
		if(!tableexist("Positions")) {
    		System.out.println("[ERROR]: table 'Positions' not exists");
    		return;
    	}
		String sql = "drop table Positions";
		stmt.executeUpdate(sql);
		
		if(!tableexist("Employer")) {
    		System.out.println("[ERROR]: table 'Employer' not exists");
    		return;
    	}
		sql = "drop table Employer";
		stmt.executeUpdate(sql);
		
		if(!tableexist("Employment_History")) {
    		System.out.println("[ERROR]: table 'Employment_History' not exists");
    		return;
    	}
		sql = "drop table Employment_History";
		stmt.executeUpdate(sql);
		
		if(!tableexist("Company")) {
    		System.out.println("[ERROR]: table 'Company' not exists");
    		return;
    	}
		sql = "drop table Company";
		stmt.executeUpdate(sql);
		
		if(!tableexist("Employee")) {
    		System.out.println("[ERROR]: table 'Employee' not exists");
    		return;
    	}
		sql = "drop table Employee";
		stmt.executeUpdate(sql);
		
		if(!tableexist("Marked")) {
    		System.out.println("[ERROR]: table 'Marked' not exists");
    		return;
    	}
		sql = "drop table Marked";
		stmt.executeUpdate(sql);
		
		System.out.println("Done! Tables are deleted!");
			
	}
	
	public void getdate(PreparedStatement pStmt, String mydate, int parameterIndex) throws ParseException{
		java.sql.Date sqlStartDate = null;
		//System.out.println("mydate is :"+mydate);
		if (mydate != null && !mydate.trim().isEmpty() && !mydate.contentEquals("NULL")) {
			SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
			java.util.Date date = sdf1.parse(mydate);
			sqlStartDate = new java.sql.Date(date.getTime());
			try {
				pStmt.setDate(parameterIndex, sqlStartDate);
			} catch (SQLException e) {
				System.out.println("[ERROR]: "+e.getMessage());
			}
		}else {
			//System.out.println(mydate + "is null");
			try {
				pStmt.setNull(parameterIndex, java.sql.Types.DATE);
			} catch (SQLException e) {
				System.out.println("[ERROR]: "+e.getMessage());
			}
		}

	}
	
	public boolean nonempty_string(String value) {
		if(value != null && !value.trim().isEmpty() && !value.contentEquals("NULL")) {
			return true;
		}else {
			return false;
		}
	}
	
	public boolean numeric_int(String value) {
		try {
			Integer.parseInt(value);
			return true;
		}catch(Exception e) {
			return false;
		}
	}
	
	public boolean positive_int(String value) {
		try {
			int num = Integer.parseInt(value);
			if(num > 0) {
				return true;
			}
			return false;
		}catch(Exception e) {
			return false;
		}
	}
	
	public boolean fourdigit_int(String value) {
		try {
			int num = Integer.parseInt(value);			
			int count = 0;
			if(num<=0) {
				return false;
			}
			for(; num != 0; num/=10, ++count) { }
			if(count==4) {
				return true;
			}	
			return false;
		}catch(Exception e) {
			return false;
		}
	}
	
	public boolean isbool(String value) {
		try {
			Boolean.parseBoolean(value);
			return true;
		}catch(Exception e) {
			return false;
		}		
	}
	
	public void readfile(File file) throws Exception{
		try {			
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = null;
			String sql = null;
			int table = 0;
			
			if(file.getName().contentEquals("employee.csv")) {
				table = 1;
				sql = "insert into Employee (Employee_ID, Name, Expected_Salary, Experience, Skills) values (?,?,?,?,?)";
			}
			
			if(file.getName().contentEquals("company.csv")) {
				table = 2;
				sql = "insert into Company (Company, Size, Founded) values (?,?,?)";
			}
			
			if(file.getName().contentEquals("history.csv")) {
				table = 3;
				sql = "insert into Employment_History (Employee_ID, Company, Position_ID, Start, End) values (?,?,?,?,?)";
			}
			
			if(file.getName().contentEquals("employer.csv")) {
				table = 4;
				sql = "insert into Employer (Employer_ID, Name, Company) values (?,?,?)";
			}
			
			if(file.getName().contentEquals("position.csv")) {
				table = 5;
				sql = "insert into Positions (Position_ID, Position_Title, Salary, Experience, Employer_ID, Status) values (?,?,?,?,?,?)";
			}
						
			PreparedStatement pStmt = conn.prepareStatement(sql);
			
			while((line = br.readLine()) != null) {
				try {
					String[] arr = line.split(",");										
					//for(int i=0; i<arr.length; i++) {
						//System.out.print(arr[i]+" ");
					//}
					//System.out.println();
					
					switch(table){
					case 1: 
						//Employee(Employee_ID char(6), Name char(30), Expected_Salary int, Experience int, Skills char(50), primary key(Employee_ID))";				    
						if(nonempty_string(arr[0])
							&& nonempty_string(arr[1]) && arr[1].length()<=30
							&& positive_int(arr[2]) && positive_int(arr[3]) 
							&& nonempty_string(arr[4]) && arr[4].length()<=50) {
							pStmt.setString(1, arr[0]);
							pStmt.setString(2, arr[1]);
							pStmt.setInt(3, Integer.parseInt(arr[2]));
							pStmt.setInt(4, Integer.parseInt(arr[3]));
							pStmt.setString(5, arr[4]);
						}else {
							System.out.println("[ERROR]: invalid input record");
						}											
						break;
						
					case 2: 
						//Company char(30), Size int, Founded int, primary key(Company))";
						if(nonempty_string(arr[0])
							&& positive_int(arr[1]) && fourdigit_int(arr[2])) {
							pStmt.setString(1, arr[0]);
							pStmt.setInt(2, Integer.parseInt(arr[1]));
							pStmt.setInt(3, Integer.parseInt(arr[2]));
						}else {
							System.out.println("[ERROR]: invalid input record");
						}						
						break;
					case 3: 
						//history(Employee_ID char(6), Company char(30), Position_ID char(6), Start date, End date, primary key(Position_ID), foreign key (Employee_ID) references Employee(Employee_ID), foreign key (Company) references Company(Company))";
						if(nonempty_string(arr[0])
						&& nonempty_string(arr[1]) && arr[1].length()<=30 
						&& nonempty_string(arr[2])) {
							pStmt.setString(1, arr[0]);
					    	pStmt.setString(2, arr[1]);
					    	pStmt.setString(3, arr[2]);
					    	getdate(pStmt, arr[3], 4);
					    	getdate(pStmt, arr[4], 5);
						}else {
							System.out.println("[ERROR]: invalid input record");
						}		    	
						break;
					case 4: 
						//employer(Employer_ID char(6), Name char(30), Company char(30) NOT NULL, primary key(Employer_ID), foreign key (Company) references Company(Company) )";
						if(nonempty_string(arr[0])
						&& nonempty_string(arr[1]) && arr[1].length()<=30
						&& nonempty_string(arr[2]) && arr[2].length()<=30) {
							pStmt.setString(1, arr[0]);
					    	pStmt.setString(2, arr[1]);
					    	pStmt.setString(3, arr[2]);
						}else {
							System.out.println("[ERROR]: invalid input record");
						}    	
						break;
					case 5: 
						//position(Position_ID char(6), Position_Title char(30), Salary int, Experience int, Employer_ID char(6) NOT NULL, Status boolean, primary key(Position_ID), foreign key (Employer_ID) references Employer(Employer_ID) )";
						if(nonempty_string(arr[0])
						&& nonempty_string(arr[1])
						&& positive_int(arr[2]) && numeric_int(arr[3]) 
						&& nonempty_string(arr[4]) && isbool(arr[5])) {
							pStmt.setString(1, arr[0]);
					    	pStmt.setString(2, arr[1]);
					    	pStmt.setInt(3, Integer.parseInt(arr[2]));
					    	pStmt.setInt(4, Integer.parseInt(arr[3]));
							pStmt.setString(5, arr[4]);
							pStmt.setBoolean(6,  Boolean.parseBoolean(arr[5]));
						}
						else {
							System.out.println("[ERROR]: invalid input record");
						}
						break;
					default: return;					
					}
					pStmt.executeUpdate();					
				}catch (Exception e){
					System.out.println("[ERROR]: "+e.getMessage());
				}
			}
		}catch(Exception e) {
			System.out.println("[ERROR]: "+e.getMessage());
		}
		
		
	}
	
	public void loaddata() throws Exception{
		
		if(!tableexist("Employee") || !tableexist("Company") || !tableexist("Employment_History")
				||!tableexist("Employer") || !tableexist("Positions") ){
					System.out.println("[ERROR]: tables not exist");
					return;
				}
		while(true) {
			try {
				System.out.println("Please enter the folder path.");
				Scanner sourcescann = new Scanner(System.in);
	            String source = sourcescann.nextLine();
	            
	            File sourceFile = new File(source);
	            File[] entries; 
	            
	            entries = sourceFile.listFiles();
	            System.out.print("Processing...");
	            boolean fileexist = false;
	            
	            for (File entry : entries){
	            	if(entry.getName().contentEquals("company.csv")) {
	            		readfile(entry);
	            		fileexist = true;
	            	}	            		               
	            }
	            
	            for (File entry : entries){
	            	if(entry.getName().contentEquals("employee.csv")) {
	            		readfile(entry);
	            		fileexist = true;
	            	}	            		               
	            }
	            
	            for (File entry : entries){
	            	if(entry.getName().contentEquals("employer.csv")) {
	            		readfile(entry);
	            		fileexist = true;
	            	}	            		               
	            }
	            
	            for (File entry : entries){
	            	if(entry.getName().contentEquals("history.csv")) {
	            		readfile(entry);
	            		fileexist = true;
	            	}	            		               
	            }
	            
	            for (File entry : entries){
	            	if(entry.getName().contentEquals("position.csv")) {
	            		readfile(entry);
	            		fileexist = true;
	            	}	            		               
	            }
	            
	            if(fileexist) {
	            	System.out.println("Data is loaded!");
	 	            return;
	            }
	           
			}catch (Exception e){
	            System.out.println("[ERROR]: Invalid floder path");
	        }		
          
		}
		
	}
	
	public void countrecords(String table) throws Exception{
		if(!tableexist(table)) {
    		System.out.println("[ERROR]: table '"+ table +"' not exists");
    		return;
    	}
		String sql = "select count(*) as mycount from "+table;
		ResultSet rs = stmt.executeQuery(sql);
		if ( rs.next() )
        {
          int records_number = rs.getInt("mycount");
          System.out.println(table +": "+ records_number);        
        }
	}
	
	public void checkdata() throws Exception{
		try {
			countrecords("Company");
			countrecords("Employee");
			countrecords("Employer");
			countrecords("Positions");
			countrecords("Company");
			countrecords("Employment_History");
			countrecords("Marked");
		}catch(Exception e) {
			System.out.println("[ERROR]: "+e.getMessage());
		}
		return;
		
	}
	

}
