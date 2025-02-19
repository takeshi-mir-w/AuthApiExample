package api

import cats.effect.Sync
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes, Response}
import org.http4s.dsl.Http4sDsl
import io.circe.generic.auto._
import org.http4s.circe._
import domain.User
import usecase.UserService

final case class CreateUserRequest(name: String)

class UserApiModule[F[_]: Sync](userService: UserService[F]) extends Http4sDsl[F] with ApiModule[F] {
  implicit val createUserDecoder: EntityDecoder[F, CreateUserRequest] = jsonOf[F, CreateUserRequest]
  implicit val userEncoder: EntityEncoder[F, User] = jsonEncoderOf[F, User]

  override def routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "users" / LongVar(id) =>
      for {
        result <- userService.getUser(id)
        resp   <- result.fold(msg => BadRequest(msg), user => Ok(user))
      } yield resp

    case req @ POST -> Root / "users" =>
      for {
        createReq <- req.as[CreateUserRequest]
        user      <- userService.createUser(createReq.name)
        resp      <- Created(user)
      } yield resp
  }
}