import slick.jdbc.SQLiteProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}
import scala.concurrent.Await
import scala.concurrent.duration._
import java.nio.file.{Files, Paths}
import scala.util.Random
import slick.jdbc.meta.MTable
import Profile._

object Password{

    case class UserPassword(access_password: String, main_password: String, blocked_aPassword: Boolean, blocked_mPassword: Boolean, accountID: String)

    // define uma tablea para armazenar os usuários
    class UserPasswords(tag: Tag) extends Table[UserPassword](tag, "user_passwords") {
        def access_password = column[String]("access_password", O.Length(256))
        def main_password = column[String]("main_password", O.Length(256))
        def blocked_aPassword = column[Boolean]("blocked_aPassword")
        def blocked_mPassword = column[Boolean]("blocked_mPassword")
        def accountID = column[String]("accountID", O.Length(256))
        def * = (access_password, main_password, blocked_aPassword, blocked_mPassword, accountID) <> (UserPassword.tupled, UserPassword.unapply)
    }
    // cria uma instância da tabela usuários 
    val users_passwords = TableQuery[UserPasswords]

    // define o URL do banco de dados
    val url = "jdbc:sqlite:sqlite/accounts.db"

    // cria uma conexão com o banco de dados SQLite
    val db = Database.forURL(url, driver = "org.sqlite.JDBC")
    
    def tableExists(tableName: String): Boolean = {
        val action = MTable.getTables(tableName)
        val result = Await.result(db.run(action), Duration.Inf)
        result.nonEmpty
    }   

    def CreateUsersPasswordsDB (): Unit = {
        println("Criando banco de dados de senhas...")
        Await.result(db.run(users_passwords.schema.create), Duration.Inf)
    }

    def CreatePasswords(accountID: String): Unit = {
        if(!tableExists("user_passwords")){
            CreateUsersPasswordsDB()
        }
        val newPassword = UserPassword(readStringInput("Digite a senha de acesso: "), readStringInput("Digite a senha principal: "), false, false, accountID)
        val insertPassword = users_passwords += newPassword
        val insertAndPrint = insertPassword.map{ up =>
            println(s"Senha de acesso e principal cadastradas com sucesso!")
        }
        Await.result(db.run(insertAndPrint), Duration.Inf)
        

    }


}