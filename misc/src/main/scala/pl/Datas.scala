package pl

import java.time.{LocalDateTime, OffsetDateTime, ZonedDateTime}
import java.util.Date


object Datas extends App {

//  {
//    import org.joda.time.DateTime
//    import org.joda.time.format.ISODateTimeFormat
//
//    val date = new DateTime()
//    val formatter = ISODateTimeFormat.dateTime()
//    val str = formatter.print(date)
//    println(s"""str = ${str}""")
//    println(s"""formatter.parseDateTime(str) = ${formatter.parseDateTime(str)}""")
//    println(s"""date.isEqual(formatter.parseDateTime(str)) = ${date.isEqual(formatter.parseDateTime(str))}""")
//  }

  {
    import java.text.{DateFormat, SimpleDateFormat}
    import java.time.format.DateTimeFormatter
    val date = LocalDateTime.now()
    val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    val str = formatter.format(date)

    println(s"""str = ${str}""")
    println(s"""date = ${date.toString}""")
    println(s"""OffsetDateTime.parse(str) = ${OffsetDateTime.parse(str)}""")
    println(s"""formatter.parse(str) = ${formatter.parse(str)}""")
    println(s"""date.isEqual(formatter.parseDateTime(str)) = ${date.equals(OffsetDateTime.parse(str))}""")
  }
}
