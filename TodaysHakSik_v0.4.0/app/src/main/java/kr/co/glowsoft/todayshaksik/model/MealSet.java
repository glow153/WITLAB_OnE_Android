package kr.co.glowsoft.todayshaksik.model;

import java.util.ArrayList;

/**
 * Created by Jake Park on 2017-12-11.
 */

public class MealSet {
    private MealSet() {}
    private static class Singleton {
        private static MealSet instance = new MealSet();
    }
    public static MealSet getInstance() {
        return Singleton.instance;
    }

    private ArrayList<Meal> meallistStudent;
    private ArrayList<Meal> meallistEmployee;
    private ArrayList<Meal> meallistDorm;
}
