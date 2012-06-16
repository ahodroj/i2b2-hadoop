i2b2-hadoop: a map/reduce query processor engine for i2b2 CRC queries
Version: 0.2


Execution mode:  

1. Export `concept_dimension` table as a CSV file 
2. Export query as XML file, from `qt_query_master`
3. `observation_fact` table contents must exist in CSV format on HDFS


To run

    hadoop jar i2b2-hadoop.jar net.hodroj.i2b2.I2B2QueryJob query.xml hdfs://<observation_fact> hdfs://<output>

The output will be a unique list of I2B2 patient_num values

Future plans:
 * Deploy as a CRC-cell in the i2b2 Hive