package hotmath.gwt.cm_admin.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class StudyProgramModel implements IsSerializable {
	
	private String title;
	private String shortTitle;
	private String descr;
	private Integer needsSubject;
	private Integer needsChapters;
	private Integer needsPassPercent;
	private Integer needsState;

	public StudyProgramModel() {
	}
	
	public StudyProgramModel(String title, String shortTitle, String descr,
			Integer needsSubject, Integer needsChapters,
			Integer needsPassPercent, Integer needsState) {
		this.title = title;
		this.shortTitle = shortTitle;
		this.descr = descr;
		this.needsSubject = needsSubject;
		this.needsChapters = needsChapters;
		this.needsPassPercent = needsPassPercent;
		this.needsState = needsState;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getShortTitle() {
		return shortTitle;
	}

	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public Integer getNeedsSubject() {
		return needsSubject;
	}

	public void setNeedsSubject(Integer needsSubject) {
		this.needsSubject = needsSubject;
	}

	public Integer getNeedsChapters() {
		return needsChapters;
	}

	public void setNeedsChapters(Integer needsChapters) {
		this.needsChapters = needsChapters;
	}

	public Integer getNeedsPassPercent() {
		return needsPassPercent;
	}

	public void setNeedsPassPercent(Integer needsPassPercent) {
		this.needsPassPercent = needsPassPercent;
	}

	public Integer getNeedsState() {
		return needsState;
	}

	public void setNeedsState(Integer needsState) {
		this.needsState = needsState;
	}
}