import org.apache.kudu.spark.kudu._
import org.apache.log4j.Logger
import org.apache.spark._

object BatchETL {

  val INPUT_MOVIES = "datalake.movies"
  val INPUT_LINKS = "datalake.links"
  val INPUT_GTAGS = "datalake.genometags"
  val KUDU_MASTER = "cloudera-vm.c.endless-upgrade-187216.internal:7051"

  val OUTPUT_KUDU_MOVIES = "impala::datamart.movies"
  val OUTPUT_KUDU_GTAGS = "impala::datamart.genometags"

/*

  //TODO in the next update
  // For now it will be do by batch reccomender

  val OUTPUT_MOVIES_BY_GENRES = "impala::datamart.genres"
*/

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local").setAppName("Kudu Batch ETL")
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
