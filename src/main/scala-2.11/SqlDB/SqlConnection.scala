package SqlDB
import java.sql.DriverManager
import java.sql.Connection
/**
  * Created by rahul on 4/10/16.
  */
object SqlConnection {

    // connect to the database named "mysql" on the localhost
    val driver = "com.mysql.jdbc.Driver"
    val url = "jdbc:mysql://localhost/testdb"
    val username = "root"
    val password = "12345"

    // there's probably a better way to do this
    var connection:Connection = null

    try {
      // make the connection
      Class.forName(driver)
      connection = DriverManager.getConnection(url, username, password)

//      // create the statement, and run the select query
//      val statement = connection.createStatement()
//
//      val resultSet = statement.executeQuery("SELECT host, user FROM user")
//
//      while ( resultSet.next() ) {
//        val host = resultSet.getString("host")
//        val user = resultSet.getString("user")
//        println("host, user = " + host + ", " + user)
//      }

    } catch {
      case e => e.printStackTrace
    }
//    connection.close()
}
