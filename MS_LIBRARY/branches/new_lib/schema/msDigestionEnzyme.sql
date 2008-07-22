-- MySQL dump 10.11
--
-- Host: localhost    Database: msData
-- ------------------------------------------------------
-- Server version	5.0.51a

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
-- Table structure for table `msDigestionEnzyme`
--

DROP TABLE IF EXISTS `msDigestionEnzyme`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `msDigestionEnzyme` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(255) NOT NULL,
  `sense` tinyint(4) default NULL,
  `cut` varchar(20) default NULL,
  `nocut` varchar(20) default NULL,
  `description` text,
  PRIMARY KEY  (`id`),
  KEY `name` (`name`)
) ENGINE=MyISAM AUTO_INCREMENT=73 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `msDigestionEnzyme`
--

LOCK TABLES `msDigestionEnzyme` WRITE;
/*!40000 ALTER TABLE `msDigestionEnzyme` DISABLE KEYS */;
INSERT INTO `msDigestionEnzyme` VALUES (1,'Trypsin',1,'KR','P',NULL),(2,'Arg-C',1,'R','P',NULL),(3,'Arg-N',0,'BD','',NULL),(4,'Asp-N_ambic',0,'DE','',NULL),(5,'Chmymotrypsin',1,'FYWL','P',NULL),(6,'CNBr',1,'M','',NULL),(7,'Formic_acid',1,'D','',NULL),(8,'Lys-C',1,'K','P',NULL),(9,'Lys-C/P',1,'K','',NULL),(10,'PepsinA',1,'FL','',NULL),(11,'Tryp-CNBr',1,'KRM','P',NULL),(12,'TrypChymo',1,'FYWLKR','P',NULL),(13,'Trypsin/P',1,'KR','',NULL);
/*!40000 ALTER TABLE `msDigestionEnzyme` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2008-07-07 16:36:00
