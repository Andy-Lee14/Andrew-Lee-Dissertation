package myApp

import scala.Numeric.Implicits._

case class Variance(var aggregatedVariance: Double = 0.0, var numBatches: Int = 0) {

  def addDataBatch(batchVariance: Double, batchSize: Int): Unit = {
    val oldSum = numBatches * batchSize * aggregatedVariance
    numBatches += 1
    aggregatedVariance = (batchVariance + oldSum) / (numBatches * batchSize)
  }

  def calculateVariance[T: Numeric](xs: Iterable[T], rollingMean: Mean, batchSize: Int): Double = {
    val avg = rollingMean.calculateMean(xs)
    val newSum = xs.map(_.toDouble).map(a => math.pow(a - avg, 2)).sum

    addDataBatch(newSum, batchSize)
    aggregatedVariance
  }
}