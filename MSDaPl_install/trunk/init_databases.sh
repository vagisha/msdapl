#!/bin/sh

# ---------------------------------------------------------------------------------
# A shell script to create and initialize the following databases:
# 1. YRC_NRSEQ (a non-redundant protein sequence database)
# 2. mygo (gene ontology database)
# ---------------------------------------------------------------------------------

# source the properties
# Don't use shell-hostile constructs in the shell-readable properties file!
. config.properties

echo "MySQL host: $db_host"
echo "MySQL user: $db_user"

# if [ "$use_db_passwd" -gt 0 ] ; then
if [ "$db_passwd" != "" ] ; then
	
	echo Using MySQL password: YES
else

	echo Using MySQL password: NO
fi

base_dir=`pwd`

# Write properties file
sh create_properties_files.sh $base_dir $db_host $db_user $db_passwd 

# Initialize YRC_NRSEQ
sh init_yrc_nrseq.sh $java_location $base_dir $db_host $db_user $db_passwd 

# Initialize GO database
# sh init_mygo.sh $base_dir $db_host $db_user $db_passwd 

# Initialize species specific databases
sh create_biodb.sh $base_dir $db_host $db_user $db_passwd 

# Create the MSDaPl databases
sh init_msdapl_databases.sh $java $javac $base_dir $db_host $db_user $db_passwd 


exit 0