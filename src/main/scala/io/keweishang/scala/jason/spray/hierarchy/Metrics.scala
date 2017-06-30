package io.keweishang.scala.jason.spray.hierarchy

case class Metric(metric_name: String, metric_value: String)

case class Metrics(timestamp: BigDecimal, hostname: String, instance: Option[String], metrics_array: List[Metric])

case class MetricEvents(events: List[Metrics]) extends Events