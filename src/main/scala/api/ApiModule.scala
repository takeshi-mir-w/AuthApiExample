package api

import org.http4s.HttpRoutes

trait ApiModule[F[_]] {
  def routes: HttpRoutes[F]
}