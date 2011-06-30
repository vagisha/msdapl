#!/bin/sh

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


# Write yrc_nrseq_db.properties file
echo "# MySQL database properties" > $base_dir/yrc_nrseq_db.properties
echo "db.host=$mysql_host" >> $base_dir/yrc_nrseq_db.properties
echo "db.name=YRC_NRSEQ" >> $base_dir/yrc_nrseq_db.properties
echo "db.username=$mysql_user" >> $base_dir/yrc_nrseq_db.properties
echo "db.password=$mysql_passwd" >> $base_dir/yrc_nrseq_db.properties

exit 0