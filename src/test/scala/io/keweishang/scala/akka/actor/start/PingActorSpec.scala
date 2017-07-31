package io.keweishang.scala.akka.actor.start

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

/**
  * Created by kshang on 31/07/2017.
  */
class PingActorSpec extends FlatSpec with Matchers with BeforeAndAfterAll {

  implicit val system = ActorSystem()

  class scoping extends TestKit(system) with ImplicitSender {
    val actorB = TestProbe("actorB")
    val actorA = TestActorRef(new PingActor(actorB.ref))
  }

  "A request sent to Actor B within the preStart() of actor A" should
    "be received by actor B and response from actor B should be received in Actor A" in new scoping {
    // The following works but let's do a test without sleep
    //    val actorB = system.actorOf(PongActor.props())
    //    val actorA = system.actorOf(PingActor.props(actorB))
    //    Thread.sleep(5000);

    // All probe stubbing has to be done after the message is sent to the actor under test.
    // When you call expectXXX, the test stops and waits for that condition to become true
    // (within an allowed amount of time). If you do this before sending the message to the test actor,
    // then that condition will never happen and your tests will always fail.
    actorB.expectMsg("Ping")
    actorB.reply("Pong")
    actorA.underlyingActor.ponged shouldEqual true
  }
}
