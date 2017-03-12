package ca.ianp.similarity

import slick.driver.SQLiteDriver.api._

package object models {
  val db = Database.forConfig("database")
}
