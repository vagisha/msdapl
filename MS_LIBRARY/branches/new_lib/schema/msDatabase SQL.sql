DROP DATABASE IF EXISTS msData;
CREATE DATABASE msData;
USE msData;


# EXPERIMENT

CREATE TABLE msExperiment (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	serverAddress VARCHAR(500),
	uploadDate date NOT NULL,,
	lastUpdate  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


# SPECTRA SIDE

CREATE TABLE msExperimentRun (
	experimentID INT UNSIGNED NOT NULL,
	runID INT UNSIGNED NOT NULL
);
ALTER TABLE msExperimentRun ADD INDEX(experimentID);
ALTER TABLE msExperimentRun ADD INDEX(runID);

CREATE TABLE msRun (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   filename VARCHAR(255),
   sha1Sum CHAR(40),
   creationTime VARCHAR(255),
   extractor VARCHAR(255),
   extractorVersion VARCHAR(255),
   extractorOptions VARCHAR(255),
   instrumentVendor VARCHAR(255),
   instrumentType VARCHAR(255),
   instrumentSN VARCHAR(255),
   acquisitionMethod VARCHAR(255),
   originalFileType VARCHAR(10),
   separateDigestion ENUM('T','F'),
   uploadDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   comment TEXT
);
ALTER TABLE msRun ADD INDEX(filename);

CREATE TABLE msRunLocation (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   runID INT UNSIGNED NOT NULL,
   serverDirectory VARCHAR(500),
   createDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE msRunLocation ADD INDEX(runID);


CREATE TABLE msScan (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   runID INT UNSIGNED NOT NULL,
   startScanNumber INT UNSIGNED,
   endScanNumber INT UNSIGNED,
   level TINYINT UNSIGNED,
   preMZ DECIMAL(18,9),
   preScanID INT UNSIGNED,
   prescanNumber INT UNSIGNED,
   retentionTime DECIMAL(10,5),
   fragmentationType CHAR(3),
   isCentroid ENUM('T','F'),
   peakCount INT UNSIGNED
);
ALTER TABLE msScan ADD INDEX(runID);
ALTER TABLE msScan ADD INDEX(startScanNumber);

CREATE TABLE msScanData (
   scanID INT UNSIGNED NOT NULL PRIMARY KEY,
   data LONGBLOB NOT NULL
)
PARTITION BY KEY()
PARTITIONS 100;
ALTER TABLE msScanData ADD INDEX(scanID);

CREATE TABLE MS2FileScanCharge (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   scanID INT UNSIGNED NOT NULL,
   charge TINYINT UNSIGNED NOT NULL,
   mass DECIMAL(18,9)
);
ALTER TABLE MS2FileScanCharge ADD INDEX(scanID);

CREATE TABLE MS2FileHeader (
   id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
   runID INTEGER NOT NULL,
   header VARCHAR(255) NOT NULL,
   value TEXT
);
ALTER TABLE MS2FileHeader ADD INDEX(runID, header);

CREATE TABLE MS2FileChargeIndependentAnalysis (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   scanID INT UNSIGNED NOT NULL,
   header VARCHAR(255),
   value TEXT
);
ALTER TABLE MS2FileChargeIndependentAnalysis ADD INDEX(scanID);

CREATE TABLE MS2FileChargeDependentAnalysis (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   scanChargeID INT UNSIGNED NOT NULL,
   header VARCHAR(255),
   value TEXT
);
ALTER TABLE MS2FileChargeDependentAnalysis ADD INDEX(scanChargeID);

CREATE TABLE msDigestionEnzyme (
   id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
   name VARCHAR(255) NOT NULL,
   sense TINYINT,
   cut VARCHAR(20),
   nocut VARCHAR(20),
   description TEXT
);
ALTER TABLE msDigestionEnzyme ADD INDEX(name);

CREATE TABLE msRunEnzyme (
   runID INT UNSIGNED NOT NULL,
   enzymeID INT UNSIGNED NOT NULL
);
ALTER TABLE msRunEnzyme ADD PRIMARY KEY (runID, enzymeID);
ALTER TABLE msRunEnzyme ADD INDEX (enzymeID);




# PEPTIDE ANALYSIS SIDE

CREATE TABLE msSearch (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   experimentID INT UNSIGNED NOT NULL,
   expDate DATE,
   serverDirectory VARCHAR(500),
   analysisProgramName VARCHAR(255),
   analysisProgramVersion VARCHAR(20),
   uploadDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE msSearch ADD INDEX(expDate);
ALTER TABLE msSearch ADD INDEX(experimentID);


CREATE TABLE SQTParams (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   searchID INT UNSIGNED NOT NULL,
   param VARCHAR(255) NOT NULL,
   value TEXT
);
ALTER TABLE SQTParams ADD INDEX(searchID,param);

CREATE TABLE ProLuCIDParams (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   searchID INT UNSIGNED NOT NULL,
   elementName VARCHAR(255) NOT NULL,
   value TEXT,
   parentID INT UNSIGNED
);
ALTER TABLE ProLuCIDParams ADD INDEX(searchID,elementName);


CREATE TABLE msRunSearch (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   runID INT UNSIGNED NOT NULL,
   searchID INT UNSIGNED NOT NULL,
   originalFileType VARCHAR(10) NOT NULL,
   searchDate DATE,
   searchDuration INT UNSIGNED,
   uploadDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE msRunSearch ADD INDEX(runID);
ALTER TABLE msRunSearch ADD INDEX(searchID);

CREATE TABLE msSearchEnzyme (
   searchID INT UNSIGNED NOT NULL,
   enzymeID INT UNSIGNED NOT NULL
);
ALTER TABLE msSearchEnzyme ADD PRIMARY KEY (searchID, enzymeID);
ALTER TABLE msSearchEnzyme ADD INDEX (enzymeID);


CREATE TABLE msRunSearchResult (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   runSearchID INT UNSIGNED NOT NULL,
   scanID INT UNSIGNED NOT NULL,
   charge TINYINT NOT NULL,
   peptide VARCHAR(500) NOT NULL,
   preResidue CHAR(1),
   postResidue CHAR(1),
   validationStatus CHAR(1)
);

ALTER TABLE msRunSearchResult ADD INDEX(runSearchID);
ALTER TABLE msRunSearchResult ADD INDEX(scanID);
ALTER TABLE msRunSearchResult ADD INDEX(charge);
ALTER TABLE msRunSearchResult ADD INDEX(peptide);
# DO I WANT ALL THESE INDICES?

CREATE TABLE msProteinMatch (
    resultID INT UNSIGNED NOT NULL,
    accession VARCHAR(255) NOT NULL
)
PARTITION BY KEY(resultID)
PARTITIONS 100;
ALTER TABLE msProteinMatch ADD PRIMARY KEY(resultID, accession);
ALTER TABLE msProteinMatch ADD INDEX(accession);


CREATE TABLE SQTSpectrumData (
   scanID INT UNSIGNED NOT NULL,
   runSearchID INT UNSIGNED NOT NULL,
   charge TINYINT UNSIGNED,
   processTime INT UNSIGNED,
   serverName VARCHAR(50),
   totalIntensity DECIMAL(18,9),
   observedMass DECIMAL(18,9),
   lowestSp DECIMAL(10,5),
   sequenceMatches INT UNSIGNED
);
ALTER TABLE SQTSpectrumData ADD PRIMARY KEY(scanID, runSearchID, charge);
ALTER TABLE SQTSpectrumData ADD INDEX (runSearchID);
ALTER TABLE SQTSpectrumData ADD INDEX (charge);

CREATE TABLE SQTFileHeader (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   runSearchID INT UNSIGNED NOT NULL,
   header VARCHAR(255) NOT NULL,
   value TEXT
);
ALTER TABLE SQTFileHeader ADD INDEX(runSearchID, header);

CREATE TABLE msSequenceDatabaseDetail (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   serverAddress VARCHAR(100),
   serverPath VARCHAR(500),
   sequenceDatabaseID INT UNSIGNED NOT NULL
);

CREATE TABLE msSearchDatabase (
   searchID INT UNSIGNED NOT NULL,
   databaseID INT UNSIGNED NOT NULL
);
ALTER TABLE msSearchDatabase ADD PRIMARY KEY(searchID, databaseID);
ALTER TABLE msSearchDatabase ADD INDEX(databaseID);

CREATE TABLE msSearchStaticMod (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   searchID INT UNSIGNED NOT NULL,
   residue CHAR(1) NOT NULL,
   modifier DECIMAL(18,9) NOT NULL
);
ALTER TABLE msSearchStaticMod ADD INDEX(searchID);

CREATE TABLE msSearchTerminalStaticMod (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   searchID INT UNSIGNED NOT NULL,
   terminus ENUM('N','C') NOT NULL,
   modifier DECIMAL(18,9) NOT NULL
);
ALTER TABLE msSearchTerminalStaticMod ADD INDEX(searchID);


CREATE TABLE msSearchDynamicMod (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   searchID INT UNSIGNED NOT NULL,
   residue CHAR(1) NOT NULL,
   modifier DECIMAL(18,9) NOT NULL,
   symbol CHAR(1)
);
ALTER TABLE msSearchDynamicMod ADD INDEX(searchID);

CREATE TABLE msSearchTerminalDynamicMod (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   searchID INT UNSIGNED NOT NULL,
   terminus ENUM('N','C') NOT NULL,
   modifier DECIMAL(18,9) NOT NULL,
   symbol CHAR(1)
);
ALTER TABLE msSearchTerminalDynamicMod ADD INDEX(searchID);


CREATE TABLE msDynamicModResult (
   modID INT UNSIGNED NOT NULL,
   resultID INT UNSIGNED NOT NULL,
   position SMALLINT UNSIGNED NOT NULL
);
ALTER TABLE msDynamicModResult ADD PRIMARY KEY(modID, resultID, position);
ALTER TABLE msDynamicModResult ADD INDEX(resultID);

CREATE TABLE msTerminalDynamicModResult (
   modID INT UNSIGNED NOT NULL,
   resultID INT UNSIGNED NOT NULL
);
ALTER TABLE msTerminalDynamicModResult ADD PRIMARY KEY(modID, resultID);
ALTER TABLE msTerminalDynamicModResult ADD INDEX(resultID);


CREATE TABLE SQTSearchResult (
   resultID INT UNSIGNED NOT NULL PRIMARY KEY,
   XCorrRank INT UNSIGNED NOT NULL,
   spRank INT UNSIGNED NOT NULL,
   deltaCN DECIMAL(10,5) NOT NULL,
   XCorr DECIMAL(10,5) NOT NULL,
   sp DECIMAL(10,5),
   calculatedMass DECIMAL(18,9),
   matchingIons INT UNSIGNED,
   predictedIons INT UNSIGNED,
   evalue DOUBLE UNSIGNED
);
ALTER TABLE SQTSearchResult ADD INDEX(XCorrRank);
ALTER TABLE SQTSearchResult ADD INDEX(spRank);
ALTER TABLE SQTSearchResult ADD INDEX(deltaCN);
ALTER TABLE SQTSearchResult ADD INDEX(XCorr);
ALTER TABLE SQTSearchResult ADD INDEX(sp);

CREATE TABLE ProLuCIDSearchResult (
   resultID INT UNSIGNED NOT NULL PRIMARY KEY,
   primaryScoreRank INT UNSIGNED NOT NULL,
   secondaryScoreRank INT UNSIGNED NOT NULL,
   deltaCN DECIMAL(10,5)  NOT NULL,
   primaryScore DOUBLE  UNSIGNED NOT NULL,
   secondaryScore DOUBLE  UNSIGNED NOT NULL,
   calculatedMass DECIMAL(18,9),
   matchingIons INT UNSIGNED,
   predictedIons INT UNSIGNED
);
ALTER TABLE ProLuCIDSearchResult ADD INDEX(primaryScoreRank);
ALTER TABLE ProLuCIDSearchResult ADD INDEX(secondaryScoreRank);
ALTER TABLE ProLuCIDSearchResult ADD INDEX(deltaCN);
ALTER TABLE ProLuCIDSearchResult ADD INDEX(primaryScore);
ALTER TABLE ProLuCIDSearchResult ADD INDEX(secondaryScore);


# TRIGGERS TO ENSURE CASCADING DELETES

DELIMITER |
CREATE TRIGGER msRunSearchResult_bdelete BEFORE DELETE ON msRunSearchResult
 FOR EACH ROW
 BEGIN
   DELETE FROM msProteinMatch WHERE resultID = OLD.id;
   DELETE FROM SQTSearchResult WHERE resultID = OLD.id;
   DELETE FROM ProLuCIDSearchResult WHERE resultID = OLD.id;
   DELETE FROM msDynamicModResult WHERE resultID = OLD.id;
   DELETE FROM msTerminalDynamicModResult WHERE resultID = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER msSearchDynamicMod_bdelete BEFORE DELETE ON msSearchDynamicMod
 FOR EACH ROW
 BEGIN
   DELETE FROM msDynamicModResult WHERE modID = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER msSearchTerminalDynamicMod_bdelete BEFORE DELETE ON msSearchTerminalDynamicMod
 FOR EACH ROW
 BEGIN
   DELETE FROM msTerminalDynamicModResult WHERE modID = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER msSequenceDatabaseDetail_bdelete BEFORE DELETE ON msSequenceDatabaseDetail
 FOR EACH ROW
 BEGIN
   DELETE FROM msSearchDatabase WHERE databaseID = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER msRunSearch_bdelete BEFORE DELETE ON msRunSearch
 FOR EACH ROW
 BEGIN
   DELETE FROM msRunSearchResult WHERE runSearchID = OLD.id;
   DELETE FROM SQTSpectrumData WHERE runSearchID = OLD.id;
   DELETE FROM SQTFileHeader WHERE runSearchID = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER msDigestionEnzyme_bdelete BEFORE DELETE ON msDigestionEnzyme
 FOR EACH ROW
 BEGIN
   DELETE FROM msSearchEnzyme WHERE enzymeID = OLD.id;
   DELETE FROM msRunEnzyme WHERE enzymeID = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER MS2FileScanCharge_bdelete BEFORE DELETE ON MS2FileScanCharge
 FOR EACH ROW
 BEGIN
   DELETE FROM MS2FileChargeDependentAnalysis WHERE scanChargeID = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER msScan_bdelete BEFORE DELETE ON msScan
 FOR EACH ROW
 BEGIN
   DELETE FROM MS2FileScanCharge WHERE scanID = OLD.id;
   DELETE FROM msRunSearchResult WHERE scanID = OLD.id;
   DELETE FROM SQTSpectrumData WHERE scanID = OLD.id;
   DELETE FROM MS2FileChargeIndependentAnalysis WHERE scanID = OLD.id;
   DELETE FROM msScanData WHERE scanID = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER msRun_bdelete BEFORE DELETE ON msRun
 FOR EACH ROW
 BEGIN
   DELETE FROM msScan WHERE runID = OLD.id;
   DELETE FROM msRunSearch WHERE runID = OLD.id;
   DELETE FROM msRunEnzyme WHERE runID = OLD.id;
   DELETE FROM MS2FileHeader WHERE runID = OLD.id;
   DELETE FROM msRunLocation WHERE runID = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER msSearch_bdelete BEFORE DELETE ON msSearch
 FOR EACH ROW
 BEGIN
   DELETE FROM msSearchDatabase WHERE searchID = OLD.id;
   DELETE FROM SQTParams WHERE searchID = OLD.id;
   DELETE FROM ProLuCIDParams WHERE searchID = OLD.id;
   DELETE FROM msSearchEnzyme WHERE searchID = OLD.id;
   DELETE FROM msRunSearch WHERE searchID = OLD.id;
   DELETE FROM msSearchStaticMod WHERE searchID = OLD.id;
   DELETE FROM msSearchTerminalStaticMod WHERE searchID = OLD.id;
   DELETE FROM msSearchDynamicMod WHERE searchID = OLD.id;
   DELETE FROM msSearchTerminalDynamicMod WHERE searchID = OLD.id;
 END;
|
DELIMITER ;