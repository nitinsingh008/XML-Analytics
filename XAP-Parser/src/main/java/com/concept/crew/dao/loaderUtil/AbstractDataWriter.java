package com.concept.crew.dao.loaderUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.concept.crew.dao.loaderUtil.raw.DataLoader;
import com.concept.crew.dao.loaderUtil.raw.DataLoader.BondWriter;
import com.concept.crew.info.raw.InstrumentRaw;
import com.concept.crew.util.Constants.BondCopy;

public abstract class AbstractDataWriter 
{

	/**
	 * DataWriterFactory
	 * 
	 * @param businessModel
	 * @param bondCopies
	 * @return
	 */
	public static Collection<AbstractDataWriter> create(Set<BondCopy> bondCopies)
	{
		List<AbstractDataWriter> instrumentWriters = new ArrayList<AbstractDataWriter>();

		for (BondCopy bondCopy : bondCopies) 
		{
			instrumentWriters.add(create(bondCopy));
		}

		Collections.sort(instrumentWriters, new WriterSequence());

		return instrumentWriters;
	}

	/**
	 * InstrumentWriter factory
	 * 
	 * @param businessModel
	 * @param bondCopy
	 * @return
	 */
	private static AbstractDataWriter create( BondCopy bondCopy)
	{
		return new MRDInstrumentWriter( bondCopy);
	}

	public abstract BondCopy getBondCopy();

	public abstract Collection<InstrumentRaw> write(Collection<InstrumentRaw> bonds);

/*	*//**
	 * MRD Writer
	 * 
	 *//*
	private static final class MRDVendorInstrumentWriter extends InstrumentWriter {

		private final MRDInstrumentWriter stagingInstrumentWriter;
		private final MRDInstrumentWriter goldenInstrumentWriter;

		public MRDVendorInstrumentWriter() 
		{
			this.stagingInstrumentWriter = new MRDInstrumentWriter(jobDetail, BondCopy.STAGING);
			this.goldenInstrumentWriter = new MRDInstrumentWriter(jobDetail, BondCopy.GOLDEN);
		}

		@Override
		public BondCopy getBondCopy() {
			return BondCopy.VENDOR;
		}

		@Override
		public Collection<Instrument> write(Collection<Instrument> bonds) {
			Collection<Instrument> written = new ArrayList<Instrument>();
			// create a new copy of incoming bonds
			// set new instrument id and version
			// using "CORE_REF_DATA.STG_INST_MASTER_SEQ"
			Collection<Instrument> stagingBonds = bonds;
			stagingInstrumentWriter.write(stagingBonds);
			written.addAll(stagingBonds);

			// create a new copy of incoming bonds
			// set new instrument id and version
			// using "CORE_REF_DATA.INST_MASTER_SEQ"
			Collection<Instrument> goldenBonds = bonds;
			goldenInstrumentWriter.write(goldenBonds);
			written.addAll(goldenBonds);

			return written;
		}

		@Override
		public String toString() {
			return "MRDVendorInstrumentWriter";
		}
	}*/

	/**
	 * MRD Writer
	 * 
	 */
	private static final class MRDInstrumentWriter extends AbstractDataWriter {

		private final BondCopy bondCopy;

		public MRDInstrumentWriter(BondCopy bondCopy) {
			this.bondCopy = bondCopy;
		}

		@Override
		public BondCopy getBondCopy() {
			return bondCopy;
		}

		@Override
		public Collection<InstrumentRaw> write(Collection<InstrumentRaw> bonds) {
			BondWriter writer = DataLoader.createWriter(bondCopy);
			writer.writeBonds(bonds);
			return bonds;
		}

		@Override
		public String toString() {
			return "MRD" + bondCopy.name() + "InstrumentWriter";
		}
	}

	/**
	 * Helper comparator
	 * 
	 */
	private static final class WriterSequence implements Comparator<AbstractDataWriter>
	{

		@Override
		public int compare(AbstractDataWriter writer1, AbstractDataWriter writer2) {
			Integer writer1Rank = writer1.getBondCopy().getPrecedence();
			Integer writer2Rank = writer2.getBondCopy().getPrecedence();
			return writer1Rank.compareTo(writer2Rank);
		}
	}
}
