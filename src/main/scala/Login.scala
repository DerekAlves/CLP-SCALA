import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
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
  val config = ConfigFactory.load()

  def UserLogin(callback: Option[String] => Unit): Unit = {
    val returnedID = BankAccountLogin()

    if (returnedID != null) {
      val PasswordBool = CheckPassword(returnedID)
      if (PasswordBool) {
        println("Login realizado com sucesso!")
        callback(Some(returnedID)) // chama a função de retorno de chamada passando o ID do usuário logado
      } else {
        println("Senha incorreta!")
        system.terminate()
      }
    } else {
      callback(None) // chama a função de retorno de chamada passando None para indicar que o usuário não foi encontrado
    }
  }

  val routes = (userID: Option[String]) =>
    path("change-password") {
      post {
        complete {
          userID match {
            case Some(id) =>
              Future {
                ChangePassword(id)
                "Senha alterada com sucesso!"
              }
            case None => "Usuário não encontrado"
          }
        }
      }
    } ~
    path("add-device") {
      post {
        complete {
          userID match {
            case Some(id) =>
              Future {
                CreateUserDevices(id)
                "Dispositivo adicionado com sucesso!"
              }
            case None => "Usuário não encontrado"
          }
        }
      }
    } ~
    path("view-profile") {
      get {
        complete {
          userID match {
            case Some(id) =>
              Future {
                val userProfile = QueryProfile(id)
                if (userProfile != null) {
                  val (cpf, name, profession, address, email) = userProfile.get
                  s"CPF: $cpf, Nome: $name, Profissão: $profession, Endereço: $address, Email: $email"
                } else {
                  "Usuário não encontrado"
                }
              }
            case None => "Usuário não encontrado"
          }
        }
      }
    }
}