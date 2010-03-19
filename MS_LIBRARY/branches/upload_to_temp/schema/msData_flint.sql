-- MySQL dump 10.13  Distrib 5.1.37, for unknown-linux-gnu (x86_64)
--
-- Host: localhost    Database: msData
-- ------------------------------------------------------
-- Server version	5.1.37-community-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `IDPickerGroupAssociation`
--

DROP TABLE IF EXISTS `IDPickerGroupAssociation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `IDPickerGroupAssociation` (
  `piRunID` int(10) unsigned NOT NULL,
  `proteinGroupID` int(10) unsigned NOT NULL,
  `peptideGroupID` int(10) unsigned NOT NULL,
  PRIMARY KEY (`piRunID`,`proteinGroupID`,`peptideGroupID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `IDPickerInputSummary`
--

DROP TABLE IF EXISTS `IDPickerInputSummary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `IDPickerInputSummary` (
  `piInputID` int(10) unsigned NOT NULL,
  `numTargetHits` int(10) unsigned DEFAULT NULL,
  `numDecoyHits` int(10) unsigned DEFAULT NULL,
  `numFilteredTargetHits` int(10) unsigned DEFAULT NULL,
  `numFilteredDecoyHits` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`piInputID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `IDPickerParam`
--

DROP TABLE IF EXISTS `IDPickerParam`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `IDPickerParam` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `piRunID` int(10) unsigned NOT NULL,
  `name` varchar(255) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `piRunID` (`piRunID`)
) ENGINE=MyISAM AUTO_INCREMENT=17 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `IDPickerPeptide`
--

