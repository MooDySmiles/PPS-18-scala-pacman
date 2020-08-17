package it.unibo.scalapacman.server.core

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import akka.actor.typed.{ActorRef, MailboxSelector}
import it.unibo.scalapacman.lib.model.GhostType
import it.unibo.scalapacman.server.util.ConfLoader
import org.scalatest.wordspec.AnyWordSpecLike

class GhostActRegistrationTest extends ScalaTestWithActorTestKit(ConfLoader.config) with AnyWordSpecLike {

  "A Ghost actor" must {

    "register himself at startup" in {
      val engineProbe = createTestProbe[Engine.EngineCommand]()
      val fakeGameId = "fakeCreateGameId"
      val ghostType = GhostType.BLINKY

      val props = MailboxSelector.fromConfig("server-app.ghost-mailbox")
      val ghostActor: ActorRef[Engine.UpdateCommand] = spawn(GhostAct(fakeGameId, engineProbe.ref, ghostType), props)

      engineProbe.receiveMessage() match {
        case Engine.RegisterGhost(`ghostActor`, `ghostType`) =>
        case _ => fail()
      }
    }
  }
}
