object MyPris {
  sealed trait Move
  case object Cooperate extends Move
  case object Defect extends Move

  sealed trait Strategy {
    def firstMove: Move
    def nextMove(opponentMove: Move): Move
  }

  case object AllD extends Strategy {
    def firstMove = Defect
    def nextMove(opponentMove: Move) = Defect
  }

  case object AllC extends Strategy {
    def firstMove = Cooperate
    def nextMove(opponentMove: Move) = Cooperate
  }

  case object TitForTat extends Strategy {
    def firstMove = Cooperate
    def nextMove(opponentMove: Move) = opponentMove
  }

  case class Round(moveA: Move, moveB: Move) {
    def reverse = Round(moveB, moveA)
  }

  type Log = List[Round]

  def game(s1: Strategy, s2: Strategy, numMoves: Int) =
    (1 until numMoves).foldLeft(List(Round(s1.firstMove, s2.firstMove))) {
      case (log, _) ⇒ Round(s1.nextMove(log.head.moveB), s2.nextMove(log.head.moveA)) :: log
    }

  def getScores(log: Log): (Int, Int) =
    log.foldLeft((0, 0)) {
      case ((scoreA, scoreB), round) => (scoreA + moveScore(round), scoreB + moveScore(round.reverse))
    }

  def moveScore(round: Round) = round match {
    case Round(Cooperate, Cooperate) => 3
    case Round(Cooperate, Defect) => 0
    case Round(Defect, Cooperate) => 5
    case Round(Defect, Defect) => 1
  }

  def main(args: Array[String]) = {
    val log = game(TitForTat, AllD, 10)
    println("Result of game:")
    println(log)
    println(s"Scores: ${getScores(log)}")
  }

}