import slick.jdbc.SQLiteProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}
import scala.concurrent.Await
import scala.concurrent.duration._



object Profile{

    def readIntInput(prompt: String): Int = {
        println(prompt)
        scala.io.StdIn.readLine().toInt 
    }

    def readStringInput(prompt: String): String = {
        println(prompt)
        scala.io.StdIn.readLine()
    }

    // define um case class para representar o usuário
    case class User(cpf: String, name: String, profession: String, address: String, email: String, theme: Int)

    // define uma tablea para armazenar os usuários
    class Users(tag: Tag) extends Table[User](tag, "users") {
        def cpf = column[String]("cpf", O.PrimaryKey)
        def name = column[String]("name")
        def profession = column[String]("profession")
        def address = column[String]("address")
        def email = column[String]("email")
        def theme = column[Int]("theme")
        def * = (cpf, name, profession, address, email, theme) <> (User.tupled, User.unapply)
    }
    // cria uma instância da tabela usuários 
    val users = TableQuery[Users]

    // define o URL do banco de dados
    val url = "jdbc:sqlite:sqlite/users.db"

    // cria uma conexão com o banco de dados SQLite
    val db = Database.forURL(url, driver = "org.sqlite.JDBC")

    // insere um novo usuário ao banco de dados
    def CreateProfile (): Unit = {
    
        val newUser = User(
        readStringInput("Digite o CPF: "),
        readStringInput("Digite o nome: "),
        readStringInput("Digite a profissão: "),
        readStringInput("Digite o endereço: "),
        readStringInput("Digite o email: "),
        readIntInput("Digite o número do tema: ")
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

    def ChangeProfile (): Option[User] = {
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
                    email = readStringInput(s"Digite o novo email (${user.email}): "),
                    theme = readIntInput(s"Digite o novo número do tema (${user.theme}): ")
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
    def QueryProfile (): Option[User] = {
        val cpf = readStringInput("Digite o CPF: ")
        val query = users.filter(_.cpf === cpf).result.headOption
        val result = db.run(query)
        val userOption = Await.result(result, Duration.Inf)
        if (userOption.isDefined){
            var userCpf = ""
            var userName = ""
            var userProfession = ""
            var userAddress = ""
            var userEmail = ""
            var userTheme = 0
            userOption.foreach { user =>
                userCpf = user.cpf
                userName = user.name
                userProfession = user.profession
                userAddress = user.address
                userEmail = user.email
                userTheme = user.theme           
            }
            println(s"CPF: $userCpf")
            println(s"Name: $userName")
            println(s"Profession: $userProfession")
            println(s"Address: $userAddress")
            println(s"Email: $userEmail")
            println(s"Theme: $userTheme")
        } else {
            println(s"CPF $cpf não foi encontrado.")
        }
        userOption // retorna o resultado da função, se é um usuário existente ou não
    }

    
}