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

props_file="$base_dir/yrc_nrseq_db.properties"
echo ""
echo "Creating properties file $props_file"

# Write yrc_nrseq_db.properties file
echo "# MySQL database properties" > $props_file
echo "db.host=$mysql_host" >> $props_file
echo "db.name=YRC_NRSEQ" >> $props_file
echo "db.username=$mysql_user" >> $props_file
echo "db.password=$mysql_passwd" >> $props_file

echo "db.ncbi.host=$mysql_host" >> $props_file
echo "db.ncbi.name=NCBI" >> $props_file
echo "db.ncbi.username=$mysql_user" >>$props_file
echo "db.ncbi.password=$mysql_passwd" >> $props_file

exit 0