#!/bin/bash

echo "START.$0"
start_time=$(date +%s)
cur_date=`date +%Y%m%d%H%M%S`
echo $cur_date

tmp_file=tmp/edi_r_comm_mention_"$cur_date".td

#>>>1.get the last partition
last_do_pt=`hdfs dfs -ls /edi/edi_conf |grep 'last_do_mention_pt' |tail -n 1|cut -f2 -d '='`
echo "INFO:last_do_pt=$last_do_pt .run export ..."

condition=""
if [ "x$last_do_pt" != "x" ];then
	condition=" WHERE B.PT_DATE > '$last_do_pt'"
fi

cd ../op	#important!!!

hive -S -e "use edi;
add jars lib/ansj_seg-2.0.8.jar
lib/nlp-lang-1.0.jar
lib/word2vec.jar
lib/com.zhongyitech.edi.NLP.omsa-v1.25.jar
lib/com.zhongyitech.edi.hive.udf.donlp-1.1.jar;
create temporary function MENTION_UDF as 'com.zhongyitech.edi.hive.udf.MentionUDF';
SELECT  A.ARR[0] AS CID,
    A.ARR[1] AS BRAND,
    A.ARR[2] AS MODEL
FROM (
    SELECT EXPLODE(MENTION_UDF(B.ID,B.COMM_INFO)) AS ARR 
    FROM EDI_M_PROD_COMMS B $condition ) A;
" >> $tmp_file

ecode=$?
if [ $ecode -ne 0 ];then
	echo "ERROR:hiveQL exec failed.exit $ecode"
	exit $ecode
fi

if [ ! -s $tmp_file ];then  #string length is zero
	echo "INFO:0 records.exit."
	exit 1
fi

echo "INFO:run import ..."
hive -e "LOAD DATA LOCAL INPATH '$tmp_file' INTO TABLE EDI.EDI_R_COMM_MENTION PARTITION (PT_DATE='$cur_date');"
ecode=$?
if [ $ecode -ne 0 ];then
	echo "ERROR:hiveQL exec failed.exit $ecode"
	exit $ecode
else
	hdfs dfs -rm -r /edi/edi_conf/last_do_mention_pt=*
	hdfs dfs -mkdir -p /edi/edi_conf/last_do_mention_pt=$cur_date
	echo "INFO:updating... edi_conf key:last_do_mention_pt=$cur_date"
	
	rm $tmp_file
fi

echo ">>>$0 DONE.spend time(s) :$(( $(date +%s) - $start_time ))"
