object Pris {

  sealed abstract class Move
  case object Cooperate extends Move
  case object Defect extends Move

  type Round = (Move, Move)

  type Log = List[Round]

  var lg: Log = List()

  trait Strategy {
    def nextMove(log: Log): Move
  }

  class AllD extends Strategy {
    def nextMove(log: Log) = Defect
  }

  class AllC extends Strategy {
    def nextMove(log: Log) = Cooperate
  }

  class TitForTat extends Strategy {
    def nextMove(log: Log) = log match {
      case Nil => Cooperate
      case r :: rs => r._2 /* other player's last move */
    }
  }

  def game(s1: Strategy, s2: Strategy, numMoves: Int): Log = {
    var log: Log = List() // from s1's point of view
    for (movNum <- 1 to numMoves) {
      var s1move: Move = s1.nextMove(log)
      var s2move: Move = s2.nextMove(switchSide(log))
      log = (s1move, s2move) :: log
    }
    log
  }

  def switchSide(log: Log): Log = {
    for ((m1, m2) <- log) yield (m2, m1)
  }

  def getScores(log: Log): (Int, Int) =
    (getScore(log), getScore(switchSide(log)))

  def getScore(log: Log): Int =
    log.map(moveScore(_)).sum

  def moveScore(round: Round): Int = round match {
    case (Cooperate, Cooperate) => 3
    case (Cooperate, Defect) => 0
    case (Defect, Cooperate) => 5
    case (Defect, Defect) => 1
  }

  def main(args: Array[String]) = {
    var tft = new TitForTat()
    var ad = new AllD()
    var log = game(tft, ad, 10)
    println("Result of game:")
    println(log)
    println(s"Scores: ${getScores(log)}")
  }

}