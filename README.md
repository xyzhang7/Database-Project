# Database-Project
* Implement a Recruitment System by Java for managing data relevant to job recruitment,involving employee, employer, position, and employment history.
* Provide an interactive interface to system administrator, employee, and employer.   
* [Specification](Spec.pdf)

##### Phase 1: 
* Design a database for the system
  * ER-diagram
  * Relational schema   
[ER-diagram & Relational Schema](phase1.pdf)

* * * 

##### Phase 2: 
* Implement a Java application
  * Administrator
    * Create tables in the database
    * Delete tables in the database
    * Load data
    * Check tables in the database
  * Employee
    * Show Available Positions
    * Mark Interested Position
    * Check Average Working Time
  * Employer
    * Post Position Recruitment 
    * Check employees and arrange an interview
    * Accept an employee
  * Error Handling 
    * Data not found
    * Invalid input
    
##### Usage: 
* Download mysql-connector-java-version from https://dev.mysql.com/downloads/connector/j/5.1.html.
Unzip to get "mysql-connector-java-version.jar".
Plug the path of "mysql-connector-java-version.jar" between "" in command line.
* Compile   
<code>javac Administrator.java</code>   
<code>javac Employer.java</code>   
<code>javac Employee.java</code>   
<code>Javac Main.java</code>   
* Run
<code>java -cp .:"" Main</code>
