/*
 * Copyright (C) 2012 GE Software Center of Excellence.
 * All rights reserved
 */
package com.ge.predix.solsvc.fdh.handler.asset.helper;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * compare two objects and report if their properties are of equal values. Note,
 * this comparison covers objects composed of the following Java objects:
 * Integer, Long, Float, Double, Boolean, String, StringBuffer, Map, List. It
 * uses the BeanInfo class to introspect the objects so it will only pick up
 * properties that have the standard getter/setter methods.
 * 
 * @author 200002567
 */
@SuppressWarnings({ "rawtypes", "nls" })
public class ObjectCompare {
    private static final Logger log = LoggerFactory.getLogger(ObjectCompare.class);

	/**
	 * Utility classes should not have a public or default constructor.
	 */
	private ObjectCompare() {

		// logger = new Log4jLogger(this.getClass());
	}

	/**
	 * the entry point to the comparison
	 * 
	 * @param obj1 -
	 * @param obj2 -
	 * @return -
	 * @throws IntrospectionException -
	 * @throws IllegalArgumentException -
	 * @throws IllegalAccessException -
	 * @throws InvocationTargetException -
	 */
	public static boolean areObjectsCongruentIgnoreCase(Object obj1, Object obj2)
			throws IntrospectionException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {

		Map<Object, List<Object>> fieldsAnalyzedMap = new HashMap<Object, List<Object>>();

		CompareResult result = recursiveCompare(obj1, obj2, fieldsAnalyzedMap,
				true, false);

		if (!result.isMatch()) {
			for (String explanation : result.getResultExplanations()) {
				log.debug(explanation);
			}
		}

		return result.isMatch();

	}

	/**
	 * the entry point to the comparison
	 * 
	 * @param obj1 -
	 * @param obj2 -
	 * @param ignoreCase -
	 * @param ingoreCrlf -
	 * @return -
	 * @throws IntrospectionException -
	 * @throws IllegalArgumentException -
	 * @throws IllegalAccessException -
	 * @throws InvocationTargetException -
	 */
	public static boolean areObjectsCongruent(Object obj1, Object obj2,
			boolean ignoreCase, boolean ingoreCrlf)
			throws IntrospectionException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {

		Map<Object, List<Object>> fieldsAnalyzedMap = new HashMap<Object, List<Object>>();

		CompareResult result = recursiveCompare(obj1, obj2, fieldsAnalyzedMap,
				ignoreCase, ingoreCrlf);

		if (!result.isMatch()) {
			for (String explanation : result.getResultExplanations()) {
				log.debug(explanation);
			}
		}

		return result.isMatch();

	}

	/**
	 * the entry point to the comparison
	 * 
	 * @param obj1 -
	 * @param obj2 -
	 * @return -
	 * @throws IntrospectionException -
	 * @throws IllegalArgumentException -
	 * @throws IllegalAccessException -
	 * @throws InvocationTargetException -
	 */
	public static boolean areObjectsCongruent(Object obj1, Object obj2)
			throws IntrospectionException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {

		Map<Object, List<Object>> fieldsAnalyzedMap = new HashMap<Object, List<Object>>();

		CompareResult result = recursiveCompare(obj1, obj2, fieldsAnalyzedMap,
				false, false);

		if (!result.isMatch()) {
			for (String explanation : result.getResultExplanations()) {
				log.debug(explanation);
			}
		}

		return result.isMatch();

	}

