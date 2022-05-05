package myApp

import scala.Numeric.Implicits._

case class Mean(var aggregatedMean: Double = 0.0, var numBatches: Int = 0) { //Default values of zero added so that the case class starts from zero

  private def addDataBatch(batchMean: Double): Unit = {
    val oldTotal = aggregatedMean * numBatches
    numBatches += 1

    aggregatedMean = (oldTotal + batchMean) / numBatches
  }

  def calculateMean[T: Numeric](xs: Iterable[T]): Double = {
    val newMean = xs.sum.toDouble / xs.size

    addDataBatch(newMean)
    aggregatedMean
  }
}