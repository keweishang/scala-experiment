package io.keweishang.scala.jason.spray.hierarchy

import spray.json._

object JsonProtocol extends DefaultJsonProtocol with App {

  implicit val sureveillanceFormat = jsonFormat6(Surveillance)
  implicit val ordonnancementFormat = jsonFormat10(Ordonnancement)
  implicit val metriqueFormat = jsonFormat2(Metric)
  implicit val metriquesFormat = jsonFormat4(Metrics)
  implicit val surveiEventsFormat = jsonFormat1(SurveillanceEvents)
  implicit val ordoEventsFormat = jsonFormat1(OrdonnancementEvents)
  implicit val metricEventsFormat = jsonFormat1(MetricEvents)

  implicit object ApiRequestJsonFormat extends JsonFormat[ApiRequest] {

    def write(a: ApiRequest) = a.events match {
      case events: MetricEvents => JsObject(
        "object_type" -> JsString(a.object_type),
        "heartbeat" -> JsNumber(a.heartbeat),
        "source" -> JsString(a.source),
        "events" -> events.events.toJson
      )
      case events: OrdonnancementEvents => JsObject(
        "object_type" -> JsString(a.object_type),
        "heartbeat" -> JsNumber(a.heartbeat),
        "source" -> JsString(a.source),
        "events" -> events.events.toJson
      )
      case events: SurveillanceEvents => JsObject(
        "object_type" -> JsString(a.object_type),
        "heartbeat" -> JsNumber(a.heartbeat),
        "source" -> JsString(a.source),
        "events" -> events.events.toJson
      )
    }

    def read(value: JsValue) = {

      value.asJsObject.getFields("object_type", "heartbeat", "source", "events") match {
        case Seq(JsString(object_type), JsNumber(heartbeat), JsString(source), events) =>
          if (object_type == "surveillance") ApiRequest(object_type, heartbeat, source, SurveillanceEvents(events.convertTo[List[Surveillance]]))
          else if (object_type == "ordonnancement") ApiRequest(object_type, heartbeat, source, OrdonnancementEvents(events.convertTo[List[Ordonnancement]]))
          else if (object_type == "metrics") ApiRequest(object_type, heartbeat, source, MetricEvents(events.convertTo[List[Metrics]]))
          else throw new DeserializationException(s"Invalid object_type value: ${object_type}")
        case _ => throw new DeserializationException("ApiRequest expected: source field and events field ")
      }
    }
  }

  // test
  val metricJson =
    """
      |{
      |  "object_type":"metrics",
      |  "heartbeat":1496755564224,
      |  "source":"lot-a-1",
      |  "events":[
      |    {
      |      "timestamp":1496755582985,
      |      "hostname":"hostname1",
      |      "instance":"instance1",
      |      "metrics_array":[
      |        {
      |          "metric_name":"metric1_1",
      |          "metric_value":"value1_1"
      |        },
      |        {
      |          "metric_name":"metric1_2",
      |          "metric_value":"value1_2"
      |        }
      |      ]
      |    }
      |  ]
      |}
    """.stripMargin
  val ordonnancementJson =
    """
      |{
      |  "object_type":"ordonnancement",
      |  "heartbeat":1496755770669,
      |  "source":"lot-a-1",
      |  "events":[
      |    {
      |      "timestamp":1496755791486,
      |      "hostname":"hostname1",
      |      "discriminant":"discriminant1",
      |      "etat":"etat1",
      |      "status":"status1",
      |      "referentiel":"ref1",
      |      "code":"code1",
      |      "debut_exec":"debut1",
      |      "heure_debut":"20170530113846",
      |      "heure_fin":"20170530113846"
      |    }
      |  ]
      |}
    """.stripMargin
  val surveiJson =
    """
      |{
      |  "object_type":"surveillance",
      |  "heartbeat":1496755870128,
      |  "source":"lot-a-1",
      |  "events":[
      |    {
      |      "criticite":"WARN",
      |      "timestamp":1496755880665,
      |      "hostname":"hostname1",
      |      "discriminant":"discriminant1",
      |      "module":"module1",
      |      "message":"hello world1"
      |    }
      |  ]
      |}
    """.stripMargin

  check(metricJson)
  check(ordonnancementJson)
  check(surveiJson)

  def check(text: String) = {
    val json = text.parseJson
    val obj = json.convertTo[ApiRequest]
    println(obj)
    val str = obj.toJson.prettyPrint
    println(str)
  }
}
