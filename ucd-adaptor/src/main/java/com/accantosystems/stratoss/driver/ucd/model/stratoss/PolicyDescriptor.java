package com.accantosystems.stratoss.driver.ucd.model.stratoss;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown=true)
public class PolicyDescriptor {

	private String metric;
	private String smoothing;
	private String threshold;
	private String action;
	private String target;
	
	public PolicyDescriptor() {
		super();
	}
	
	public PolicyDescriptor(String metric, String smoothing, String threshold, String action, String target) {
		super();
		this.metric = metric;
		this.smoothing = smoothing;
		this.threshold = threshold;
		this.action = action;
		this.target = target;
	}

	public String getMetric() {
		return metric;
	}
	public void setMetric(String metric) {
		this.metric = metric;
	}
	public String getSmoothing() {
		return smoothing;
	}
	public void setSmoothing(String smoothing) {
		this.smoothing = smoothing;
	}
	public String getThreshold() {
		return threshold;
	}
	public void setThreshold(String threshold) {
		this.threshold = threshold;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((metric == null) ? 0 : metric.hashCode());
		result = prime * result + ((smoothing == null) ? 0 : smoothing.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		result = prime * result + ((threshold == null) ? 0 : threshold.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PolicyDescriptor other = (PolicyDescriptor) obj;
		if (action == null) {
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		if (metric == null) {
			if (other.metric != null)
				return false;
		} else if (!metric.equals(other.metric))
			return false;
		if (smoothing == null) {
			if (other.smoothing != null)
				return false;
		} else if (!smoothing.equals(other.smoothing))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		if (threshold == null) {
			if (other.threshold != null)
				return false;
		} else if (!threshold.equals(other.threshold))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format(
				"{\"_class\":\"PolicyDescriptor\", \"metric\":\"%s\", \"smoothing\":\"%s\", \"threshold\":\"%s\", \"action\":\"%s\", \"target\":\"%s\"}",
				metric, smoothing, threshold, action, target);
	}
	
}
