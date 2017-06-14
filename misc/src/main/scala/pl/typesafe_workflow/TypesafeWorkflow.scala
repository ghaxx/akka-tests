package pl.typesafe_workflow

object TypesafeWorkflow extends App {

  trait State {
    def process: State
  }

  class ==>[T, R]

  class Init(good: Boolean) extends State {
    override def process: State = if (good) {
      new Accepted
    } else {
      new Accepted
    }
  }

  object Init {
    implicit val ev: ==>[Init, Accepted] = new ==>[Init, Accepted]
    implicit val ev2: ==>[Init, Rejected] = new ==>[Init, Rejected]
  }

  class Accepted extends State {
    override def process: State = new Done
  }

  object Accepted {
    implicit val ev: ==>[Accepted, Done] = new ==>[Accepted, Done]
  }

  class Rejected extends State {
    override def process: State = new Done
  }

  object Rejected {
    implicit val ev: ==>[Rejected, Done] = new ==>[Rejected, Done]
  }

  class Done extends State {
    override def process: State = this
  }

  def process(s: Accepted)(implicit F: Accepted ==> Done): Done = {
    new Done
  }

  def process2(s: Init)(implicit F: Init ==> Done): Done = {
    new Done
  }

  process(new Accepted)
//  process2(new Init(true))

}
