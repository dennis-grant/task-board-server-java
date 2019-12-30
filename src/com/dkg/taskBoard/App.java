package com.dkg.taskBoard;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class App {
	private ConnectionFactory connectionFactory;
	private int nextTaskId;

	public App(ConnectionFactory cf) throws ClassNotFoundException, SQLException {
		this.connectionFactory = cf;
		this.nextTaskId = getLastTaskId() + 1;
	}

	public ArrayList<TaskChange> newTask(String style, String tag, int latestChangeIdReceived) throws ClassNotFoundException, SQLException {
		TaskChange tc;
		
		tc = new TaskChange();
		tc.setAction("add");
		tc.setTaskId(this.nextTaskId++);
		tc.setIssueNumber("issue no.");
		tc.setDescription("description");
		tc.setAssignedTo("assigned to");
		tc.setColumnIndex(1);
		tc.setStyle(style);
		tc.setTag(tag);
		insertTaskChange(tag, tc);

		return getLatestChanges(tag, latestChangeIdReceived);
	}

	public ArrayList<TaskChange> updateTask(Task task, int latestChangeIdReceived) throws ClassNotFoundException, SQLException {
		TaskChange tc;
		
		tc = new TaskChange(task);
		tc.setAction("update");
		insertTaskChange(task.getTag(), tc);

		return getLatestChanges(task.getTag(), latestChangeIdReceived);
	}

	public ArrayList<TaskChange> moveTaskLeft(Task task, int latestChangeIdReceived) throws ClassNotFoundException, SQLException {
		TaskChange tc;
		
		tc = new TaskChange(task);
		tc.setColumnIndex(tc.getColumnIndex() - 1);
		tc.setAction("update");
		insertTaskChange(task.getTag(), tc);

		return getLatestChanges(task.getTag(), latestChangeIdReceived);
	}

	public ArrayList<TaskChange> moveTaskRight(Task task, int latestChangeIdReceived) throws ClassNotFoundException, SQLException {
		TaskChange tc;

		tc = new TaskChange(task);
		tc.setColumnIndex(tc.getColumnIndex() + 1);
		tc.setAction("update");
		insertTaskChange(task.getTag(), tc);

		return getLatestChanges(task.getTag(), latestChangeIdReceived);
	}

	public ArrayList<TaskChange> deleteTask(int taskId, String tag, int latestChangeIdReceived)  throws ClassNotFoundException, SQLException {
		TaskChange tc;

		tc = new TaskChange();
		tc.setTaskId(taskId);
		tc.setAction("delete");
		insertTaskChange(tag, tc);
		return getLatestChanges(tag, latestChangeIdReceived);
	}

	public ArrayList<TaskChange> getLatestChanges(String tag, int latestChangeIdReceived) throws ClassNotFoundException, SQLException {
		Connection connection;
		ArrayList<TaskChange> changes;
		PreparedStatement pstmt;
		ResultSet result;		
		TaskChange tmpChange;
		StringBuffer query;

		changes = new ArrayList<TaskChange>();
		pstmt = null;
		result = null;
		query = new StringBuffer();
		query.append("select changeId, action, taskId, issueNumber, description, assignedTo, columnIndex, style, tag ");
		query.append("from TaskChange ");
		query.append("where tag = ? ");
		query.append("and changeId > ? ");
		query.append("order by changeId ");

		connection = this.connectionFactory.getConnection();
		pstmt = connection.prepareStatement(query.toString());
		pstmt.setString(1, tag);
		pstmt.setInt(2, latestChangeIdReceived);
		result = pstmt.executeQuery();	

		while (result.next()) {
			tmpChange = new TaskChange();
			tmpChange.setChangeId(result.getInt(1));
			tmpChange.setAction(result.getString(2));
			tmpChange.setTaskId(result.getInt(3));
			tmpChange.setIssueNumber(result.getString(4));
			tmpChange.setDescription(result.getString(5));
			tmpChange.setAssignedTo(result.getString(6));
			tmpChange.setColumnIndex(result.getInt(7));
			tmpChange.setStyle(result.getString(8));
			tmpChange.setTag(result.getString(9));
			changes.add(tmpChange);
		}

		result.close();
		pstmt.close();
		connection.close();

		return changes;
	}

	private void insertTaskChange(String tag, TaskChange taskChange) throws ClassNotFoundException, SQLException {
		Connection connection;
		PreparedStatement pstmt;
		StringBuffer insert;

		pstmt = null;
		insert = new StringBuffer();
		insert.append("insert ");
		insert.append("into TaskChange ( action, taskId, issueNumber, description, assignedTo, columnIndex, style, tag) ");
		insert.append("values (?, ?, ?, ?, ?, ?, ?, ?) ");

		connection = this.connectionFactory.getConnection();
		pstmt = connection.prepareStatement(insert.toString());
		pstmt.setString(1, taskChange.getAction());
		pstmt.setInt(2, taskChange.getTaskId());
		pstmt.setString(3, taskChange.getIssueNumber());
		pstmt.setString(4, taskChange.getDescription());
		pstmt.setString(5, taskChange.getAssignedTo());
		pstmt.setInt(6, taskChange.getColumnIndex());
		pstmt.setString(7, taskChange.getStyle());
		pstmt.setString(8, tag);

		pstmt.executeUpdate();
		pstmt.close();
		connection.close();
	}

	private int getLastTaskId() throws ClassNotFoundException, SQLException {
		Connection connection;
		PreparedStatement pstmt;
		ResultSet result;		
		int lastTaskId;

		connection = this.connectionFactory.getConnection();
		pstmt = connection.prepareStatement( "select max(taskId) from TaskChange");
		result = pstmt.executeQuery();	
		
		if (result.next()) {
			lastTaskId = result.getInt(1);
		}
		else {
			lastTaskId = 0;
		}

		result.close();
		pstmt.close();
		connection.close();

		return lastTaskId;
	}
}
