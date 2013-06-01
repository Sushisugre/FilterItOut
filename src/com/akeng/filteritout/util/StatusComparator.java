package com.akeng.filteritout.util;

import java.util.Comparator;

import com.akeng.filteritout.entity.Status;

public class StatusComparator implements Comparator<Status> {

	@Override
	public int compare(Status arg0, Status arg1) {
		return arg1.getWeight().compareTo(arg0.getWeight());
	}

}
