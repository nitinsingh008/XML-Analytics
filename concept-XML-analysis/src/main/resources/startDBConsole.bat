@echo off
ECHO *********************************************************************
ECHO ******* ******* ******* Starting XAP Database Console ******* ******* 
ECHO **** Useful Commands 		   
ECHO **** Connecting to database
ECHO **** connect 'jdbc:derby://localhost:1527/<Path>\<dbName>';
ECHO **** connect 'jdbc:derby://localhost:1527/C:\XAP\xapDB\dbName';
ECHO **** SHOW SCHEMAS;
ECHO **** SHOW TABLES;
ECHO **** DESCRIBE TABLES;
ECHO **** SELECT * FROM TABLE_NAME;
ECHO *********************************************************************

CALL db-derby-10.12.1.1\bin\ij