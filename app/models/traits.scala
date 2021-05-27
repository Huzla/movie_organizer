package models

import utils.InvalidFormatException

trait CsvEncodable {
  def toCsv(): String
}
