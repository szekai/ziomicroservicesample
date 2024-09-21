package ziomicroservices.challenge

import zio.http.Server
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}
import ziomicroservices.challenge.controller.ChallengeController
import ziomicroservices.challenge.repository.InMemoryChallengeAttemptRepository
import ziomicroservices.challenge.service.{ChallengeServiceImpl, RandomGeneratorServiceImpl}

object Main extends ZIOAppDefault {
  def run: ZIO[Environment with ZIOAppArgs with Scope, Throwable, Any] =
    Server
      .serve(
        ChallengeController.apply()
      )
      .provide(
        Server.defaultWithPort(8080),
        RandomGeneratorServiceImpl.layer,
        ChallengeServiceImpl.layer,
        InMemoryChallengeAttemptRepository.layer
      )
}
