-- MySQL dump 10.13  Distrib 5.1.26-rc, for apple-darwin9.0.0b5 (i686)
--
-- Host: localhost    Database: nrseq_test
-- ------------------------------------------------------
-- Server version	5.1.26-rc-log

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
-- Table structure for table `tblDatabase`
--

DROP TABLE IF EXISTS `tblDatabase`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tblDatabase` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `tblDatabase`
--

LOCK TABLES `tblDatabase` WRITE;
/*!40000 ALTER TABLE `tblDatabase` DISABLE KEYS */;
INSERT INTO `tblDatabase` VALUES (1,'my/test/database',NULL),(2,'my/test/database2',NULL);
/*!40000 ALTER TABLE `tblDatabase` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblProtein`
--

DROP TABLE IF EXISTS `tblProtein`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tblProtein` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `proteinID` int(10) unsigned NOT NULL,
  `sequenceID` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `tblProtein`
--

LOCK TABLES `tblProtein` WRITE;
/*!40000 ALTER TABLE `tblProtein` DISABLE KEYS */;
/*!40000 ALTER TABLE `tblProtein` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblProteinDatabase`
--

DROP TABLE IF EXISTS `tblProteinDatabase`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tblProteinDatabase` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `proteinID` int(10) unsigned NOT NULL,
  `databaseID` int(10) unsigned NOT NULL,
  `accessionString` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `URL` varchar(255) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `isCurrent` enum('T','F') DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `tblProteinDatabase`
--

LOCK TABLES `tblProteinDatabase` WRITE;
/*!40000 ALTER TABLE `tblProteinDatabase` DISABLE KEYS */;
INSERT INTO `tblProteinDatabase` VALUES (1,25,1,'accession_string_1',NULL,NULL,'2008-08-28 20:49:34','T'),(2,28,2,'accession_string_4',NULL,NULL,'2008-08-28 20:49:47','T'),(3,26,1,'accession_string_2',NULL,NULL,'2008-08-28 22:10:16','T'),(4,27,1,'accession_string_3',NULL,NULL,'2008-08-28 22:10:25','T'),(5,29,2,'accession_string_5',NULL,NULL,'2008-08-28 22:10:37','T'),(6,30,2,'accession_string_6',NULL,NULL,'2008-08-28 22:10:44','T');
/*!40000 ALTER TABLE `tblProteinDatabase` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblProteinSequence`
--

DROP TABLE IF EXISTS `tblProteinSequence`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tblProteinSequence` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `sequence` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `tblProteinSequence`
--

LOCK TABLES `tblProteinSequence` WRITE;
/*!40000 ALTER TABLE `tblProteinSequence` DISABLE KEYS */;
/*!40000 ALTER TABLE `tblProteinSequence` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2008-08-29  4:53:24
