# recommendation-ws
RESTful recommendation logic for recommending activities and restaurants.

Budiness logic service that is defined by the following recommendation rules:
* always recommend 5 items
* recommend to new users with no ratings and no preference the top rated items -> TODO: implement listActivitiesWithCount and listRestaurantsWithCount
* recommend to new user with no ratings (3 preference at least) -> item based recommendation
* recommend to existing users with rating -> user based recommendation
* recommend activity -> filter="activity" in 'type'
* recommend restaurant -> filter="restaurant" in 'type'
* recommend item based on location
* recommend item based on topic