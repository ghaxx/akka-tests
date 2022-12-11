import scala.util.Try

object CardBoxes extends App {

  case class GameCardInfo(
    gameName: String,
    cardNumber: Int
  ) {
    override def toString: String = s"$gameName ($cardNumber)"
  }

  case class CardBoxInventory(
    numberOfBoxes: Int,
    boxSize: Int
  )

  case class AvailableCards(number: Int) {
    def removeCards(number: Int) = {
      if (this.number - number < 0)
        throw new RuntimeException(s"Not enough cards")
      copy(this.number - number)
    }
  }

  object AvailableCards {
    def apply(c: CardBoxInventory): AvailableCards =
      AvailableCards(c.numberOfBoxes * c.boxSize)
  }

  case class CardsStore(available: List[AvailableCards], sleevedGames: List[GameCardInfo] = List.empty) {
    def sleeveGame(game: GameCardInfo): CardsStore = {
      available match {
        case head :: rest if head.number >= game.cardNumber =>
          CardsStore(head.removeCards(game.cardNumber) :: rest, game :: sleevedGames)
        case head :: rest =>
          CardsStore(rest, sleevedGames).sleeveGame(game)
        case Nil => this
      }
    }
  }

  val games = List(
    GameCardInfo("Root", 116),
    GameCardInfo("Twilight Struggle", 130),
    GameCardInfo("Dominant Species", 27),
//    GameCardInfo("Brass: Birmingham", 76),
    GameCardInfo("Terraforming Mars", 334),
//    GameCardInfo("Pax Pamir", 142),
  )

  val availableCards = List(
    CardBoxInventory(5, 50),
//    CardBoxInventory(3, 100)
  ).map(AvailableCards(_))

  val cardStore = CardsStore(availableCards)

  val stores = games.permutations.flatMap { gamesPermutation =>
    Try {
      gamesPermutation.foldLeft(cardStore) { (store, game) =>
        store.sleeveGame(game)
      }
    }.toOption
  }

  val result = stores.toList.sortBy(_.sleevedGames.length).reverse.take(5).toList
  result.foreach(g => println(g.sleevedGames))
}
