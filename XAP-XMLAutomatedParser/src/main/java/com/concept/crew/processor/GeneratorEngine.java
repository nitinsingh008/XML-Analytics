package com.concept.crew.processor;

import com.concept.crew.generator.EssentialsGenerator;
import com.concept.crew.generator.IGenerate;
import com.concept.crew.generator.LoadSaveProcessorGenerator;
import com.concept.crew.generator.LoaderTypeGenerator;
import com.concept.crew.generator.ParentWrapperGenerator;
import com.concept.crew.generator.ScheduleGenerator;
import com.concept.crew.info.GenerateRequest;

public enum GeneratorEngine {

	PARENT_WRAPPER(new ParentWrapperGenerator()),
	ESSENTIALS(new EssentialsGenerator()),
	SCHEDULES(new ScheduleGenerator()),
	LOADER_TYPE(new LoaderTypeGenerator()),
	PROCESSOR(new LoadSaveProcessorGenerator());
	
	private final IGenerate generator;
	
	GeneratorEngine(IGenerate generator){
		this.generator = generator;
	}
	
	public IGenerate getGenerator() {
		return generator;
	}

	public static void generateAll(GenerateRequest request){
		for(GeneratorEngine gen : values()){
			gen.getGenerator().generate(request);
		}
	}
}
