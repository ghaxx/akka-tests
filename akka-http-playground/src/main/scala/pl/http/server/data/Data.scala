package pl.http.server.data

object Data
  extends akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
with spray.json.DefaultJsonProtocol {

  implicit val aNumberFormat = jsonFormat1(ANumber.apply)

}
