#!/bin/sh

# ---------------------------------------------------------------------------------
# A shell script to create and initialize the following databases:
# 1. YRC_NRSEQ
# 2. 
# ---------------------------------------------------------------------------------

mysql_user=$1;
use_passwd=$2;

base_dir=`pwd`
schema_dir=${base_dir}/schema
fasta_dir=${base_dir}/fasta
java_dir=${base_dir}/java
log_dir=${base_dir}/logs

java=/usr/bin/java

echo ${base_dir}

# Make sure we get the MySQL user name otherwise die
if [ $# -lt 1 ] ; then
	
	echo "Usage: $0 mysql_username [use_mysql_password]";
	echo "       use_mysql_passwd should be 1 if using a password, 0 otherwise. Default is 0.";
	echo "       You will be prompted for your MySQL password.";
	exit 1; 
fi

if [ $# -lt 2 ] ; then

	use_passwd=0;
fi

# 
# CREATE YRC_NRSEQ DATABASE
# 
echo "Creating YRC_NRSEQ"
if [ $use_passwd -gt 0 ] ; then

	echo "mysql -u${mysql_user} -p < ${schema_dir}/YRC_NRSEQ.sql"
	mysql -u${mysql_user} -p < ${schema_dir}/YRC_NRSEQ.sql
	
else 
	echo "mysql -u${mysql_user} < ${schema_dir}/YRC_NRSEQ.sql"
	mysql -u${mysql_user} < ${schema_dir}/YRC_NRSEQ.sql
fi

STATUS=$?
if [ $STATUS -gt 0 ] ; then

	exit 1;
fi
echo ""


# 
# UPLOAD THE STANDARD FASTA FILES
# 
echo "Uploading SGD fasta file"
echo "${java} -classpath ${base_dir} -jar ${java_dir}/fastaparser.jar SGD ${fasta_dir}/SGD.fasta"
${java} -classpath ${base_dir} -jar ${java_dir}/fastaparser.jar SGD ${fasta_dir}/SGD.fasta > ${log_dir}/SGD.fastaparser.out
STATUS=$?
if [ $STATUS -gt 0 ] ; then
	echo "There was an error uploading SGD.fasta. Please look at ${log_dir}/SGD.fastaparser.out for more details."
        exit 1;
fi
echo "Done uploading SGD fasta file."
echo ""

echo "Uploading Wormbase fasta file"
echo "${java} -classpath ${base_dir} -jar ${java_dir}/fastaparser.jar Wormbase ${fasta_dir}/WormBase.fasta"
${java} -classpath ${base_dir} -jar ${java_dir}/fastaparser.jar Wormbase ${fasta_dir}/WormBase.fasta > ${log_dir}/WormBase.fastaparser.out
STATUS=$?
if [ $STATUS -gt 0 ] ; then
        echo "There was an error uploading WormBase.fasta. Please look at ${log_dir}/WormBase.fastaparser.out for details."
        exit 1;
fi

echo "Done uploading WormBase fasta file."
echo ""



echo "Uploading Flybase fasta file"
echo "${java} -classpath ${base_dir} -jar ${java_dir}/fastaparser.jar FlyBase ${fasta_dir}/FlyBase.fasta"
${java} -classpath ${base_dir} -jar ${java_dir}/fastaparser.jar FlyBase ${fasta_dir}/FlyBase.fasta > ${log_dir}/FlyBase.fastaparser.out
STATUS=$?
if [ $STATUS -gt 0 ] ; then
        echo "There was an error uploading FlyBase.fasta. Please look at ${log_dir}/FlyBase.fastaparser.out for details."
        exit 1;
fi

echo "Done uploading FlyBase fasta file."
echo ""


echo "Uploading Human IPI fasta file"

echo "Uploading HGNC fasta file"



