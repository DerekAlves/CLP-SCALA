
import slick.jdbc.SQLiteProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}
import Delete._
import OpenAccount._
object Main extends App {

  println("Digite 1 para inserir usuário ou 2 para apagar todos os usuários (Apenas para testes, isso deve ser removido depois):")
  
  val input = scala.io.StdIn.readInt()
  input match {
    case 1 => CreateAccount()// Abrir conta, essa função terá a opção de criar perfil de usuário. 
    //Depois definir uma opção de login para fazer as funções de gerenciamento da conta, como alterar tema, alterar senha, ver movimentações, etc.
    case 2 => deleteAllUsers()
    case _ => println("Opção inválida")
  }
  //
  
} 
