package ziomicroservices.challenge.controller

import zio.*
import zio.http.*
import zio.json.*
import zio.test.*
import zio.test.Assertion.equalTo
import ziomicroservices.challenge.model.{Challenge, ChallengeAttempt, User}
import ziomicroservices.challenge.repository.{ChallengeAttemptRepository, InMemoryChallengeAttemptRepository}
import ziomicroservices.challenge.service.{ChallengeServiceImpl, RandomGeneratorServiceImpl}

object ChallengeControllerTest extends ZIOSpecDefault {
  TestRandom.setSeed(42L)
  val app = ChallengeController()
  def spec = {
    suite("Test Challenge Controller")(
      test("Get attempts of users") {
        val entity = ChallengeAttempt(User("TestUser"), Challenge(2, 2), 4)
        for {
          repo <- ZIO.service[ChallengeAttemptRepository]
          _ <- repo.save(entity)
          response <- app.runZIO(Request.get(URL(Path.root ++ Path("challenges/users/TestUser"))) ).map(x => x.body)
        } yield assert(response)(equalTo(Response.json(List(entity).toJson).body))
      },
      test("Get result of a previous attempt") {
        val entity = ChallengeAttempt(User("TestUser"), Challenge(2, 2), 4)
        for {
          repo <- ZIO.service[ChallengeAttemptRepository]
          id <- repo.save(entity)
          response <- app.runZIO(Request.get(URL(Path.root ++ Path(s"challenges/results/$id")))).map(x => x.body)
        } yield assert(response)(equalTo(Response.json(entity.toJson).body))
      },
      test("Controller should return True when validating challenge attempt") {
        val app = ChallengeController()
        val req = Request.post(
          URL(Path.root ++ Path("challenges/attempt")),
          Body.fromString("""{"user":{"alias":"TestUser"},"challenge":{"valueA":2,"valueB":2},"resultAttempt":4}"""))
        assertZIO(app.runZIO(req).map(x => x.body))(equalTo(Response.json("true").body))
      },
      test("Controller should return False when validating challenge attempt") {
        val app = ChallengeController()
        val req = Request.post(
          URL(Path.root ++ Path("challenges/attempt")),
          Body.fromString("""{"user":{"alias":"TestUser"},"challenge":{"valueA":2,"valueB":2},"resultAttempt":5}"""))
        assertZIO(app.runZIO(req).map(x => x.body))(equalTo(Response.json("false").body))
      }).provide(
      RandomGeneratorServiceImpl.layer,
      ChallengeServiceImpl.layer,
      InMemoryChallengeAttemptRepository.layer
    )
  }

}
