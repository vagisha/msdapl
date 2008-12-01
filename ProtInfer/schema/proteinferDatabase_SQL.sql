DROP DATABASE IF EXISTS proteinfer_test;
CREATE DATABASE proteinfer_test;
USE proteinfer_test;

CREATE TABLE proteinferRun (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	status CHAR(1) NOT NULL,
	program VARCHAR(255) NOT NULL,
	dateCreated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	dateCompleted DATETIME,
	comments TEXT
);

CREATE TABLE IDPickerRunSummary (
	pinferID INT UNSIGNED NOT NULL PRIMARY KEY,
	numUnfilteredProteins INT UNSIGNED,
	numUnfilteredPeptides INT UNSIGNED
);

CREATE TABLE proteinferInput (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    pinferID INT UNSIGNED NOT NULL,
    runSearchID INT UNSIGNED NOT NULL
);
ALTER TABLE proteinferInput ADD INDEX (pinferID);
ALTER TABLE proteinferInput ADD INDEX (runSearchID);

CREATE TABLE IDPickerInputSummary (
	pinferInputID INT UNSIGNED NOT NULL PRIMARY KEY,
	numTargetHits INT UNSIGNED,
    numDecoyHits INT UNSIGNED,
    numFilteredTargetHits INT UNSIGNED
);

CREATE TABLE proteinferFilter (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    pinferID INT UNSIGNED NOT NULL,
    filterName VARCHAR(255) NOT NULL,
   	filterValue VARCHAR(255)
);
ALTER TABLE  proteinferFilter ADD INDEX (pinferID);

CREATE TABLE proteinferProtein (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	nrseqDbProteinID INT UNSIGNED NOT NULL,
	pinferID INT UNSIGNED NOT NULL,
    coverage DOUBLE UNSIGNED,
    userAnnotation TEXT,
    userValidation CHAR(1)
);
ALTER TABLE  proteinferProtein ADD INDEX (pinferID);

CREATE TABLE IDPickerProtein (
	pinferProteinID INT UNSIGNED NOT NULL PRIMARY KEY,
	clusterID INT UNSIGNED NOT NULL,
    groupID INT UNSIGNED NOT NULL,
    isParsimonious TINYINT NOT NULL DEFAULT 0
);


CREATE TABLE proteinferPeptide (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	pinferID INT UNSIGNED NOT NULL
);

CREATE TABLE IDPickerPeptide (
	pinferPeptideID INT UNSIGNED NOT NULL PRIMARY KEY,
	groupID INT UNSIGNED NOT NULL
);


CREATE TABLE proteinferPeptideProteinMatch (
    pinferProteinID INT UNSIGNED NOT NULL,
    pinferPeptideID INT UNSIGNED NOT NULL
);
ALTER TABLE proteinferPeptideProteinMatch ADD PRIMARY KEY (pinferProteinID, pinferPeptideID);


CREATE TABLE IDPickerGroupAssociation (
	pinferID INT UNSIGNED NOT NULL,
	idpickerProteinGroupID INT UNSIGNED NOT NULL,
	idpickerPeptideGroupID INT UNSIGNED NOT NULL
);
ALTER TABLE IDPickerGroupAssociation ADD INDEX(pinferID);


CREATE TABLE proteinferSpectrumMatch (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    msRunSearchResultID INT UNSIGNED NOT NULL,
    pinferPeptideID INT UNSIGNED NOT NULL,
    rank INT UNSIGNED NOT NULL
);
ALTER TABLE  proteinferSpectrumMatch ADD INDEX (msRunSearchResultID);

CREATE TABLE IDPickerSpectrumMatch (
	pinferSpectrumMatchID INT UNSIGNED NOT NULL PRIMARY KEY,
	fdr DOUBLE UNSIGNED
);



# TRIGGERS TO ENSURE CASCADING DELETES

DELIMITER |
CREATE TRIGGER proteinferSpectrumMatch_bdelete BEFORE DELETE ON proteinferSpectrumMatch
 FOR EACH ROW
 BEGIN
   DELETE FROM IDPickerSpectrumMatch WHERE pinferSpectrumMatchID = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER proteinferPeptide_bdelete BEFORE DELETE ON proteinferPeptide
 FOR EACH ROW
 BEGIN
   DELETE FROM proteinferSpectrumMatch WHERE pinferPeptideID = OLD.id;
   DELETE FROM IDPickerPeptide WHERE pinferPeptideID = OLD.id;
   DELETE FROM proteinferPeptideProteinMatch WHERE pinferPeptideID = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER proteinferProtein_bdelete BEFORE DELETE ON proteinferProtein
 FOR EACH ROW
 BEGIN
 	DELETE FROM IDPickerProtein WHERE pinferProteinID = OLD.id;
   	DELETE FROM proteinferPeptideProteinMatch WHERE pinferProteinID = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER proteinferInput_bdelete BEFORE DELETE ON proteinferInput
 FOR EACH ROW
 BEGIN
 	DELETE FROM IDPickerInputSummary WHERE pinferInputID = OLD.id;
 END;
|
DELIMITER ;


DELIMITER |
CREATE TRIGGER proteinferRun_bdelete BEFORE DELETE ON proteinferRun
 FOR EACH ROW
 BEGIN
 	DELETE FROM IDPickerRunSummary WHERE pinferID = OLD.id;
  	DELETE FROM proteinferFilter WHERE pinferID = OLD.id;
   	DELETE FROM proteinferInput WHERE pinferID = OLD.id;
  	DELETE FROM proteinferProtein WHERE pinferID = OLD.id;
   	DELETE FROM proteinferPeptide WHERE pinferID = OLD.id;
   	DELETE FROM IDPickerGroupAssociation WHERE pinferID = OLD.id;
 END;
|
DELIMITER ;
