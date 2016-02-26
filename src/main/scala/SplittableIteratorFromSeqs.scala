package example

import org.apache.flink.util.SplittableIterator
import java.util.{Iterator => JIterator}
import scala.collection.JavaConverters.asJavaIteratorConverter

/** Simulates a parallel source with multiple partitions. */
case class SplittableIteratorFromSeqs[T](seqs: Seq[T]*) extends SplittableIterator[T] {
  override def getMaximumNumberOfSplits(): Int = seqs.size
  override def split(partitionCount: Int): Array[JIterator[T]] = seqs.toArray.map(_.toIterator.asJava)

  //don't use this directly as an iterator
  override def hasNext(): Boolean = ???
  override def next(): T = ???
}
