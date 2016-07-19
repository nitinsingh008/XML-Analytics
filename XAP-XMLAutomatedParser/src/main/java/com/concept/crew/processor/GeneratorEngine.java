package com.concept.crew.processor;

import com.concept.crew.generator.DelimiterDataExtractorGenerator;
import com.concept.crew.generator.EssentialsGenerator;
import com.concept.crew.generator.FileProcessorGenerator;
import com.concept.crew.generator.IGenerate;
import com.concept.crew.generator.LoadSaveProcessorGenerator;
import com.concept.crew.generator.LoaderTypeGenerator;
import com.concept.crew.generator.ParentWrapperGenerator;
import com.concept.crew.generator.ScheduleGenerator;
import com.concept.crew.generator.XMLParserGenerator;
import com.concept.crew.info.GenerateRequest;
import org.apache.log4j.Logger;

public enum GeneratorEngine 
{

	PARENT_WRAPPER(new ParentWrapperGenerator()),
	ESSENTIALS(new EssentialsGenerator()),
	SCHEDULES(new ScheduleGenerator()),
	LOADER_TYPE(new LoaderTypeGenerator()),
	PROCESSOR(new LoadSaveProcessorGenerator()),
	XML_PARSER(new XMLParserGenerator()),
	FILE_RUNNER(new FileProcessorGenerator()),
	DELIMITER_RUNNER(new DelimiterDataExtractorGenerator());

	private static Logger 		logger 			= Logger.getLogger(GeneratorEngine.class);
	
	private final IGenerate generator;
	
	GeneratorEngine(IGenerate generator){
		this.generator = generator;
	}
	
	public IGenerate getGenerator() {
		return generator;
	}

	public static void generateAll(GenerateRequest request)
	{
		for(GeneratorEngine gen : values())
		{
			gen.getGenerator().generate(request);
		}
	}
}
