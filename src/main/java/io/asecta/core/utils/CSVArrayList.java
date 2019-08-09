package io.asecta.core.utils;

import java.util.ArrayList;
import java.util.function.Supplier;

public class CSVArrayList<T> extends ArrayList<T> implements Supplier<String> {

	private static final long serialVersionUID = -1516739230280577137L;
	private String csvString = "";

	@Override
	public boolean add(T e) {
		boolean b = super.add(e);
		csvString = ListUtils.toCSV(this);
		return b;
	}

	@Override
	public T remove(int index) {
		T t = super.remove(index);
		csvString = ListUtils.toCSV(this);
		return t;
	}

	public String getCSV() {
		return csvString;
	}

	public String get() {
		return csvString;
	}

}
