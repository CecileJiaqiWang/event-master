package rpc;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
<<<<<<< Updated upstream
import org.json.JSONException;
<<<<<<< Updated upstream

import algorithm.GeoRecommendation;
import entity.Item;

import algorithm.GeoRecommendation;
import entity.Item;
=======
import org.json.JSONObject;
=======

import algorithm.GeoRecommendation;
import entity.Item;
>>>>>>> Stashed changes
>>>>>>> Stashed changes

/**
 * Servlet implementation class RecommendItem
 */
@WebServlet(name = "recommend", urlPatterns = { "/recommend" })
public class RecommendItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RecommendItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    String userId = request.getParameter("user_id");
	    double lat = Double.parseDouble(request.getParameter("lat"));
	    double lon = Double.parseDouble(request.getParameter("lon"));
	    
	    GeoRecommendation recommendation = new GeoRecommendation();
	    // Get Recommendation.
	    List<Item> items = recommendation.recommendItems(userId, lat, lon);
		
		
		JSONArray array = new JSONArray();
		for (Item item : items) {
			array.put(item.toJSONObject());
		}
		
		RpcHelper.writeJsonArray(response, array);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
