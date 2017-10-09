package com.accantosystems.stratoss.driver.ucd.model.ucd;

import java.util.ArrayList;
import java.util.List;

public class ApplicationProcessDetails extends ApplicationProcessInfo {

	private final List<ApplicationProcessProperty> propDefs = new ArrayList<ApplicationProcessProperty>();
	private ProcessActivity rootActivity;

	public List<ApplicationProcessProperty> getPropDefs() {
		return propDefs;
	}
	public ProcessActivity getRootActivity() {
		return rootActivity;
	}
	public void setRootActivity(ProcessActivity rootActivity) {
		this.rootActivity = rootActivity;
	}

	@Override
	public String toString() {
		return String.format("{\"_class\":\"ApplicationProcessDetails\", \"propDefs\":%s, \"rootActivity\":%s}",
				propDefs, rootActivity);
	}
	
}
