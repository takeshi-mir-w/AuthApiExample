package api

import cats.effect.IO
import io.circe.Json
import io.circe.literal._
import io.circe.parser._
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._
import org.http4s.Status
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import org.typelevel.cats.effect.testing.scalatest.AsyncIOSpec
import infrastructure.InMemoryUserRepository
import usecase.UserService

class UserApiModuleSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers {
  "UserApiModule routes" - {
    "POST /users should create a user" in {
      for {
        repo <- InMemoryUserRepository.create[IO]
        userService = new UserService[IO](repo)
        api = new UserApiModule[IO](userService)
        // POST リクエスト作成。circe を使って JSON を生成
        request = Request[IO](method = Method.POST, uri = uri"/users")
          .withEntity(Json.obj("name" -> Json.fromString("Bob")))
        response <- api.routes.orNotFound.run(request)
        body <- response.as[Json]
      } yield {
        response.status shouldBe Status.Created
        (body.hcursor.downField("name").as[String]).toOption.get shouldBe "Bob"
      }
    }

    "GET /users/:id should return a user" in {
      for {
        repo <- InMemoryUserRepository.create[IO]
        userService = new UserService[IO](repo)
        createdUser <- userService.createUser("Charlie")
        api = new UserApiModule[IO](userService)
        request = Request[IO](method = Method.GET, uri = uri"/users" / createdUser.id.toString)
        response <- api.routes.orNotFound.run(request)
        body <- response.as[Json]
      } yield {
        response.status shouldBe Status.Ok
        (body.hcursor.downField("name").as[String]).toOption.get shouldBe "Charlie"
      }
    }
  }
}