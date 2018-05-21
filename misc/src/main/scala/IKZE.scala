object IKZE {

  import math._

  val lat = 33
  val oszczędzane = 3000
  val limit_ikze = 5331.0

  def main(args: Array[String]): Unit = {
    val roczna_stopa_zwrotu = roczna_srednia_stopa_zwrotu(13, 3)
    println(f"""roczna_stopa_zwrotu = $roczna_stopa_zwrotu%,.5f""")
    val ikze = na_ikze(roczna_stopa_zwrotu)
    println(f"""ikze = $ikze%,.2f""")
    val konto_obok_ikze = na_koncie(oszczędzane - (limit_ikze / 12))
    println(f"""konto_obok_ikze = $konto_obok_ikze,.2f""")
    val ikze_i_konto = ikze + konto_obok_ikze
    println(f"""ikze_i_konto = $ikze_i_konto%,.2f""")
    val samo_konto = na_koncie(oszczędzane)
    println(f"""samo_konto = $samo_konto%,.2f""")
    val zysk_na_rok = (ikze_i_konto - samo_konto) / lat
    println(f"""zysk_na_rok = $zysk_na_rok%,.2f""")
  }

  def roczna_srednia_stopa_zwrotu(stopa_zwrotu: Double, ile_lat: Int): Double = {
    val stopa = pow(1.0 + (stopa_zwrotu / 100), 1.0 / ile_lat)
    stopa
  }

  def na_ikze(roczna_stopa_zwrotu: Double) = {
    val p_na_rok = roczna_stopa_zwrotu
    val wplata_roczna = limit_ikze
    val zgromadzone = (1 to 30).foldLeft(wplata_roczna) {
      case (total, _) =>
        //    println(f"$rok%2d: $total%06.3f")
        val oplata_za_zarzadzanie = total * 0.008
        total * p_na_rok + wplata_roczna - oplata_za_zarzadzanie
    }

    val do_wypłaty = zgromadzone * 0.9
    do_wypłaty
  }

  def na_koncie(odkladając_miesięcznie: Double) = {
    def p_na_rok(kwota: Double) = kwota match {
      case _ if kwota < 500000 => 0.005
      case _ if kwota < 1000000 => 0.006
      case _ if kwota < 5000000 => 0.007
      case _ if kwota < 10000000 => 0.008
      case _ => 0.009
    }

    val wpłata_roczna = 12 * odkladając_miesięcznie
    val zgromadzone = (1 to 30).foldLeft(wpłata_roczna) {
      case (total, _) =>
        //    println(f"$rok%2d: $total%06.3f")
        val odsetki = total * p_na_rok(total)
        val podatek = odsetki * 0.19
        total + odsetki + wpłata_roczna - podatek
    }

    zgromadzone
  }

}
