package com.dkg.taskBoard;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class TBServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private App app;
	private int connCnt = 0;

	public void init() {
		String dbUrl;
		String dbUser;
		String dbPassword;

		dbUrl = this.getInitParameter("dbUrl");
		dbUser = this.getInitParameter("dbUser");
		dbPassword = this.getInitParameter("dbPassword");

		try {
			this.app = new App(getConnectionFactory(dbUrl, dbUser, dbPassword));
		}
		catch(Exception e) {
			
		}
	}

	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String cmd;
		int latestChangeIdReceived;
		String tag;
		int taskId;
		ArrayList<TaskChange> changes;

		this.connCnt++;
		changes = new ArrayList<TaskChange>();
		try {
			cmd = request.getParameter("cmd");
			latestChangeIdReceived = Integer.parseInt(request.getParameter("latestChangeIdReceived"));
			tag = request.getParameter("tag");

			if (cmd.equals("getLatestChanges")) {
				changes = this.app.getLatestChanges(tag, latestChangeIdReceived);
			}
			else if (cmd.equals("newTask")) {
				changes = this.app.newTask(request.getParameter("style"), tag, latestChangeIdReceived);
			}
			else if (cmd.equals("updateTask")) {
				changes = this.app.updateTask(getTaskFromRequest(request), latestChangeIdReceived);
			}
			else if (cmd.equals("moveTaskLeft")) {
				changes = this.app.moveTaskLeft(getTaskFromRequest(request), latestChangeIdReceived);
			}
			else if (cmd.equals("moveTaskRight")) {
				changes = this.app.moveTaskRight(getTaskFromRequest(request), latestChangeIdReceived);
			}
			else if (cmd.equals("deleteTask")) {
				taskId = Integer.parseInt(request.getParameter("taskId"));
				changes = this.app.deleteTask(taskId, tag, latestChangeIdReceived);
			}
		}
		catch(Exception e) {
			System.out.println("\n\n=========" + e.getMessage() + "========\n\n");
		}

		System.out.println("\n=========== number of connections: " + this.connCnt  + "==========\n");
		response.setContentType("text/plain");
		response.getWriter().print(changesToJSON(changes));
	}

	private Task getTaskFromRequest(HttpServletRequest request) {
		Task t;

		t = new Task();
		t.setTaskId(Integer.parseInt(request.getParameter("taskId")));
		t.setIssueNumber(request.getParameter("issueNumber"));
		t.setDescription(request.getParameter("description"));
		t.setAssignedTo(request.getParameter("assignedTo"));
		t.setColumnIndex(Integer.parseInt(request.getParameter("columnIndex")));
		t.setStyle(request.getParameter("style"));
		t.setTag(request.getParameter("tag"));

		return t;
	}

	private String changesToJSON(ArrayList<TaskChange> changes) throws IOException {
		TaskChange tc;
		JSONArray tcList;
		JSONObject tmpChange;
		JSONObject tmpTask;
		StringWriter json;

		tcList = new JSONArray();
		for (int i = 0; i < changes.size(); i++) {
			tc = changes.get(i);

			tmpChange = new JSONObject();
			tmpChange.put("changeId", tc.getChangeId());
			tmpChange.put("action", tc.getAction());
			
			tmpTask = new JSONObject();
			tmpTask.put("taskId", tc.getTaskId());
			tmpTask.put("issueNumber", tc.getIssueNumber());
			tmpTask.put("description", tc.getDescription());
			tmpTask.put("assignedTo", tc.getAssignedTo());
			tmpTask.put("columnIndex", tc.getColumnIndex());
			tmpTask.put("style", tc.getStyle());
			tmpTask.put("tag", tc.getTag());

			tmpChange.put("task", tmpTask);

			tcList.add(tmpChange);
		}
		json = new StringWriter();
		tcList.writeJSONString(json);

		return json.toString();
	}

	private ConnectionFactory getConnectionFactory(String connectionString, String user, String password) {
		ConnectionFactory connectionFactory;
		String driverClass;

		driverClass = "com.mysql.jdbc.Driver";
		connectionFactory = new ConnectionFactory(driverClass, connectionString, user, password);

		return connectionFactory;
	}
}
