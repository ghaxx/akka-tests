package pl.http.clients


import com.sun.jersey.api.client.{Client, ClientResponse}

import scala.concurrent.{Future, Promise}
import com.sun.jersey.client.apache4.{ApacheHttpClient4, config}
import com.sun.jersey.client.apache4.config.DefaultApacheHttpClient4Config

/**
  * This one is tricky. Documentation is scarce and dependencies are not linked, so -core and -client had to be adde manually
  */
object JerseyApacheClient4 extends App with ClientTestScenario {

//  private val executorService = Executors.newSingleThreadExecutor()
//  implicit val ec = ExecutionContext.fromExecutorService(executorService)
//    implicit val ec = ExecutionContext.fromExecutorService(Executors.newWorkStealingPool())
//    implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(10))

  implicit class ScalaAsyncHttpClient(val httpClient: Client) extends AnyVal {
    def asyncExecute(request: String): Future[ClientResponse] = {
      asyncExecuteAndMap(request)(identity)
    }
    def asyncExecuteAsString(request: String): Future[String] = {
      asyncExecuteAndMap(request)(r => r.getEntity(classOf[String]))
    }
    def asyncExecuteAndMap[T](request: String)(mapper: ClientResponse => T): Future[T] = {
      val p = Promise[T]()
      val response = httpClient.resource(request).get(classOf[ClientResponse])
      p.success(mapper(response))
      response.close()
      p.future
    }
  }

  val clientConfig = new com.sun.jersey.client.apache4.config.DefaultApacheHttpClient4Config()
  val httpClient: Client = com.sun.jersey.client.apache4.ApacheHttpClient4.create(clientConfig)
//  val httpClient = HttpAsyncClientBuilder
//    .create
//    .build
  val get1 = "http://localhost:8080/count"

  val name = "jersey"
  def makeRequest: Future[String] = httpClient.asyncExecuteAsString(get1)
  runTest()
}
