DROP DATABASE IF EXISTS msData_test2;

CREATE DATABASE msData_test2;

USE msData_test2;



# SPECTRA SIDE



CREATE TABLE msExperiment (

    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,

    expDate DATE,

    serverAddress VARCHAR(500),

    serverDirectory VARCHAR(500)

);

ALTER TABLE msExperiment ADD INDEX(expDate);



CREATE TABLE msExperimentRun (

    runID INT UNSIGNED NOT NULL,

    experimentID INT UNSIGNED NOT NULL

);

ALTER TABLE msExperimentRun ADD PRIMARY KEY (runID, experimentID );

ALTER TABLE msExperimentRun ADD INDEX (experimentID );



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

    dataType VARCHAR(255),

    acquisitionMethod VARCHAR(255),

    originalFileType VARCHAR(10),

    uploadDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    comment TEXT

);

ALTER TABLE msRun ADD INDEX(filename);



CREATE TABLE msScan (

    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,

    runID INT UNSIGNED NOT NULL,

    startScanNumber INT UNSIGNED,

    endScanNumber INT UNSIGNED,

    level TINYINT UNSIGNED,

    preMZ DECIMAL(10,5),

    preScanID INT UNSIGNED,

    prescanNumber INT UNSIGNED,

    retentionTime DECIMAL(10,5),

    fragmentationType CHAR(3)

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

    mass DECIMAL(10,5)

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



CREATE TABLE msPeptideSearch (

    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,

    runID INT UNSIGNED NOT NULL,

    experimentID INT UNSIGNED NOT NULL,

    originalFileType VARCHAR(10) NOT NULL,

    analysisProgramName VARCHAR(255),

    analysisProgramVersion VARCHAR(10),

    searchDate DATE,

    searchDuration INT UNSIGNED,

    precursorMassMethod VARCHAR(20),

    precursorMassTolerance DECIMAL(10,5),

    fragmentMassMethod VARCHAR(20),

    fragmentMassTolerance DECIMAL(10,5),

    uploadDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);

ALTER TABLE msPeptideSearch ADD INDEX(runID);

ALTER TABLE msPeptideSearch ADD INDEX(experimentID);



CREATE TABLE msSearchEnzyme (

    searchID INT UNSIGNED NOT NULL,

    enzymeID INT UNSIGNED NOT NULL

);

ALTER TABLE msSearchEnzyme ADD PRIMARY KEY (searchID, enzymeID);

ALTER TABLE msSearchEnzyme ADD INDEX (enzymeID);





CREATE TABLE msPeptideSearchResult (

    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,

    searchID INT UNSIGNED NOT NULL,

    scanID INT UNSIGNED NOT NULL,

    charge TINYINT NOT NULL,

    peptide VARCHAR(500) NOT NULL,

    calculatedMass DECIMAL(10,5),

    matchingIons INT UNSIGNED,

    predictedIons INT UNSIGNED,

    preResidue CHAR(1),

    postResidue CHAR(1),

    validationStatus CHAR(1)

);



ALTER TABLE msPeptideSearchResult ADD INDEX(searchID);

ALTER TABLE msPeptideSearchResult ADD INDEX(scanID);

ALTER TABLE msPeptideSearchResult ADD INDEX(charge);

ALTER TABLE msPeptideSearchResult ADD INDEX(peptide);

# DO I WANT ALL THESE INDICES?



CREATE TABLE msProteinMatch (

    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,

    resultID INT UNSIGNED NOT NULL,

    accession VARCHAR(255) NOT NULL,

    description VARCHAR(2000)

);

ALTER TABLE msProteinMatch ADD INDEX (resultID);



CREATE TABLE SQTSpectrumData (

    scanID INT UNSIGNED NOT NULL,

    searchID INT UNSIGNED NOT NULL,

    charge TINYINT UNSIGNED,

    processTime INT UNSIGNED,

    serverName VARCHAR(50),

    totalIntensity DECIMAL(10,5),

    observedMass DECIMAL(10,5 ),

    lowestSp DECIMAL(10,5),

    sequenceMatches INT UNSIGNED

);

ALTER TABLE SQTSpectrumData ADD PRIMARY KEY(scanID, searchID, charge);

ALTER TABLE SQTSpectrumData ADD INDEX (searchID);

ALTER TABLE SQTSpectrumData ADD INDEX (charge);



CREATE TABLE SQTSearchHeader (

    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,

    searchID INT UNSIGNED NOT NULL,

    header VARCHAR(255) NOT NULL,

    value TEXT

);

ALTER TABLE SQTSearchHeader ADD INDEX(searchID, header);



CREATE TABLE msSequenceDatabaseDetail (

    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,

    serverAddress VARCHAR(100),

    serverPath VARCHAR(500),

    sequenceLength INT UNSIGNED,

    proteinCount INT UNSIGNED

);



CREATE TABLE msSearchDatabase (

    searchID INT UNSIGNED NOT NULL,

    databaseID INT UNSIGNED NOT NULL

);

ALTER TABLE msSearchDatabase ADD PRIMARY KEY(searchID, databaseID);

ALTER TABLE msSearchDatabase ADD INDEX(databaseID);



CREATE TABLE msPeptideSearchStaticMod (

    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,

    searchID INT UNSIGNED NOT NULL,

    residue CHAR(1) NOT NULL,

    modifier DECIMAL(10,5) NOT NULL

);

ALTER TABLE msPeptideSearchStaticMod ADD INDEX(searchID);



CREATE TABLE msPeptideSearchDynamicMod (

    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,

    searchID INT UNSIGNED NOT NULL,

    residue CHAR(1) NOT NULL,

    modifier DECIMAL(10,5) NOT NULL,

    symbol CHAR(1)

);

ALTER TABLE msPeptideSearchDynamicMod ADD INDEX(searchID);



CREATE TABLE msDynamicModResult (

    modID INT UNSIGNED NOT NULL,

    resultID INT UNSIGNED NOT NULL,

    position SMALLINT UNSIGNED NOT NULL

);

ALTER TABLE msDynamicModResult ADD PRIMARY KEY(modID, resultID, position);

ALTER TABLE msDynamicModResult ADD INDEX(resultID);



CREATE TABLE SQTSearchResult (

    resultID INT UNSIGNED NOT NULL PRIMARY KEY,

    XCorrRank INT UNSIGNED NOT NULL,

    spRank INT UNSIGNED NOT NULL,

    deltaCN DECIMAL(10,5) NOT NULL,

    XCorr DECIMAL(10,5) NOT NULL,

    sp DECIMAL(10,5) NOT NULL

);

ALTER TABLE SQTSearchResult ADD INDEX(XCorrRank);

ALTER TABLE SQTSearchResult ADD INDEX(spRank);

ALTER TABLE SQTSearchResult ADD INDEX(deltaCN);

ALTER TABLE SQTSearchResult ADD INDEX(XCorr);

ALTER TABLE SQTSearchResult ADD INDEX(sp);





# TRIGGERS TO ENSURE CASCADING DELETES



DELIMITER |

CREATE TRIGGER msPeptideSearchResult_bdelete BEFORE DELETE ON msPeptideSearchResult

  FOR EACH ROW

  BEGIN

    DELETE FROM msProteinMatch WHERE resultID = OLD.id;

    DELETE FROM SQTSearchResult WHERE resultID = OLD.id;

    DELETE FROM msDynamicModResult WHERE resultID = OLD.id;

  END;

|

DELIMITER ;



DELIMITER |

CREATE TRIGGER msPeptideSearchDynamicMod_bdelete BEFORE DELETE ON msPeptideSearchDynamicMod

  FOR EACH ROW

  BEGIN

    DELETE FROM msDynamicModResult WHERE modID = OLD.id;

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

CREATE TRIGGER msPeptideSearch_bdelete BEFORE DELETE ON msPeptideSearch

  FOR EACH ROW

  BEGIN

    DELETE FROM msSearchDatabase WHERE searchID = OLD.id;

    DELETE FROM msPeptideSearchStaticMod WHERE searchID = OLD.id;

    DELETE FROM msPeptideSearchDynamicMod WHERE searchID = OLD.id;

    DELETE FROM msPeptideSearchResult WHERE searchID = OLD.id;

    DELETE FROM msSearchEnzyme WHERE searchID = OLD.id;

    DELETE FROM SQTSpectrumData WHERE searchID = OLD.id;

    DELETE FROM SQTSearchHeader WHERE searchID = OLD.id;

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

    DELETE FROM msPeptideSearchResult WHERE scanID = OLD.id;

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

    DELETE FROM msPeptideSearch WHERE runID = OLD.id;

    DELETE FROM msRunEnzyme WHERE runID = OLD.id;

    DELETE FROM MS2FileHeader WHERE runID = OLD.id;

    DELETE FROM msExperimentRun WHERE runID = OLD.id;

  END;

|

DELIMITER ;



DELIMITER |

CREATE TRIGGER msExperiment_bdelete BEFORE DELETE ON msExperiment

  FOR EACH ROW

  BEGIN

    DELETE FROM msExperimentRun WHERE experimentID = OLD.id;

    DELETE FROM msPeptideSearch WHERE experimentID = OLD.id;

  END;

|

DELIMITER ;

