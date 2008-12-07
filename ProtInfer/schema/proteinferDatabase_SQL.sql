DROP DATABASE IF EXISTS proteinfer_test;
CREATE DATABASE proteinfer_test;
USE proteinfer_test;

CREATE TABLE msProteinInferRun (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	program VARCHAR(255) NOT NULL,
	dateRun DATETIME,
	comments TEXT(10)
);

CREATE TABLE IDPickerRunSummary (
	piRunID INT UNSIGNED NOT NULL PRIMARY KEY,
	numUnfilteredProteins INT UNSIGNED,
	numUnfilteredPeptides INT UNSIGNED
);

CREATE TABLE msProteinInferInput (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    piRunID INT UNSIGNED NOT NULL,
    runSearchID INT UNSIGNED NOT NULL
);
ALTER TABLE msProteinInferInput ADD INDEX (piRunID);
ALTER TABLE msProteinInferInput ADD INDEX (runSearchID);

CREATE TABLE IDPickerInputSummary (
	piInputID INT UNSIGNED NOT NULL PRIMARY KEY,
	numTargetHits INT UNSIGNED,
    numDecoyHits INT UNSIGNED,
    numFilteredTargetHits INT UNSIGNED
);

CREATE TABLE IDPickerFilter (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    piRunID INT UNSIGNED NOT NULL,
    filterName VARCHAR(255) NOT NULL,
   	filterValue VARCHAR(255)
);
ALTER TABLE  IDPickerFilter ADD INDEX (piRunID);

CREATE TABLE msProteinInferProtein (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	nrseqProteinID INT UNSIGNED NOT NULL,
	piRunID INT UNSIGNED NOT NULL,
    coverage DOUBLE UNSIGNED,
    userValidation CHAR(1),
    userAnnotation TEXT(10)
);
ALTER TABLE  msProteinInferProtein ADD INDEX (piRunID);
ALTER TABLE  msProteinInferProtein ADD INDEX (nrseqProteinID);

CREATE TABLE IDPickerProtein (
	piProteinID INT UNSIGNED NOT NULL PRIMARY KEY,
	clusterID INT UNSIGNED NOT NULL,
    groupID INT UNSIGNED NOT NULL,
    isParsimonious TINYINT NOT NULL DEFAULT 0
);


CREATE TABLE msProteinInferPeptide (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	sequence VARCHAR(255) NOT NULL
);

CREATE TABLE IDPickerPeptide (
	piPeptideID INT UNSIGNED NOT NULL PRIMARY KEY,
	groupID INT UNSIGNED NOT NULL
);

#CREATE TABLE proteinferIon(
#	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
#	pinferPeptideID INT UNSIGNED NOT NULL,
#	charge INT UNSIGNED NOT NULL,
#  	modificationStateID INT UNSIGNED NOT NULL
#);

CREATE TABLE msProteinInferProteinPeptideMatch (
    piProteinID INT UNSIGNED NOT NULL,
    piPeptideID INT UNSIGNED NOT NULL
);
ALTER TABLE msProteinInferProteinPeptideMatch ADD PRIMARY KEY (piProteinID, piPeptideID);


CREATE TABLE IDPickerGroupAssociation (
	piRunID INT UNSIGNED NOT NULL,
	proteinGroupID INT UNSIGNED NOT NULL,
	peptideGroupID INT UNSIGNED NOT NULL
);
ALTER TABLE IDPickerGroupAssociation ADD INDEX(piRunID);


CREATE TABLE msProteinInferSpectrumMatch (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    msRunSearchResultID INT UNSIGNED NOT NULL,
    piPeptideID INT UNSIGNED NOT NULL
);
ALTER TABLE  msProteinInferSpectrumMatch ADD INDEX (msRunSearchResultID);
ALTER TABLE  msProteinInferSpectrumMatch ADD INDEX (piPeptideID);


CREATE TABLE IDPickerSpectrumMatch (
	piSpectrumMatchID INT UNSIGNED NOT NULL PRIMARY KEY,
	fdr DOUBLE UNSIGNED
);



# TRIGGERS TO ENSURE CASCADING DELETES

DELIMITER |
CREATE TRIGGER msProteinInferSpectrumMatch_bdelete BEFORE DELETE ON msProteinInferSpectrumMatch
 FOR EACH ROW
 BEGIN
   DELETE FROM IDPickerSpectrumMatch WHERE piSpectrumMatchID = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER msProteinInferPeptide_bdelete BEFORE DELETE ON msProteinInferPeptide
 FOR EACH ROW
 BEGIN
   DELETE FROM msProteinInferSpectrumMatch WHERE piPeptideID = OLD.id;
   DELETE FROM IDPickerPeptide WHERE piPeptideID = OLD.id;
   DELETE FROM msProteinInferProteinPeptideMatch WHERE piPeptideID = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER msProteinInferProtein_bdelete BEFORE DELETE ON msProteinInferProtein
 FOR EACH ROW
 BEGIN
 	DELETE FROM IDPickerProtein WHERE piProteinID = OLD.id;
   	DELETE FROM msProteinInferProteinPeptideMatch WHERE piProteinID = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER msProteinInferInput_bdelete BEFORE DELETE ON msProteinInferInput
 FOR EACH ROW
 BEGIN
 	DELETE FROM IDPickerInputSummary WHERE piInputID = OLD.id;
 END;
|
DELIMITER ;


DELIMITER |
CREATE TRIGGER msProteinInferRun_bdelete BEFORE DELETE ON msProteinInferRun
 FOR EACH ROW
 BEGIN
 	DELETE FROM IDPickerRunSummary WHERE piRunID = OLD.id;
  	DELETE FROM IDPickerFilter WHERE piRunID = OLD.id;
   	DELETE FROM msProteinInferInput WHERE piRunID = OLD.id;
  	DELETE FROM msProteinInferProtein WHERE piRunID = OLD.id;
   	DELETE FROM IDPickerGroupAssociation WHERE piRunID = OLD.id;
 END;
|
DELIMITER ;
