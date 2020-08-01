import java.sql.*;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.Date;
import java.text.SimpleDateFormat;


public class Employer {
    Scanner reader = new Scanner(System.in);
    Connection conn = null;
    Statement stmt = null;

    public Employer(Connection myConn, Statement myStmt) throws Exception{
        conn = myConn;
        stmt = myStmt;
    }

    public void run() throws Exception{
        while (true){   // prompt
            System.out.println("Employer, what would you like to do");
            System.out.println("1. Post Position Recruitment");
            System.out.println("2. Check employees and arrange an interview");
            System.out.println("3. Accept an eomployee");
            System.out.println("4. Go back");
            System.out.println("Please enter [1-4]");

            int input;
            while (true){   // get valid input
                try{
                    input = reader.nextInt();
                    if (input < 5 && input > 0)
                        break;
                    System.out.println("Invalid input. Try again: ");
                } catch (Exception e){
                    System.out.println("Invalid input. Please enter 1-4: ");
                    String s=reader.next();
                }
            }
            switch (input){
                case 1:
                    position();
                    break;
                case 2:
                    interview();
                    break;
                case 3:
                    accept();
                    break;
                case 4:
                    return ;
                default:    break;
            }
        }
    }

    public void position() throws Exception{

        String psql = "INSERT INTO Positions (Position_ID, Position_Title, Salary, Experience, Employer_ID, Status) " +
                "values(?, ?, ?, ?, ?, 1)";

        PreparedStatement pstmt = conn.prepareStatement(psql);
        
        // Employer_ID
        System.out.println("Please enter your ID");
        String id = reader.next();        
        while(!valid_pid(id)){
            System.out.println("Invalid ID, please enter again");
            id = reader.next();
        }
        pstmt.setString(5, id);

        // Position_ID
        int exist = 1;
        String pid = "";
        while (exist != 0) {    // Generate a pid....  You Du Ba!!!!!!!
            exist = 0;
            pid = "pid" + (char) (int) (Math.random() * 26 + 97) + (char) (int) (Math.random() * 26 + 97) + (char) (int) (Math.random() * 26 + 97);
            String check_id = "SELECT Position_ID " +
                    "FROM Positions " +
                    "WHERE Position_ID = '%s'";
            check_id = String.format(check_id, pid);
            ResultSet rs = stmt.executeQuery(check_id);
            while (rs.next())    exist++;
        }
//        System.out.println(pid);
        pstmt.setString(1, pid);

        // Title
        System.out.println("Please enter the position title");
        String title = reader.next();
        pstmt.setString(2, title);

        // Salary
        System.out.println("Please enter an upper bound of salary");
        int salary = reader.nextInt();
        pstmt.setInt(3, salary);

        // experience
        System.out.println("Please enter the required experience (press enter to skip)");
        String experience = reader.nextLine(); // can be NULL, TODO
        experience = reader.nextLine();
        int exp;
        if (experience.length() == 0)   exp = 0;
        else    exp = Integer.parseInt(experience);
        pstmt.setInt(4, exp);

        // Count potential employees
        String valid_pos = "SELECT COUNT(*) " +
                "FROM Employee E, Employment_History H " +
                "WHERE E.Skills like '%"+title+"%' and E.Employee_ID = H.Employee_ID and H.End is not null and E.Experience > ? and E.Expected_Salary < ?";
        PreparedStatement pstmt_v = conn.prepareStatement(valid_pos);
//        pstmt_v.setString(1, title);
        pstmt_v.setInt(1, exp);
        pstmt_v.setInt(2, salary);
        int poten_count = 0;
        ResultSet poten_rs = pstmt_v.executeQuery();
        poten_rs.next();
        poten_count = poten_rs.getInt(1);

        if (poten_count == 0){
            System.out.println("No potential employees, adding position failed.");
            return;
        }
        pstmt.execute();
        System.out.println(poten_count + " potential employees are found. The position recruitment is posted");
    }

