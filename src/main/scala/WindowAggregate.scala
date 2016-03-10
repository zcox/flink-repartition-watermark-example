package example

import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector
import org.joda.time.{DateTime, DateTimeZone}

/** The aggregate value of elements in a keyed time window. */
case class WindowAggregate[K, R](
    aggregate: R,
    maybeKey: Option[K] = None,
    maybeStart: Option[Long] = None, 
    maybeEnd: Option[Long] = None) {
  def withKeyStartAndEnd(key: K, start: Long, end: Long): WindowAggregate[K, R] = copy(maybeKey = Some(key), maybeStart = Some(start), maybeEnd = Some(end))
  def keyString = maybeKey.map(_.toString) getOrElse "<key undefined>"
  def toString(maybeTimestamp: Option[Long]) = maybeTimestamp.map(t => new DateTime(t, DateTimeZone.UTC).toString)
  def startString = toString(maybeStart) getOrElse "<start undefined>"
  def endString = toString(maybeEnd) getOrElse "<end undefined>"
  override def toString(): String = s"WindowAggregate: aggregate=$aggregate for key=$keyString in [$startString, $endString), system time = ${new DateTime(System.currentTimeMillis)}"
}

object WindowAggregate {
  def zero[K, R: Numeric]: WindowAggregate[K, R] = WindowAggregate[K, R](implicitly[Numeric[R]].zero)

  /** Can be used as the (R, T) => R fold function to count elements. */
  def count[K, R: Numeric](count: WindowAggregate[K, R], element: Any): WindowAggregate[K, R] = {
    val numeric = implicitly[Numeric[R]]
    count.copy(aggregate = numeric.plus(count.aggregate, numeric.one))
  }

  def plus[K, R: Numeric](a1: WindowAggregate[K, R], a2: WindowAggregate[K, R]): WindowAggregate[K, R] = 
    WindowAggregate(implicitly[Numeric[R]].plus(a1.aggregate, a2.aggregate))

  //TODO since collect now takes an Iterable[R], it needs to use the same aggregate function that was used to pre-aggregate
  //TODO or if the Iterable[R] should only ever contain one element (since window was pre-aggregated) then just use aggregates.head
  /** Can be used as the (K, W, Iterable[R], Collector[R]) => Unit window apply function to emit the aggregate. */
  def collect[K, R: Numeric](key: K, window: TimeWindow, aggregates: Iterable[WindowAggregate[K, R]], collector: Collector[WindowAggregate[K, R]]): Unit = 
    collector.collect(aggregates.reduce(plus[K, R] _).withKeyStartAndEnd(key, window.getStart, window.getEnd))
}
