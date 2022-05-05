package myApp

import ngp.logging.LazyLogging
import scalaj.http.{Http, HttpOptions}


object slackNotification extends LazyLogging {

  def slackConfig(config: ApplicationConfig): Unit = {
    val source = scala.io.Source.fromFile("identified-problem-gamblers.txt")
    log.info("problem gambler list is being processed")
    val lines = try source.mkString("") finally source.close()

    val response =
      if (config.slackProxyHost.isDefined && config.slackProxyPort.isDefined)
        Http("https://hooks.slack.com/services/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").postData(
          getData(lines).stripMargin)
          .header("Content-Type", "application/json")
          .header("Charset", "UTF-8")
          .proxy(config.slackProxyHost.get, config.slackProxyPort.get)
          .option(HttpOptions.readTimeout(10000)).asString
      else
        Http("https://hooks.slack.com/services/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").postData(
          getData(lines).stripMargin)
          .header("Content-Type", "application/json")
          .header("Charset", "UTF-8")
          .option(HttpOptions.readTimeout(10000)).asString

    log.info(response.toString)
    log.info("Slack Notification Has Been Sent")
  }

  private def getData(lines: String): String = {
    s"""{"text": "PROBLEM GAMBLERS HAVE BEEN DETECTED.",
        "blocks": [
        {
          "type": "header",
          "text": {
            "type": "plain_text",
            "text": "AT RISK CUSTOMERS HAVE BEEN DETECTED  |  ACTION REQUIRED  :alert:",
            "emoji": true
          }
        },
        {
          "type": "divider"
        },
        {
          "type": "section",
          "text": {
            "type": "mrkdwn",
            "text": "Promotions have detected potentially at risk customers, please review the relevant dashboards and begin the customer outreach process if required."
          },
          "accessory": {
            "type": "image",
            "image_url": "https://lh3.googleusercontent.com/f_ay4gC-Oz4ovnS6ocJjb9w2EpZmFJgwDF1bwYnrn4jmsMQZqZzJhfkqdI_scZbckg=s200",
            "alt_text": "cute cat"
          }
        },
        {
          "type": "divider"
        },
        {
          "type": "section",
          "text": {
            "type": "mrkdwn",
            "text": "*PLEASE CHECK THE KIBANA DASHBOARD TO SEE IN DEPTH-DATA*       :arrow_right: \n *ON AFFECTED CUSTOMERS.*"
          },
          "accessory": {
            "type": "button",
            "text": {
            "type": "plain_text",
            "text": ":kibana:",
            "emoji": true
          },
            "style": "primary",
            "value": "click_me_123",
            "url": "https://c1438b6d582046588ace7523cf24a55b.test.ece.skybet.net/app/lens#/edit/424dc210-b694-11ec-ad5b-e9c4e837500d?_g=(filters:!(),query:(language:kuery,query:'%22Risk%20Customer%20Deposit%22'),refreshInterval:(pause:!t,value:0),time:(from:now-24h%2Fh,to:now))",
            "action_id": "button-action"
          }
        },
                {
          "type": "section",
          "text": {
            "type": "mrkdwn",
            "text": "*PLEASE CHECK THE GRAFANA DASHBOARD TO SEE IN-DEPTH DATA*    :arrow_right: \n *ON AFFECTED CUSTOMERS*"
          },
          "accessory": {
            "type": "button",
            "text": {
            "type": "plain_text",
            "text": ":grafana:",
            "emoji": true
          },
            "style": "primary",
            "value": "click_me_123",
            "url": "https://grafana.skybet.net/d/Li16qdQ7k/safer-gambling-app-test-env-only))",
            "action_id": "button-action"
          }
        },
        {
          "type": "divider"
        },
        {
          "type": "section",
          "text": {
            "type": "mrkdwn",
            "text": "*        REVIEW STATUS* "
          },
          "accessory": {
            "type": "static_select",
            "placeholder": {
            "type": "plain_text",
            "text": "  :octagonal_sign:   REQUIRES REVIEW",
            "emoji": true
          },
            "options": [
          {
            "text": {
            "type": "plain_text",
            "text": "  :green_check_mark:   COMPLETE",
            "emoji": true
          },
            "value": "value-0"
          },
          {
            "text": {
            "type": "plain_text",
            "text": "  :large_orange_circle:   IN PROGRESS",
            "emoji": true
          },
            "value": "value-1"
          },
          {
            "text": {
            "type": "plain_text",
            "text": "  :octagonal_sign:   REQUIRES REVIEW",
            "emoji": true
          },
            "value": "value-2"
          }
            ],
            "action_id": "static_select-action"
          }
        },
        {
          "type": "divider"
        },
        {
          "type": "section",
          "text": {
            "type": "mrkdwn",
            "text": "*At Risk Customer List* \n *Batch Num | Cust ID | Deposit Amount* \n $lines",
          }
        }
        ]
      }"""
  }
}
