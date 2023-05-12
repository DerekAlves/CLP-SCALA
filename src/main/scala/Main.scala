
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
import scala.util.{Success, Failure}
import scala.concurrent.Future
import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory

//TODO: VERIFICAR SE NÃO HÁ REPETIÇÃO DE DEFINIÇÕES DO SERVIDOR NO ARQUIVO OPENACCOUNT
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
    path("login") {
      post {
        complete {
          Future {
            UserLogin(callback)
            "Login realizado com sucesso!"
            
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