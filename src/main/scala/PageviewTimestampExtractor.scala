package example

import org.apache.flink.streaming.api.functions.TimestampExtractor

class PageviewTimestampExtractor extends TimestampExtractor[Pageview] {
  private[this] var lastTimestamp = Long.MinValue
  override def extractTimestamp(pageview: Pageview, currentTimestamp: Long): Long = {
    Thread.sleep(1) //slows down processing so we can see watermarks progressing and windows closing
    lastTimestamp = pageview.timestamp
    lastTimestamp
  }
  override def extractWatermark(pageview: Pageview, currentTimestamp: Long): Long = lastTimestamp - 1
  override def getCurrentWatermark(): Long = lastTimestamp - 1
}
