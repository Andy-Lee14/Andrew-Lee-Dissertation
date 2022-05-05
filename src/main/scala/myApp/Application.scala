package myApp

import net.logstash.logback.argument.StructuredArguments
import ngp.NgpMBean
import ngp.logging.LazyLogging
import ngp.metrics.{NgpMetric, NgpMetricsHandler}

import java.io.{FileOutputStream, PrintStream}
import scala.io.Source

case class NgpAndyMBean(metric: String) extends NgpMBean(s"type=andy-metric", metric)

case class DepositsCountMetric() extends NgpMetric("deposit-count", "deposit count")

case class RiskCustomerCountMetric() extends NgpMetric("risk-customer-count", "risk customer count")

object Application extends App with LazyLogging {
  log.info("App has started running")

  val numberOfStdDeviations = 3.5 //Scale as required

  val applicationConfig: ApplicationConfig = ApplicationConfig()
  val depositsCountMetric = DepositsCountMetric()
  val riskCustomerCountMetric = RiskCustomerCountMetric()

  val saferGamblingMBean = NgpAndyMBean("safer-gambling")
  saferGamblingMBean.addMetric(depositsCountMetric)
  saferGamblingMBean.addMetric(riskCustomerCountMetric)
  NgpMetricsHandler.register(saferGamblingMBean)

  val lines: Iterator[String] = Source.fromResource("customerData2.csv").getLines() //Configure data set 1 or 2
  val data1 = lines.map(_.split(',').map(_.trim)) // Split each line on the comma and strip out whitespace
  val data2 = data1.map { case Array(x, y) => (x, y.toDouble) } // map each resulting array into a tuple
  val data3 = data2.toSeq // Convert from Iterator to Array

  val batchSize: Int = 10000
  var rollingMean: Mean = new Mean
  var rollingVariance: Variance = new Variance

  depositsCountMetric.increaseValue(data3.length)

  data3.grouped(batchSize).foreach(batch => processBatch(batch))

  private def stdDev[T: Numeric](xs: Iterable[T]): Double = math.sqrt(rollingVariance.calculateVariance(xs, rollingMean, batchSize))

  def processBatch(batch: Seq[(String, Double)]): Unit = {

    val stdDevCustomerData = stdDev(batch.map(_._2)) * numberOfStdDeviations // Look at data3 array and take the second item on the line (customer deposit amount) and multiply by stdDev

    println("mean " + rollingMean.aggregatedMean.round + " variance " + rollingVariance.aggregatedVariance.round + " stdDev " + stdDevCustomerData.round)

    val problemGamblerList = batch.collect {
      case user: (String, Double)
        if user._2 > rollingMean.aggregatedMean + stdDevCustomerData => user
    }
    problemGamblerList.foreach { x =>
      println(x)
    }

    riskCustomerCountMetric.increaseValue(problemGamblerList.size)

    Console.withOut(new PrintStream(new FileOutputStream("identified-problem-gamblers.txt", true))) {
      problemGamblerList.foreach { x =>
        println(rollingMean.numBatches, x._1, x._2.toInt)

        val markers = List(
          StructuredArguments.keyValue("customer_id", x._1),
          StructuredArguments.keyValue("amount", x._2.toInt)
        )

        log.info("Risk Customer Deposit", markers: _*)
      }

    }

    Thread.sleep(1000)
    log.info("App has finished batches")
  }

  slackNotification.slackConfig(applicationConfig)
  log.info("App sleeping for 30 secs")
  Thread.sleep(600000) //allows for the visualisation of the metrics in Prometheus
  log.info("App has stopped running")
}