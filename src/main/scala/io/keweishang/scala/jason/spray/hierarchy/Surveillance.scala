package io.keweishang.scala.jason.spray.hierarchy

case class Surveillance(criticite: String, timestamp: Long,hostname: String, discriminant: String, module: String, message: String)

case class SurveillanceEvents(events: List[Surveillance]) extends Events