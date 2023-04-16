package com.neuymin.web.jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
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
