package org.plumber.client.domain;

import java.util.Date;

/**
 * Created by jglanz on 10/21/14.
 */
public class Timestampable {

	protected Date createdAt, modifiedAt;

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(Date modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	public void ensureDates() {
		if (createdAt == null)
			createdAt = new Date();

		if (modifiedAt == null)
			modifiedAt = new Date();
	}

	public void modified() {
		modifiedAt = new Date();
	}
}
