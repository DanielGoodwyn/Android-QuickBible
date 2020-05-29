package com.quickbible.quickbible;

public class Scripture implements Comparable<Scripture> {
	Integer getBook() {
		return book;
	}

	Integer getChapter() {
		return chapter;
	}

	Integer getVerse() {
		return verse;
	}

	Integer book;
	Integer chapter;
	private Integer verse;
	private Integer order;

	Scripture() {
	}

	;

	Scripture(Integer book, Integer chapter, Integer verse, Integer order) {
		this.book = book;
		this.chapter = chapter;
		this.verse = verse;
		this.order = order;
	}

	@Override
	public int compareTo(Scripture scripture) {
		if (this.order < scripture.order)
			return 1;
		else if (this.order > scripture.order)
			return -1;
		return 0;
	}
}
