package io.keweishang.scala.akka.cluster

import akka.actor.{ActorSystem, Props}
import akka.routing.BalancingPool
import com.typesafe.config.ConfigFactory

/**
  * Created by kshang on 30/06/2017.
  */
object ClusterMain extends App {
  val system = ActorSystem("helloAkka")
  val clusterController = system.actorOf(Props[ClusterController], "clusterController")

//  val workers = system.actorOf(BalancingPool(5).props(Props[Greetor]), "my-balancing-pool-router")
//
//  ClusterReceptionistExtension(system).registerService(workers)
}
