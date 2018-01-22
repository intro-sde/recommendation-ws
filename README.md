# recommendation-ws
RESTful recommendation logic for recommending activities and restaurants.

Budiness logic service that is defined by the following recommendation rules:
* we always recommend 5 items
* when the user has no ratings we use item based recommendation based on one random item from the user's preferences, and if they don't have preference we just give 5 random items with given type and city 
* when the user has ratings we use user based recommendation with respect to user's ratings and preferences
* we always take city and activity type into consideration

The following methods for the different endpoints are available in this service:

https://sde-recommendation-ws.herokuapp.com/:

- /recommend @GET |[userId, type, city] | Returns 5 recommended items according to the rules above.

Reference: Recombee API (version 1.6.0), Available at: https://docs.recombee.com/api.html
