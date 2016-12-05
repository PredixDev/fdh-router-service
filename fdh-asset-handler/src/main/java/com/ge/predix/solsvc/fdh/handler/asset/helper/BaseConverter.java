package com.ge.predix.solsvc.fdh.handler.asset.helper;

import java.util.List;

import org.mimosa.osacbmv3_3.DMVector;
import org.mimosa.osacbmv3_3.DataEvent;

/**
 * This is the base class for Osa DataType Converters.
 */
abstract public class BaseConverter {

	/**
	 * 
	 */
	protected List<Double> xAxisDeltas = null;
	/**
	 * 
	 */
	protected Double xAxisStart = null;

	/**
	 * This is the common method for copying basic DataEvent properties
	 * 
	 * @param src
	 *            source DataEvent
	 * @param dest
	 *            destination DataEvent
	 * @param toClazz
	 *            -
	 * @throws OsacbmConversionException
	 *             when conversion error
	 */
	@SuppressWarnings("nls")
	protected void copyBaseDataEventProperties(DataEvent src, DataEvent dest, Class<?> toClazz)
			throws OsacbmConversionException {
		if (src == null) {
			throw new OsacbmConversionException("src is null for base properties copy");
		}
		if (dest == null) {
			throw new OsacbmConversionException("dest is null for base properties copy");
		}
		dest.setAlertStatus(src.isAlertStatus());
		dest.setConfid(src.getConfid());
		dest.setId(src.getId());
		dest.setSequenceNum(src.getSequenceNum());
		dest.setSite(src.getSite());
		dest.setTime(src.getTime());

		if (Utils.isOsaSingleton(toClazz) && this.xAxisDeltas != null) {
			// adjust the time by the xAxisStart and first delta
			if (!toClazz.equals(DMVector.class)) {
				// DMVector use the original source's time - the x value should
				// have been set in the DMVector specific code
				Utils.addToTime(dest.getTime(), this.xAxisStart + sum(this.xAxisDeltas));
			}
		}

		// copy quality values
		List<Double> qualityValues = Utils.getQualityAsList(src);
		if (qualityValues != null) {
			if (Utils.isOsaSingleton(dest.getClass())) {
				Utils.setQuality(dest, qualityValues.get(qualityValues.size() - 1));
			} else {
				Utils.setQualityList(dest, qualityValues);
			}
		}
	}

	private Double sum(List<Double> values) {
		Double ret = 0.0;
		for (Double value : values) {
			ret += value;
		}
		return ret;
	}

	/**
	 * @param clazz
	 *            type of class
	 * @return dataEvent
	 * @throws OsacbmConversionException
	 *             conversion errors
	 */
	abstract public DataEvent convertTo(Class<?> clazz) throws OsacbmConversionException;

}
