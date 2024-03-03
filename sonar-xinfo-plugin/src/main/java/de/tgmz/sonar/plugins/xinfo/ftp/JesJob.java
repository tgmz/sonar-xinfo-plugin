/*******************************************************************************
  * Copyright (c) 26.02.2024 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo.ftp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JesJob {
	private static final Pattern JOB_DETAIL_PATTERN = Pattern
            .compile("(?<name>[^ ]+) +(?<handle>[^ ]+) +(?<owner>[^ ]+) +(?<status>[^ ]+) +(?<class>[^ ]+)"
                    + "(?<completion> +(?<result>[^=]+)=(?<code>\\d+)(?<spool> +(?<files>\\d+) +spool files)?)? +");

    private String name;
    private String handle;
    private String owner;
    private String status;
    private String type;
    private Integer conditionCode = null;
    private Integer abendCode = null;
    private Integer spoolFileCount = null;

    public boolean parseDetails(String details) {
        Matcher matcher = JOB_DETAIL_PATTERN.matcher(details);
        if (!matcher.matches()) {
            return false;
        }

        name = matcher.group("name");
        handle = matcher.group("handle");
        owner = matcher.group("owner");
        status = matcher.group("status");
        type = matcher.group("class");
        if (matcher.group("completion") != null) {
            String result = matcher.group("result");
            if (result.equals("RC")) {
                conditionCode = Integer.parseInt(matcher.group("code"));
            } else if (result.equals("ABEND")) {
                abendCode = Integer.parseInt(matcher.group("code"));
            }
            if (matcher.group("spool") != null) {
                spoolFileCount = Integer.parseInt(matcher.group("files"));
            }
        }

        return true;
    }

	public String getName() {
		return name;
	}

	public String getHandle() {
		return handle;
	}

	public String getOwner() {
		return owner;
	}

	public String getStatus() {
		return status;
	}

	public String getType() {
		return type;
	}

	public Integer getConditionCode() {
		return conditionCode;
	}

	public Integer getAbendCode() {
		return abendCode;
	}

	public Integer getSpoolFileCount() {
		return spoolFileCount;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}

	@Override
	public String toString() {
		return String.format(
				"JesJob [name=%s, handle=%s, owner=%s, status=%s, type=%s, conditionCode=%s, abendCode=%s, spoolFileCount=%s]",
				name, handle, owner, status, type, conditionCode, abendCode, spoolFileCount);
	}
}