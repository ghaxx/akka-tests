Some(10).flatMap {
  case i =>
    Some(5).map { case j => i * j
  }
}