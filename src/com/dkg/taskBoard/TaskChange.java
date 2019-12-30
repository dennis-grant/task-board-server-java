package com.dkg.taskBoard;

public class TaskChange extends Task {
	private int changeId;
	private String action;

	public TaskChange() {
		this.changeId = 0;
		this.action = "";
	}

	public TaskChange(Task task) {
		this.changeId = 0;
		this.action = "";
		this.setTaskId(task.getTaskId());
		this.setIssueNumber(task.getIssueNumber());
		this.setDescription(task.getDescription());
		this.setAssignedTo(task.getAssignedTo());
		this.setColumnIndex(task.getColumnIndex());
		this.setStyle(task.getStyle());
		this.setTag(task.getTag());
	}

	public int getChangeId() {
		return changeId;
	}

	public void setChangeId(int changeId) {
		this.changeId = changeId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
}
