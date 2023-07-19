package sg.edu.nus.iss.workshop28.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sg.edu.nus.iss.workshop28.service.ReviewService;

@RestController
@RequestMapping
public class ReviewController {

    @Autowired
    ReviewService rService;
    
    /**
     *  GET /game/<game_id>/reviews
        Accept: application/json
     */
    @GetMapping(path="/game/{game_id}/reviews", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getReviewByGameId(@PathVariable String game_id) {

        String result = rService.getReviewByGameId(game_id);
        
        if (result == null) {
            return ResponseEntity.status(400).body("Game ID provided does not exist");
        }

        return ResponseEntity.status(200).body(result);
    }

    /**
     *  GET /games/highest
        Accept: application/json
     */
    @GetMapping(path="/games/highest", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getReviewByHighestRating(@RequestParam String limit) {
        String jsonString = rService.getReviewByRating(limit, "highest");

        if (jsonString == null) {
            return ResponseEntity.status(400).body("Bad request");
        }

        return ResponseEntity.status(200).body(jsonString);        
    }


    /**
     *  GET /games/lowest
        Accept: application/json
    */
    @GetMapping(path="/games/lowest", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getReviewByLowestRating(@RequestParam String limit) {
        String jsonString = rService.getReviewByRating(limit, "lowest");

        if (jsonString == null) {
            return ResponseEntity.status(400).body("Bad request");
        }

        return ResponseEntity.status(200).body(jsonString);        
    }
}
