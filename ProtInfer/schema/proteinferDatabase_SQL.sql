DROP DATABASE IF EXISTS proteinfer;
CREATE DATABASE proteinfer;
USE proteinfer;


CREATE TABLE ProteinferRun (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	status CHAR(1),
	dateCreated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	dateCompleted TIMESTAMP
);

CREATE TABLE ProteinferInput (
    pinferID INT UNSIGNED NOT NULL,
    runSearchID INT UNSIGNED NOT NULL
);
ALTER TABLE ProteinferInput ADD PRIMARY KEY (pinferID, runSearchID);

CREATE TABLE ProteinferFilter (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    pinferID INT UNSIGNED NOT NULL,
    filterName VARCHAR(255) NOT NULL,
   	value VARCHAR(255)
);
ALTER TABLE  ProteinferFilter ADD INDEX (pinferID);

CREATE TABLE ProteinferProtein (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	pinferID INT UNSIGNED NOT NULL,
	accession VARCHAR(255) NOT NULL,
    nrseqProteinID INT UNSIGNED NOT NULL,
    coverage DOUBLE UNSIGNED,
    clusterID INT UNSIGNED NOT NULL,
    groupID INT UNSIGNED NOT NULL,
    isParsimonious BIT NOT NULL DEFAULT 0,
    userAnnotation TEXT,
    userValidation CHAR(1)
);
ALTER TABLE  ProteinferProtein ADD INDEX (pinferID);
ALTER TABLE  ProteinferProtein ADD INDEX (nrseqProteinID);


CREATE TABLE ProteinferPeptide (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	pinferID INT UNSIGNED NOT NULL,
    groupID INT UNSIGNED NOT NULL,
    sequence VARCHAR(255) NOT NULL
);

CREATE TABLE ProteinferPeptideProteinMatch (
    pinferProteinID INT UNSIGNED NOT NULL,
    pinferPeptideID INT UNSIGNED NOT NULL
);
ALTER TABLE ProteinferPeptideProteinMatch ADD PRIMARY KEY (pinferProteinID, pinferPeptideID);

CREATE TABLE ProteinferSpectrumMatch (
    msRunSearchResultID INT UNSIGNED NOT NULL,
    pinferPeptideID INT UNSIGNED NOT NULL,
    fdr DOUBLE UNSIGNED
);
ALTER TABLE  ProteinferSpectrumMatch ADD INDEX (msRunSearchResultID);



# TRIGGERS TO ENSURE CASCADING DELETES

DELIMITER |
CREATE TRIGGER ProteinferPeptide_bdelete BEFORE DELETE ON ProteinferPeptide
 FOR EACH ROW
 BEGIN
   DELETE FROM ProteinferSpectrumMatch WHERE pinferPeptideID = OLD.id;
   DELETE FROM ProteinferPeptideProteinMatch WHERE pinferPeptideID = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER ProteinferProtein_bdelete BEFORE DELETE ON ProteinferProtein
 FOR EACH ROW
 BEGIN
   DELETE FROM ProteinferPeptideProteinMatch WHERE pinferProteinID = OLD.id;
 END;
|
DELIMITER ;


DELIMITER |
CREATE TRIGGER ProteinferRun_bdelete BEFORE DELETE ON ProteinferRun
 FOR EACH ROW
 BEGIN
   DELETE FROM ProteinferFilter WHERE pinferID = OLD.id;
   DELETE FROM ProteinferInput WHERE pinferID = OLD.id;
   DELETE FROM ProteinferProtein WHERE pinferID = OLD.id;
   DELETE FROM ProteinferPeptide WHERE pinferID = OLD.id;
 END;
|
DELIMITER ;
