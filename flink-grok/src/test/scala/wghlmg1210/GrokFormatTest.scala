package wghlmg1210

import java.util.Properties

import io.krakens.grok.api.GrokCompiler
import org.apache.commons.text.StringSubstitutor
import org.scalatest.FunSuite
import org.slf4j.{Logger, LoggerFactory}

import scala.io.Source

/**
  * Created by huangxingbiao@github.com on 09/03/2020.
  */
class GrokFormatTest extends FunSuite {

  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  val compiler: GrokCompiler = GrokCompiler.newInstance
  compiler.registerDefaultPatterns()

  val props: Properties = {
    val props = new Properties()

    val stream = this.getClass.getClassLoader
      .getResourceAsStream("grok-format.conf")
    props.load(stream)

    props
  }

  val REGEX_FORMAT: String = props.getProperty("grok.regex")
  val FORMATTER: String = props.getProperty("grok.format")

  test("test grok compile") {
    REGEX_FORMAT.split(" ").foreach(regex => {
      println(regex)
      compiler.compile(regex)
    })
  }

  test("test grok capture and string format") {
    val stream = this.getClass.getClassLoader.getResourceAsStream("nginx.log")
    val grok = compiler.compile(REGEX_FORMAT)

    Source.fromInputStream(stream).getLines()
      .foreach(line => {
        logger.info("line: {}", line)

        val values = grok.capture(line)
        logger.info("capture result: {}", values)

        val message = StringSubstitutor.replace(FORMATTER, values)
        logger.info("format result: {}", message)

        logger.info("message item size: {}", message.split("\t").length)
      })
  }

}
