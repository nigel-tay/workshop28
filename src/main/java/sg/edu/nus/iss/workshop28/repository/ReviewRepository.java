package sg.edu.nus.iss.workshop28.repository;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

@Repository
public class ReviewRepository {

    private final String C_GAMES = "games";

    private final String F_GID = "gid";
    private final String F_COMMENTS = "comments";
    private final String F_REVIEWS = "reviews";
    
    @Autowired
    MongoTemplate mTemplate;

    /**
     * db.getCollection('games').aggregate(
        [
            { $match: { gid: 90 } },
            {
            $lookup: {
                from: 'comments',
                localField: 'gid',
                foreignField: 'gid',
                as: 'reviews'
            }
            },
            {
            $project: {
                game_id: '$reviews.gid',
                name: 1,
                year: 1,
                rank: '$ranking',
                users_rated: 1,
                url: 1,
                thumbnail: '$image',
                reviews: '$reviews._id'
            }
            }
        ]
        );
    */
    public List<Document> getReviewByGameId(String gameId) {
        int intGameId = Integer.parseInt(gameId);
        Criteria criteria = Criteria.where(F_GID).is(intGameId);
        MatchOperation mo = Aggregation.match(criteria);
        LookupOperation lo = Aggregation.lookup(F_COMMENTS, F_GID, F_GID, F_REVIEWS);
        ProjectionOperation po = Aggregation.project("gid", "name", "year", "ranking", "users_rated", "url", "image", "reviews._id");
        Aggregation pipeline = Aggregation.newAggregation(mo, lo, po);
        AggregationResults<Document> results = mTemplate.aggregate(pipeline, C_GAMES, Document.class);

        return results.getMappedResults();
    }

    
}