    public void interview() throws Exception {

        // Find the positions posted by user
        String find_pos = "SELECT P.Position_ID " +
                "FROM Positions P " +
                "WHERE P.Employer_ID = '%s'";

        System.out.println("Please enter your ID: ");
        String employer_id = reader.next();       
        while(!valid_pid(employer_id)){
            System.out.println("Invalid ID, please enter again");
            employer_id = reader.next();
        }

        System.out.println("The id of position recruitments posted by you are ");

        find_pos = String.format(find_pos, employer_id);
        ResultSet pos_rs = stmt.executeQuery(find_pos);
        while(pos_rs.next()){
            String s = pos_rs.getString("Position_ID");
            System.out.println(s);
        }

        // Choose one position
        System.out.println("Please pick one position ID");
        String pick_id = reader.next();

        // Find the employees who are interested in this position
        System.out.println("The employees who marked interested in this recruitment are");
        System.out.println("Employee_ID, Name, Expected_Salary, Experience, Skills");

        String find_emp = "SELECT E.Employee_ID, E.Name, E.Expected_Salary, E.Experience, E.Skills " +
                "FROM Marked M, Employee E " +
                "WHERE M.Position_ID = '%s' and M.Employee_ID = E.Employee_ID ";
        find_emp = String.format(find_emp, pick_id);
        ResultSet emp_rs = stmt.executeQuery(find_emp);
        int count = 0;
        while (emp_rs.next()){
            count ++;
            String e_ID = emp_rs.getString("Employee_ID");
            String name = emp_rs.getString("Name");
            int salary = emp_rs.getInt("Expected_Salary");
            int experience = emp_rs.getInt("Expected_salary");
            String skills = emp_rs.getString("Skills");
            System.out.println(e_ID +", "+ name +", "+ salary +", "+ experience +", "+ skills);
        }
        if (count == 0){     System.out.println("No employee is interested in this position"); return; }

        // Pick an employee and arrange interview, change the status in Marked
        System.out.println("Please pick one employee by Employee_ID");
        String emp_ID = reader.next();

        String update_positions = "UPDATE Marked " +
                "SET Status = 0 " + //TODO
                "WHERE Position_ID = '%s' and Employee_ID = '%s'";
        update_positions = String.format(update_positions, pick_id, emp_ID);
        stmt.executeUpdate(update_positions);
        System.out.println("An IMMEDIATE interview has done");
    }

    public void accept() throws  Exception{
        System.out.println("Please enter your ID");
        String employer_id = reader.next();
        while(!valid_pid(employer_id)){
            System.out.println("Invalid ID, please enter again");
            employer_id = reader.next();
        }

        System.out.println("Please enter the the employee_ID you want to hire");
        String employee_id = reader.next();

        System.out.println("Please enter the position the employee is hired for");
        String position_id = reader.next();

        // Check valid
        String valid = "SELECT Status " +
                "FROM Marked " +
                "WHERE Position_ID = '%s' and Employee_ID = '%s' and Status = 0";
        valid = String.format(valid, position_id, employee_id);
        ResultSet valid_rs = stmt.executeQuery(valid);
        int status = -1;
        while(valid_rs.next()){
            status = valid_rs.getInt("Status");
        }
        if (status == 1)   { System.out.println("The employee is not valid, please interview first"); return; }     // not interviewed
        else if (status == -1){  System.out.println("The employee is not interested in this position"); return; }   // not interested

        // change the status of the position
        String set_pos_invalid = "UPDATE Positions " +
                "SET Status = 0 " +
                "WHERE Position_ID = '%s'";
        set_pos_invalid = String.format(set_pos_invalid, position_id);
        stmt.executeUpdate(set_pos_invalid);

        // get the company
        String find_company = "SELECT Company " +
                "FROM Employer " +
                "WHERE Employer_ID = '%s'";
        find_company = String.format(find_company, employer_id);
        ResultSet find_company_rs = stmt.executeQuery(find_company);
        find_company_rs.next();
        String company = find_company_rs.getString("Company");

        // current time
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(new Date());

        // update employee_history
        String update_history = "INSERT Employment_History " +
                "SET Employee_ID = '%s', Company = '%s', Position_ID = '%s', Start = '%s'";
        update_history = String.format(update_history, employee_id, company, position_id, date);
        stmt.executeUpdate(update_history);
        System.out.println("An Employment_History record is created, details are");
        System.out.println("Employee_ID, Company, Position_ID, Start, End");
        System.out.println(employee_id +", "+ company +", "+ position_id +", "+ date +", "+ "NULL");
    }

    public boolean valid_pid(String pid) throws Exception{
        String check_valid_pid = "SELECT Count(*) " +
                "FROM Employer " +
                "WHERE Employer_ID = '%s'";
        check_valid_pid = String.format(check_valid_pid, pid);
        ResultSet count_pid = stmt.executeQuery(check_valid_pid);
        count_pid.next();
        int count = count_pid.getInt(1);
        if (count == 1) return true;
        else            return false;
    }
}

