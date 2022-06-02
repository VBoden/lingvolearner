package com.boden.lingvolearner.services;

import java.util.HashMap;
import java.util.Map;

public enum Stage {
	FOREIGN_TO_NATIVE(0), NATIVE_TO_FOREIGN(1), WRITING_WORDS(2);

	private int index;
	private static final Map<Integer, Stage> map;

	static {
		map = new HashMap<>();
		for (Stage v : Stage.values()) {
			map.put(v.index, v);
		}
	}

	private Stage(int index) {
		this.index = index;
	}

	public static Stage getFirst() {
		return map.get(0);
	}

	public boolean isFirst() {
		return index == 0;
	}

	public boolean isLast() {
		return index == Stage.values().length-1;
	}

	public Stage getPrevious() {
		int prev = (index + (Stage.values().length - 1)) % Stage.values().length;
		return map.get(prev);
	}

	public Stage getNext() {
		int next = (index + 1) % Stage.values().length;
		return map.get(next);
	}
}
