package pl.car

sealed trait CarState

case object Moving extends CarState
case object Standing extends CarState
