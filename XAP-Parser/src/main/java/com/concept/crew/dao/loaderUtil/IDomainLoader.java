package com.concept.crew.dao.loaderUtil;

import java.util.List;

import com.concept.crew.dao.loaderUtil.raw.IDataDomainLoader;

public interface IDomainLoader 
{
	public List<Class<? extends IDataDomainLoader>> getDomainReaders();

	public List<Class<? extends IDataDomainLoader>> getDomainWriters();

	public String getOrderBy();
}
