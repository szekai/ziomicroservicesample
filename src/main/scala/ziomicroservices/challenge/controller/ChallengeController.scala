package ziomicroservices.challenge.controller

import zio.ZIO
import zio.json.*
import zio.http.*
import zio.http.Header.{AccessControlAllowMethods, AccessControlAllowOrigin, Origin}
import zio.http.Middleware.{CorsConfig, cors}
import ziomicroservices.challenge.model.{Challenge, ChallengeAttempt}
import ziomicroservices.challenge.service.ChallengeService

object ChallengeController:

  val config: CorsConfig =
    CorsConfig(
      allowedOrigin = {
        case origin@Origin.Value(_, host, _) => Some(AccessControlAllowOrigin.All)
        case _ => Some(AccessControlAllowOrigin.All)
      },
      allowedMethods = AccessControlAllowMethods(Method.PUT, Method.DELETE, Method.GET, Method.POST),
    )

  def apply(): Routes[ChallengeService, Nothing] =
    Routes(
      Method.GET / Root / "challenges" / "random"  -> handler(ChallengeService.createRandomMultiplication().map(response => Response.json(response.toJson))),
      Method.GET / "greet" -> handler { (req: Request) =>
        val name = req.queryParamToOrElse("name", "World")
        Response.text(s"Hello $name!")
      },
      Method.POST / Root / "challenges" / "attempt" -> handler { (req: Request) => (
            for {
              u <- req.body.asString.map(_.fromJson[ChallengeAttempt])
              r <- u match
                case Left(e) =>
                  ZIO
                    .debug(s"Failed to parse the input: $e")
                    .as(Response.text(e)) //.withStatus(Status.BadRequest))
                case Right(u) =>
                  ChallengeService.checkAttempt(u).map(out => Response.json(out.toJson))
            } yield r).orDie
      },
      Method.GET / "challenges" /"results" / string("id") ->
        handler { (id: String, req: Request) =>
            ChallengeService.getAttemptById(id).map(out => Response.json(out.toJson)).orDie
        },
      Method.GET / Root / "challenges" /"users" / string("userAlias") ->
        handler { (userAlias: String, req: Request) =>
          ChallengeService.getAttemptsByUser(userAlias).map(out => Response.json(out.toJson)).orDie
        }

    )@@ cors(config)
