package ca.ianp.similarity

import slick.driver.MySQLDriver.api._

package object models {
  val db = Database.forConfig("database")
}
