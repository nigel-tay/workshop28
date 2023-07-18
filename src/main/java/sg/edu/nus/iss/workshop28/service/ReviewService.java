package sg.edu.nus.iss.workshop28.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import sg.edu.nus.iss.workshop28.repository.ReviewRepository;

@Service
public class ReviewService {
    
    @Autowired
    private ReviewRepository rRepo;

    /**
     * gid
     * name
     * year 
     * ranking 
     * users_rated 
     * url 
     * image 
     * _id
     */
    public JsonObject buildJson(int gid, String name, int year, int ranking, int users_rated, String url, String image, List<String> reviews) {
        JsonObject json = Json.createObjectBuilder()
                                .add("game_id", gid)
                                .add("name", name)
                                .add("year", year)
                                .add("rank", ranking)
                                .add("users_rated", users_rated)
                                .add("url", url)
                                .add("thumbnail", gid)
                                .add("reviews", Json.createArrayBuilder(reviews))
                                .add("timestamp", new Date().toString())
                                .build();
        return json;
    }

    public String getReviewByGameId(String game_id) {
        List<Document> resultList = rRepo.getReviewByGameId(game_id);
        
        if (resultList.get(0) == null) {
            return null;
        }
        
        Document result = resultList.get(0);
        int gid = result.getInteger("gid");
        String name = result.getString("name");
        int year = result.getInteger("year");
        int ranking = result.getInteger("ranking");
        int users_rated = result.getInteger("users_rated");
        String url = result.getString("url");
        String image = result.getString("image");
        List<String> reviews = new ArrayList<>();

        for (ObjectId reviewId: result.getList("_id", ObjectId.class)) {
            reviews.add(reviewId.toString());
        }

        JsonObject reviewJson = buildJson(gid, name, year, ranking, users_rated, url, image, reviews);

        return reviewJson.toString();
    }

}
