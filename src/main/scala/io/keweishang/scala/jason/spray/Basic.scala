package io.keweishang.scala.jason.spray

/**
  * Created by kshang on 27/06/2017.
  *
  * In spray-jsons terminology a 'JsonProtocol' is nothing but a bunch of implicit values of type JsonFormat[T],
  * whereby each JsonFormat[T] contains the logic of how to convert instance of T to and from JSON. All JsonFormat[T]s
  * of a protocol need to be "mece" (mutually exclusive, collectively exhaustive), i.e. they are not allowed to overlap
  * and together need to span all types required by the application.
  * This may sound more complicated than it is. spray-json comes with a DefaultJsonProtocol, which already covers all of
  * Scala's value types as well as the most important reference and collection types. As long as your code uses nothing
  * more than these you only need the DefaultJsonProtocol.
  */
object Basic extends App {

  case class Color(red: Int, green: Int, blue: Int, name: Option[String] = None)
  case class Rainbow(name: String, colors: List[Color])

  import spray.json._

  // DefaultJsonProtocol contains all the jsonFormats for default scala types.
  // We just have to add our customised type here.
  object MyJsonProtocol extends DefaultJsonProtocol {
    implicit val colorFormat = jsonFormat4(Color)
    implicit val rainbowFormat = jsonFormat2(Rainbow)
  }

  import MyJsonProtocol._

  // String <--> Json Abstract Syntax Tree
  val source =
    """{ "some": "JSON source" }"""
  val jsonAst = source.parseJson // or JsonParser(source)
  val json = jsonAst.prettyPrint

  // Json Abstract Syntax Tree <--> Scala object
  val jsonAst2 = List(1, 2, 3).toJson
  val myObject = jsonAst2.convertTo[List[Int]]

  // Custom case class: String <--> AST <--> Scala object
  val colorStr = Color(95, 158, 160, Some("blue")).toJson.prettyPrint
  printString(colorStr)
  val colorObj = colorStr.parseJson.convertTo[Color]
  printObj(colorObj)
  println()

  // Custom case class containing custom case class
  val rainbowStr = Rainbow("black-white-rainbow", List(Color(0,0,0, Some("white")), Color(255,255,255, Some("black")))).toJson.prettyPrint
  printString(rainbowStr)
  val rainbowObj = rainbowStr.parseJson.convertTo[Rainbow]
  printObj(rainbowObj)
  println()

  // Required fields. By default, all fields of case class are required
  val missRequiredString =
    """
      |{
      |  "red": 95,
      |  "blue": 160,
      |  "name": "blue"
      |}
    """.stripMargin
  // AST is schema agnostic
  val missRequiredJson = missRequiredString.parseJson
//  Only when we convert AST to Scala object, we encounter spray.json.DeserializationException: Object is missing required member 'green'
//  val unconstructableScalaObj = missRequiredJson.convertTo[Color]
//  printObj(unconstructableScalaObj)

  // Optional fields. Case class field type is Option[T].
  val missOptString =
    """
      |{
      |  "red": 95,
      |  "green": 158,
      |  "blue": 160
      |}
    """.stripMargin
  val colorMissOptObj = missOptString.parseJson.convertTo[Color]
  printObj(colorMissOptObj)
  val colorMissOptString = colorMissOptObj.toJson.prettyPrint
  printString(colorMissOptString)
  println()

  def printString(text: String) = println(s"Json string:\n$text")
  def printObj(text: Any) = println(s"Scala object:\n$text")
}
