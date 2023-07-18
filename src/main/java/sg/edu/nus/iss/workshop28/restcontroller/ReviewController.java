package sg.edu.nus.iss.workshop28.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sg.edu.nus.iss.workshop28.service.ReviewService;

@RestController
@RequestMapping
public class ReviewController {

    @Autowired
    ReviewService rService;
    
    @GetMapping(path="/game/{game_id}/reviews", produces=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> getReviewByGameId(@PathVariable String game_id) {
        
        return ResponseEntity.ok(rService.getReviewByGameId(game_id));
    }

    /**
     *  GET /game/<game_id>/reviews
        Accept: application/json
     */
}
