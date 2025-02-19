import cats.effect.{IO, IOApp}
import com.comcast.ip4s._
import org.http4s.implicits._
import org.http4s.ember.server.EmberServerBuilder
import api.{ApiModule, ApiRouter, UserApiModule, AnotherApiModule}
import infrastructure.InMemoryUserRepository
import usecase.UserService

object Main extends IOApp.Simple {

  override def run: IO[Unit] =
    for {
      // アプリケーション全体で利用するリポジトリの生成
      repo <- InMemoryUserRepository.create[IO]
      // ユースケース層の生成（User に関する処理）
      userService = new UserService[IO](repo)

      // 複数のAPIモジュールを生成
      userApiModule    = new UserApiModule[IO](userService)
      anotherApiModule = new AnotherApiModule[IO]

      // APIモジュールをリストで集約（User など内部は Main で直接参照しない）
      modules: List[ApiModule[IO]] = List(
        userApiModule,
        anotherApiModule
        // 他のAPIモジュールがあればここに追加
      )

      // 集約したルートを生成
      httpApp = ApiRouter.aggregateRoutes[IO](modules).orNotFound

      // サーバを起動
      _ <- EmberServerBuilder.default[IO]
        .withHost(ipv4"0.0.0.0")
        .withPort(port"8080")
        .withHttpApp(httpApp)
        .build
        .use(_ => IO.never)
    } yield ()
}