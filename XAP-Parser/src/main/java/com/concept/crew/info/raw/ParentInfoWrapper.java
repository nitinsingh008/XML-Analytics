package com.concept.crew.info.raw;

import com.concept.crew.info.jaxb.Instrument;

public class ParentInfoWrapper
{
	private Instrument data;
	private Long 		pkeyId;
	
	public Instrument getInstrument(){
		return data;
	}
	public void setInstrument(Instrument data) {
		this.data = data;
	}
	public Long getPkeyId() {
		return pkeyId;
	}
	public void setPkeyId(Long pkeyId) {
		this.pkeyId = pkeyId;
	}
}
