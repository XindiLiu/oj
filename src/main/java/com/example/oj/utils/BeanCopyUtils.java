package com.example.oj.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.FeatureDescriptor;
import java.util.stream.Stream;

/**
 * Utility class for copying properties between Java beans.
 */
public class BeanCopyUtils {

	/**
	 * Copies properties from the source object to the target object,
	 * ignoring properties in the source that are null.
	 *
	 * @param src    the source bean from which to copy properties
	 * @param target the target bean to which properties are copied
	 */
	public static void copyNonNullSrcProperties(Object src, Object target) {
		String[] emptyNames = getNullPropertyNames(src);
		BeanUtils.copyProperties(src, target, emptyNames);
	}

	/**
	 * Copies properties from the source object to the target object,
	 * only copying properties in the target that are null.
	 *
	 * @param src    the source bean from which to copy properties
	 * @param target the target bean to which properties are copied
	 */
	public static void copyNullTargetProperties(Object src, Object target) {
		String[] emptyNames = getNullPropertyNames(target);
		BeanUtils.copyProperties(src, target, emptyNames);
	}

	/**
	 * Retrieves an array of property names from the given object
	 * that have null values.
	 *
	 * @param source the object to inspect for null properties
	 * @return an array of property names with null values
	 */
	private static String[] getNullPropertyNames(Object source) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		return Stream.of(src.getPropertyDescriptors())
				.map(FeatureDescriptor::getName)
				.filter(propertyName -> src.getPropertyValue(propertyName) == null)
				.toArray(String[]::new);
	}
}