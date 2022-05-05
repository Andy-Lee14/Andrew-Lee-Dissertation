package myApp

import com.typesafe.config.{Config, ConfigFactory}
import ngp.logging.LazyLogging

import scala.util.Try

object ApplicationConfig extends LazyLogging {

  def apply(config: Config = ConfigFactory.load.resolve): ApplicationConfig = {
    val conf = ApplicationConfig(
      slackProxyHost = Try(Some(config.getString("app.slack.message.proxy.host"))).getOrElse(None),
      slackProxyPort = Try(Some(config.getInt("app.slack.message.proxy.port"))).getOrElse(None),
    )

    log.info("Configuration is: ")
    log.info(conf.toString)

    conf
  }
}

case class ApplicationConfig(
                              slackProxyHost: Option[String],
                              slackProxyPort: Option[Int]
                            )