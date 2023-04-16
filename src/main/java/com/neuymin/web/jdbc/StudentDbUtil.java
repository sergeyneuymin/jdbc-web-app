package com.neuymin.web.jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StudentDbUtil {

    private DataSource dataSource;

    public StudentDbUtil(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Student> getStudents() throws Exception {

        List<Student> students = new ArrayList<>();

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // get a connection
            connection = dataSource.getConnection();
            // create sql stmt
            String sql = "select * from student order by last_name";
            statement = connection.createStatement();
            // execute the query
            resultSet = statement.executeQuery(sql);

            while(resultSet.next()) {
                int id = resultSet.getInt("id");
                String firstname = resultSet.getString("first_name");
                String lastname = resultSet.getString("last_name");
                String email = resultSet.getString("email");

                Student tempStudent = new Student(id, firstname, lastname, email);

                students.add(tempStudent);
            }


            return students;
        } finally {
            close(connection, statement, resultSet);
        }

    }

    public void addStudent(Student theStudent) throws Exception {

        Connection myConn = null;
        PreparedStatement myStmt = null;

        try {
            // get db connection
            myConn = dataSource.getConnection();

            // create sql for insert
            String sql = "insert into student "
                    + "(first_name, last_name, email) "
                    + "values (?, ?, ?)";

            myStmt = myConn.prepareStatement(sql);

            // set the param values for the student
            myStmt.setString(1, theStudent.getFirstName());
            myStmt.setString(2, theStudent.getLastName());
            myStmt.setString(3, theStudent.getEmail());

            // execute sql insert
            myStmt.execute();
        }
        finally {
            // clean up JDBC objects
            close(myConn, myStmt, null);
        }
    }

    public Student getStudent(String theStudentId) throws Exception {

        Student theStudent = null;

        Connection myConn = null;
        PreparedStatement myStmt = null;
        ResultSet myRs = null;
        int studentId;

        try {
            // convert student id to int
            studentId = Integer.parseInt(theStudentId);

            // get connection to database
            myConn = dataSource.getConnection();

            // create sql to get selected student
            String sql = "select * from student where id=?";

            // create prepared statement
            myStmt = myConn.prepareStatement(sql);

            // set params
            myStmt.setInt(1, studentId);

            // execute statement
            myRs = myStmt.executeQuery();

            // retrieve data from result set row
            if (myRs.next()) {
                String firstName = myRs.getString("first_name");
                String lastName = myRs.getString("last_name");
                String email = myRs.getString("email");

                // use the studentId during construction
                theStudent = new Student(studentId, firstName, lastName, email);
            }
            else {
                throw new Exception("Could not find student id: " + studentId);
            }

            return theStudent;
        }
        finally {
            // clean up JDBC objects
            close(myConn, myStmt, myRs);
        }
    }

    public void updateStudent(Student theStudent) throws Exception {

        Connection myConn = null;
        PreparedStatement myStmt = null;

        try {
            // get db connection
            myConn = dataSource.getConnection();

            // create SQL update statement
            String sql = "update student "
                    + "set first_name=?, last_name=?, email=? "
                    + "where id=?";

            // prepare statement
            myStmt = myConn.prepareStatement(sql);

            // set params
            myStmt.setString(1, theStudent.getFirstName());
            myStmt.setString(2, theStudent.getLastName());
            myStmt.setString(3, theStudent.getEmail());
            myStmt.setInt(4, theStudent.getId());

            // execute SQL statement
            myStmt.execute();
        }
        finally {
            // clean up JDBC objects
            close(myConn, myStmt, null);
        }
    }

    public void deleteStudent(String theStudentId) throws Exception {

        Connection myConn = null;
        PreparedStatement myStmt = null;

        try {
            // convert student id to int
            int studentId = Integer.parseInt(theStudentId);

            // get connection to database
            myConn = dataSource.getConnection();

            // create sql to delete student
            String sql = "delete from student where id=?";

            // prepare statement
            myStmt = myConn.prepareStatement(sql);

            // set params
            myStmt.setInt(1, studentId);

            // execute sql statement
            myStmt.execute();
        }
        finally {
            // clean up JDBC code
            close(myConn, myStmt, null);
        }
    }


    private  void close(Connection connection, Statement statement, ResultSet resultSet) {
        try {

            if(resultSet != null) resultSet.close();
            if(statement != null) statement.close();
            //put back to connection pool
            if(connection != null) connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
