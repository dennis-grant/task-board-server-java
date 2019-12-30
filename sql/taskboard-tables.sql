DROP TABLE IF EXISTS `task_board`.`TaskChange`;
CREATE TABLE  `task_board`.`TaskChange` (
  `changeId` int(10) NOT NULL AUTO_INCREMENT,
  `action` varchar(15) NOT NULL DEFAULT "",
  `taskId` int(10) NOT NULL,
  `issueNumber` varchar(15) NOT NULL DEFAULT "",
  `description` varchar(200) NOT NULL DEFAULT "",
  `assignedTo` varchar(50) NOT NULL DEFAULT "",
  `columnIndex` int(10) NOT NULL,
  `style` varchar(20) NOT NULL DEFAULT "",
  `tag` varchar(200) NOT NULL DEFAULT "",
  PRIMARY KEY (`changeId`),
  KEY `changeId` (`changeId`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;
