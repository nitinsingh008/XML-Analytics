package com.concept.crew.info.raw;

import com.concept.crew.info.jaxb.Instrument;

public class InstrumentRaw
{
	private Instrument markitBond;
	private Long bondId;
	private String markitPkey;

	public Long getBondId() 
	{
		return bondId;
	}

	public void setBondId(Long bondId) 
	{
		this.bondId = bondId;
	}

	public String getMarkitPkey() {
		return markitPkey;
	}

	public void setMarkitPkey(String markitPkey) {
		this.markitPkey = markitPkey;
	}

	public Instrument getMarkitBond() 
	{
		return markitBond;
	}

	public void setMarkitBond(Instrument markitBond) 
	{
		this.markitBond = markitBond;
	}


	
}
