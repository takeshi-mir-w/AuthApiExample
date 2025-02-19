package usecase

import domain.{User, UserRepository}
import cats.Monad
import cats.syntax.all._

class UserService[F[_]: Monad](userRepo: UserRepository[F]) {

  def getUser(id: Long): F[Either[String, User]] =
    userRepo.getUser(id).map {
      case Some(user) => Right(user)
      case None       => Left(s"User with id $id not found")
    }

  def createUser(name: String): F[User] = {
    // シンプルな実装例。実際はID生成ロジック等を検討してください。
    val newUser = User(System.currentTimeMillis(), name)
    userRepo.createUser(newUser)
  }
}
