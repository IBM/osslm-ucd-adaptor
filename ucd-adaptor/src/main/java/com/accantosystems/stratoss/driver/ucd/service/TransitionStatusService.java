package com.accantosystems.stratoss.driver.ucd.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.accantosystems.stratoss.driver.ucd.model.resourcemanager.TransitionStatus;

@Service
public class TransitionStatusService {

	private final Map<String, TransitionStatus> statusMap = new ConcurrentHashMap<>();
	
	public TransitionStatus getTransitionStatus(String id) {
		return statusMap.get(id);
	}
	
	public void addTransitionStatus(TransitionStatus status) {
		statusMap.put(status.getRequestId(), status);
	}
	
	public void removeTransitionStatus(String id) {
		statusMap.remove(id);
	}

}
