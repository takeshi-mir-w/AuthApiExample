package infrastructure

import cats.effect.Sync
import org.http4s._
import org.http4s.dsl.Http4sDsl
import usecase.UserService
import domain.User
import io.circe.generic.auto._
import org.http4s.circe._

class UserRoutes[F[_]: Sync](userService: UserService[F]) extends Http4sDsl[F] {
  // jsonエンコーダ/デコーダのインポート
  implicit val userDecoder: EntityDecoder[F, CreateUserRequest] = jsonOf[F, CreateUserRequest]
  implicit val userEncoder: EntityEncoder[F, User] = jsonEncoderOf[F, User]

  // POSTリクエスト時の受信データの型を定義
  final case class CreateUserRequest(name: String)

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    // GET /users/{id} で取得
    case GET -> Root / "users" / LongVar(id) =>
      for {
        result <- userService.getUser(id)
        resp   <- result.fold(msg => BadRequest(msg), user => Ok(user))
      } yield resp

    // POST /users で新規作成
    case req @ POST -> Root / "users" =>
      for {
        createReq <- req.as[CreateUserRequest]
        user      <- userService.createUser(createReq.name)
        resp      <- Created(user)
      } yield resp
  }
}