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


def UserLogin(): Unit = {
    val returnedID = BankAccountLogin()

    if (returnedID != null) {
        val PasswordBool = CheckPassword(returnedID)
        if (PasswordBool) {
            println("Login realizado com sucesso!")
        }
        else {
            println("Senha incorreta!")
            system.terminate()
        }
    }
    val routes = 
            path("change-password") {
                post {
                    complete {
                        Future {
                            ChangePassword(returnedID)
                            "Senha alterada com sucesso!"
                        }
                    }
                }
            } ~
            path("add-device") {
                post {
                    complete {
                        Future {
                            CreateUserDevices(returnedID)
                            "Dispositivo adicionado com sucesso!"
                        }
                    }
                }
            } ~
            path("view-profile"){
              get {
                complete {
                  Future {
                    val userProfile = QueryProfile(returnedID)
                    if (userProfile != null) {
                      val (cpf, name, profession, address, email) = userProfile.get
                      s"CPF: $cpf, Nome: $name, Profissão: $profession, Endereço: $address, Email: $email"
                    } else {
                      "Usuário não encontrado"
                    }
                  }
                }
              }
            }
  
  }
  

}