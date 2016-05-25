package com.concept.crew.generator;

import com.concept.crew.info.GenerateRequest;

public enum GeneratorEngine {

	/**
	 * Add enum for new classes to be generated 
	 */
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
