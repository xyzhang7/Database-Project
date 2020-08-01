import java.io.File;
import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;


public class Main {
    static Scanner sc = new Scanner(System.in);
    static Connection conn = null;
    static Statement stmt = null;
  
    public static void main(String[] args) throws Exception {
        String dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/db10";
		String dbUsername = "Group10";
        String dbPassword = "CSCI3170";
        
        // Connect to the Server
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbAddress, dbUsername, dbPassword);
            stmt = conn.createStatement();
        } catch (SQLException a) {
            System.out.println("[ERROR] "+a.getMessage());
        }
        // Prompt
        while (true){
            System.out.println("Welcome! Who are you?");
            System.out.println("1. An administrator");
            System.out.println("2. An employee");
            System.out.println("3. An employer");
            System.out.println("4. Exit");
            System.out.println("Please enter [1-4].");

            int user_input_number;
            while (true){   // get valid input
                try{
                    user_input_number = sc.nextInt();
                    if (user_input_number < 5 && user_input_number > 0)
                        break;
                    System.out.println("Invalid input. Please Try again: ");
                } catch (InputMismatchException e){
                    System.out.println("Invalid input. Please Try again: ");
                    String s=sc.next();
                }
            }
            switch (user_input_number){
                case 1:
                    Administrator admin_driver = new Administrator(conn, stmt);
                    admin_driver.menu();
                    break;
                case 2:
                    Employee employee = new Employee();
                    employee.employee_func(conn);
                    break;
                case 3:
                    Employer employer = new Employer(conn, stmt);
                    employer.run();
                    break;
                case 4:
                    System.out.println("Byebye!");
                    return;
                    // break;
                default:
                    break;
            }
        }
    }
}