import com.typesafe.config.ConfigFactory
import org.apache.kudu.spark.kudu._
import org.apache.log4j.Logger
import org.apache.spark._

object BatchETL {

  var INPUT_MOVIES = ""
  var INPUT_LINKS = ""
  var INPUT_GTAGS = ""
  var KUDU_MASTER = "" //"cloudera-vm.c.endless-upgrade-187216.internal:7051"

  var OUTPUT_KUDU_MOVIES = ""
  var OUTPUT_KUDU_GTAGS = ""

  var SPARK_APPNAME = ""
  var SPARK_MASTER = ""

/*

  //TODO in the next update
  // For now it will be do by batch reccomender

  val OUTPUT_MOVIES_BY_GENRES = "impala::datamart.genres"
*/

  def main(args: Array[String]): Unit = {

    val configuration = ConfigFactory.load("BatchETL")
    INPUT_MOVIES = configuration.getString("betl.hive.input.movies")
    INPUT_LINKS = configuration.getString("betl.hive.input.links")
    INPUT_GTAGS = configuration.getString("betl.hive.input.gtags")

    KUDU_MASTER = configuration.getString("betl.kudu.master")
    OUTPUT_KUDU_MOVIES = configuration.getString("betl.kudu.output.movies")
    OUTPUT_KUDU_GTAGS = configuration.getString("betl.kudu.output.gtags")

    SPARK_APPNAME = configuration.getString("betl.spark.app_name")
    SPARK_MASTER = configuration.getString("betl.spark.master")


    val conf = new SparkConf().setMaster(SPARK_MASTER).setAppName(SPARK_APPNAME)
    val sc = new SparkContext(conf)
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
    val kuduContext = new KuduContext(KUDU_MASTER, sc)

    val log = Logger.getLogger(getClass.getName)

    log.info("***** Load Movies, Links and Genome_tags from Hive Data Lake *****")

    val movies = sqlContext.sql(s"select * from ${INPUT_MOVIES}")
    val links = sqlContext.sql(s"select * from ${INPUT_LINKS}")
    val gtags = sqlContext.sql(s"select * from ${INPUT_GTAGS}")

    log.info("***** Do nothing on tags *****")

    val gtagCols = Seq("tagid", "tag")
    val outGTag = gtags.select(gtagCols.head, gtagCols.tail: _*)

    log.info("***** Merge movies and links (links are useless) *****")

    val outMovies = ETL.enrichMovies(movies, links)

    log.info("***** Store Genometags and enriched movies to Kudu Data Mart *****")

    kuduContext.insertRows(outGTag, OUTPUT_KUDU_GTAGS)
    kuduContext.insertRows(outMovies, OUTPUT_KUDU_MOVIES)

    log.info("***** Close Spark session *****")

    sc.stop()

  }

}
