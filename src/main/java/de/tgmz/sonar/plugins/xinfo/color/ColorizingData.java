/*******************************************************************************
  * Copyright (c) 13.11.2016 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo.color;

import org.sonar.api.batch.sensor.highlighting.TypeOfText;

/**
 * Highlighted area.
 */
public class ColorizingData implements Comparable<ColorizingData> {
	private int startLineNumber;
	private int startOffset;
	private int endLineNumber;
	private int endOffset;
	private TypeOfText type;

	@SuppressWarnings("unused") 	// Content is only used for debugging
	private String content;

	public ColorizingData(int startLineNumber, int startOffset, int endLineNumber, int endOffset, String content, TypeOfText type) {
		super();
		
		if (startLineNumber != endLineNumber) {
			throw new IllegalArgumentException("Multiline colorizing is not supported yet");
		}
		
		this.startLineNumber = startLineNumber;
		this.startOffset = startOffset;
		this.endLineNumber = endLineNumber;
		this.endOffset = endOffset;
		this.content = content;
		this.type = type;
	}

	public int getStartOffset() {
		return startOffset;
	}

	public int getEndOffset() {
		return endOffset;
	}

	public TypeOfText getType() {
		return type;
	}

	public int getStartLineNumber() {
		return startLineNumber;
	}

	public int getEndLineNumber() {
		return endLineNumber;
	}

	/* 
	 * Two ColorizingData are considered equal if they overlap. 
	 * So we can simply add them to a Set and let it handle overlapping areas.
	 */
	@Override
	public int compareTo(ColorizingData other) {
		if (this.getStartLineNumber() > other.getEndLineNumber()) {
			return 80 * (other.getEndLineNumber() - this.getStartLineNumber());
		}
		
		if (this.getEndLineNumber() < other.getStartLineNumber()) {
			return 80 * (other.getStartLineNumber() - this.getEndLineNumber());
		}
				
		if (this.getStartOffset() >= other.getEndOffset()) {
			return other.getEndOffset() - this.getStartOffset();
		}
		
		if (this.getEndOffset() <= other.getStartOffset()) {	// No overlaps
			return other.getStartOffset() - this.getEndOffset();
		}
		
		return 0;
	}
	/* 
	 * Two ColorizingData are considered equal if they overlap. 
	 * So we can simply add them to a Set and let it handle overlapping areas.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null) {
			return false;
		}
		
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		return compareTo((ColorizingData) obj) == 0;
	}
	
	/*
	 * Must override. The contract says, that two equal ColorizedData 
	 * must have the same hashCode but we cannot decide this.  
	 */
	@Override
	public int hashCode() {
		return 0;
	}
}
