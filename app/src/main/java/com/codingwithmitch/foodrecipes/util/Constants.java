package com.codingwithmitch.foodrecipes.util;

public class Constants {

    public static final String BASE_URL = "https://www.food2fork.com";
    public static final long RECIPE_REFRESH_TIME = 60 * 60 * 24 * 30; // 30 days to refresh recipe
    public static final int PAGINATION_NUMBER = 30;

    // YOU NEED YOUR OWN API KEY!!!!!!!!!!!!! https://www.food2fork.com/about/api
    public static final String API_KEY = "dadc63b6325aaf398163b40fea9b5e79";


    public static final String[] DEFAULT_SEARCH_CATEGORIES =
            {"Barbeque", "Breakfast", "Chicken", "Beef", "Brunch", "Dinner", "Wine", "Italian"};

    public static final String[] DEFAULT_SEARCH_CATEGORY_IMAGES =
            {
                    "barbeque",
                    "breakfast",
                    "chicken",
                    "beef",
                    "brunch",
                    "dinner",
                    "wine",
                    "italian"
            };
}
