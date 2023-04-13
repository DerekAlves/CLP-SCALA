import slick.jdbc.SQLiteProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}
import scala.concurrent.Await
import scala.concurrent.duration._
import java.nio.file.{Files, Paths}
import scala.util.Random
import slick.jdbc.meta.MTable


object BankAccount{

    case class ClientAccount(cpf: String, branch_name: Int, branch_code: Int, bank_account_number: Int, sort_number: Int, account_ID: String)
    
    class ClientAccounts(tag: Tag) extends Table[ClientAccount](tag, "client_bank_accounts"){
        def cpf = column[String]("cpf", O.PrimaryKey, O.Length(11))
        def branch_name = column[Int]("branch_name") //numero da agencia
        def branch_code = column[Int]("branch_code") //digito da agencia
        def bank_account_number = column[Int]("bank_account_number") //numero da conta
        def sort_number = column[Int]("sort_number") //digito da conta
        def account_ID = column[String]("account_ID", O.Length(256)) //id da conta
        def * = (cpf, branch_name, branch_code, bank_account_number, sort_number, account_ID) <> (ClientAccount.tupled, ClientAccount.unapply)
    }
    
    val db = Database.forURL("jdbc:sqlite:sqlite/accounts.db", driver="org.sqlite.JDBC")
    val clientaccounts = TableQuery[ClientAccounts]
    
    def CreateBankDB (): Unit = {
        Await.result(db.run(clientaccounts.schema.create), Duration.Inf)
    }
    
    def tableExists(tableName: String): Boolean = {
        val action = MTable.getTables(tableName)
        val result = Await.result(db.run(action), Duration.Inf)
        result.nonEmpty
    }
    
    def generateRandomString(length: Int): String = {
        val randomChars = Random.alphanumeric.take(length).mkString
        randomChars
    }

    def generateAccountNumbers(): (Int, Int, Int, Int, String) = {
        // Gerar número da agência (4 dígitos)
        val branchName = Random.nextInt(10000)
        // Gerar dígito da agência (1 digito)
        val branchCode = Random.nextInt(10)
        // Gerar número da conta bancária (8 dígitos)
        val bankAccountNumber = Random.nextInt(100000000)
        // Gerar dígito da conta (2 dígitos)
        val sortNumber = Random.nextInt(100)
        // Gerar ID da conta (256 caracteres)
        val accountID = generateRandomString(256)

        (branchName, branchCode, bankAccountNumber, sortNumber, accountID)
    }

    def createBankAccount(cpf: String): Unit = {
        println("Criando conta bancária")
        
        if (!tableExists("client_bank_accounts")) {
            CreateBankDB()
        }
        
        val (branchName, branchCode, bankAccountNumber, sortNumber, accountID) = generateAccountNumbers()
        val bankAccount = ClientAccount(cpf,branchName, branchCode, bankAccountNumber, sortNumber, accountID)
        val insertAction = clientaccounts += bankAccount
        
        // executa a ação de inserção e mostra o resultado
        
        val result = db.run(insertAction)
        result.onComplete {
            case Success(rowsAffected) => println(s"Inserida $rowsAffected linha(s)")
            case Failure(exception) => println(s"Erro ao inserior conta bancária: ${exception.getMessage}")
        }

        db.close()
    }

    def clearBankDB(): Unit = {
        val deleteAction = clientaccounts.delete
        val deleteResult = db.run(deleteAction)
            deleteResult.onComplete {
                case Success(rowsAffected) => println(s"Deleted $rowsAffected rows")
                case Failure(exception) => println(s"Error deleting accounts: ${exception.getMessage}")
            }
            db.close()
    }



}