package pl.car

sealed trait Engine

case object V12 extends Engine
case object I6 extends Engine
case object BrokenEngine extends Engine
