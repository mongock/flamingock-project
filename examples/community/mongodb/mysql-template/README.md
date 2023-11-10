# Mysql with docker
1. docker pull mysql/mysql-server:8.0.32
2. docker run --name='my_sql_container' -d -p 3306:3306 mysql/mysql-server
3. docker logs my_sql_container -> Pick password where it says "[Entrypoint] GENERATED ROOT PASSWORD : ..."
4. docker exec -it my_sql_container bash
5. cd /var/lib/mysql
6. run "mysql -u root -p" and enter the password retrieved in step(3)
7. run "ALTER USER 'root'@'localhost' IDENTIFIED BY 'root_password';"
8. run "use mysql;"
9. run "CREATE USER 'flamingock_user'@'%' IDENTIFIED BY 'password';"
10. run "GRANT ALL PRIVILEGES ON *.* TO 'flamingock_user'@'%' WITH GRANT OPTION;"
11. run "FLUSH PRIVILEGES;"
12. connect from client to localhost on 3306 with flamingock_user/password



        try
        {
            // create our mysql database connection
            String myDriver = "com.mysql.cj.jdbc.Driver";
            String myUrl = "jdbc:mysql://localhost/flamingock";
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myUrl, "flamingock_user", "password");

            String query = "SELECT * FROM people";

            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery(query);

            // iterate through the java resultset
            while (rs.next())
            {
                int id = rs.getInt("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                Integer dateCreated = rs.getInt("age");

                // print the results
                System.out.format("%s, %s, %s, %d\n", id, firstName, lastName, dateCreated);
            }
            st.close();
        }
        catch (Exception e)
        {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        }