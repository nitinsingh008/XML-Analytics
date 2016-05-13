package com.concept.crew.util;

import java.io.Serializable;
import java.text.SimpleDateFormat;

public class Constants 
{
	public static final SimpleDateFormat	fileDataFormat 			= new SimpleDateFormat("MMddyyyy");
	public static final SimpleDateFormat	outputfileDataFormat 	= new SimpleDateFormat("MMddyyyy-kkmmss"); //MMDDYYYY-HHMMSS

	public static final String 				FTP_HOST 				= "ftp.host";
	public static final String 				FTP_USERNAME 			= "ftp.username";
	public static final String 				FTP_PASSWORD 			= "ftp.password";
	
	public static final String 				SFTP_HOST 				= "bbg.host";
	public static final String 				SFTP_USERNAME 			= "bbg.username";
	public static final String 				SFTP_PASSWORD 			= "bbg.password";
	public static final String 				SFTP_PORT 				= "bbg.port";
	public static final String 				SFTP_DOWNLOAD 			= "sftp.download";
	public static final String 				SFTP_UPLOAD 			= "sftp.upload";
	public static final String 				SFTP_FOLDER_OUT  		= "sftp.folder.out";
	public static final String 				SFTP_FOLDER_ROOT  		= "sftp.folder.root";
	
	
	public static final String 				STATUS_FILE_IN 		    = "IN";
	public static final String 				STATUS_FILE_OUT 		= "OUT";
	public static final String 				STATUS_RECEIVED 		= "RECEIVED";
	public static final String 				STATUS_PENDING 			= "PENDING";
	public static final String 				STATUS_WAITING 			= "WAITING"; 
	public static final String 				STATUS_FOUND_LOCAL 		= "FOUND_LOCAL";
	public static final String 				STATUS_FOUND_REMOTE 	= "FOUND_REMOTE"; 
	public static final String 				STATUS_NOT_FOUND 		= "NOT_FOUND"; 	
	public static final String 				STATUS_FAILED 			= "FAILED";
	public static final String 				STATUS_ERROR 			= "ERROR";
	public static final String 				VENDOR_DATA_ERROR 		= "VENDOR_DATA_ERROR";
	public static final String 				VENDOR_DATA_ERROR_REMARKS= "Duplicate ID_BB_GLOBAL exists for identifier";
	
	public static final String 				STATUS_SENT 			= "SENT"; 
	public static final String 				STATUS_COMPLETED 		= "COMPLETED";
	
	public static final String 				FIXED_INCOME 			= "FIXED_INCOME";
	public static final String 				EQUITY 					= "EQUITY";
	public static final String 				FOLDER_SEP	    		= "/";
	public static final String 				FOLDER_LOCAL_DIR_ROOT 	= "/var/cache/refdata";
	public static final String 				FOLDER_BBG_INTERNAL 	= "/BBG/Internal";
	public static final String 				FOLDER_BBG_EXTERNAL 	= "/BBG/External";
	public static final String 				FOLDER_IN 				= "/IN";
	public static final String 				FOLDER_OUT 				= "/OUT";
	public static final String 				FOLDER_ARCHIVE 			= "/ARCHIVE";
	public static final String 				BBG 					= "BBG";
	public static final int 			    IN_LIMIT 			    = 1000;
	public static final String 			    ISIN 			        = "ISIN";
	public static final String 			    CUSIP 			        = "CUSIP";
	public static final String 			    COMMON_CODE 			= "COMMON_CODE";
	public static final String 			    WKN			 			= "WKN";
	public static final String 			    SEDOL			 		= "SEDOL";	
	public static final String 			    CIN			 			= "CIN";
	public static final String 			    LOCAL_CODE			 	= "LOCAL_CODE";
	
	public static final String 			    IDENTIFIER_ID			= "IDENTIFIER_ID";
	public static final String 			    ID_BB_GLOBAL			= "ID_BB_GLOBAL";
	public static final String 			    TICKER					= "TICKER";
	public static final String 			    EXCH_CODE				= "EXCH_CODE";
	public static final String 			    NAME					= "NAME";
	public static final String 			    MARKET_SECTOR_DES		= "MARKET_SECTOR_DES";
	public static final String 			    SECURITY_TYPE_FILE		= "SECURITY_TYPE_FILE";
	public static final String 			    FEED_SOURCE				= "FEED_SOURCE";
	public static final String 			    ID_BB_SEC_NUM_DES		= "ID_BB_SEC_NUM_DES";
	public static final String 			    COMPOSITE_ID_BB_GLOBAL	= "COMPOSITE_ID_BB_GLOBAL";
	public static final String 			    ID_BB_GLOBAL_SHARE_CLASS_LEVEL		= "ID_BB_GLOBAL_SHARE_CLASS_LEVEL";
	public static final String 			    ID_BB_UNIQUE			= "ID_BB_UNIQUE";
	

	
	public static enum RequestType  
	{
		LIVE, 
		TEST, 
		DAILY, 
		BACKFILL;
	}
	
	public static enum BondCopy implements Serializable 
	{
		RAW		(1, "CORE_FEED.SE_RAW_MARKITBOND_SEQ"), //
		STAGING (2, "CORE_FEED.SE_STG_MARKITBOND_SEQ"), //
		GOLDEN  (3, "CORE_FEED.SE_GLD_MARKITBOND_SEQ");

		private final Integer precedence;
		private final String instrumentIdSequence;
		private final String markitInstrumentIdSequence;

		private BondCopy(Integer precedence) 
		{
			this(precedence, null, null);
		}

		private BondCopy(Integer precedence, 
						 String instrumentIdSequence) 
		{
			this(precedence, instrumentIdSequence, null);
		}

		private BondCopy(Integer precedence, String instrumentIdSequence, String markitInstrumentIdSequence) 
		{
			this.precedence = precedence;
			this.instrumentIdSequence = instrumentIdSequence;
			this.markitInstrumentIdSequence = markitInstrumentIdSequence;
		}

		public Integer getPrecedence() 
		{
			return precedence;
		}

		public String getInstrumentIdSequence() 
		{
			return instrumentIdSequence;
		}

		public String getMarkitInstrumentIdSequence() 
		{
			return markitInstrumentIdSequence;
		}
	};
	
	public enum QueryIDType 
	{
		BOND_ID, PKEY, MARKIT_PKEY;
	}
}
