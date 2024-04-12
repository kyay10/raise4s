package in.rcard.raise4s

/** Runs a computation `block` using [[Raise]], and return its outcome as [[Either]].
  *   - [[Right]] represents success,
  *   - [[Left]] represents logical failure.
  *
  * This function re-throws any exceptions thrown within the [[Raise]] block.
  *
  * @param block
  *   A computation that can raise errors of type `Error`
  * @tparam A
  *   The type of the value returned by the computation
  * @tparam Error
  *   The type of the logical error that can be raised by the computation
  * @return
  *   An [[Either]] representing the outcome of the computation
  */
def either[A, Error](block: Raise[Error] ?=> A): Either[Error, A] =
  fold(block, error => Left(error), value => Right(value))

class OptionRaise(val raise: Raise[Option[Nothing]]) extends Raise[Option[Nothing]]:
  override def raise(error: Option[Nothing]): Nothing = raise.raise(error)

object OptionPredef:
  extension [A](option: Option[A])(using optionRaise: OptionRaise)
    def bind(): A = option.getOrElse(raise(None))

def option[A](block: OptionRaise ?=> A): Option[A] =
  fold(
    {
      given optionRaise: OptionRaise = new OptionRaise(new DefaultRaise()) // ???
      block(using optionRaise)
    },
    _ => None,
    Some(_)
  )
