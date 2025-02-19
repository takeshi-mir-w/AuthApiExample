package infrastructure

import domain.{User, UserRepository}
import cats.effect.Sync
import cats.effect.Ref
import cats.syntax.all._

class InMemoryUserRepository[F[_]: Sync] private (ref: Ref[F, Map[Long, User]])
  extends UserRepository[F] {

  override def getUser(id: Long): F[Option[User]] =
    ref.get.map(_.get(id))

  override def createUser(user: User): F[User] =
    ref.update(_ + (user.id -> user)).as(user)
}

object InMemoryUserRepository {
  def create[F[_]: Sync]: F[InMemoryUserRepository[F]] =
    Ref.of[F, Map[Long, User]](Map.empty).map(new InMemoryUserRepository[F](_))
}