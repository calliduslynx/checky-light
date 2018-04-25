package com.calliduslynx.checky

import org.junit.Assert
import org.junit.Test

class CheckOutTest {

  private infix fun Any?.mustBe(expected: Any?) = Assert.assertEquals(expected, this)

  private fun newCheckOut() = CheckOut(
      Rule("A", 50, 3 forOnly 130),
      Rule("B", 30, 2 forOnly 45),
      Rule("C", 20),
      Rule("D", 15)
  )

  private fun price(items: String): Int {
    val co = newCheckOut()
    items.toCharArray().forEach { co.scan(it.toString()) }
    return co.total()
  }

  // ******************************************************************************************************************

  @Test fun `einfache Berechnungen funktionieren`() {
    price("") mustBe 0
    price("A") mustBe 50
    price("AB") mustBe 80
    price("CDBA") mustBe 115

    price("AA") mustBe 100
    price("AAA") mustBe 130
    price("AAAA") mustBe 180
    price("AAAAA") mustBe 230
    price("AAAAAA") mustBe 260

    price("AAAB") mustBe 160
    price("AAABB") mustBe 175
    price("AAABBD") mustBe 190
    price("DABABA") mustBe 190
  }

  @Test fun `inkementelle Berechnungen funktionieren`() {
    val co = newCheckOut()
    co.scan("A");
    co.total() mustBe 50

    co.scan("B");
    co.total() mustBe 80

    co.scan("A");
    co.total() mustBe 130

    co.scan("A");
    co.total() mustBe 160

    co.scan("B");
    co.total() mustBe 175
  }
}