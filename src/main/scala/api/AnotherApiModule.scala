package api

import cats.effect.Sync
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

class AnotherApiModule[F[_]: Sync] extends Http4sDsl[F] with ApiModule[F] {
  override def routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "hello" =>
      Ok("Hello from Another API!")
  }
}