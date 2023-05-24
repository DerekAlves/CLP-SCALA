
import slick.jdbc.SQLiteProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import OpenAccount._
import Profile._
import BankAccount._
import Login._
import Password._
import Devices._
import scala.util.{Success, Failure}
import scala.concurrent.Future
import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory

object Main extends App {

  def callback(result: Option[String]): Unit = {
  result match {
    case Some(res) => println(res)
    case None => println("Não foi possível realizar a operação")
  }
}


  val config = ConfigFactory.load()
  // Cria um ActorSystem e um ActorMaterializer para Akka HTTP
  implicit val system = ActorSystem("http-server", ConfigFactory.load().getConfig("akka"))
  implicit val materializer = ActorMaterializer()
  println("System started")
  var userID = "Null"
  val routes =
    path("create-account") {
      post {
        complete {
          Future {
            CreateAccount()
            "Conta criada com sucesso!"
            
          }
        }
      }
    } ~
    path("login") {
      post {
        complete {
          Future {
            userID = UserLogin()
            "Login realizado com sucesso!"
            
          }
        }
      }
    } ~
    path("change-password") {
    post {
      complete {
              ChangePassword(userID)
              "Senha alterada com sucesso!"
        }
        
        
      }
    }~
    path("change-profile") {
    post {
      complete {
              ChangeProfile(userID)
              "Perfil alterado com sucesso!"
        }
        
        
      }
    }~
  path("add-device") {
    post {
      complete {
        CreateUserDevices(userID)
              "Dispositivo adicionado com sucesso!"
            }
          
        }
      }~
  path("view-profile") {
    get {
      complete {
          val userProfile = QueryProfile(userID)
          if (userProfile != null) {
            val (cpf, name, profession, address, email) = userProfile.get
            s"CPF: $cpf, Nome: $name, Profissão: $profession, Endereço: $address, Email: $email"
          } else {
            "Usuário não encontrado"
          }    
      }
    }
  } ~
    path("view-bank-account") {
    get {
      complete {
          val userBankAccount = ViewBankAccount(userID)
          if (userBankAccount != null) {
            val (branch_name, branch_code, account_number, sort_number, balance) = userBankAccount.get
            s"Agência: $branch_name-$branch_code, Número da conta: $account_number-$sort_number, Saldo: $balance"
          } else {
            "Usuário não encontrado"
          }    
      }
    }
  }


  // Inicia o servidor HTTP na porta 8000
  Http().bindAndHandle(routes, "localhost", 8000)

  system.scheduler.scheduleOnce(5.minute) {
    println("System shutdown")
    system.terminate()
    
  }

}