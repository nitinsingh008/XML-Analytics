package com.concept.crew.util;

import java.io.Serializable;
import java.text.SimpleDateFormat;

public class Constants 
{
	
	public static final String 				STATUS_FAILED 			= "FAILED";
	public static final String 				STATUS_ERROR 			= "ERROR";
	public static final String 				VENDOR_DATA_ERROR 		= "VENDOR_DATA_ERROR";
	public static final String 				VENDOR_DATA_ERROR_REMARKS= "Duplicate ID_BB_GLOBAL exists for identifier";
	
	public static final String 				STATUS_SENT 			= "SENT"; 
	public static final String 				STATUS_COMPLETED 		= "COMPLETED";
	
	
	public static enum BondCopy implements Serializable 
	{
		RAW		(1, "CORE_REF_DATA.INSTRUMENT_SEQ"), //
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
