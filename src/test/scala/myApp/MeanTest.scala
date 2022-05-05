package myApp

import org.scalatest.{Matchers, WordSpec}

class MeanTest extends WordSpec with Matchers {

  "Mean class" should {

    "correctly calculate the mean value" when {

      "there are two different objects" in {
        val firstMean: Mean = Mean()
        val firstBatch = (0 to 4).map(_ => 5).toList
        val expectedFirstMean = 5.0

        val secondMean: Mean = Mean()
        val secondBatch = (0 to 4).map(x => x + 1).toList
        val expectedSecondMean = 3.0

        firstMean.calculateMean(firstBatch) shouldBe expectedFirstMean
        secondMean.calculateMean(secondBatch) shouldBe expectedSecondMean
      }

      "there is one object" in {
        val mean: Mean = Mean()

        val firstBatch = (0 to 4).map(_ => 5).toList
        val expectedAggregatedValue = 5.0
        val secondBatch = (0 to 4).map(x => x + 1).toList
        val expectedAggregatedValue2 = 4.0

        mean.calculateMean(firstBatch) shouldBe expectedAggregatedValue
        mean.calculateMean(secondBatch) shouldBe expectedAggregatedValue2
      }

    }

  }

}