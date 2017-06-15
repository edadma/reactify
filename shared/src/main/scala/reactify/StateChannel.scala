package reactify

import java.util.concurrent.atomic.AtomicBoolean

import reactify.bind._

trait StateChannel[T] extends State[T] with Channel[T] {
  override def set(value: => T): Unit
  override def static(value: T): Unit = super.static(value)

  def bind[V](that: StateChannel[V], setNow: BindSet = BindSet.LeftToRight)
             (implicit t2v: T => V, v2t: V => T): Binding[T, V] = {
    setNow match {
      case BindSet.LeftToRight => that := t2v(this)
      case BindSet.RightToLeft => this := v2t(that)
      case BindSet.None => // Nothing
    }
    val changing = new AtomicBoolean(false)
    val leftToRight = this.attach { t =>
      if (changing.compareAndSet(false, true)) {
        println(s"Assigning this to that...")
        try {
          that := t2v(get)
        } finally {
          changing.set(false)
        }
        println("\tfinished assigning this to that...")
      }
    }
    val rightToLeft = that.attach { t =>
      if (changing.compareAndSet(false, true)) {
        println(s"Assigning that to this...")
        try {
          StateChannel.this := v2t(that.get)
        } finally {
          changing.set(false)
        }
        println("\tfinished assigning that to this...")
      }
    }
    new Binding(this, that, leftToRight, rightToLeft)
  }
}