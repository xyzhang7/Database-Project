import java.sql.*;
import java.io.*;
import java.util.*;

public class Employee {

    public static ResultSet avail_position (Connection conn, ResultSet rs1) throws SQLException{
        String skills=rs1.getString("Skills");
        String exe_sal=rs1.getString("Expected_Salary");
        String exp=rs1.getString("Experience");
        String avail_pos="select * from Positions P, Employer E, Company C "+
        "where P.Employer_ID=E.Employer_ID and C.Company=E.Company and "+
        "Status=True and locate(Position_Title, ?) > 0 and Salary>= ? and Experience <= ?";
        PreparedStatement stmt2 = conn.prepareStatement(avail_pos);
        stmt2.setString(1,skills);
        stmt2.setString(2,exe_sal);
        stmt2.setString(3,exp);
        ResultSet rs = stmt2.executeQuery();
        return rs;
    }

    public static void employee_func (Connection conn) throws IOException {
        //String dbAddress = "jdbc:mysql://localhost:3306/proj3170";
        //String dbUsername = "root";
        //String dbPassword = "123456";
        //Connection conn = null;
    try {
            //Class.forName("com.mysql.jdbc.Driver");
            //conn = DriverManager.getConnection(dbAddress, dbUsername, dbPassword);
        do{
            System.out.println("Employee, what would you like to do?");
            System.out.println("1. Show Available Positions");
            System.out.println("2. Mark Interested Position");
            System.out.println("3. Check Average Working Time");
            System.out.println("4. Go back");

            BufferedReader br = new BufferedReader(new 
                      InputStreamReader(System.in));

            System.out.println("Please enter [1-4].");
            char num;
            num = (char) br.read();
            br.readLine();

            while (num!='1' && num!='2' && num!='3' && num!='4')
            {
                System.out.println("[ERROR] Invalid input.");
                System.out.println("Please enter [1-4].");
                num = (char) br.read();
                br.readLine();
            }
            
            if (num=='4') return;

            System.out.println("Please enter your ID.");
            String eID;
            ResultSet rs1;
            PreparedStatement stmt1;

            do{   
            eID = br.readLine();
            stmt1 = conn.prepareStatement("select * from Employee where Employee_id=?");
            stmt1.setString(1,eID);
            rs1 = stmt1.executeQuery(); 
            if(rs1.next()==false)
                System.out.println("The employee does not exist. Please enter another Employee_ID.");
            else break;
            } while(true);
             
            if (num=='1')
            {
                ResultSet rs=avail_position(conn, rs1);
                if (rs.next()!=false) 
                {
                    System.out.println("Your available positions are:");
                    System.out.println("Position_ID, Position_Title, Salary, Company, Size, Founded");
                   
                    do{
                    String Position_ID=rs.getString("Position_ID");
                    String Position_Title=rs.getString("Position_Title");
                    String Salary=rs.getString("Salary");
                    String Company=rs.getString("Company");
                    String Size=rs.getString("Size");
                    String Founded=rs.getString("Founded");
                    System.out.println(Position_ID+", "+Position_Title+", "+Salary+", "+Company+", "+Size+", "+Founded);
                    }while(rs.next());
                }
                else
                {
                    System.out.println("You do not have any available positions.");
                }
            }
            else if(num=='2'){
                PreparedStatement stmt2 = conn.prepareStatement("select Company from Employment_History where Employee_id=?");
                stmt2.setString(1,eID);
                ResultSet rs2 = stmt2.executeQuery(); 
                Set<String> comps = new HashSet<String>();
                while(rs2.next()){
                    String Company=rs2.getString("Company");
                    comps.add(Company);
                }

                PreparedStatement stmt3 = conn.prepareStatement("select Position_ID from Marked where employee_id=?");
                stmt3.setString(1,eID);
                ResultSet rs3 = stmt3.executeQuery(); 
                Set<String> marked = new HashSet<String>();
                while(rs3.next()){
                    String position=rs3.getString("Position_ID");
                    marked.add(position);
                }

                Set<String> may_interested = new HashSet<String>();
                System.out.println("Your interested positions are:");
                ResultSet rs=avail_position(conn, rs1);
                
                while(rs.next()){
                    String Position_ID=rs.getString("Position_ID");
                    String Position_Title=rs.getString("Position_Title");
                    String Salary=rs.getString("Salary");
                    String Company=rs.getString("Company");
                    String Size=rs.getString("Size");
                    String Founded=rs.getString("Founded");
                    if (!comps.contains(Company) && !marked.contains(Position_ID)){
                        may_interested.add(Position_ID);
                        System.out.println(Position_ID+", "+Position_Title+", "+Salary+", "+Company+", "+Size+", "+Founded);
                    }
                }

                if (may_interested.size()==0) 
                {
                     System.out.println("There are no possible interested positions for you.");
                     continue;
                }

                System.out.println("Please enter one interested Position_ID.");
                String pID;
                do{    
                    pID = br.readLine();
                    if (!may_interested.contains(pID))
                        System.out.println("Invalid Position_ID, please enter again:");
                    else break;
                } while (true);
                
                PreparedStatement stmt4 = conn.prepareStatement("INSERT INTO Marked VALUES (?,?,True)");
                stmt4.setString(1,pID);
                stmt4.setString(2,eID);
                stmt4.execute(); 
                System.out.println("Done.");
            }
            else if(num=='3'){
            String count_rec="select count(*) AS cnt from Employment_History "+ 
            "where Employee_ID=? and End!='NULL' ";
            PreparedStatement pstmt = conn.prepareStatement(count_rec);
            pstmt.setString(1,eID);
            int count;
            ResultSet cnt = pstmt.executeQuery(); 
            cnt.next();
            count=cnt.getInt("cnt");
            if (count<3) System.out.println("Less that 3 records.");
            else {
            String avg_rec="select AVG(DATEDIFF(End,Start)) AS avg from Employment_History "+ 
            "where Employee_ID=? and End!='NULL' "+
            "order by Position_ID DESC limit 3";
            PreparedStatement pstmt2 = conn.prepareStatement(avg_rec);
            pstmt2.setString(1,eID);
            ResultSet avg = pstmt2.executeQuery();
            double avg_time;
            avg.next();
            avg_time=avg.getDouble("avg");
            System.out.println("Your average working time is: "+avg_time+" days.");
            }
                
                
            }
            

        } while(true);
            
        
        } catch (SQLException e) {
         System.out.println(e);
        }


    }

}

