package reactify.group

import reactify.reaction.{GroupReactions, Reactions}
import reactify.{State, Var}

case class VarGroup[T](name: Option[String], items: List[Var[T]]) extends Var[T] with Group[T, Var[T]]{
  override lazy val reactions: Reactions[T] = new GroupReactions[T, Var[T]](this)

  override def set(value: => T): Unit = items.foreach(_.set(value))

  override def state: State[T] = ???

  override def and(that: Var[T]): Var[T] = VarGroup(name, items ::: List(that))
}