package sg.edu.nus.iss.workshop28.repository;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
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

    /**
     * db.getCollection('games').aggregate(
        [
            { $limit: 5 },
            {
            $lookup: {
                from: 'comments',
                localField: 'gid',
                foreignField: 'gid',
                as: 'review'
            }
            },
            { $unwind: { path: '$review' } },
            { $sort: { 'review.rating': 1 } },
            {
            $group: {
                _id: '$gid',
                name: { $first: '$name' },
                rating: { $first: '$review.rating' },
                user: { $first: '$review.user' },
                comment: { $first: '$review.c_text' },
                review_id: { $first: '$review._id' }
            }
            },
            { $sort: { _id: 1 } },
            {
            $project: {
                _id: 1,
                name: 1,
                rating: 1,
                user: 1,
                comment: 1,
                review_id: 1
            }
            }
        ]
        );
     */

    public List<Document> getReviewByHighestRating(String limit) {
        Long longLimit = Long.parseLong(limit);
        LimitOperation limitOp = Aggregation.limit(longLimit);
        LookupOperation lookupOp = Aggregation.lookup("comments", "gid", "gid", "review");
        UnwindOperation unwindOp = Aggregation.unwind("review");
        SortOperation sortOp = Aggregation.sort(Direction.DESC, "review.rating");
        GroupOperation groupOp = Aggregation.group("gid")
                                                .first("name").as("name")
                                                .first("review.rating").as("rating")
                                                .first("review.user").as("user")
                                                .first("review.c_text").as("comment")
                                                .first("review._id").as("review_id");
        SortOperation sortOp2 = Aggregation.sort(Direction.ASC, "_id");
        Aggregation pipeline = Aggregation.newAggregation(limitOp, lookupOp, unwindOp, sortOp, groupOp, sortOp2);

        return mTemplate.aggregate(pipeline, C_GAMES, Document.class).getMappedResults();
    }

    
}
