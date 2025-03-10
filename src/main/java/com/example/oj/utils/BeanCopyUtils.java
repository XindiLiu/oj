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
	 */
	public static void copyNonNullSrcProperties(Object src, Object target) {
		String[] emptyNames = getNullPropertyNames(src);
		BeanUtils.copyProperties(src, target, emptyNames);
	}

	/**
	 * Copies properties from the source object to the target object,
	 * only copying properties in the target that are null.
	 */
	public static void copyNullTargetProperties(Object src, Object target) {
		String[] emptyNames = getNullPropertyNames(target);
		BeanUtils.copyProperties(src, target, emptyNames);
	}

	/**
	 * Retrieves an array of property names from the given object
	 * that have null values.
	 */
	private static String[] getNullPropertyNames(Object source) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		return Stream.of(src.getPropertyDescriptors())
				.map(FeatureDescriptor::getName)
				.filter(propertyName -> src.getPropertyValue(propertyName) == null)
				.toArray(String[]::new);
	}
}