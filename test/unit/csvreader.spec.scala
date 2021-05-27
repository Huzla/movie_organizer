import org.scalatest._
import org.scalatestplus.play._
import java.nio.file._
import java.io._

import models.{CsvWritable, CsvReadable}
import services.CsvReader
import utils.InvalidFormatException

// Define a test data model
case class Post(name: String, message: String) extends CsvWritable {
  def toCsv() = {
    return String.format("%s,%s", this.name, this.message)
  }
}

object Post extends CsvReadable {
  def fromCsvString(csvStr: String): Post = {
    try {
      val splitArr = csvStr.split(",").map(_.trim)

      return Post(splitArr[0], splitArr[1])
    }
    catch {
      case e: Exception => throw InvalidFormatException(e.getMessage())
    }
  }
}

object LongPost extends CsvReadable {
  def fromCsvString(csvStr: String): Post = {
    try {
      val splitArr = csvStr.split(",").map(_.trim)

      return Post(splitArr[0], splitArr[1], splitArr[2])
    }
    catch {
      case e: Exception => throw InvalidFormatException(e.getMessage())
    }
  }
}

class CsvReaderSpec extends PlaySpec with BeforeAndAfter {
  var csvReader = new CsvReader()
  var tempDir: Path = null
  var testFilePath: String = ""
  var testFile: File = null
  val validTestString: String = "janne,Moi\npete,No moi vaan\nTeppo Testaaja,Olkaa hiljaa kun minä testaan!"
  

  before {
    // Create test data
    tempDir = Files.createTempDirectory("csv_test")
    println(String.format("Created %s", tempDir.toString()))

    testFilePath = String.format("%s/%s", tempDir.toString(), "test.csv")

    testFile = new File(testFilePath)

    println(String.format("Created %s", testFilePath))

    val writer = new BufferedWriter(new FileWriter(testFile))

    
    writer.write(validTestString)
    writer.close()
  }

  "CsvReader" should {
    
    "parse comma separated list files into Vector of given type" in {
      assert(csvReader.read(testFilePath, Post) == validTestString.split("\n").map(nameAndMessage => {
        val dataArr = nameAndMessage.split(",").map(_.trim)
        Post(dataArr(0), dataArr(1))
      }).toVector) 
    }

    "throw a file not found exception when the specified path does not match a file" in {
      an [java.io.FileNotFoundException] must be thrownBy csvReader.read(tempDir, Post)
    }

    "throw an invalid format exception when the read data does not mathc the data model" in {
      an [InvalidFormatException] must be thrownBy csvReader.read(testFilePath, LongPost)
    }

    "save changes to disk" in {
      val testVector: Vector[Post] = validTestString.split("\n").map(nameAndMessage => {
        val dataArr = nameAndMessage.split(",").map(_.trim)
        Post(dataArr(0), dataArr(1))
      }).toVector :+ Post("Teppo testaaja", "Testaus on iloa")

      csvReader.save(testFilePath, testVector)
      assert(csvReader.read(testFilePath, Post) == testVector) 
    }

  }

  after {
    Files.delete(testFile.toPath())
    Files.delete(tempDir)
  }
}