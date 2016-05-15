package com.concept.crew.info.raw;

import com.concept.crew.info.jaxb.Instrument;

public class InstrumentRaw
{
	private Instrument instrument;
	private Long 		pkeyId;
	
	public Instrument getInstrument() 
	{
		return instrument;
	}
	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}
	public Long getPkeyId() {
		return pkeyId;
	}
	public void setPkeyId(Long pkeyId) {
		this.pkeyId = pkeyId;
	}
	


	
}
