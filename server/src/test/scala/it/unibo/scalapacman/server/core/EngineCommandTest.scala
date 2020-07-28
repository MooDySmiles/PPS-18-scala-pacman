package it.unibo.scalapacman.server.core

import akka.actor.testkit.typed.scaladsl.{ScalaTestWithActorTestKit, TestProbe}
import akka.actor.typed.ActorRef
import it.unibo.scalapacman.server.util.Settings
import org.scalatest.wordspec.AnyWordSpecLike

class EngineCommandTest extends ScalaTestWithActorTestKit with AnyWordSpecLike {

  val fakeGameId = "fakeCreateGameId"

  private var engineActor: ActorRef[Engine.EngineCommand] = _
  private var watcherPlayerProbe: TestProbe[Engine.UpdateCommand] = _

  override def beforeAll(): Unit = {
    engineActor = spawn(Engine(fakeGameId))
    watcherPlayerProbe = createTestProbe[Engine.UpdateCommand]()

    engineActor ! Engine.RegisterPlayer(watcherPlayerProbe.ref)
  }

  "An Engine actor" must {
    "stops after pause command" in {

      engineActor ! Engine.Pause()

      watcherPlayerProbe.expectNoMessage(Settings.gameRefreshRate * 2)
    }

    "resume game after resume command" in {

      engineActor ! Engine.Pause()
      //attendo che il gioco sia messo in pausa
      Thread.sleep(Settings.gameRefreshRate.toMillis * 2)

      engineActor ! Engine.Resume()
      watcherPlayerProbe.receiveMessage(Settings.gameRefreshRate * 2) match {
        case Engine.UpdateMsg(_) =>
        case _ => fail()
      }
    }
  }
}