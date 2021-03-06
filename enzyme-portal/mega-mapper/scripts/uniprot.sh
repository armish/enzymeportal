#!/bin/bash
# Creates an initial mega-map with both Swiss-Prot and TrEMBL enzymes.

#DOWNLOAD_BASE=ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/complete
EBINOCLE_DATA=/ebi/extserv/projects/ebinocle/data
UNIPROT_DATA=$EBINOCLE_DATA/uniprot/latest
SWISSPROT=$UNIPROT_DATA/uniprot_sprot.xml
TREMBL=$UNIPROT_DATA/uniprot_trembl.xml

. $(dirname $0)/mvnBuild.sh $1

echo "Starting UniProt import - $(date)"
java -classpath $CP uk.ac.ebi.ep.mm.app.UniprotSaxParser -file $SWISSPROT \
&& java -classpath $CP uk.ac.ebi.ep.mm.app.UniprotSaxParser -file $TREMBL
echo "Finished UniProt import - $(date)"