	/**
	 * this method recursively walks through the properties in the objects being
	 * compared. If their values match then true is returned otherwise false is
	 * returned. As false is returned, messages indicating where the comparison
	 * failed are generated.
	 * 
	 * @param obj1
	 * @param obj2
	 * @param fieldsAnalyzedMap
	 * @return
	 * @throws IntrospectionException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private static CompareResult recursiveCompare(Object obj1, Object obj2,
			Map<Object, List<Object>> fieldsAnalyzedMap, boolean ignoreCase,
			boolean ignoreCrlf) throws IntrospectionException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {

		if (obj1 == null && obj2 == null) {
			return pushResult(true, "match - both objs are null", null);
			// return true;
		}

		if (obj1 == null) {
			return pushResult(false, "Obj1 is null but not obj2: obj2.class="
					+ obj2.getClass().getName(), null);
			// if (logDifferences) {
			// log.debug("Obj1 is null but not obj2: obj2.class="+obj2.getClass().getName());
			// }
			// return false;
		}

		if (obj2 == null) {
			return pushResult(false, "Obj2 is null but not obj1: obj1.class="
					+ obj1.getClass().getName(), null);
			// if (logDifferences) {
			// log.debug("Obj2 is null but not obj1: obj1.class="+obj1.getClass().getName());
			// }
			// return false;
		}

		if (!obj1.getClass().equals(obj2.getClass())) {
			return pushResult(false,
					"Objects are not the same class: obj1.class="
							+ obj1.getClass().getName() + ", obj2.class="
							+ obj2.getClass().getName(), null);
			// if (logDifferences) {
			// log.debug("Objects are not the same class: obj1.class="+obj1.getClass().getName()+", obj2.class="+obj2.getClass().getName());
			// }
			// return false;
		}

		if (fieldsAnalyzedMap.get(obj1) != null) {
			fieldsAnalyzedMap.get(obj1).add(obj2);
		} else {
			List<Object> matchList = new ArrayList<Object>();
			matchList.add(obj2);
			fieldsAnalyzedMap.put(obj1, matchList);
		}
		if (fieldsAnalyzedMap.get(obj2) != null) {
			fieldsAnalyzedMap.get(obj2).add(obj1);
		} else {
			List<Object> matchList = new ArrayList<Object>();
			matchList.add(obj1);
			fieldsAnalyzedMap.put(obj2, matchList);
		}

		if (obj1 instanceof Collection) {
			if (((Collection) obj1).size() != ((Collection) obj2).size()) {
				// Error:Collection objects have different sizes
				return pushResult(false,
						"Objects are different sizes: obj1.size="
								+ ((Collection) obj1).size() + ", obj2.size="
								+ ((Collection) obj2).size(), null);
				// if (logDifferences) {
				// log.debug("Objects are different sizes: obj1.size="+((Collection)obj1).size()+", obj2.size="+((Collection)obj2).size());
				// }
				// return false;
			}

			// Don't assume the two collections are in the same order.
			// for each object in the first collection
			// find the first congruent object in the second collection and
			// remove that congruent object from the second collection
			// if no congruent object is found, then the two collections are not
			// congruent
			// if after processing all objects in the first collection, there
			// are objects left from the second collection
			// then the collections are not congruent
			// otherwise the collections are congruent
			Object[] collection1 = ((Collection) obj1).toArray();
			Object[] collection2 = ((Collection) obj2).toArray();
			return compareCollections(collection1, collection2,
					fieldsAnalyzedMap, ignoreCase, ignoreCrlf);
		}

		if (obj1 instanceof Map) {
			@SuppressWarnings({ "unchecked", "cast" })
			Set<Object> keys1 = ((Map) obj1).keySet();
			@SuppressWarnings({ "unchecked", "cast" })
			Set<Object> keys2 = ((Map) obj2).keySet();
			Object[] keys1Array = keys1.toArray();
			Object[] keys2Array = keys2.toArray();

			if (keys1.size() != keys2.size()) {
				// Error:Collection objects have different sizes
				return pushResult(
						false,
						"Objects are different keys sizes: keys1.size="
								+ keys1.size() + ", keys2.size=" + keys2.size(),
						null);
				// if (logDifferences) {
				// log.debug("Objects are different keys sizes: keys1.size="+keys1.size()+", keys2.size="+keys2.size());
				// }
				// return false;
			}

			CompareResult collectionResult = compareCollections(keys1Array,
					keys2Array, fieldsAnalyzedMap, ignoreCase, ignoreCrlf);
			if (!collectionResult.isMatch()) {
				return pushResult(false,
						"collection compare failed for the maps:" + obj1 + ","
								+ obj2, collectionResult);
			}
			// if (!compareCollections(keys1Array, keys2Array,
			// fieldsAnalyzedMap, logDifferences)) {
			// return false;
			// }

			for (Object key1 : keys1) {
				Object value1 = ((Map) obj1).get(key1);
				Object value2 = ((Map) obj2).get(key1);
				CompareResult recursiveCompare = recursiveCompare(value1,
						value2, fieldsAnalyzedMap, ignoreCase, ignoreCrlf);
				if (!recursiveCompare.isMatch()) {
					return pushResult(false, "Found map differences at key: "
							+ key1, recursiveCompare);
				}
				// if (!recursiveCompare(value1, value2, fieldsAnalyzedMap,
				// logDifferences)) {
				// if (logDifferences) {
				// log.debug("Found map differences at key: "+key1);
				// }
				// return false;
				// }
			}

			return pushResult(true, "match", null);
			// return true;
		}

		BeanInfo beanInfo = Introspector.getBeanInfo(obj1.getClass());
		PropertyDescriptor[] properties = beanInfo.getPropertyDescriptors();

		for (PropertyDescriptor property : properties) {
			Method readMethod = property.getReadMethod();

			if (readMethod == null) {
				continue;
			}
			Object innerObj1 = readMethod.invoke(obj1);
			Object innerObj2 = readMethod.invoke(obj2);

			if (innerObj1 == null && innerObj2 == null) {
				continue;
			}

			if (innerObj1 == null || innerObj2 == null) {
				// Error: Objects don't match at property:
				return pushResult(false, "Objects don't match on property:"
						+ property.getName() + ". One is null. obj1="
						+ innerObj1 + ", obj2=" + innerObj2, null);
			}

			if (innerObj1.equals(innerObj2)) {
				continue;
			}

			if (!innerObj1.getClass().equals(innerObj2.getClass())) {
				return pushResult(false,
						"Objects are not the same class: obj1.class="
								+ innerObj1.getClass().getName()
								+ ", obj2.class="
								+ innerObj2.getClass().getName(), null);
				// if (logDifferences) {
				// log.debug("Objects are not the same class: obj1.class="+obj1.getClass().getName()+", obj2.class="+obj2.getClass().getName());
				// }
				// return false;
			}

			// if we are hitting objects that have or are already being analyzed
			// for congruence, then both must be
			// being analyzed for congruence and when innerobj1 was added to the
			// map, its partner (map entry) must be innerobj2 and
			// when innerobj2 was added to the map, its partner must be
			// innerobj1
			List<Object> mapEntry1 = fieldsAnalyzedMap.get(innerObj1);
			List<Object> mapEntry2 = fieldsAnalyzedMap.get(innerObj2);
			if (mapEntry1 != null && mapEntry1.contains(innerObj2)
					&& mapEntry2 != null && mapEntry2.contains(innerObj1)) {
				continue;
			}

			if (isJavaLangObject(innerObj1) && isJavaLangObject(innerObj2)) {
				CompareResult compareResult = compareJavaLangObjs(innerObj1,
						innerObj2, null, ignoreCase, ignoreCrlf);
				if (!compareResult.isMatch()) {
					return pushResult(
							false,
							"Objects don't match at java property:"
									+ property.getName() + ", obj1="
									+ innerObj1 + ", obj2=" + innerObj2,
							compareResult);
				}
			} else {
				CompareResult compareResult = recursiveCompare(innerObj1,
						innerObj2, fieldsAnalyzedMap, ignoreCase, ignoreCrlf);
				if (!compareResult.isMatch()) {
					return pushResult(false,
							"Objects don't match at object property:"
									+ property.getName() + ", obj1="
									+ innerObj1 + ", obj2=" + innerObj2,
							compareResult);
				}
			}

		}

		if (isJavaLangObject(obj1) || isJavaLangObject(obj2)) {
			CompareResult compareResult = compareJavaLangObjs(obj1, obj2, null,
					ignoreCase, ignoreCrlf);
			if (!compareResult.isMatch()) {
				return pushResult(false, "Objects don't match  obj1=" + obj1
						+ ", obj2=" + obj2, compareResult);
			}
		}

		return pushResult(true, "match", null);
	}

	@SuppressWarnings("null")
	private static CompareResult compareCollections(Object[] collection1,
			Object[] collection2, Map<Object, List<Object>> fieldsAnalyzedMap,
			boolean ignoreCase, boolean ignoreCrlf)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, IntrospectionException {
		// Don't assume the two collections are in the same order.
		// for each object in the first collection
		// find the first congruent object in the second collection and remove
		// that congruent object from the second collection
		// if no congruent object is found, then the two collections are not
		// congruent
		// if after processing all objects in the first collection, there are
		// objects left from the second collection
		// then the collections are not congruent
		// otherwise the collections are congruent

		// build a map of the second collection
		Map<Integer, Object> obj2Map = new HashMap<Integer, Object>();
		for (int i = 0; i < collection2.length; i++) {
			obj2Map.put(i, collection2[i]);
		}

		for (int i = 0; i < collection1.length; i++) {
			Object collection1Obj = collection1[i];
			Set<Integer> obj2Keys = obj2Map.keySet();

			List<Object> obj1MatchList = fieldsAnalyzedMap.get(collection1Obj);

			boolean matchFound = false;
			CompareResult collectionCompareResult = null;
			for (Integer obj2Key : obj2Keys) {
				Object collection2Obj = obj2Map.get(obj2Key);

				List<Object> obj2MatchList = fieldsAnalyzedMap
						.get(collection2Obj);
				if (obj1MatchList != null
						&& obj1MatchList.contains(collection2Obj)
						&& obj2MatchList != null
						&& obj2MatchList.contains(collection1Obj)) {
					matchFound = true;
					break;
				}

				collectionCompareResult = recursiveCompare(collection1Obj,
						collection2Obj, fieldsAnalyzedMap, ignoreCase,
						ignoreCrlf);
				if (collectionCompareResult.isMatch()) {
					obj2Map.remove(obj2Key);
					matchFound = true;
					break;
				}
				// if (recursiveCompare(collection1Obj, collection2Obj,
				// fieldsAnalyzedMap, true)) {
				// log.debug("Recursive collection compare found a match at pos in collection1:"+i+" position in collection2:"+obj2Key);
				// obj2Map.remove(obj2Key);
				// matchFound = true;
				// break;
				// }

				// if the recursive comparison in this loop added obj1 or obj2
				// to the fieldsAnalyzedMap
				// remove them
				if (collection1Obj != null && collection2Obj != null) {
					List<Object> collection1Congruents = fieldsAnalyzedMap
							.get(collection1Obj);
					if ((obj1MatchList == null || !(obj1MatchList
							.contains(collection2Obj)))
							&& collection1Congruents != null
							&& collection1Congruents.contains(collection2Obj)) {
						collection1Congruents.remove(collection2Obj);
					}
					List<Object> collection2Congruents = fieldsAnalyzedMap
							.get(collection2Obj);
					if ((obj2MatchList == null || !(obj2MatchList
							.contains(collection1Obj)))
							&& collection2Congruents != null
							&& collection2Congruents.contains(collection1Obj)) {
						collection2Congruents.remove(collection1Obj);
					}
				}
			}
			if (!matchFound) {
				return pushResult(false,
						"objects are different in collection. No match found for obj1:"
								+ collection1Obj.toString(),
						collectionCompareResult);
				// if (logDifferences) {
				// log.debug("objects are different at collection, no match found for obj1:"+collection1Obj.toString());
				// }
				// return false;
			}
		}
		if (obj2Map.size() > 0) {
			return pushResult(
					false,
					"objects are different in collection. We have "
							+ obj2Map.size()
							+ " objects in obj2 that are not in ojb1", null);
			// if (logDifferences) {
			// log.debug("objects are different at collection"+
			// obj2Map.size()+" objects in obj2 that are not in ojb1");
			// }
			// return false;
		}

		return pushResult(true, "match", null);
		// return true;

	}

	private static boolean isJavaLangObject(Object obj) {

		if (obj instanceof Integer || obj instanceof Double
				|| obj instanceof Long || obj instanceof Float
				|| obj instanceof StringBuffer || obj instanceof String
				|| obj instanceof Boolean) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("cast")
	private static CompareResult compareJavaLangObjs(Object obj1, Object obj2,
			CompareResult resultStack, boolean ignoreCase, boolean ignoreCrlf) {

		if (obj1 instanceof Integer) {
			if (obj2 instanceof Integer && ((Integer) obj1).equals(obj2)) {
				return pushResult(true, "match:" + ((Integer) obj1).intValue()
						+ "=" + ((Integer) obj2).intValue(), resultStack);
			}
			// log.debug("objects are different:"+obj1.toString()+","+obj2.toString());
			return pushResult(false, ((Integer) obj1).intValue() + "!="
					+ ((Integer) obj2).intValue(), resultStack);
			// return false;
		}
		if (obj1 instanceof Double) {
			if (obj2 instanceof Double && ((Double) obj1).equals(obj2)) {
				return pushResult(true,
						"match:" + ((Double) obj1).doubleValue() + "="
								+ ((Double) obj2).doubleValue(), resultStack);
				// return true;
			}
			return pushResult(false, ((Double) obj1).doubleValue() + "!="
					+ ((Double) obj2).doubleValue(), resultStack);
			// log.debug("objects are different:"+obj1.toString()+","+obj2.toString());
			// return false;
		}
		if (obj1 instanceof Long) {
			if (obj2 instanceof Long && ((Long) obj1).equals(obj2)) {
				return pushResult(true, "match:" + ((Long) obj1).longValue()
						+ "=" + ((Long) obj2).longValue(), resultStack);
				// return true;
			}
			return pushResult(false, ((Long) obj1).longValue() + "!="
					+ ((Long) obj2).longValue(), resultStack);
			// log.debug("objects are different:"+obj1.toString()+","+obj2.toString());
			// return false;
		}
		if (obj1 instanceof Float) {
			if (obj2 instanceof Float && ((Float) obj1).equals(obj2)) {
				return pushResult(true, "match:" + ((Float) obj1).floatValue()
						+ "=" + ((Float) obj2).floatValue(), resultStack);
				// return true;
			}
			return pushResult(false, ((Float) obj1).floatValue() + "!="
					+ ((Float) obj2).floatValue(), resultStack);
			// log.debug("objects are different:"+obj1.toString()+","+obj2.toString());
			// return false;
		}
		if (obj1 instanceof Boolean) {
			if (obj2 instanceof Boolean && ((Boolean) obj1).equals(obj2)) {
				return pushResult(true,
						"match:" + ((Boolean) obj1).booleanValue() + "="
								+ ((Boolean) obj2).booleanValue(), resultStack);
				// return true;
			}
			return pushResult(false, ((Boolean) obj1).booleanValue() + "!="
					+ ((Boolean) obj2).booleanValue(), resultStack);
			// log.debug("objects are different:"+obj1.toString()+","+obj2.toString());
			// return false;
		}
		if (obj1 instanceof StringBuffer) {
			String obj1Str = obj1.toString();
			if (ignoreCrlf) {
				obj1Str = obj1Str.replaceAll("\\r\\n", "\n");
			}
			if (ignoreCase) {
				if (obj2 instanceof StringBuffer) {
					String obj2Str = obj2.toString();
					if (ignoreCrlf) {
						obj2Str = obj2Str.replaceAll("\\r\\n", "\n");
					}
					if (obj1Str.equalsIgnoreCase(obj2Str)) {
						return pushResult(true, "match:" + (obj1) + "="
								+ (obj2), resultStack);
					}
				}
				return pushResult(false, (obj1) + "!=" + (obj2), resultStack);
				// log.debug("objects are different:"+obj1.toString()+","+obj2.toString());
				// return false;
				// return true;
			}
			if (obj2 instanceof StringBuffer) {
				String obj2Str = obj2.toString();
				if (ignoreCrlf) {
					obj2Str = obj2Str.replaceAll("\\r\\n", "\n");
				}
				if (obj1Str.equals(obj2Str)) {
					return pushResult(true, "match:" + (obj1) + "=" + (obj2),
							resultStack);
				}
			}
			return pushResult(false, (obj1) + "!=" + (obj2), resultStack);
			// log.debug("objects are different:"+obj1.toString()+","+obj2.toString());
			// return false;
			// return true;
		}
		if (obj1 instanceof String) {
			if (ignoreCrlf) {
				obj1 = ((String) obj1).replaceAll("\\r\\n", "\n");
			}
			if (ignoreCase) {
				if (obj2 instanceof String) {
					if (ignoreCrlf) {
						obj2 = ((String) obj2).replaceAll("\\r\\n", "\n");
					}
					if (((String) obj1).equalsIgnoreCase((String) obj2)) {
						return pushResult(true, "match:" + ((String) obj1)
								+ "=" + ((String) obj2), resultStack);
					}
					// return true;
				}
				return pushResult(false, ((String) obj1) + "!="
						+ ((String) obj2), resultStack);
				// log.debug("objects are different:"+obj1.toString()+","+obj2.toString());
				// return false;
			}
			if (obj2 instanceof String) {
				if (ignoreCrlf) {
					obj2 = ((String) obj2).replaceAll("\\r\\n", "\n");
				}
				if (((String) obj1).equals(obj2)) {
					return pushResult(true, "match:" + ((String) obj1) + "="
							+ ((String) obj2), resultStack);
					// return true;
				}
			}
			return pushResult(false, ((String) obj1) + "!=" + ((String) obj2),
					resultStack);
			// log.debug("objects are different:"+obj1.toString()+","+obj2.toString());
			// return false;
		}

		return pushResult(false, "compareJavaLangObjs() compare failed on "
				+ obj1.getClass().getName() + " and "
				+ obj2.getClass().getName(), resultStack);
		// return false;

	}

	private static CompareResult pushResult(boolean result, String explanation,
			CompareResult currentResult) {
		if (currentResult == null) {
			return new CompareResult(result, explanation);
		}

		currentResult.addExplanation(explanation);
		currentResult.setIsMatch(result);
		return currentResult;
	}

}
