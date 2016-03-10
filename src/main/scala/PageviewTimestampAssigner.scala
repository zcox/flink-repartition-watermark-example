package example

import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks
import org.apache.flink.streaming.api.watermark.Watermark

class PageviewTimestampAssigner extends AssignerWithPeriodicWatermarks[Pageview] {
  private[this] var lastTimestamp = Long.MinValue
  override def extractTimestamp(pageview: Pageview, previousElementTimestamp: Long): Long = {
    Thread.sleep(1) //slows down processing so we can see watermarks progressing and windows closing
    lastTimestamp = pageview.timestamp
    lastTimestamp
  }
  override def getCurrentWatermark(): Watermark = new Watermark(lastTimestamp - 1)
}
