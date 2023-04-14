
import slick.jdbc.SQLiteProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}
import Profile._
import BankAccount._
import Password._
import Devices._
object OpenAccount {

    def CreateAccount (): Unit = {
        println("Definição do perfil de usuário:")
        val returnedCPF = CreateProfile()
        val returnedAccountID = createBankAccount(returnedCPF)
        CreatePasswords(returnedAccountID)
        CreateUserDevices(returnedAccountID)
    }


}
