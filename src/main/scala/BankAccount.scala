import slick.jdbc.SQLiteProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}
import scala.concurrent.Await
import scala.concurrent.duration._
import java.nio.file.Paths

object BankAccount{

    case class ClientAccount(cpf: String, branch_name: Int, branch_code: Int, bank_account_number: Int, sort_number: Int, account_ID: String)
    
    class ClientAccounts(tag: Tag) extends Table[ClientAccount](tag, "clientaccounts"){
        def cpf = column[String]("cpf", O.PrimaryKey, O.Length(11))
        def branch_name = column[Int]("branch_name") //numero da agencia
        def branch_code = column[Int]("branch_code") //digito da agencia
        def bank_account_number = column[Int]("bank_account_number") //numero da conta
        def sort_number = column[Int]("sort_number") //digito da conta
        def account_ID = column[String]("account_ID", O.Length(256)) //id da conta
        def * = (cpf, branch_name, branch_code, bank_account_number, sort_number, account_ID) <> (ClientAccount.tupled, ClientAccount.unapply)
    }
    val db = Database.forURL("jdbc:sqlite:sqlite/clientAccounts.db", driver="org.sqlite.JDBC")
    val clientaccounts = TableQuery[ClientAccounts]
    def CreateBankDB (): Unit = {
        println("Criando banco de dados")
        Await.result(db.run(clientaccounts.schema.create), Duration.Inf)
    }

}