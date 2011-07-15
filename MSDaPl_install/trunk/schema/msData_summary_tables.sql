# ##############################################################################################
# Percolator result stats
# ##############################################################################################
CREATE TABLE PeptideTerminiStats (
	analysisID INT UNSIGNED NOT NULL PRIMARY KEY,
	scoreCutoff DOUBLE UNSIGNED NOT NULL,
	scoreType VARCHAR(20) NOT NULL,
	totalResultCount INT UNSIGNED NOT NULL,
	numResultsWithEnzTerm_0 INT UNSIGNED NOT NULL,
	numResultsWithEnzTerm_1 INT UNSIGNED NOT NULL,
	numResultsWithEnzTerm_2 INT UNSIGNED NOT NULL,
	ntermMinusOneAminoAcidCount VARCHAR(255),
	ntermAminoAcidCount VARCHAR(255),
	ctermAminoAcidCount VARCHAR(255),
	ctermPlusOneAminoAcidCount VARCHAR(255),
	enzymeID INT UNSIGNED,
	enzyme VARCHAR(20)
);

CREATE TABLE PercolatorFilteredPsmResult (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	runSearchAnalysisID INT UNSIGNED NOT NULL,
	qvalue DOUBLE UNSIGNED NOT NULL,
	total INT UNSIGNED NOT NULL,
	filtered INT UNSIGNED NOT NULL
);
ALTER TABLE PercolatorFilteredPsmResult ADD INDEX (runSearchAnalysisID);
ALTER TABLE PercolatorFilteredPsmResult ADD UNIQUE INDEX (runSearchAnalysisID, qvalue);

CREATE TABLE PercolatorFilteredBinnedPsmResult (
	percPsmResultID INT UNSIGNED NOT NULL,
	binStart INT UNSIGNED NOT NULL,
	binEnd INT UNSIGNED NOT NULL,
	total INT UNSIGNED NOT NULL,
	filtered INT UNSIGNED NOT NULL
);
ALTER TABLE PercolatorFilteredBinnedPsmResult ADD INDEX (percPsmResultID);


CREATE TABLE PercolatorFilteredSpectraResult (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	runSearchAnalysisID INT UNSIGNED NOT NULL,
	qvalue DOUBLE UNSIGNED NOT NULL,
	total INT UNSIGNED NOT NULL,
	filtered INT UNSIGNED NOT NULL
);
ALTER TABLE PercolatorFilteredSpectraResult ADD INDEX (runSearchAnalysisID);
ALTER TABLE PercolatorFilteredSpectraResult ADD UNIQUE INDEX (runSearchAnalysisID, qvalue);

CREATE TABLE PercolatorFilteredBinnedSpectraResult (
	percScanResultID INT UNSIGNED NOT NULL,
	binStart INT UNSIGNED NOT NULL,
	binEnd INT UNSIGNED NOT NULL,
	total INT UNSIGNED NOT NULL,
	filtered INT UNSIGNED NOT NULL
);
ALTER TABLE PercolatorFilteredBinnedSpectraResult ADD INDEX (percScanResultID);

# ##############################################################################################
# Protein Inference results stats
# ##############################################################################################
CREATE TABLE proteinInferRunSummary (
  piRunID INT unsigned NOT NULL PRIMARY KEY,
  groupCount INT unsigned NOT NULL,
  proteinCount INT unsigned NOT NULL,
  peptSeqCount INT unsigned NOT NULL,
  ionCount INT unsigned NOT NULL,
  spectrumCount INT unsigned NOT NULL,
  minSpectrumCount INT unsigned NOT NULL,
  maxSpectrumCount INT unsigned NOT NULL
 );
 
CREATE TABLE proteinProphetRunSummary (
  piRunID int(10) unsigned NOT NULL PRIMARY KEY,
  prophetGroupCount int(10) unsigned NOT NULL,
  groupCount int(10) unsigned NOT NULL,
  proteinCount int(10) unsigned NOT NULL,
  peptSeqCount int(10) unsigned NOT NULL,
  ionCount int(10) unsigned NOT NULL,
  spectrumCount int(10) unsigned NOT NULL,
  minSpectrumCount INT unsigned NOT NULL,
  maxSpectrumCount INT unsigned NOT NULL
); 
 
# --------------------------------------------------------------------------------------------
# ADD TRIGGERS
# --------------------------------------------------------------------------------------------
DELIMITER |
CREATE TRIGGER PercolatorFilteredPsmResult_bdelete BEFORE DELETE ON PercolatorFilteredPsmResult
 FOR EACH ROW
 BEGIN
   DELETE FROM PercolatorFilteredBinnedPsmResult WHERE percPsmResultID = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER PercolatorFilteredSpectraResult_bdelete BEFORE DELETE ON PercolatorFilteredSpectraResult
 FOR EACH ROW
 BEGIN
   DELETE FROM PercolatorFilteredBinnedSpectraResult WHERE percScanResultID = OLD.id;
 END;
|
DELIMITER ;
 




