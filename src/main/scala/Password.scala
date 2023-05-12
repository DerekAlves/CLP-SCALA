import slick.jdbc.SQLiteProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}
import scala.concurrent.Await
import scala.concurrent.duration._
import java.nio.file.{Files, Paths}
import scala.util.Random
import slick.jdbc.meta.MTable
import Profile._
import com.typesafe.config.ConfigFactory

object Password{

    val config = ConfigFactory.load()
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
    val db = Database.forURL(url, driver = "org.sqlite.JDBC", executor = AsyncExecutor("test", numThreads=10, queueSize=1000))
    //val db = Database.forConfig("slick.db")
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
        //db.close()
        return
        

    }
    def CheckPassword(accountID: String): Boolean = {

        val query = users_passwords.filter(_.accountID === accountID)
        val result = Await.result(db.run(query.result), Duration.Inf)
        val user = result.head
        if(user.blocked_aPassword){
            println("Senha de acesso bloqueada!")
            return false
        }
        else{
            val password = readStringInput("Digite a senha de acesso: ")
            if(password == user.access_password){
                return true
            }
            else{
                println("Senha de acesso incorreta!")
                return false
            }
        }
    
    }

    //Implemente uma função ChangePassword que recebe como parâmetro o accountID e altera a senha principal do usuário. 
    //A função deve receber a nova senha principal e atualizar o banco de dados.
    def ChangePassword(accountID: String): Unit = {
        val query = users_passwords.filter(_.accountID === accountID)
        val result = Await.result(db.run(query.result), Duration.Inf)
        val user = result.head
        if(user.blocked_mPassword){
            println("Senha principal bloqueada!")
            return
        }
        else{
            val newPassword = readStringInput("Digite a nova senha principal: ")
            val updateQuery = users_passwords.filter(_.accountID === accountID).map(_.main_password).update(newPassword)
            val updateAndPrint = updateQuery.map{ up =>
                println(s"Senha principal alterada com sucesso!")
            }
            Await.result(db.run(updateAndPrint), Duration.Inf)
            return
        }
    }


}