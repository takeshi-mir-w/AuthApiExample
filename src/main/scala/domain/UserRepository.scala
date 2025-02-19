package domain

trait UserRepository[F[_]] {
  def getUser(id: Long): F[Option[User]]
  def createUser(user: User): F[User]
}