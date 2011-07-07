#!/bin/sh

# ---------------------------------------------------------------------------------
# A script to create the following databases from mysqldumps:
# 1. SangerPombe
# 2. flybase
# 3. wormbase
# 4. sgd_static_201005
# 5. hgnc_static_200708
# 6. cgd_static_200704
# 7. go_human
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

mysql_str=""
if [ "$mysql_passwd" != "" ] ; then
	mysql_str="--host=$mysql_host --user=$mysql_user --password=$mysql_passwd"
	
else
	mysql_str="--host=$mysql_host --user=$mysql_user"
fi
# echo "$mysql_str"


mysqldumpdir=$base_dir/mysqldump

databases=(SangerPombe flybase wormbase sgd_static_201005 hgnc_static_200708 cgd_static_200704 go_human)
# echo ${databases[@]}

for database in "${databases[@]}"
do
	echo "Creating database $database"
	gunzip $mysqldumpdir/$database.sql.gz
	# echo "mysql $mysql_str < $mysqldumpdir/$database.sql"
	mysql $mysql_str < $mysqldumpdir/$database.sql
	gzip $mysqldumpdir/$database.sql
	STATUS=$?
	if [ $STATUS -gt 0 ] ; then
		echo "There was an error create the database $database"
		exit 1;
	fi
	echo ""
done

exit 0