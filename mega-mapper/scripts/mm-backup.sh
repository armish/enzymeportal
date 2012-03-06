#!/bin/bash
# Makes a backup of the mega-map.
# Param:
# $1: database environment (enzdev|ezprel)

. $(dirname $0)/readDbConfig.sh

EP_BACKUP_DIR=/nfs/panda/production/steinbeck/ep/backup
DB_DUMP=$EP_BACKUP_DIR/ep-mm-$(date +%Y%m%d_%H%M).dmp

$ORACLE_HOME/bin/exp ${DB_USER}/${DB_PASSWD}@${1} \
    parfile=$(dirname $0)/export.par file=$DB_DUMP
gzip $DB_DUMP