DROP TABLE IF EXISTS `IDPickerPeptide`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `IDPickerPeptide` (
  `piPeptideID` int(10) unsigned NOT NULL,
  `groupID` int(10) unsigned NOT NULL,
  PRIMARY KEY (`piPeptideID`),
  KEY `groupID` (`groupID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `IDPickerProtein`
--

DROP TABLE IF EXISTS `IDPickerProtein`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `IDPickerProtein` (
  `piProteinID` int(10) unsigned NOT NULL,
  `clusterID` int(10) unsigned NOT NULL,
  `groupID` int(10) unsigned NOT NULL,
  `nsaf` double unsigned DEFAULT NULL,
  `isParsimonious` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`piProteinID`),
  KEY `clusterID` (`clusterID`),
  KEY `groupID` (`groupID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `IDPickerSpectrumMatch`
--

DROP TABLE IF EXISTS `IDPickerSpectrumMatch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `IDPickerSpectrumMatch` (
  `piSpectrumMatchID` int(10) unsigned NOT NULL,
  `fdr` double unsigned DEFAULT NULL,
  PRIMARY KEY (`piSpectrumMatchID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `MS2FileChargeDependentAnalysis`
--

DROP TABLE IF EXISTS `MS2FileChargeDependentAnalysis`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MS2FileChargeDependentAnalysis` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `scanChargeID` int(10) unsigned NOT NULL,
  `header` varchar(255) DEFAULT NULL,
  `value` text,
  PRIMARY KEY (`id`),
  KEY `scanChargeID` (`scanChargeID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `MS2FileChargeIndependentAnalysis`
--

DROP TABLE IF EXISTS `MS2FileChargeIndependentAnalysis`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MS2FileChargeIndependentAnalysis` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `scanID` int(10) unsigned NOT NULL,
  `header` varchar(255) DEFAULT NULL,
  `value` text,
  PRIMARY KEY (`id`),
  KEY `scanID` (`scanID`)
) ENGINE=MyISAM AUTO_INCREMENT=1279251 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `MS2FileHeader`
--

DROP TABLE IF EXISTS `MS2FileHeader`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MS2FileHeader` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `runID` int(11) NOT NULL,
  `header` varchar(255) NOT NULL,
  `value` text,
  PRIMARY KEY (`id`),
  KEY `runID` (`runID`,`header`)
) ENGINE=MyISAM AUTO_INCREMENT=21 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `MS2FileScanCharge`
--

DROP TABLE IF EXISTS `MS2FileScanCharge`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MS2FileScanCharge` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `scanID` int(10) unsigned NOT NULL,
  `charge` tinyint(3) unsigned NOT NULL,
  `mass` decimal(18,9) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `scanID` (`scanID`)
) ENGINE=MyISAM AUTO_INCREMENT=364990 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`vsharma`@`localhost`*/ /*!50003 TRIGGER MS2FileScanCharge_bdelete BEFORE DELETE ON MS2FileScanCharge
 FOR EACH ROW
 BEGIN
   DELETE FROM MS2FileChargeDependentAnalysis WHERE scanChargeID = OLD.id;
 END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `MascotParams`
--

DROP TABLE IF EXISTS `MascotParams`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MascotParams` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `searchID` int(10) unsigned NOT NULL,
  `param` varchar(255) NOT NULL,
  `value` text,
  PRIMARY KEY (`id`),
  KEY `searchID` (`searchID`,`param`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `MascotSearchResult`
--

DROP TABLE IF EXISTS `MascotSearchResult`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MascotSearchResult` (
  `resultID` int(10) unsigned NOT NULL,
  `rank` int(10) unsigned NOT NULL,
  `ionScore` decimal(10,5) NOT NULL,
  `identityScore` decimal(10,5) NOT NULL,
  `homologyScore` decimal(10,5) NOT NULL,
  `expect` decimal(10,5) NOT NULL,
  `star` int(10) unsigned NOT NULL,
  `calculatedMass` decimal(18,9) DEFAULT NULL,
  `matchingIons` int(10) unsigned DEFAULT NULL,
  `predictedIons` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`resultID`),
  KEY `ionScore` (`ionScore`),
  KEY `identityScore` (`identityScore`),
  KEY `homologyScore` (`homologyScore`),
  KEY `expect` (`expect`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `PeptideProphetAnalysis`
--

DROP TABLE IF EXISTS `PeptideProphetAnalysis`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PeptideProphetAnalysis` (
  `searchAnalysisID` int(10) unsigned NOT NULL,
  `filename` varchar(255) NOT NULL,
  PRIMARY KEY (`searchAnalysisID`),
  KEY `searchAnalysisID` (`searchAnalysisID`,`filename`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `PeptideProphetROC`
--

DROP TABLE IF EXISTS `PeptideProphetROC`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PeptideProphetROC` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `searchAnalysisID` int(10) unsigned NOT NULL,
  `sensitivity` double unsigned NOT NULL,
  `error` double unsigned NOT NULL,
  `minProbability` double unsigned NOT NULL,
  `numCorrect` int(10) unsigned NOT NULL,
  `numIncorrect` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `searchAnalysisID` (`searchAnalysisID`)
) ENGINE=MyISAM AUTO_INCREMENT=903 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `PeptideProphetResult`
--

DROP TABLE IF EXISTS `PeptideProphetResult`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PeptideProphetResult` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `resultID` int(10) unsigned NOT NULL,
  `runSearchAnalysisID` int(10) unsigned NOT NULL,
  `probability` double unsigned NOT NULL,
  `fVal` double NOT NULL,
  `numEnzymaticTermini` int(10) unsigned NOT NULL,
  `numMissedCleavages` int(10) unsigned NOT NULL,
  `massDifference` double NOT NULL,
  `probabilityNet_0` double unsigned DEFAULT NULL,
  `probabilityNet_1` double unsigned DEFAULT NULL,
  `probabilityNet_2` double unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `runSearchAnalysisID` (`runSearchAnalysisID`),
  KEY `resultID` (`resultID`),
  KEY `probability` (`probability`)
) ENGINE=MyISAM AUTO_INCREMENT=336093 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `PercolatorParams`
--

DROP TABLE IF EXISTS `PercolatorParams`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PercolatorParams` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `searchAnalysisID` int(10) unsigned NOT NULL,
  `param` varchar(255) NOT NULL,
  `value` text,
  PRIMARY KEY (`id`),
  KEY `searchAnalysisID` (`searchAnalysisID`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `PercolatorResult`
--

DROP TABLE IF EXISTS `PercolatorResult`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PercolatorResult` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `resultID` int(10) unsigned NOT NULL,
  `runSearchAnalysisID` int(10) unsigned NOT NULL,
  `qvalue` double unsigned NOT NULL,
  `pep` double unsigned DEFAULT NULL,
  `discriminantScore` double DEFAULT NULL,
  `predictedRetentionTime` decimal(10,5) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `runSearchAnalysisID` (`runSearchAnalysisID`),
  KEY `resultID` (`resultID`),
  KEY `qvalue` (`qvalue`),
  KEY `pep` (`pep`),
  KEY `discriminantScore` (`discriminantScore`)
) ENGINE=MyISAM AUTO_INCREMENT=364976 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ProLuCIDParams`
--

DROP TABLE IF EXISTS `ProLuCIDParams`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ProLuCIDParams` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `searchID` int(10) unsigned NOT NULL,
  `elementName` varchar(255) NOT NULL,
  `value` text,
  `parentID` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `searchID` (`searchID`,`elementName`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ProLuCIDSearchResult`
--

DROP TABLE IF EXISTS `ProLuCIDSearchResult`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ProLuCIDSearchResult` (
  `resultID` int(10) unsigned NOT NULL,
  `primaryScoreRank` int(10) unsigned NOT NULL,
  `secondaryScoreRank` int(10) unsigned NOT NULL,
  `deltaCN` decimal(10,5) NOT NULL,
  `primaryScore` double unsigned NOT NULL,
  `secondaryScore` double unsigned NOT NULL,
  `calculatedMass` decimal(18,9) DEFAULT NULL,
  `matchingIons` int(10) unsigned DEFAULT NULL,
  `predictedIons` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`resultID`),
  KEY `primaryScoreRank` (`primaryScoreRank`),
  KEY `secondaryScoreRank` (`secondaryScoreRank`),
  KEY `deltaCN` (`deltaCN`),
  KEY `primaryScore` (`primaryScore`),
  KEY `secondaryScore` (`secondaryScore`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ProteinProphetParam`
--

DROP TABLE IF EXISTS `ProteinProphetParam`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ProteinProphetParam` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `piRunID` int(10) unsigned NOT NULL,
  `name` varchar(255) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `piRunID` (`piRunID`)
) ENGINE=MyISAM AUTO_INCREMENT=289 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ProteinProphetProtein`
--

DROP TABLE IF EXISTS `ProteinProphetProtein`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ProteinProphetProtein` (
  `piProteinID` int(10) unsigned NOT NULL,
  `proteinProphetGroupID` int(10) unsigned NOT NULL,
  `groupID` int(10) unsigned NOT NULL,
  `probability` double unsigned NOT NULL,
  `confidence` double unsigned DEFAULT NULL,
  `subsumed` tinyint(4) NOT NULL DEFAULT '0',
  `totalSpectrumCount` int(10) unsigned NOT NULL,
  `pctSpectrumCount` double unsigned DEFAULT NULL,
  PRIMARY KEY (`piProteinID`),
  KEY `groupID` (`groupID`),
  KEY `proteinProphetGroupID` (`proteinProphetGroupID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ProteinProphetProteinGroup`
--

DROP TABLE IF EXISTS `ProteinProphetProteinGroup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ProteinProphetProteinGroup` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `piRunID` int(10) unsigned NOT NULL,
  `groupNumber` int(10) unsigned NOT NULL,
  `probability` double unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `piRunID` (`piRunID`,`probability`)
) ENGINE=MyISAM AUTO_INCREMENT=45387 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ProteinProphetProteinIon`
--

DROP TABLE IF EXISTS `ProteinProphetProteinIon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ProteinProphetProteinIon` (
  `piProteinID` int(10) unsigned NOT NULL,
  `piIonID` int(10) unsigned NOT NULL,
  `weight` double unsigned NOT NULL,
  `initialProbability` double unsigned NOT NULL,
  `nspAdjProbability` double unsigned NOT NULL,
  `numSiblingPeptides` double unsigned NOT NULL,
  `isContributingEvidence` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`piProteinID`,`piIonID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ProteinProphetROC`
--

DROP TABLE IF EXISTS `ProteinProphetROC`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ProteinProphetROC` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `piRunID` int(10) unsigned NOT NULL,
  `sensitivity` double unsigned NOT NULL,
  `falsePositiveErrorRate` double unsigned NOT NULL,
  `minProbability` double unsigned NOT NULL,
  `numCorrect` int(10) unsigned NOT NULL,
  `numIncorrect` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `piRunID` (`piRunID`)
) ENGINE=MyISAM AUTO_INCREMENT=577 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ProteinProphetRun`
--

DROP TABLE IF EXISTS `ProteinProphetRun`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ProteinProphetRun` (
  `piRunID` int(10) unsigned NOT NULL,
  `filename` varchar(255) NOT NULL,
  PRIMARY KEY (`piRunID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ProteinProphetSubsumedProtein`
--

DROP TABLE IF EXISTS `ProteinProphetSubsumedProtein`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ProteinProphetSubsumedProtein` (
  `subsumedProteinID` int(10) unsigned NOT NULL,
  `subsumingProteinID` int(10) unsigned NOT NULL,
  KEY `subsumedProteinID` (`subsumedProteinID`),
  KEY `subsumingProteinID` (`subsumingProteinID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SQTFileHeader`
--

DROP TABLE IF EXISTS `SQTFileHeader`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SQTFileHeader` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `runSearchID` int(10) unsigned NOT NULL,
  `header` varchar(255) NOT NULL,
  `value` text,
  PRIMARY KEY (`id`),
  KEY `runSearchID` (`runSearchID`,`header`)
) ENGINE=MyISAM AUTO_INCREMENT=89 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SQTParams`
--

DROP TABLE IF EXISTS `SQTParams`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SQTParams` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `searchID` int(10) unsigned NOT NULL,
  `param` varchar(255) NOT NULL,
  `value` text,
  PRIMARY KEY (`id`),
  KEY `searchID` (`searchID`,`param`)
) ENGINE=MyISAM AUTO_INCREMENT=901 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SQTSearchResult`
--

DROP TABLE IF EXISTS `SQTSearchResult`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SQTSearchResult` (
  `resultID` int(10) unsigned NOT NULL,
  `XCorrRank` int(10) unsigned NOT NULL,
  `spRank` int(10) unsigned NOT NULL,
  `deltaCN` decimal(10,5) NOT NULL,
  `deltaCNstar` decimal(10,5) DEFAULT NULL,
  `XCorr` decimal(10,5) NOT NULL,
  `sp` decimal(10,5) DEFAULT NULL,
  `calculatedMass` decimal(18,9) DEFAULT NULL,
  `matchingIons` int(10) unsigned DEFAULT NULL,
  `predictedIons` int(10) unsigned DEFAULT NULL,
  `evalue` double unsigned DEFAULT NULL,
  PRIMARY KEY (`resultID`),
  KEY `XCorrRank` (`XCorrRank`),
  KEY `spRank` (`spRank`),
  KEY `deltaCN` (`deltaCN`),
  KEY `XCorr` (`XCorr`),
  KEY `sp` (`sp`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SQTSpectrumData`
--

DROP TABLE IF EXISTS `SQTSpectrumData`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SQTSpectrumData` (
  `scanID` int(10) unsigned NOT NULL,
  `runSearchID` int(10) unsigned NOT NULL,
  `charge` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `processTime` int(10) unsigned DEFAULT NULL,
  `serverName` varchar(50) DEFAULT NULL,
  `totalIntensity` decimal(18,9) DEFAULT NULL,
  `lowestSp` decimal(10,5) DEFAULT NULL,
  `sequenceMatches` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`scanID`,`runSearchID`,`charge`),
  KEY `runSearchID` (`runSearchID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `XtandemParams`
--

DROP TABLE IF EXISTS `XtandemParams`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `XtandemParams` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `searchID` int(10) unsigned NOT NULL,
  `param` varchar(255) NOT NULL,
  `value` text,
  PRIMARY KEY (`id`),
  KEY `searchID` (`searchID`,`param`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `XtandemSearchResult`
--

DROP TABLE IF EXISTS `XtandemSearchResult`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `XtandemSearchResult` (
  `resultID` int(10) unsigned NOT NULL,
  `rank` int(10) unsigned NOT NULL,
  `hyperscore` decimal(10,5) NOT NULL,
  `nextscore` decimal(10,5) NOT NULL,
  `bscore` decimal(10,5) NOT NULL,
  `yscore` decimal(10,5) NOT NULL,
  `expect` decimal(10,5) NOT NULL,
  `calculatedMass` decimal(18,9) DEFAULT NULL,
  `matchingIons` int(10) unsigned DEFAULT NULL,
  `predictedIons` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`resultID`),
  KEY `hyperscore` (`hyperscore`),
  KEY `nextscore` (`nextscore`),
  KEY `bscore` (`bscore`),
  KEY `yscore` (`yscore`),
  KEY `expect` (`expect`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `msDigestionEnzyme`
--

DROP TABLE IF EXISTS `msDigestionEnzyme`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msDigestionEnzyme` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `sense` tinyint(4) DEFAULT NULL,
  `cut` varchar(20) DEFAULT NULL,
  `nocut` varchar(20) DEFAULT NULL,
  `description` text,
  PRIMARY KEY (`id`),
  KEY `name` (`name`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`vsharma`@`localhost`*/ /*!50003 TRIGGER msDigestionEnzyme_bdelete BEFORE DELETE ON msDigestionEnzyme
 FOR EACH ROW
 BEGIN
   DELETE FROM msSearchEnzyme WHERE enzymeID = OLD.id;
   DELETE FROM msRunEnzyme WHERE enzymeID = OLD.id;
 END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `msDynamicModResult`
--

DROP TABLE IF EXISTS `msDynamicModResult`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msDynamicModResult` (
  `modID` int(10) unsigned NOT NULL,
  `resultID` int(10) unsigned NOT NULL,
  `position` smallint(5) unsigned NOT NULL,
  PRIMARY KEY (`modID`,`resultID`,`position`),
  KEY `resultID` (`resultID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `msExperiment`
--

DROP TABLE IF EXISTS `msExperiment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msExperiment` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `serverAddress` varchar(500) DEFAULT NULL,
  `serverDirectory` varchar(500) DEFAULT NULL,
  `uploadDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastUpdate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `comments` text,
  `instrumentID` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=16 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`vsharma`@`localhost`*/ /*!50003 TRIGGER msExperiment_bdelete BEFORE DELETE ON msExperiment
 FOR EACH ROW
 BEGIN
   DELETE FROM msSearch WHERE experimentID = OLD.id;
   DELETE FROM msExperimentRun WHERE experimentID = OLD.id;
 END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `msExperimentRun`
--

DROP TABLE IF EXISTS `msExperimentRun`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msExperimentRun` (
  `experimentID` int(10) unsigned NOT NULL,
  `runID` int(10) unsigned NOT NULL,
  PRIMARY KEY (`runID`,`experimentID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `msInstrument`
--

DROP TABLE IF EXISTS `msInstrument`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msInstrument` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `msProteinInferInput`
--

DROP TABLE IF EXISTS `msProteinInferInput`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msProteinInferInput` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `piRunID` int(10) unsigned NOT NULL,
  `inputID` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `piRunID` (`piRunID`),
  KEY `inputID` (`inputID`)
) ENGINE=MyISAM AUTO_INCREMENT=82 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`vsharma`@`localhost`*/ /*!50003 TRIGGER msProteinInferInput_bdelete BEFORE DELETE ON msProteinInferInput
 FOR EACH ROW
 BEGIN
 	DELETE FROM IDPickerInputSummary WHERE piInputID = OLD.id;
 END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `msProteinInferIon`
--

DROP TABLE IF EXISTS `msProteinInferIon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msProteinInferIon` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `piPeptideID` int(10) unsigned NOT NULL,
  `charge` int(10) unsigned NOT NULL,
  `modifiedSequence` varchar(255) NOT NULL,
  `modificationStateID` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `piPeptideID` (`piPeptideID`),
  KEY `charge` (`charge`),
  KEY `modificationStateID` (`modificationStateID`)
) ENGINE=MyISAM AUTO_INCREMENT=143662 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `msProteinInferPeptide`
--

DROP TABLE IF EXISTS `msProteinInferPeptide`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msProteinInferPeptide` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `piRunID` int(10) unsigned NOT NULL,
  `sequence` varchar(255) NOT NULL,
  `uniqueToProtein` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `piRunID` (`piRunID`)
) ENGINE=MyISAM AUTO_INCREMENT=128925 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`vsharma`@`localhost`*/ /*!50003 TRIGGER msProteinInferPeptide_bdelete BEFORE DELETE ON msProteinInferPeptide
 FOR EACH ROW
 BEGIN
   DELETE FROM msProteinInferIon WHERE piPeptideID = OLD.id;
   DELETE FROM IDPickerPeptide WHERE piPeptideID = OLD.id;
   DELETE FROM msProteinInferProteinPeptideMatch WHERE piPeptideID = OLD.id;
 END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `msProteinInferProtein`
--

DROP TABLE IF EXISTS `msProteinInferProtein`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msProteinInferProtein` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `nrseqProteinID` int(10) unsigned NOT NULL,
  `piRunID` int(10) unsigned NOT NULL,
  `coverage` double unsigned DEFAULT NULL,
  `userValidation` char(1) DEFAULT NULL,
  `userAnnotation` tinytext,
  PRIMARY KEY (`id`),
  KEY `piRunID` (`piRunID`),
  KEY `nrseqProteinID` (`nrseqProteinID`),
  KEY `nrseqProteinID_2` (`nrseqProteinID`,`piRunID`)
) ENGINE=MyISAM AUTO_INCREMENT=53016 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`vsharma`@`localhost`*/ /*!50003 TRIGGER msProteinInferProtein_bdelete BEFORE DELETE ON msProteinInferProtein
 FOR EACH ROW
 BEGIN
 	DELETE FROM IDPickerProtein WHERE piProteinID = OLD.id;
 	DELETE FROM ProteinProphetProtein WHERE piProteinID = OLD.id;
   	DELETE FROM msProteinInferProteinPeptideMatch WHERE piProteinID = OLD.id;
   	DELETE FROM ProteinProphetSubsumedProtein WHERE subsumedProteinID = OLD.id;
   	DELETE FROM ProteinProphetSubsumedProtein WHERE subsumingProteinID = OLD.id;
   	DELETE FROM ProteinProphetProteinIon WHERE piProteinID = OLD.id;
 END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `msProteinInferProteinPeptideMatch`
--

DROP TABLE IF EXISTS `msProteinInferProteinPeptideMatch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msProteinInferProteinPeptideMatch` (
  `piProteinID` int(10) unsigned NOT NULL,
  `piPeptideID` int(10) unsigned NOT NULL,
  PRIMARY KEY (`piProteinID`,`piPeptideID`),
  KEY `piPeptideID` (`piPeptideID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `msProteinInferRun`
--

DROP TABLE IF EXISTS `msProteinInferRun`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msProteinInferRun` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `program` varchar(255) NOT NULL,
  `programVersion` varchar(255) NOT NULL,
  `inputGenerator` varchar(255) NOT NULL,
  `dateRun` datetime DEFAULT NULL,
  `comments` tinytext,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=39 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`vsharma`@`localhost`*/ /*!50003 TRIGGER msProteinInferRun_bdelete BEFORE DELETE ON msProteinInferRun
 FOR EACH ROW
 BEGIN
  	DELETE FROM IDPickerParam WHERE piRunID = OLD.id;
  	DELETE FROM IDPickerGroupAssociation WHERE piRunID = OLD.id;
   	DELETE FROM msProteinInferInput WHERE piRunID = OLD.id;
  	DELETE FROM msProteinInferProtein WHERE piRunID = OLD.id;
  	DELETE FROM msProteinInferPeptide WHERE piRunID = OLD.id;
  	DELETE FROM ProteinProphetRun WHERE piRunID = OLD.id;
  	DELETE FROM ProteinProphetParam WHERE piRunID = OLD.id;
  	DELETE FROM ProteinProphetROC WHERE piRunID = OLD.id;
  	DELETE FROM ProteinProphetProteinGroup WHERE piRunID = OLD.id;
 END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `msProteinInferSpectrumMatch`
--

DROP TABLE IF EXISTS `msProteinInferSpectrumMatch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msProteinInferSpectrumMatch` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `piIonID` int(10) unsigned NOT NULL,
  `resultID` int(10) unsigned NOT NULL,
  `rankForPeptide` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `resultID` (`resultID`),
  KEY `piIonID` (`piIonID`)
) ENGINE=MyISAM AUTO_INCREMENT=352451 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`vsharma`@`localhost`*/ /*!50003 TRIGGER msProteinInferSpectrumMatch_bdelete BEFORE DELETE ON msProteinInferSpectrumMatch
 FOR EACH ROW
 BEGIN
   DELETE FROM IDPickerSpectrumMatch WHERE piSpectrumMatchID = OLD.id;
 END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `msProteinMatch`
--

DROP TABLE IF EXISTS `msProteinMatch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msProteinMatch` (
  `resultID` int(10) unsigned NOT NULL,
  `accession` varchar(255) NOT NULL,
  PRIMARY KEY (`resultID`,`accession`),
  KEY `accession` (`accession`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1
/*!50100 PARTITION BY KEY (resultID)
PARTITIONS 100 */;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `msRun`
--

DROP TABLE IF EXISTS `msRun`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msRun` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `filename` varchar(255) DEFAULT NULL,
  `sha1Sum` char(40) DEFAULT NULL,
  `creationTime` varchar(255) DEFAULT NULL,
  `extractor` varchar(255) DEFAULT NULL,
  `extractorVersion` varchar(255) DEFAULT NULL,
  `extractorOptions` varchar(255) DEFAULT NULL,
  `instrumentVendor` varchar(255) DEFAULT NULL,
  `instrumentType` varchar(255) DEFAULT NULL,
  `instrumentSN` varchar(255) DEFAULT NULL,
  `acquisitionMethod` varchar(255) DEFAULT NULL,
  `originalFileType` varchar(10) DEFAULT NULL,
  `separateDigestion` enum('T','F') DEFAULT NULL,
  `uploadDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `comment` text,
  PRIMARY KEY (`id`),
  KEY `filename` (`filename`)
) ENGINE=MyISAM AUTO_INCREMENT=53 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`vsharma`@`localhost`*/ /*!50003 TRIGGER msRun_bdelete BEFORE DELETE ON msRun
 FOR EACH ROW
 BEGIN
   DELETE FROM msScan WHERE runID = OLD.id;
   DELETE FROM msRunSearch WHERE runID = OLD.id;
   DELETE FROM msRunEnzyme WHERE runID = OLD.id;
   DELETE FROM MS2FileHeader WHERE runID = OLD.id;
   DELETE FROM msRunLocation WHERE runID = OLD.id;
   DELETE FROM msExperimentRun WHERE runID = OLD.id;
 END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `msRunEnzyme`
--

DROP TABLE IF EXISTS `msRunEnzyme`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msRunEnzyme` (
  `runID` int(10) unsigned NOT NULL,
  `enzymeID` int(10) unsigned NOT NULL,
  PRIMARY KEY (`runID`,`enzymeID`),
  KEY `enzymeID` (`enzymeID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `msRunLocation`
--

DROP TABLE IF EXISTS `msRunLocation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msRunLocation` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `runID` int(10) unsigned NOT NULL,
  `serverDirectory` varchar(500) DEFAULT NULL,
  `createDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `runID` (`runID`)
) ENGINE=MyISAM AUTO_INCREMENT=53 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `msRunSearch`
--

DROP TABLE IF EXISTS `msRunSearch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msRunSearch` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `runID` int(10) unsigned NOT NULL,
  `searchID` int(10) unsigned NOT NULL,
  `originalFileType` varchar(50) NOT NULL,
  `searchDate` date DEFAULT NULL,
  `searchDuration` int(10) unsigned DEFAULT NULL,
  `uploadDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `runID` (`runID`),
  KEY `searchID` (`searchID`)
) ENGINE=MyISAM AUTO_INCREMENT=62 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`vsharma`@`localhost`*/ /*!50003 TRIGGER msRunSearch_bdelete BEFORE DELETE ON msRunSearch
 FOR EACH ROW
 BEGIN
   DELETE FROM msRunSearchResult WHERE runSearchID = OLD.id;
   DELETE FROM SQTSpectrumData WHERE runSearchID = OLD.id;
   DELETE FROM SQTFileHeader WHERE runSearchID = OLD.id;
   DELETE FROM msRunSearchAnalysis WHERE runSearchID = OLD.id;
 END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `msRunSearchAnalysis`
--

DROP TABLE IF EXISTS `msRunSearchAnalysis`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msRunSearchAnalysis` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `searchAnalysisID` int(10) unsigned NOT NULL,
  `runSearchID` int(10) unsigned NOT NULL,
  `originalFileType` varchar(10) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `searchAnalysisID` (`searchAnalysisID`),
  KEY `runSearchID` (`runSearchID`)
) ENGINE=MyISAM AUTO_INCREMENT=85 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`vsharma`@`localhost`*/ /*!50003 TRIGGER msRunSearchAnalysis_bdelete BEFORE DELETE ON msRunSearchAnalysis
 FOR EACH ROW
 BEGIN
 	DELETE FROM PercolatorResult WHERE runSearchAnalysisID = OLD.id;
 	DELETE FROM PeptideProphetResult WHERE runSearchAnalysisID = OLD.id;
 END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `msRunSearchResult`
--

DROP TABLE IF EXISTS `msRunSearchResult`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msRunSearchResult` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `runSearchID` int(10) unsigned NOT NULL,
  `scanID` int(10) unsigned NOT NULL,
  `charge` tinyint(4) NOT NULL,
  `observedMass` decimal(18,9) DEFAULT NULL,
  `peptide` varchar(500) NOT NULL,
  `preResidue` char(1) DEFAULT NULL,
  `postResidue` char(1) DEFAULT NULL,
  `validationStatus` char(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `runSearchID` (`runSearchID`),
  KEY `scanID` (`scanID`),
  KEY `peptide` (`peptide`(10)),
  KEY `runSearchID_2` (`runSearchID`,`scanID`)
) ENGINE=MyISAM AUTO_INCREMENT=1521966 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`vsharma`@`localhost`*/ /*!50003 TRIGGER msRunSearchResult_bdelete BEFORE DELETE ON msRunSearchResult
 FOR EACH ROW
 BEGIN
   DELETE FROM msProteinMatch WHERE resultID = OLD.id;
   DELETE FROM SQTSearchResult WHERE resultID = OLD.id;
   DELETE FROM MascotSearchResult WHERE resultID = OLD.id;
   DELETE FROM XtandemSearchResult WHERE resultID = OLD.id;
   DELETE FROM ProLuCIDSearchResult WHERE resultID = OLD.id;
   DELETE FROM msDynamicModResult WHERE resultID = OLD.id;
   DELETE FROM msTerminalDynamicModResult WHERE resultID = OLD.id;
   DELETE FROM PercolatorResult WHERE resultID = OLD.id;
   DELETE FROM PeptideProphetResult WHERE resultID = OLD.id;
 END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `msScan`
--

DROP TABLE IF EXISTS `msScan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msScan` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `runID` int(10) unsigned NOT NULL,
  `startScanNumber` int(10) unsigned DEFAULT NULL,
  `endScanNumber` int(10) unsigned DEFAULT NULL,
  `level` tinyint(3) unsigned DEFAULT NULL,
  `preMZ` decimal(18,9) DEFAULT NULL,
  `preScanID` int(10) unsigned DEFAULT NULL,
  `prescanNumber` int(10) unsigned DEFAULT NULL,
  `retentionTime` decimal(10,5) DEFAULT NULL,
  `fragmentationType` char(3) DEFAULT NULL,
  `isCentroid` enum('T','F') DEFAULT NULL,
  `peakCount` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `runID` (`runID`),
  KEY `startScanNumber` (`startScanNumber`),
  KEY `runID_2` (`runID`,`startScanNumber`)
) ENGINE=MyISAM AUTO_INCREMENT=1021465 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`vsharma`@`localhost`*/ /*!50003 TRIGGER msScan_bdelete BEFORE DELETE ON msScan
 FOR EACH ROW
 BEGIN
   DELETE FROM MS2FileScanCharge WHERE scanID = OLD.id;
   DELETE FROM msRunSearchResult WHERE scanID = OLD.id;
   DELETE FROM SQTSpectrumData WHERE scanID = OLD.id;
   DELETE FROM MS2FileChargeIndependentAnalysis WHERE scanID = OLD.id;
   DELETE FROM msScanData WHERE scanID = OLD.id;
 END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `msScanData`
--

DROP TABLE IF EXISTS `msScanData`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msScanData` (
  `scanID` int(10) unsigned NOT NULL,
  `type` char(1) NOT NULL,
  `data` longblob NOT NULL,
  PRIMARY KEY (`scanID`),
  KEY `scanID` (`scanID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1
/*!50100 PARTITION BY KEY ()
PARTITIONS 100 */;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `msSearch`
--

DROP TABLE IF EXISTS `msSearch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msSearch` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `experimentID` int(10) unsigned NOT NULL,
  `expDate` date DEFAULT NULL,
  `serverDirectory` varchar(500) DEFAULT NULL,
  `analysisProgramName` varchar(255) DEFAULT NULL,
  `analysisProgramVersion` varchar(20) DEFAULT NULL,
  `uploadDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `expDate` (`expDate`),
  KEY `experimentID` (`experimentID`)
) ENGINE=MyISAM AUTO_INCREMENT=16 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`vsharma`@`localhost`*/ /*!50003 TRIGGER msSearch_bdelete BEFORE DELETE ON msSearch
 FOR EACH ROW
 BEGIN
   DELETE FROM msSearchDatabase WHERE searchID = OLD.id;
   DELETE FROM SQTParams WHERE searchID = OLD.id;
   DELETE FROM MascotParams WHERE searchID = OLD.id;
   DELETE FROM XtandemParams WHERE searchID = OLD.id;
   DELETE FROM ProLuCIDParams WHERE searchID = OLD.id;
   DELETE FROM msSearchEnzyme WHERE searchID = OLD.id;
   DELETE FROM msRunSearch WHERE searchID = OLD.id;
   DELETE FROM msSearchStaticMod WHERE searchID = OLD.id;
   DELETE FROM msSearchTerminalStaticMod WHERE searchID = OLD.id;
   DELETE FROM msSearchDynamicMod WHERE searchID = OLD.id;
   DELETE FROM msSearchTerminalDynamicMod WHERE searchID = OLD.id;
 END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `msSearchAnalysis`
--

DROP TABLE IF EXISTS `msSearchAnalysis`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msSearchAnalysis` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `programName` varchar(255) NOT NULL,
  `programVersion` varchar(255) DEFAULT NULL,
  `uploadDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=43 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`vsharma`@`localhost`*/ /*!50003 TRIGGER msSearchAnalysis_bdelete BEFORE DELETE ON msSearchAnalysis
 FOR EACH ROW
 BEGIN
   	DELETE FROM PercolatorParams WHERE searchAnalysisID = OLD.id;
   	DELETE FROM PeptideProphetAnalysis WHERE searchAnalysisID = OLD.id;
   	DELETE FROM PeptideProphetROC WHERE searchAnalysisID = OLD.id;
	DELETE FROM msRunSearchAnalysis WHERE searchAnalysisID = OLD.id;
 END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `msSearchDatabase`
--

DROP TABLE IF EXISTS `msSearchDatabase`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msSearchDatabase` (
  `searchID` int(10) unsigned NOT NULL,
  `databaseID` int(10) unsigned NOT NULL,
  PRIMARY KEY (`searchID`,`databaseID`),
  KEY `databaseID` (`databaseID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `msSearchDynamicMod`
--

DROP TABLE IF EXISTS `msSearchDynamicMod`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msSearchDynamicMod` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `searchID` int(10) unsigned NOT NULL,
  `residue` char(1) NOT NULL,
  `modifier` decimal(18,9) NOT NULL,
  `symbol` char(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `searchID` (`searchID`)
) ENGINE=MyISAM AUTO_INCREMENT=24 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`vsharma`@`localhost`*/ /*!50003 TRIGGER msSearchDynamicMod_bdelete BEFORE DELETE ON msSearchDynamicMod
 FOR EACH ROW
 BEGIN
   DELETE FROM msDynamicModResult WHERE modID = OLD.id;
 END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `msSearchEnzyme`
--

DROP TABLE IF EXISTS `msSearchEnzyme`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msSearchEnzyme` (
  `searchID` int(10) unsigned NOT NULL,
  `enzymeID` int(10) unsigned NOT NULL,
  PRIMARY KEY (`searchID`,`enzymeID`),
  KEY `enzymeID` (`enzymeID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `msSearchStaticMod`
--

DROP TABLE IF EXISTS `msSearchStaticMod`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msSearchStaticMod` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `searchID` int(10) unsigned NOT NULL,
  `residue` char(1) NOT NULL,
  `modifier` decimal(18,9) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `searchID` (`searchID`)
) ENGINE=MyISAM AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `msSearchTerminalDynamicMod`
--

DROP TABLE IF EXISTS `msSearchTerminalDynamicMod`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msSearchTerminalDynamicMod` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `searchID` int(10) unsigned NOT NULL,
  `terminus` enum('N','C') NOT NULL,
  `modifier` decimal(18,9) NOT NULL,
  `symbol` char(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `searchID` (`searchID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`vsharma`@`localhost`*/ /*!50003 TRIGGER msSearchTerminalDynamicMod_bdelete BEFORE DELETE ON msSearchTerminalDynamicMod
 FOR EACH ROW
 BEGIN
   DELETE FROM msTerminalDynamicModResult WHERE modID = OLD.id;
 END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `msSearchTerminalStaticMod`
--

DROP TABLE IF EXISTS `msSearchTerminalStaticMod`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msSearchTerminalStaticMod` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `searchID` int(10) unsigned NOT NULL,
  `terminus` enum('N','C') NOT NULL,
  `modifier` decimal(18,9) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `searchID` (`searchID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `msSequenceDatabaseDetail`
--

DROP TABLE IF EXISTS `msSequenceDatabaseDetail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msSequenceDatabaseDetail` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `serverAddress` varchar(100) DEFAULT NULL,
  `serverPath` varchar(500) DEFAULT NULL,
  `sequenceDatabaseID` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`vsharma`@`localhost`*/ /*!50003 TRIGGER msSequenceDatabaseDetail_bdelete BEFORE DELETE ON msSequenceDatabaseDetail
 FOR EACH ROW
 BEGIN
   DELETE FROM msSearchDatabase WHERE databaseID = OLD.id;
 END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `msTerminalDynamicModResult`
--

DROP TABLE IF EXISTS `msTerminalDynamicModResult`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msTerminalDynamicModResult` (
  `modID` int(10) unsigned NOT NULL,
  `resultID` int(10) unsigned NOT NULL,
  PRIMARY KEY (`modID`,`resultID`),
  KEY `resultID` (`resultID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `nrseqProteinCache`
--

DROP TABLE IF EXISTS `nrseqProteinCache`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `nrseqProteinCache` (
  `proteinID` int(10) unsigned NOT NULL,
  `name` varchar(1000) NOT NULL,
  `description` varchar(1000) NOT NULL,
  KEY `name` (`name`(10)),
  KEY `proteinID` (`proteinID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2010-03-19 12:11:30
