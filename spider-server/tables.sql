CREATE DATABASE spidercluster
  DEFAULT CHARSET ='utf8';
USE spidercluster;

CREATE TABLE tasks (
  id          INT NOT NULL AUTO_INCREMENT,
  name        VARCHAR(100),
  url         VARCHAR(1000),
  cookies     TEXT,
  headers     TEXT,
  parameters  VARCHAR(500),
  refer       VARCHAR(1000),
  type        VARCHAR(45),
  recursive   BOOL,
  parseRegex  VARCHAR(300),
  subTaskJson TEXT,
  PRIMARY KEY (id)
);

ALTER TABLE tasks ADD INDEX url(url(233));
ALTER TABLE tasks ADD INDEX name(name);
