package example

import org.joda.time.{DateTime, DateTimeZone, Interval}
import scala.util.Random
import scala.collection.mutable.ListBuffer
import java.util.UUID

case class Pageview(
  url: String,
  timestamp: Long,
  eventId: String = UUID.randomUUID.toString)

object Pageview {
  val urlBase = "http://site.com/"
  val urlCount = 10
  def randomUrl = urlBase + Random.nextInt(urlCount)
  def randomPageview(timestamp: DateTime): Pageview = Pageview(randomUrl, timestamp.getMillis)

  def randomPageviews(interval: Interval, millisBetweenEvents: Long): Seq[Pageview] = {
    var timestamp = interval.getStart
    val pageviews = ListBuffer.empty[Pageview]
    while (timestamp isBefore interval.getEnd) {
      pageviews += randomPageview(timestamp)
      timestamp = timestamp plus millisBetweenEvents
    }
    pageviews
  }
}
