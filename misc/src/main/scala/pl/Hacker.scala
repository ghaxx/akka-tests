package pl

object Hacker extends App {

  sealed trait State
  object State {
    sealed trait Caffeinated extends State
    sealed trait Decaffeinated extends State
  }

  def caffeinated: Hacker[State.Caffeinated] = new Hacker
  def decaffeinated: Hacker[State.Decaffeinated] = new Hacker

  var hacker = Hacker.caffeinated
//  hacker = hacker.hackOn
//  hacker = hacker.hackOn
//  Hacker.caffeinated.hackOn.drinkCoffee.hackOn.drinkCoffee
//  Hacker.caffeinated.hackOn.hackOn
}

class Hacker[+S <: Hacker.State] private {
  import Hacker._

//  def hackOn[T >: S <: State.Caffeinated]: Hacker[State.Decaffeinated] = {
//    println("Hacking, hacking, hacking!")
//    new Hacker
//  }
//
//  def drinkCoffee[T >: S <: State.Decaffeinated]: Hacker[State.Caffeinated] = {
//    println("Slurp ...")
//    new Hacker
//  }
//def hackOn(implicit ev: S =:= State.Caffeinated): Hacker[State.Decaffeinated] = {
//  println("Hacking, hacking, hacking!")
//  new Hacker
//}
//
//  def drinkCoffee(implicit ev: S =:= State.Decaffeinated): Hacker[State.Caffeinated] = {
//    println("Slurp ...")
//    new Hacker
//  }
}
