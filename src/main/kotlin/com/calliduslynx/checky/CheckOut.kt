package com.calliduslynx.checky

import java.util.concurrent.atomic.AtomicInteger

// ******************************************************
// ***** external api ***********************************
// ******************************************************
// ***** SpecialPrice
infix fun Int.forOnly(priceForAll: Int) = SpecialPrice(this, priceForAll)

class SpecialPrice(val count: Int, val priceForAll: Int)

// ***** Rule
class Rule(
    val item: String,
    val price: Int,
    val specialPrice: SpecialPrice? = null
)

// ******************************************************
// ***** internal api ***********************************
// ******************************************************
private class Saving(val count: Int, val difference: Int)


// ***** CheckOut
class CheckOut(vararg rules: Rule) {
  /**
   * the map holds all items (as key) and their prices (as value)
   */
  private val pricePerItem = rules.map { it.item to it.price }.toMap()

  /**
   * the map holds all items (as key) which have a saving. Saving means that
   * there is a specialPrice if enought items are bought
   */
  private val savingsPerItem = rules
      .filter { it.specialPrice != null }                           // filter: we only want to handle items with a special price 
      .map {
        val normalPrice = it.specialPrice!!.count * it.price        // first calculate what item would have cost without
        val difference = normalPrice - it.specialPrice.priceForAll  // calculate the difference with the special price
        it.item to Saving(it.specialPrice.count, difference)        // create MapEnty (item, Saving)
      }.toMap()

  /**
   * a simple map to count the items (is only used for items with saving)
   */
  private val countPerItem = mutableMapOf<String, AtomicInteger>()

  /**
   * holds current total
   */
  private var currentTotal = 0

  /**
   * performs the scanning (sum is calculated directly, special prices are also included)
   *
   * @throws IllegalArgumentException if there is no price for given item
   */
  fun scan(item: String) {
    currentTotal += pricePerItem[item]
        ?: throw IllegalArgumentException("Item '$item' not found")

    savingsPerItem[item]?.apply {
      handleSaving(item, this)
    }
  }

  private fun handleSaving(item: String, saving: Saving) {
    val count = countPerItem.getOrPut(item, { AtomicInteger(0) }).incrementAndGet() // increase counter for item and get value
    if (count == saving.count) {                                                    // check if there are enough items for a saving
      currentTotal -= saving.difference                                             // reduce by difference
      countPerItem[item]!!.set(0)                                                   // reset counter
    }
  }

  fun total(): Int = currentTotal
}