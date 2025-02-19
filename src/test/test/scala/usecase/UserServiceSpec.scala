package usecase

import org.scalatest.funsuite.AsyncFunSuite
import cats.effect.IO
import infrastructure.InMemoryUserRepository

class UserServiceSpec extends AsyncFunSuite {

  test("createUser should create a user and getUser should retrieve it") {
    val program = for {
      repo <- InMemoryUserRepository.create[IO]
      userService = new UserService[IO](repo)
      createdUser <- userService.createUser("Alice")
      fetchedUserResult <- userService.getUser(createdUser.id)
    } yield {
      assert(fetchedUserResult.isRight)
      fetchedUserResult match {
        case Right(user) =>
          assert(user.name == "Alice")
        case _ => fail("User not found")
      }
    }
    program.unsafeToFuture()
  }
}