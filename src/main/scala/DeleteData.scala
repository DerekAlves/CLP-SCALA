import slick.jdbc.SQLiteProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}

object Delete {
  // define a case class to represent the user
  case class User(cpf: String, name: String, profession: String, adress: String, email: String, profile: Int, theme: Int)
  // define a table to store the users
 class Users(tag: Tag) extends Table[User](tag, "users") {
    def cpf = column[String]("cpf", O.PrimaryKey)
    def name = column[String]("name")
    def profession = column[String]("profession")
    def address = column[String]("address")
    def email = column[String]("email")
    def profile = column[Int]("profile")
    def theme = column[Int]("theme")
    def * = (cpf, name, profession, address, email, profile, theme) <> (User.tupled, User.unapply)
  }
  // create an instance of the Users table
  val users = TableQuery[Users]
   
  val url = "jdbc:sqlite:sqlite/users.db"
  // create a connection to the SQLite database
  val db = Database.forURL(url, driver = "org.sqlite.JDBC")
  // define a function to delete all users from the database
  def deleteAllUsers(): Unit = {
    val deleteAction = users.delete

    // execute the delete action and print the result
    val deleteResult = db.run(deleteAction)
    deleteResult.onComplete {
      case Success(rowsAffected) => println(s"Deleted $rowsAffected rows")
      case Failure(exception) => println(s"Error deleting users: ${exception.getMessage}")
    }
    db.close()
  }
  
}
