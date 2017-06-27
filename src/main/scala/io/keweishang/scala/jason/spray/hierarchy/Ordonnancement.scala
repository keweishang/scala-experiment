package io.keweishang.scala.jason.spray.hierarchy

case class Ordonnancement(timestamp: Long, hostname: String,discriminant: String, etat: String, status: String, referentiel: String, code: String, debut_exec: String, heure_debut: String, heure_fin: String)

case class OrdonnancementEvents(events: List[Ordonnancement]) extends Events