
import org.apache.spark.sql.Row
import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest._

class ETLSpec
  extends FlatSpec{

  "The ETL process" should
    "merge movies and links in orther to take only useful info" in {



    val conf = new SparkConf().setMaster("local").setAppName("Kudu Batch ETL Test")
    val sc = new SparkContext(conf)
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)

    val movies = sc.parallelize(Seq(
      (1, 1, "anna", "romantico")
    ))

    val links = sc.parallelize(Seq(
      (1, 1, "imdbID-9", "tmdbID-7")
    ))

//    movies.foreach(println)
//    links.foreach(println)

    val mdf = sqlContext.createDataFrame(movies).toDF("id","movieid","title", "genres")
    val ldf = sqlContext.createDataFrame(links).toDF("id","movieid","imdbid", "tmdbid")

    val em = ETL.enrichMovies(mdf, ldf)

    assert(em.rdd.map{case Row(movieid, title, genres, link) => link}.collect()(0) == "https://www.themoviedb.org/movie/tmdbID-7")
  }


}
