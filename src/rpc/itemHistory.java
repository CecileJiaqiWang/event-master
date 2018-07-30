package rpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

/**
 * Servlet implementation class itemHistory
 */
@WebServlet(name = "history", urlPatterns = { "/history" })
public class itemHistory extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public itemHistory() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String userId = request.getParameter("user_id");
		JSONArray array = new JSONArray();
		
		DBConnection conn = DBConnectionFactory.getConnection();
		
		try {
			Set<Item> items = conn.getFavoriteItems(userId);
			for (Item item: items) {
				JSONObject obj = item.toJSONObject();
				obj.append("favorite", true);
				array.put(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
		
		RpcHelper.writeJsonArray(response, array);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		DBConnection conn = DBConnectionFactory.getConnection();
		if (conn == null) {
			System.err.println("DB connection failed!");
			return;
		}
		try {
			JSONObject input = RpcHelper.readJsonObject(request);
			String userId = input.getString("user_id");

			// Get all the favorite events.
			JSONArray array = input.getJSONArray("favorite");
			List<String> itemIds = new ArrayList<>();
			for (int i = 0; i < array.length(); i++) {
				itemIds.add(array.getString(i));
			}
			// Save the information in history table.
			conn.setFavoriteItems(userId, itemIds);
//			System.out.println("user_id:" + userId);
//			System.out.println("item_id:" + itemIds);
			// Mark as successful.
			RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Close connection anyway.
			conn.close();
		}
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		DBConnection conn = DBConnectionFactory.getConnection();
		if (conn == null) {
			System.err.println("DB connection failed!");
		}
		try {
			JSONObject input = RpcHelper.readJsonObject(request);
			String userId = input.getString("user_id");

			// Get all the favorite events.
			JSONArray array = input.getJSONArray("favorite");
			List<String> itemIds = new ArrayList<>();
			for (int i = 0; i < array.length(); i++) {
				itemIds.add(array.getString(i));
			}
			// Save the information in history table.
			conn.unsetFavoriteItems(userId, itemIds);
			// Mark as successful.
			RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Close connection anyway.
			conn.close();
		}
	}

}
