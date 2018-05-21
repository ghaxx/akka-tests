package pl.split_graph

trait Input{
  def index: Int
}

case class Add(
  x: Int,
  index: Int
) extends Input

case class Update(
  x: Int,
  index: Int
) extends Input

trait Output {
  def index: Int
}

case class UpdateResult(
  x: Int,
  index: Int
) extends Output

case class NoOp(
  index: Int
) extends Output
