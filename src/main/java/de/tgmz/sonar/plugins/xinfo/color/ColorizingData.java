/*******************************************************************************
  * Copyright (c) 13.11.2016 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo.color;

import javax.annotation.Nullable;

import org.sonar.api.batch.sensor.highlighting.TypeOfText;

/**
 * Highlighted area.
 */
public class ColorizingData {
	private int startLineNumber;
	private int startOffset;
	private int endLineNumber;
	private int endOffset;
	private TypeOfText type;
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

	public boolean overlap(@Nullable Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (this == obj) {
			return true;
		}
		
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		ColorizingData other = (ColorizingData) obj;
		
		// This area starts after the other ends
		if (this.getStartLineNumber() > other.getEndLineNumber()) {
			return false;
		}
		
		// This area ends before the other starts
		if (this.getEndLineNumber() < other.getStartLineNumber()) {
			return false;
		}
				
		if (this.getStartLineNumber() == other.getEndLineNumber()
			&& this.getStartOffset() >= other.getEndOffset()) {
			return false;
		}
		
		if (this.getEndLineNumber() == other.getStartLineNumber()
			&& this.getEndOffset() <= other.getStartOffset()) {	// No overlaps
			return false;
		}
		
		return true;
	}

	public String getContent() {
		return content;
	}
}
