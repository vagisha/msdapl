DROP TABLE IF EXISTS msPostSearchAnalysis;
CREATE TABLE msPostSearchAnalysis (
		id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
		searchID INT UNSIGNED NOT NULL,
		serverDirectory VARCHAR(500),
	   	analysisProgramName VARCHAR(255) NOT NULL,
	   	analysisProgramVersion VARCHAR(20),
	   	uploadDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE msPostSearchAnalysis ADD INDEX(searchID);



DROP TABLE IF EXISTS PercolatorOutput;
CREATE TABLE PercolatorOutput (
		id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
		percID INT UNSIGNED NOT NULL,
		runSearchID INT UNSIGNED NOT NULL,
		originalFileType VARCHAR(10) NOT NULL
);
ALTER TABLE PercolatorOutput ADD INDEX(percID);
ALTER TABLE PercolatorOutput ADD INDEX(runSearchID);



DROP TABLE IF EXISTS PercolatorSQTHeader;
CREATE TABLE PercolatorSQTHeader (
		id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
		percOutputID INT UNSIGNED NOT NULL,
		header VARCHAR(255) NOT NULL,
   		value TEXT
);
ALTER TABLE PercolatorSQTHeader ADD INDEX(percOutputID, header);



DROP TABLE IF EXISTS PercolatorResult;
CREATE TABLE PercolatorResult (
		resultID INT UNSIGNED NOT NULL PRIMARY KEY,
		percOutputID INT UNSIGNED NOT NULL,
		qvalue DOUBLE UNSIGNED NOT NULL,
		pep DOUBLE UNSIGNED,
		discriminantScore DOUBLE UNSIGNED
);
ALTER TABLE ADD INDEX(percOutputID);


##################################################################################################
# TRIGGERS TO ENSURE CASCADING DELETES
##################################################################################################

DELIMITER |
CREATE TRIGGER PercolatorOutput_bdelete BEFORE DELETE ON PercolatorOutput
 FOR EACH ROW
 BEGIN
   DELETE FROM PercolatorSQTHeader WHERE percOutputID = OLD.id;
   DELETE FROM PercolatorResult WHERE percOutputID = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER msPostSearchAnalysist_bdelete BEFORE DELETE ON msPostSearchAnalysis
 FOR EACH ROW
 BEGIN
   DELETE FROM PercolatorOutput WHERE percID = OLD.id;
 END;
|
DELIMITER ;


