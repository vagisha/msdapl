#!/bin/sh

# ---------------------------------------------------------------------------------
# A script to create and initialize the YRC_NRSEQ database for the following
# organisms:
# 1. S. cerevisiae (Source: SGD)
# 2. C. elegans  (Source: WormBase)
# 3. D. melanogaster (Source: FlyBase)
# 4. Human (Sources: IPI, SwissProt, HGNC)
# ---------------------------------------------------------------------------------

java=$1;
base_dir=$2;
mysql_host=$3; # host name
mysql_user=$4; # MySQL username
mysql_passwd=$5; # MySQL password


if [ $# -lt 5 ] ; then
	mysql_passwd=""
fi

# Make sure we get all the required parameters
if [ $# -lt 4 ] ; then
	
	echo "Usage: $0 base_dir mysql_host mysql_username [mysql_password]";
	exit 1; 
fi
	
schema_dir="$base_dir/schema"
fasta_dir="$base_dir/fasta"
java_dir="$base_dir/java"
log_dir="$base_dir/logs"

# java=/usr/bin/java

mysql_str=""
if [ "$mysql_passwd" != "" ] ; then
	mysql_str="--host=$mysql_host --user=$mysql_user --password=$mysql_passwd"
	
else
	mysql_str="--host=$mysql_host --user=$mysql_user"
fi
# echo "$mysql_str"


# 
# CREATE YRC_NRSEQ DATABASE
# 
echo "Creating YRC_NRSEQ"
mysql $mysql_str < $schema_dir/YRC_NRSEQ.sql

STATUS=$?
if [ $STATUS -gt 0 ] ; then
	echo "Error creating YRC_NRSEQ database"
	exit 1;
fi
echo ""


# 
# UPLOAD THE STANDARD FASTA FILES
# 

#-------------------------------------------------
# SDG fasta file
#-------------------------------------------------
echo "Uploading SGD fasta file"
echo "$java -classpath $base_dir -jar $java_dir/fastaparser.jar SGD $fasta_dir/SGD.fasta"

$java -jar $java_dir/fastaparser.jar SGD $fasta_dir/SGD.fasta > $log_dir/SGD.fastaparser.out
STATUS=$?
if [ $STATUS -gt 0 ] ; then
	echo "There was an error uploading SGD.fasta. Please look at $log_dir/SGD.fastaparser.out for more details."
        exit 1;
fi
echo "Done uploading SGD fasta file."
echo ""

#-------------------------------------------------
# WormBase fasta file
#-------------------------------------------------
echo "Uploading Wormbase fasta file"
echo "$java -jar $java_dir/fastaparser.jar Wormbase $fasta_dir/WormBase.fasta"
$java -classpath $base_dir -jar $java_dir/fastaparser.jar Wormbase $fasta_dir/WormBase.fasta > $log_dir/WormBase.fastaparser.out
STATUS=$?
if [ $STATUS -gt 0 ] ; then
        echo "There was an error uploading WormBase.fasta. Please look at $log_dir/WormBase.fastaparser.out for details."
        exit 1;
fi

echo "Done uploading WormBase fasta file."
echo ""


#-------------------------------------------------
# FlyBase fasta file
#-------------------------------------------------
echo "Uploading Flybase fasta file"
echo "$java -jar $java_dir/fastaparser.jar FlyBase $fasta_dir/FlyBase.fasta"
$java -classpath $base_dir -jar $java_dir/fastaparser.jar FlyBase $fasta_dir/FlyBase.fasta > $log_dir/FlyBase.fastaparser.out
STATUS=$?
if [ $STATUS -gt 0 ] ; then
        echo "There was an error uploading FlyBase.fasta. Please look at $log_dir/FlyBase.fastaparser.out for details."
        exit 1;
fi

echo "Done uploading FlyBase fasta file."
echo ""


echo "Uploading Human IPI fasta file"

echo "Uploading HGNC fasta file"

echo "Uploading SwissProt file"

exit 0
