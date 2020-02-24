import org.apache.spark.sql.SparkSession
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession


object SparkLocalSessionCheck {
  def main(arg: Array[String]): Unit = {
    //Logger.getLogger("org.apache").setLevel(Level.OFF) // Want to control from code level. Not recommended
    //val log =  Logger.getLogger(this.getClass.getCanonicalName) // If logger configured with package/class name
    val sparkSession: SparkSession = SparkSession.builder.getOrCreate()

    val appName = sparkSession.sparkContext.getConf.get("spark.app.name")
    val log = Logger.getLogger(appName) //app level general info log
    val log_metrics = Logger.getLogger(appName + "-metrics") //app level metrics info log
    val tableName = "LS_TAB_PRODUCTS"

    val csvProducts = sparkSession.read.option("inferSchema", true).option("header", true).
      csv("/usr/local/share/workspace/codebase/sparkpros/sampledata/sample_products.csv")
    csvProducts.createOrReplaceTempView(tableName)
    log.info("starting message count @" + appName)
    val count = sparkSession.sql(s"select * from $tableName").count()
    log.info("Printing config params : ")
    sparkSession.sparkContext.getConf.getAll.foreach(println)
    log_metrics.info("tbl_cnt," + tableName + "," + count)
    log.info("++++++++++++++++++++++++++PRINTING LOCAL SESSION TABLE+++++++++++++++++++++++++++")
    sparkSession.sql(s"select * from $tableName").show()
  }
}
