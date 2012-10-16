i2b2-hadoop: a map/reduce query processor engine for i2b2 CRC queries

i2b2 (Informatics for Integrating Biology & the Bedside) is an open-source platform for de-identified cohort 
discovery, and for managing and delivering clinical data sets for research with appropriate IRB approval. 
Sponsored by the National Institutes of Health (NIH), i2b2 is a widely accepted tool among CTSA sites and other 
Academic Medical Centers (AMCs), and has also found increasing use at other organizations for research and clinical 
performance improvement initiatives.

An i2b2 implementation consists of a data mart of clinical, research, and administrative data, and an interface to 
construct and manage queries and data sets. This project provides a Hadoop implementatin of the data mart query
engine, known as the CRC. 

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