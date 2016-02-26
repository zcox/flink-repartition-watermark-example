import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.windows.TimeWindow

package object example {
  //TODO fix compiler error
  // implicit class AggregateWindowedStream[T, K](windowedStream: WindowedStream[T, K, TimeWindow]) {
  //   def count = windowedStream.apply(WindowAggregate.zero[K, Long], WindowAggregate.count[K, Long] _, WindowAggregate.collect[K, Long] _)
  // }
}
