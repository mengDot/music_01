/*
SQLyog Ultimate v12.08 (64 bit)
MySQL - 5.7.19-log : Database - kwmusic
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`kwmusic` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `kwmusic`;

/*Table structure for table `tb_music` */

DROP TABLE IF EXISTS `tb_music`;

CREATE TABLE `tb_music` (
  `musicid` int(11) NOT NULL AUTO_INCREMENT,
  `musicrid` varchar(50) DEFAULT NULL,
  `artist` varchar(50) DEFAULT NULL,
  `pic` varchar(100) DEFAULT NULL,
  `isstar` int(11) DEFAULT NULL,
  `rid` int(11) DEFAULT NULL,
  `duration` int(11) DEFAULT NULL,
  `releaseDate` varchar(50) DEFAULT NULL,
  `album` varchar(50) DEFAULT NULL,
  `albumid` int(11) DEFAULT NULL,
  `pay` varchar(50) DEFAULT NULL,
  `artistid` int(11) DEFAULT NULL,
  `albumpic` varchar(150) DEFAULT NULL,
  `songTimeMinutes` varchar(50) DEFAULT NULL,
  `pic120` varchar(150) DEFAULT NULL,
  `musicname` varchar(50) DEFAULT NULL,
  `musictext` text,
  PRIMARY KEY (`musicid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `tb_music` */

/*Table structure for table `tb_singer` */

DROP TABLE IF EXISTS `tb_singer`;

CREATE TABLE `tb_singer` (
  `singerid` int(11) NOT NULL AUTO_INCREMENT,
  `artistFans` int(11) DEFAULT NULL,
  `albumNum` int(11) DEFAULT NULL,
  `mvNum` int(11) DEFAULT NULL,
  `pic` varchar(100) DEFAULT NULL,
  `musicNum` int(11) DEFAULT NULL,
  `pic120` varchar(100) DEFAULT NULL,
  `isStar` int(11) DEFAULT NULL,
  `content_type` int(11) DEFAULT NULL,
  `aartist` varchar(50) DEFAULT NULL,
  `singername` varchar(50) DEFAULT NULL,
  `pic70` varchar(100) DEFAULT NULL,
  `id` int(11) DEFAULT NULL,
  `pic300` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`singerid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `tb_singer` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;




insert into tb_singer(artistFans,albumNum,mvNum,musicNum,pic,pic70,pic120,pic300,aartist,singername,id) values(0,0,0,0,'','','','','','',0);

delete from tb_singer;
commit;