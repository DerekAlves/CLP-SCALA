import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import Profile._
import BankAccount._
import Password._
import Devices._
import spray.json.DefaultJsonProtocol._
import spray.json._
import scala.concurrent.Future
import scala.util.{Success, Failure}
import slick.jdbc.SQLiteProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import akka.protobufv3.internal.Duration
import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory

object Login {
  implicit val system = ActorSystem("http-server", ConfigFactory.load().getConfig("akka"))
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  val config = ConfigFactory.load()
  val mediatorHost = "http://localhost:8023"
  val createAccountPath = "/create-account"
  
  case class MediatorRequest(`type`: String, action: String, content: Option[String])
  
  implicit val mediatorRequestFormat = jsonFormat3(MediatorRequest)
  
  case class Mediator(request: HttpRequest) {
    // Função send que envia uma solicitação HTTP para o mediador
    def send(): Future[HttpResponse] = {
      Http().singleRequest(request)
    }

    // Função que recebe uma resposta do mediador e retorna o conteúdo da resposta
    def receive(response: HttpResponse): Future[String] = {
      Unmarshal(response.entity).to[String]
    }
  }

  def UserLogin(): Unit = {
    val returnedID = BankAccountLogin()
    if (returnedID != null){
        val PasswordBool = CheckPassword(returnedID)
        if (PasswordBool){
            println("Login realizado com sucesso!")
            // Colocar aqui todas as funcionalidades que o usuário pode realizar após o login: Ver perfil, mudar senha, mudar perfil, ver movimentações, etc.
        }
    }
 }
}