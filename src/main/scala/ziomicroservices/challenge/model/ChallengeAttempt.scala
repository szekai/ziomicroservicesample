package ziomicroservices.challenge.model

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

case class ChallengeAttempt(
                             user: User,
                             challenge: Challenge,
                             resultAttempt: Int,
                             var check: Option[Boolean] = None
                           )

object ChallengeAttempt:
  given JsonEncoder[ChallengeAttempt] = DeriveJsonEncoder.gen[ChallengeAttempt]
  given JsonDecoder[ChallengeAttempt] = DeriveJsonDecoder.gen[ChallengeAttempt]
