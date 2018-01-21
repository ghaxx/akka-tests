package pl.instances

object ObjInstancesClasses extends App {

  class T1 extends T {

  }
  class T2 extends T {

  }

  val t1 = new T1()
  t1.TObject.id

  val t2 = new T2()
  t2.TObject.id
  t2.TObject.id

}


trait T {
  object TObject {
    var i = 0
    def id = {
      i += 1
      println("" + + i + ": "+this.toString)
    }
  }
}