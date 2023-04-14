import slick.jdbc.SQLiteProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}
import scala.concurrent.Await
import scala.concurrent.duration._
import java.nio.file.{Files, Paths}
import scala.util.Random
import slick.jdbc.meta.MTable

object Devices {

    case class UserDevice(device_name: String, device_id: String, accountID: String)

    // define uma tablea para armazenar os usuários
    class UserDevices(tag: Tag) extends Table[UserDevice](tag, "user_devices") {
        def device_name = column[String]("device_name", O.Length(256))
        def device_id = column[String]("device_id", O.PrimaryKey, O.Length(256))
        def accountID = column[String]("accountID", O.Length(256))
        def * = (device_name, device_id, accountID) <> (UserDevice.tupled, UserDevice.unapply)
    }
    // cria uma instância da tabela usuários 
    val user_devices = TableQuery[UserDevices]

    // define o URL do banco de dados
    val url = "jdbc:sqlite:sqlite/accounts.db"

    // cria uma conexão com o banco de dados SQLite
    val db = Database.forURL(url, driver = "org.sqlite.JDBC")
    
    def tableExists(tableName: String): Boolean = {
        val action = MTable.getTables(tableName)
        val result = Await.result(db.run(action), Duration.Inf)
        result.nonEmpty
    }   

    def CreateUserDevicesDB (): Unit = {
        println("Criando banco de dados de dispositivos...")
        Await.result(db.run(user_devices.schema.create), Duration.Inf)
    }

    def CreateUserDevices(accountID: String): Unit = {
        if(!tableExists("user_devices")){
            CreateUserDevicesDB()
        }
    }


}