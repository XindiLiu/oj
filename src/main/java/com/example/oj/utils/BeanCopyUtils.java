package com.example.oj.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.FeatureDescriptor;
import java.util.stream.Stream;
import java.util.Set;
import java.util.HashSet;

public class BeanCopyUtils {

	public static void copyNonNullProperties(Object src, Object target) {
		String[] emptyNames = getNullPropertyNames(src);
		BeanUtils.copyProperties(src, target, emptyNames);
	}

	public static void copyNullPropertiesTaget(Object src, Object target) {
		String[] emptyNames = getNullPropertyNames(target);
		BeanUtils.copyProperties(src, target, emptyNames);
	}

	private static String[] getNullPropertyNames(Object source) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		return Stream.of(src.getPropertyDescriptors())
				.map(FeatureDescriptor::getName)
				.filter(propertyName -> src.getPropertyValue(propertyName) == null)
				.toArray(String[]::new);
	}
}