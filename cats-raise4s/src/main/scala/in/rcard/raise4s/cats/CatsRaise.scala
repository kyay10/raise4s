package in.rcard.raise4s.cats

import cats.Semigroup
import in.rcard.raise4s.Raise

object CatsRaise {

  /** Transform every element of `iterable` using the given `transform`, or accumulate all the
   * occurred errors using the [[Semigroup]] type class defined on the `Error` type.
   *
   * <h2>Example</h2>
   * {{{
   * case class MyError2(errors: List[String])
   *
   * given Semigroup[MyError2] with {
   *   def combine(error1: MyError2, error2: MyError2): MyError2 =
   *     MyError2(error1.errors ++ error2.errors)
   * }
   *
   * val block: List[Int] raises MyError2 =
   *   CatsRaise.mapOrAccumulate(List(1, 2, 3, 4, 5)) { value1 =>
   *     value1 + 1
   *   }
   * val actual = Raise.fold(
   *   block,
   *   error => fail(s"An error occurred: $error"),
   *   identity
   * )
   * actual shouldBe List(2, 3, 4, 5, 6)
   * }}}
   *
   * @param iterable
   *   The collection of elements to transform
   * @param transform
   *   The transformation to apply to each element that can raise an error of type `Error`
   * @param r
   *   The Raise context
   * @tparam Error
   *   The type of the logical error that can be raised. It must have a [[Semigroup]] instance available
   * @tparam A
   *   The type of the elements in the `iterable`
   * @tparam B
   *   The type of the transformed elements
   * @return
   *   A list of transformed elements
   */
  inline def mapOrAccumulate[Error: Semigroup, A, B](iterable: Iterable[A])(
      inline transform: Raise[Error] ?=> A => B
  )(using r: Raise[Error]): List[B] =
    Raise.mapOrAccumulate(iterable, Semigroup[Error].combine)(transform)
}
