import slick.jdbc.SQLiteProfile.api._
import slick.jdbc.SQLiteProfile.api.Database
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}
import scala.concurrent.Await
import scala.concurrent.duration._
import java.nio.file.{Files, Paths}
import com.typesafe.config.ConfigFactory
import BankAccount._

object Profile{

    
    val config = ConfigFactory.load()
    def readIntInput(prompt: String): Int = {
        println(prompt)
        scala.io.StdIn.readLine().toInt 
    }

    def readStringInput(prompt: String): String = {
        println(prompt)
        scala.io.StdIn.readLine()
    }

    // define um case class para representar o usuário
    case class UserProfile(cpf: String, name: String, profession: String, address: String, email: String)

    // define uma tablea para armazenar os usuários
    class UserProfiles(tag: Tag) extends Table[UserProfile](tag, "user_profiles") {
        def cpf = column[String]("cpf", O.PrimaryKey, O.Length(11))
        def name = column[String]("name")
        def profession = column[String]("profession")
        def address = column[String]("address")
        def email = column[String]("email")
        def * = (cpf, name, profession, address, email) <> (UserProfile.tupled, UserProfile.unapply)
    }
    // cria uma instância da tabela usuários 
    val users = TableQuery[UserProfiles]

    // define o URL do banco de dados
    val url = "jdbc:sqlite:sqlite/accounts.db"

    // cria uma conexão com o banco de dados SQLite
   //val db = Database.forConfig("slick.db")
   val db = Database.forURL(url, driver = "org.sqlite.JDBC", executor = AsyncExecutor("test", numThreads=10, queueSize=1000))


    def CreateUsersDB (): Unit = {
        println("Criando banco de dados...")
        Await.result(db.run(users.schema.create), Duration.Inf)
    }

    // insere um novo usuário ao banco de dados
    def CreateProfile (): String = {
        
        if (!Files.exists(Paths.get("sqlite/accounts.db"))){
            CreateUsersDB()
        }
        val cpf = readStringInput("Digite o CPF: ")
        val newUser = UserProfile(cpf,
        readStringInput("Digite o nome: "),
        readStringInput("Digite a profissão: "),
        readStringInput("Digite o endereço: "),
        readStringInput("Digite o email: ")
        )
        val insertAction = users += newUser
        
        // executa a ação de inserção e mostra o resultado
        val insertResult = db.run(insertAction)
        insertResult.onComplete {
        case Success(rowsAffected) => 
            println(s"Inserida $rowsAffected linha(s)")
        case Failure(exception) => println(s"Erro ao inserir usuário: ${exception.getMessage}")
        }
        

        return cpf
        
    }

    def ChangeProfile (): Option[UserProfile] = {
        val cpf = readStringInput("Digite o CPF: ")
        val query = users.filter(_.cpf === cpf).result.headOption
        val result= db.run(query)
        val userOption = Await.result(result, Duration.Inf)
        if (userOption.isDefined){
            userOption.map { user =>
                val updatedUser = user.copy(
                    name = readStringInput(s"Digite o novo nome (${user.name}): "),
                    profession = readStringInput(s"Digite a nova profissão (${user.profession}): "),
                    address = readStringInput(s"Digite o novo endereço (${user.address}): "),
                    email = readStringInput(s"Digite o novo email (${user.email}): ")
                )
                val updateAction = users.filter(_.cpf === cpf).update(updatedUser)
                val rowsAffected = Await.result(db.run(updateAction), Duration.Inf)
                if (rowsAffected > 0) updatedUser else user
            }
        }else{
            println(s"CPF $cpf não foi encontrado")
        }
        userOption


    }
    def QueryProfile (accountID: String):  Option[(String, String, String, String, String)] = {
        var Query_CPF_From_ID = BankAccount.clientaccounts.filter(_.account_ID === accountID).map(_.cpf).result.headOption
        val CPF_From_ID = Await.result(db.run(Query_CPF_From_ID), Duration.Inf)
        val query = users.filter(_.cpf === CPF_From_ID).result.headOption
        val result = db.run(query)
        val userOption = Await.result(result, Duration.Inf)
        if (userOption.isDefined){
            var userCpf = ""
            var userName = ""
            var userProfession = ""
            var userAddress = ""
            var userEmail = ""
            userOption.foreach { user =>
                userCpf = user.cpf
                userName = user.name
                userProfession = user.profession
                userAddress = user.address
                userEmail = user.email 
            }
            println(s"CPF: $userCpf")
            println(s"Name: $userName")
            println(s"Profession: $userProfession")
            println(s"Address: $userAddress")
            println(s"Email: $userEmail")
            Some(userCpf, userName, userProfession, userAddress, userEmail)
        } else {
            println(s"CPF $CPF_From_ID não foi encontrado.")
            None
        }
        
    }
    def CheckLoginCPF (): String = {
        val cpf = readStringInput("Digite o CPF: ")
        val query = users.filter(_.cpf === cpf).result.headOption
        val result = db.run(query)
        val userOption = Await.result(result, Duration.Inf)
        if (userOption.isDefined){
            return cpf
        } else {
            println(s"CPF $cpf não foi encontrado.")
            return ""
        }
        
    }

    def deleteAllUsers(): Unit = {
        val deleteAction = users.delete
        val deleteResult = db.run(deleteAction)
            deleteResult.onComplete {
                case Success(rowsAffected) => println(s"Deleted $rowsAffected rows")
                case Failure(exception) => println(s"Error deleting users: ${exception.getMessage}")
            }
            
    }

    
}