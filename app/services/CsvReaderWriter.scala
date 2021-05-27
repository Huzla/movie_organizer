package services

import java.io._ 
import models.CsvEncodable

class CsvReaderWriter[D <: CsvEncodable] {
  def read(filePath: String, apply: (String) => D): Vector[D] = {
    val bufferedSource = scala.io.Source.fromFile(filePath)

    var result = Vector[D]()

    for (line <- bufferedSource.getLines()) {
      println(line)
      result = result :+ apply(line)
    }

    bufferedSource.close

    return result
  }

  def save(filePath: String, data: Iterable[D]) = {
    val file = new File(filePath)
    val writer = new BufferedWriter(new FileWriter(file))

    for (instance <- data) {
      writer.write(instance.toCsv())
    }

    writer.close()
  }
}