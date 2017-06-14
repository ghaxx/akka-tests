val a = new java.util.concurrent.LinkedBlockingQueue[Int](3)

a.add(1)
a

a.add(2)
a
a.remainingCapacity()
a.add(3)
a
a.remainingCapacity()
try {
  a.add(4)
  a
} catch {
  case _: IllegalStateException =>
    while(a.remainingCapacity() < 1) {
      a.take()
    }
}
