
import slick.jdbc.SQLiteProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}
import OpenAccount._
import Profile._
import BankAccount._
object Main extends App {

  println("Digite\n1 para inserir usuário\n2 para limpar todos os bancos de dados (Apenas para testes, isso deve ser removido depois)" +
    "\n3 para mudar perfil do usuário\n4 para consultar um usuário")
  
  val input = scala.io.StdIn.readInt()
  input match {
    case 1 => CreateAccount()// Abrir conta, essa função terá a opção de criar perfil de usuário. 
    //Depois definir uma opção de login para fazer as funções de gerenciamento da conta, como alterar tema, alterar senha, ver movimentações, etc.
    case 2 => {
      deleteAllUsers()
      clearBankDB()
    }
    case 3 => ChangeProfile()
    case 4 => {
      val userProfile = QueryProfile()
      if (userProfile != null) {
        val (cpf, name, profession, address, email, theme) = userProfile.get
      }
    }  
    case _ => println("Opção inválida")
  }
  
} 
