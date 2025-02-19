package api

import cats.effect.Async
import org.http4s.HttpRoutes
import org.http4s.server.Router
import cats.syntax.semigroupk._ // for <+> operator

object ApiRouter {
  def aggregateRoutes[F[_]: Async](modules: List[ApiModule[F]]): HttpRoutes[F] =
    modules.map(_.routes).reduceLeft(_ <+> _)
}