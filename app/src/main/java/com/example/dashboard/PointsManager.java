package com.example.dashboard;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Manages points for the Who's Who game
 * Handles earning, storing, and retrieving points
 */
public class PointsManager {

    private static final String PREFS_NAME = "WhosWhoPrefs";
    private static final String KEY_TOTAL_POINTS = "total_points";
    private static final String KEY_DATE_PREFIX = "date_";
    private static final String KEY_STREAK = "current_streak";
    private static final int POINTS_PER_CORRECT_ANSWER = 10;

    /**
     * Add points for a correct answer
     * @param context Application context
     * @return The total points earned for the day after adding
     */
    public static int addPoints(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Get today's date as string
        String today = getTodayString();

        // Get current points for today
        int dailyPoints = prefs.getInt(KEY_DATE_PREFIX + today, 0);
        dailyPoints += POINTS_PER_CORRECT_ANSWER;

        // Get total lifetime points
        int totalPoints = prefs.getInt(KEY_TOTAL_POINTS, 0);
        totalPoints += POINTS_PER_CORRECT_ANSWER;

        // Update streak
        updateStreak(context);

        // Save updated points
        editor.putInt(KEY_DATE_PREFIX + today, dailyPoints);
        editor.putInt(KEY_TOTAL_POINTS, totalPoints);
        editor.apply();

        return dailyPoints;
    }

    /**
     * Get today's total points
     * @param context Application context
     * @return Points earned today
     */
    public static int getDailyPoints(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String today = getTodayString();
        return prefs.getInt(KEY_DATE_PREFIX + today, 0);
    }

    /**
     * Get total lifetime points
     * @param context Application context
     * @return Total points earned
     */
    public static int getTotalPoints(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_TOTAL_POINTS, 0);
    }

    /**
     * Get current streak (consecutive days with points)
     * @param context Application context
     * @return Current streak in days
     */
    public static int getCurrentStreak(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_STREAK, 0);
    }

    /**
     * Update the current streak
     * @param context Application context
     */
    private static void updateStreak(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String today = getTodayString();
        String yesterday = getYesterdayString();

        // Check if user earned points yesterday
        boolean hadPointsYesterday = prefs.getInt(KEY_DATE_PREFIX + yesterday, 0) > 0;
        int currentStreak = prefs.getInt(KEY_STREAK, 0);

        // If this is first time earning points today
        if (prefs.getInt(KEY_DATE_PREFIX + today, 0) == 0) {
            if (hadPointsYesterday) {
                // Continue streak
                currentStreak++;
            } else {
                // Reset streak
                currentStreak = 1;
            }
            editor.putInt(KEY_STREAK, currentStreak);
            editor.apply();
        }
    }

    /**
     * Get today's date as a string
     * @return Date string in format "yyyy-MM-dd"
     */
    private static String getTodayString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return dateFormat.format(new Date());
    }

    /**
     * Get yesterday's date as a string
     * @return Date string in format "yyyy-MM-dd"
     */
    private static String getYesterdayString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date yesterday = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        return dateFormat.format(yesterday);
    }
}
