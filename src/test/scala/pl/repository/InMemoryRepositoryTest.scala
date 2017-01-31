package pl.repository

import org.scalatest.FunSuite

class InMemoryRepositoryTest extends FunSuite {

  test("testGet") {

  }

  test("testPut") {
    val repo = new InMemoryRepository
    repo.put(0, "asdf")
    assert(repo.get(0) == "asdf")
    repo.put(1, 89)
    assert(repo.get(1) == "89")
  }

}
