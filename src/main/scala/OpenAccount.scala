
import slick.jdbc.SQLiteProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}
import Profile._

object OpenAccount {

    def CreateAccount (): Unit = {
        println("Definição do perfil de usuário:")
        CreateProfile()
         
    }


}
