import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps
import slick.jdbc.SQLiteProfile.api._
import scala.concurrent._
import ExecutionContext.Implicits.global

class Companies(tag: Tag) extends Table[(Int, String)](tag, "COMPANIES") {
  def id = column[Int]("ID", O.PrimaryKey) // This is the primary key column
  def name = column[String]("NAME")
  // Every table needs a * projection with the same type as the table's type parameter
  def * = (id, name)
}

class Computers(tag: Tag) extends Table[(Int, String, Int)](tag, "COMPUTERS") {
  def id = column[Int]("ID", O.PrimaryKey) // This is the primary key column
  def name = column[String]("NAME")
  def manufacturerId = column[Int]("MANUFACTURER_ID")
  
  // Every table needs a * projection with the same type as the table's type parameter
  def * = (id, name, manufacturerId)
}

object Example extends App {
  val companies = TableQuery[Companies]
  val computers = TableQuery[Computers]
  // connection info for a pre-populated throw-away, in-memory db for this demo, which is freshly initialized on every run
  val url = "jdbc:sqlite:src/main/sql/data.db"
  val db = Database.forURL(url, driver = "org.sqlite.JDBC")

  val q = companies.join(computers).on(_.id === _.manufacturerId)
    .map { case (co, cp) => (co.name, cp.name) }
 
  Await.result(db.run(q.result).map { result =>
    println(result.groupBy { case (co, cp) => co }
      .mapValues(_.map { case (co, cp) => cp })
      .mkString("\n")
    )
  }, 60 seconds)
  
}
