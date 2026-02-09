package br.com.finances.api.generic;

import java.time.LocalDate;
import java.util.List;

public class ScrollDTO<S> {

	List<S> data;
	boolean hasNext;
	Long lastId;
	LocalDate lastDate;

	public ScrollDTO(List<S> data, boolean hasNext) {
		this.data = data;
		this.hasNext = hasNext;
	}

	public List<S> getData() {
		return data;
	}

	public boolean getHasNext() {
		return hasNext;
	}

	public Long getLastId() {
		return lastId;
	}

	public void setLastId(Long lastId) {
		this.lastId = lastId;
	}

	public LocalDate getLastDate() {
		return lastDate;
	}

	public void setLastDate(LocalDate lastDate) {
		this.lastDate = lastDate;
	}
}
