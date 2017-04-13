package pl.cache.testing

trait TestCase {
  def name: String
  def warmup(): Unit
  def run(n: Long): Long

}
