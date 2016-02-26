package example

import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.TimeCharacteristic.EventTime
import org.apache.flink.streaming.api.windowing.time.Time
import org.joda.time.{DateTime, DateTimeZone, Interval}
import org.joda.time.format.DateTimeFormat

object Main extends App {
  val yyyyMMddFormat = DateTimeFormat.forPattern("yyyy-MM-dd").withZone(DateTimeZone.UTC)
  def date(s: String): DateTime = yyyyMMddFormat.parseDateTime(s)

  val millisBetweenEvents = 1000
  val pageviews = Seq(
    Pageview.randomPageviews(new Interval(date("2016-02-01"), date("2016-02-03")), millisBetweenEvents),
    Pageview.randomPageviews(new Interval(date("2016-02-02"), date("2016-02-04")), millisBetweenEvents))

  val environment = StreamExecutionEnvironment.getExecutionEnvironment
  environment.setParallelism(pageviews.size)
  environment.setStreamTimeCharacteristic(EventTime)
  environment.getConfig.enableTimestamps()
  environment
    .fromParallelCollection(SplittableIteratorFromSeqs(pageviews: _*))
    .assignTimestamps(new PageviewTimestampExtractor)
    .keyBy(_.url)
    .timeWindow(Time.hours(1)) //really need a Time DSL like scala.concurrent.duration. e.g.: .timeWindow(1 hour)
    .apply(WindowAggregate.zero[String, Long], WindowAggregate.count[String, Long] _, WindowAggregate.collect[String, Long] _) //hide this ugly thing in a nice util method in an implicit class
    .print()
  println(environment.getExecutionPlan)
  environment.execute("repartition-watermark-example")
}
