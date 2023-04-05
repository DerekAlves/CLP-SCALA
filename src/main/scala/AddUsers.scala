import slick.jdbc.SQLiteProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}


object AddUsers{

    def readIntInput(prompt: String): Int = {
        println(prompt)
        scala.io.StdIn.readLine().toInt 
    }

    def readStringInput(prompt: String): String = {
        println(prompt)
        scala.io.StdIn.readLine()
    }

    // define um case class para representar o usuário
    case class User(cpf: String, name: String, profession: String, address: String, email: String, profile: Int, theme: Int)

    // define uma tablea para armazenar os usuários
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
    // cria uma instância da tabela usuários 
    val users = TableQuery[Users]

    // define o URL do banco de dados
    val url = "jdbc:sqlite:sqlite/users.db"

    // cria uma conexão com o banco de dados SQLite
    val db = Database.forURL(url, driver = "org.sqlite.JDBC")
    
    // insere um novo usuário ao banco de dados
    def AddUser(): Unit = {
    
        val newUser = User(
        readStringInput("Digite o CPF: "),
        readStringInput("Digite o nome: "),
        readStringInput("Digite a profissão: "),
        readStringInput("Digite o endereço: "),
        readStringInput("Digite o email: "),
        readIntInput("Digite o perfil: "),
        readIntInput("Digite o tema: ")
        )
        val insertAction = users += newUser
        
        // executa a ação de inserção e mostra o resultado
        val insertResult = db.run(insertAction)
        insertResult.onComplete {
        case Success(rowsAffected) => println(s"Inserted $rowsAffected rows")
        case Failure(exception) => println(s"Error inserting user: ${exception.getMessage}")
        }
        db.close()
    }
    
}