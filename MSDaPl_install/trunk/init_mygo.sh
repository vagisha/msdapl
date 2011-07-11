#!/bin/sh

# ---------------------------------------------------------------------------------
# A script to initialize the mygo database
# ---------------------------------------------------------------------------------

base_dir=$1;
mysql_host=$2; # host name
mysql_user=$3; # MySQL username
mysql_passwd=$4; # MySQL password


if [ $# -lt 4 ] ; then
	mysql_passwd=""
fi

# Make sure we get all the required parameters
if [ $# -lt 3 ] ; then
	
	echo "Usage: $0 base_dir mysql_host mysql_username [mysql_password]";
	exit 1; 
fi

go_dir=${base_dir}/go
java_dir=${base_dir}/java
log_dir=${base_dir}/logs

mysql_str=""
if [ "$mysql_passwd" != "" ] ; then
	mysql_str="--host=$mysql_host --user=$mysql_user --password=$mysql_passwd"
	
else
	mysql_str="--host=$mysql_host --user=$mysql_user"
fi
# echo "$mysql_str"


# 
# Create mygo database
# 
echo "Creating mygo"
mysql $mysql_str -e 'DROP DATABASE IF EXISTS mygo'
mysql $mysql_str -e 'CREATE DATABASE mygo'


STATUS=$?
if [ $STATUS -gt 0 ] ; then
	echo "There was an error create mygo database"
	exit 1;
fi
echo ""

# expand go_daily-termdb-tables.tar.gz
cd $go_dir
# tar -xvf go_daily-termdb-tables.tar.gz

# create the tables in the database
cd go_daily-termdb-tables
FILES=*.sql
for f in $FILES
do
	mysql $mysql_str --database mygo < $f
done

exit 0

