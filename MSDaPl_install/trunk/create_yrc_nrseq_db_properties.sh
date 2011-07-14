#!/bin/sh

# source the properties
# Don't use shell-hostile constructs in the shell-readable properties file!
. config.properties

base_dir=$1; # this is the directory where the file will be created

if [ $# -lt 1 ] ; then
	base_dir="."
fi


props_file="$base_dir/yrc_nrseq_db.properties"
echo ""
echo "Creating properties file $props_file"

# Write yrc_nrseq_db.properties file
echo "# MySQL database properties" > $props_file
echo "db.host=$db_host" >> $props_file
echo "db.name=YRC_NRSEQ" >> $props_file
echo "db.username=$db_user" >> $props_file
echo "db.password=$db_passwd" >> $props_file

echo "db.ncbi.host=$db_host" >> $props_file
echo "db.ncbi.name=NCBI" >> $props_file
echo "db.ncbi.username=$db_user" >>$props_file
echo "db.ncbi.password=$db_passwd" >> $props_file

exit 0