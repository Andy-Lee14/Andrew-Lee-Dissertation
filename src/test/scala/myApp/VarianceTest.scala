package myApp

import org.scalatest.{Matchers, WordSpec}

class VarianceTest extends WordSpec with Matchers {

  "Variance class" should {

    "correctly calculate the variance" when {

      "there are two different objects" in {
        val firstMean: Mean = Mean()
        val firstVariance: Variance = Variance()
        val firstBatch = (0 to 4).map(_ => 5).toList
        val firstBatchSize = firstBatch.size

        val secondMean: Mean = Mean()
        val secondVariance: Variance = Variance()
        val secondBatch = (0 to 4).map(x => x + 1).toList
        val secondBatchSize = secondBatch.size

        firstVariance.calculateVariance(firstBatch, firstMean, firstBatchSize) shouldBe 0.0
        secondVariance.calculateVariance(secondBatch, secondMean, secondBatchSize) shouldBe 2.0
      }

      "there is one object" in {
        val mean: Mean = Mean()
        val variance: Variance = Variance()

        val firstBatch = (0 to 4).map(_ => 5).toList
        val firstBatchSize = firstBatch.size

        val secondBatch = (0 to 4).map(x => x + 1).toList
        val secondBatchSize = secondBatch.size

        variance.calculateVariance(firstBatch, mean, firstBatchSize) shouldBe 0.0
        variance.calculateVariance(secondBatch, mean, secondBatchSize) shouldBe 1.5
      }

    }

  }

}