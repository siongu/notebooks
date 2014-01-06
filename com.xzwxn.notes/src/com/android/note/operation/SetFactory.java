package com.android.note.operation;

import java.util.HashSet;
import java.util.Set;

public class SetFactory {
	private static SetFactory instaceFactory = null;
	private Set<String> set = new HashSet<String>();

	private SetFactory() {
	}

	public static SetFactory getInstance() {
		if (instaceFactory == null) {
			instaceFactory = new SetFactory();
		}
		return instaceFactory;
	}

	public void addData(String str) {
		set.add(str);
	}

	public Set<String> getData() {
		return set;
	}

	public void delItem(String item) {
		set.remove(item);
	}

	public void clear() {
		set.clear();
	}

	public void addAll(Set<String> st) {
		set.addAll(st);
	}
}
