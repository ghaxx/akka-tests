package pl.github.client

import com.typesafe.config.ConfigFactory
import io.netty.handler.codec.http.HttpHeaders
import io.netty.handler.ssl.{SslContextBuilder, SslProvider}
import org.asynchttpclient.netty.ssl.InsecureTrustManagerFactory
import org.asynchttpclient.proxy.ProxyServer
import org.asynchttpclient.{DefaultAsyncHttpClient, DefaultAsyncHttpClientConfig}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.io.StdIn

object Client extends App {
  import pl.http.clients.ScalaAsyncHttpClient._

  val config =  ConfigFactory.load()

  val token = config.getString("github.access-token")
    val uri = config.getString("github.uri")

  lazy val clientConfig = {
    val builder = new DefaultAsyncHttpClientConfig.Builder()
    val sslContext =
      SslContextBuilder
        .forClient()
        .sslProvider(SslProvider.JDK)
        .trustManager(InsecureTrustManagerFactory.INSTANCE)
        .build()

    builder
      .setSslContext(sslContext)
      .build()
  }
  val client = new DefaultAsyncHttpClient(clientConfig)

  implicit class F[T](val f: Future[T]) extends AnyVal {
    def futureValue: T = {
      Await.result(f, 10 seconds)
    }
  }

  def makeRequest(url: String) = {
    println("get url: " + url)
    val s = client
      .prepareGet(s"$uri/api/v3${url}?access_token=${token}")
      .setHeader(HttpHeaders.Names.ACCEPT, HttpHeaders.Values.APPLICATION_JSON)
      .asyncExecuteAsString().futureValue
    println(s)
  }

  def postRequest(url: String, data: String) = {
    println("post url: " + url)
    val s = client
      .preparePost(s"https://alm-github.systems.uk.hsbc/api/v3${url}?access_token=${token}")
      .setBody(data)
      .setHeader(HttpHeaders.Names.ACCEPT, HttpHeaders.Values.APPLICATION_JSON)
      .asyncExecuteAsString().futureValue
    println(s)
  }

//  println(s"s1 = ${makeRequest("/user/repos")}")
//  makeRequest("/user/orgs")
//  makeRequest("/orgs/OTCCCDEV/repos")
//  makeRequest("/repos/OTCCCDEV/jars")
  postRequest("/repos/OTCCCDEV/jars/statuses/ba43e625876dfce16b3c4e2377c81f5c37d11dfc",
    """{
      |"state": "failure",
      |  "target_url": "https://example.com/build/status",
      |  "description": "The build succeeded!",
      |  "context": "continuous-integration/teamcity"
      |}
    """.stripMargin)


//  var line = ""
//
//  do {
//    line = StdIn.rea
//  } while (line != "")
//
}

