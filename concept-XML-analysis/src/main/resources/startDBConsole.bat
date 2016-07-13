@echo off
ECHO *********************************************************************
ECHO ******* ******* ******* Starting XAP Database Console ******* ******* 
ECHO **** Useful Commands shown below		   
ECHO **** Connecting to database
ECHO **** connect "'jdbc:derby://localhost:1527/<Path>\<dbName>';"
ECHO **** connect 'jdbc:derby://localhost:1527/C:\XAP\xapDB\dbname' user 'user' password 'pass';
ECHO **** Example
ECHO **** connect 'jdbc:derby://localhost:1527/C:\XAP\xapDB\dbName';
ECHO **** SHOW SCHEMAS; 			(Showing List of Schemas)
ECHO **** SHOW TABLES;  			(Showing List of Tables)
ECHO **** DESCRIBE TABLES;			(Describe about Table)
ECHO **** SELECT * FROM TABLE_NAME;
ECHO **** SHOW INDEXES				(Display all indexes in database)
ECHO **** EXIT;						(Exiting console)
ECHO *********************************************************************

CALL db-derby-10.12.1.1\bin\ij