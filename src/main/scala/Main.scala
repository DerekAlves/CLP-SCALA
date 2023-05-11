
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
import scala.util.{Success, Failure}
import scala.concurrent.Future
import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory

//TODO: VERIFICAR SE NÃO HÁ REPETIÇÃO DE DEFINIÇÕES DO SERVIDOR NO ARQUIVO OPENACCOUNT
object Main extends App {

  val config = ConfigFactory.load()
  // Cria um ActorSystem e um ActorMaterializer para Akka HTTP
  implicit val system = ActorSystem("http-server", ConfigFactory.load().getConfig("akka"))
  implicit val materializer = ActorMaterializer()
  println("System started")
  // Define as rotas para as solicitações HTTP

  // Crie uma rota para a função CreateAccount() através do método POST
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
    path("change-profile") {
      post {
        complete {
          Future {
            ChangeProfile()
            "Perfil do usuário alterado com sucesso!"
          }
        }
      }
    } ~
    path("query-profile") {
      get {
        complete {
          Future {
            val userProfile = QueryProfile()
            if (userProfile != null) {
              val (cpf, name, profession, address, email, theme) = userProfile.get
              s"CPF: $cpf, Nome: $name, Profissão: $profession, Endereço: $address, Email: $email"
            } else {
              "Usuário não encontrado"
            }
          }
        }
      }
    }
  


  // Inicia o servidor HTTP na porta 8000
  Http().bindAndHandle(routes, "localhost", 8023)

  // Encerra o sistema após 1 minuto
  system.scheduler.scheduleOnce(1.minute) {
    println("System shutdown")
    system.terminate()
    
  }

}