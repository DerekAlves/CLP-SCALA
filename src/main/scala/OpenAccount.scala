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

object OpenAccount {
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

  def CreateAccount(): Unit = {
    println("Definição do perfil de usuário:")
    val returnedCPF = CreateProfile()
    val returnedAccountID = createBankAccount(returnedCPF)
    CreatePasswords(returnedAccountID)
    CreateUserDevices(returnedAccountID)
    
    val mediatorRequest = MediatorRequest("Abrir conta", createAccountPath, Some(returnedAccountID.toJson.toString))
    
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = s"$mediatorHost$createAccountPath",
      entity = HttpEntity(ContentTypes.`application/json`, mediatorRequest.toJson.toString)
    )
    val mediator = Mediator(request)
    val responseFuture: Future[HttpResponse] = mediator.send()
    
    responseFuture.onComplete {
      case Success(response) => 
        response.status match {
          case StatusCodes.OK => 
            println("Conta criada com sucesso!")
          case _ => 
            println(s"Erro ao criar conta. Status code: ${response.status}")
        }
        
        mediator.receive(response).onComplete {
          case Success(responseContent) => 
            println(s"Conteúdo da resposta do mediador: $responseContent")
          case Failure(exception) => 
            println(s"Erro ao ler conteúdo da resposta: ${exception.getMessage}")
        }
        
      case Failure(exception) => 
        println(s"Erro ao criar conta: ${exception.getMessage}")
    }
    system.terminate()
  }
}
