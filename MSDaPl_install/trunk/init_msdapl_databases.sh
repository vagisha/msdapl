#!/bin/sh

# ---------------------------------------------------------------------------------
# A script to create and initialize the following databases:
# 1. msData (this database stores scans, search results etc.)
# 2. mainDb (this database stores project, user information etc.)
# 3. jobQueue (this database stores information related to upload jobs)
# ---------------------------------------------------------------------------------

java=$1;
javac=$2
base_dir=$3;
mysql_host=$4; # host name
mysql_user=$5; # MySQL username
mysql_passwd=$6; # MySQL password


if [ $# -lt 6 ] ; then
	mysql_passwd=""
fi

# Make sure we get all the required parameters
if [ $# -lt 5 ] ; then
	
	echo "Usage: $0 java_exe_location javac_exe_location base_dir mysql_host mysql_username [mysql_password]";
	exit 1; 
fi

schema_dir=${base_dir}/schema

mysql_str=""
if [ "$mysql_passwd" != "" ] ; then
	mysql_str="--host=$mysql_host --user=$mysql_user --password=$mysql_passwd"
	
else
	mysql_str="--host=$mysql_host --user=$mysql_user"
fi
# echo "$mysql_str"



databases=(msData mainDb jobQueue)
# echo ${databases[@]}

for database in "${databases[@]}"
do
	echo "Creating database $database"
	# echo "mysql $mysql_str < $schema_dir/$database.sql"
	mysql $mysql_str < $schema_dir/$database.sql

	STATUS=$?
	if [ $STATUS -gt 0 ] ; then
		echo "There was an error create the database $database"
		exit 1;
	fi
	echo ""
done

# Initialize the mainDb database
# Create "administrators" group and one group for the lab
# Create one admin user and one normal user
java_dir="$base_dir/java"
cd $java_dir
echo "Initializing mainDb database"
$javac MainDbInitializer.java
$java -classpath .:./mysql-connector-java-5.1.6-bin.jar:/Users/silmaril/WORK/UW/MSDaPl_install/ MainDbInitializer

exit 0
