package com.example.recallquest10;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.CountDownTimer;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EasyMode extends AppCompatActivity {

    private final FrameLayout[] frontCards = new FrameLayout[16];
    private final FrameLayout[] backCards = new FrameLayout[16];
    private final boolean[] isFrontVisible = new boolean[16];
    private final boolean[] isMatched = new boolean[16]; // Track matched cards
    private final int[] randomNumbers = new int[16]; // Expanded to 16 for shuffling
    private final List<Integer> flippedCards = new ArrayList<>(); // Track flipped cards
    private CountDownTimer countDownTimer; // Timer variable
    private TextView timerText; // TextView for displaying timer
    private GameDatabaseHelper dbHelper; // To store data in SQLite
    private long currentSessionId; // To track the current game session ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a4_easy_mode);

        dbHelper = new GameDatabaseHelper(this);

        startNewGameSession();

        timerText = findViewById(R.id.timerText); // Link timer TextView

        // Start 30-second timer
        startTimer();
        // Initialize all front and back cards
        initializeCards();

        // Generate and assign random numbers to the cards
        assignRandomNumbersToBackCards();

        // Set click listeners for each card
        setCardClickListeners();
    }

    // Initialize all front and back cards
    private void initializeCards() {
        frontCards[0] = findViewById(R.id.front1);
        backCards[0] = findViewById(R.id.back1);
        frontCards[1] = findViewById(R.id.front2);
        backCards[1] = findViewById(R.id.back2);
        frontCards[2] = findViewById(R.id.front3);
        backCards[2] = findViewById(R.id.back3);
        frontCards[3] = findViewById(R.id.front4);
        backCards[3] = findViewById(R.id.back4);
        frontCards[4] = findViewById(R.id.front5);
        backCards[4] = findViewById(R.id.back5);
        frontCards[5] = findViewById(R.id.front6);
        backCards[5] = findViewById(R.id.back6);
        frontCards[6] = findViewById(R.id.front7);
        backCards[6] = findViewById(R.id.back7);
        frontCards[7] = findViewById(R.id.front8);
        backCards[7] = findViewById(R.id.back8);
        frontCards[8] = findViewById(R.id.front9);
        backCards[8] = findViewById(R.id.back9);
        frontCards[9] = findViewById(R.id.front10);
        backCards[9] = findViewById(R.id.back10);
        frontCards[10] = findViewById(R.id.front11);
        backCards[10] = findViewById(R.id.back11);
        frontCards[11] = findViewById(R.id.front12);
        backCards[11] = findViewById(R.id.back12);
        frontCards[12] = findViewById(R.id.front13);
        backCards[12] = findViewById(R.id.back13);
        frontCards[13] = findViewById(R.id.front14);
        backCards[13] = findViewById(R.id.back14);
        frontCards[14] = findViewById(R.id.front15);
        backCards[14] = findViewById(R.id.back15);
        frontCards[15] = findViewById(R.id.front16);
        backCards[15] = findViewById(R.id.back16);
    }

    // Assign random numbers to the TextViews in the back cards
    private void assignRandomNumbersToBackCards() {
        // Generate and shuffle random numbers
        generateRandomNumbers();

        // Get TextViews from back cards
        TextView[] backTexts = new TextView[16];
        backTexts[0] = findViewById(R.id.backText1);
        backTexts[1] = findViewById(R.id.backText2);
        backTexts[2] = findViewById(R.id.backText3);
        backTexts[3] = findViewById(R.id.backText4);
        backTexts[4] = findViewById(R.id.backText5);
        backTexts[5] = findViewById(R.id.backText6);
        backTexts[6] = findViewById(R.id.backText7);
        backTexts[7] = findViewById(R.id.backText8);
        backTexts[8] = findViewById(R.id.backText9);
        backTexts[9] = findViewById(R.id.backText10);
        backTexts[10] = findViewById(R.id.backText11);
        backTexts[11] = findViewById(R.id.backText12);
        backTexts[12] = findViewById(R.id.backText13);
        backTexts[13] = findViewById(R.id.backText14);
        backTexts[14] = findViewById(R.id.backText15);
        backTexts[15] = findViewById(R.id.backText16);

        // Assign random numbers to the cards
        for (int i = 0; i < 16; i++) {
            backTexts[i].setText(String.valueOf(randomNumbers[i]));
        }
    }

    // Generate 8 random numbers and shuffle for 16 positions
    private void generateRandomNumbers() {
        Random random = new Random();
        Integer[] numbers = new Integer[8];

        // Generate 8 random numbers
        for (int i = 0; i < 8; i++) {
            numbers[i] = random.nextInt(100); // Random numbers between 0 and 99999
        }

        // Duplicate the numbers for matching
        for (int i = 0; i < 8; i++) {
            randomNumbers[i] = numbers[i];
            randomNumbers[i + 8] = numbers[i]; // Matching number
        }

        // Shuffle the randomNumbers array to randomize the card positions
        List<Integer> numberList = Arrays.asList(Arrays.stream(randomNumbers).boxed().toArray(Integer[]::new));
        Collections.shuffle(numberList);
        for (int i = 0; i < numberList.size(); i++) {
            randomNumbers[i] = numberList.get(i);
        }
    }

    // Set click listeners to handle card flips
    private void setCardClickListeners() {
        for (int i = 0; i < 16; i++) {
            final int index = i; // Create a final variable for use in the listener
            frontCards[i].setOnClickListener(v -> flipCard(index));
            backCards[i].setOnClickListener(v -> flipCard(index));
            isFrontVisible[i] = true; // Initialize to show the front card
        }
    }

    // Flip card logic
    private void flipCard(int index) {
        // Check if the card is already matched to avoid flipping it again
        if (isMatched[index] || flippedCards.contains(index)) {
            return; // Do nothing if the card is already matched or already flipped
        }

        AnimatorSet frontAnim = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.front_flip);
        AnimatorSet backAnim = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.back_flip);

        // Show the back of the card
        if (isFrontVisible[index]) {
            frontAnim.setTarget(frontCards[index]);
            backAnim.setTarget(backCards[index]);
            frontCards[index].setVisibility(View.GONE);
            backCards[index].setVisibility(View.VISIBLE);
        } else {
            frontAnim.setTarget(backCards[index]);
            backAnim.setTarget(frontCards[index]);
            backCards[index].setVisibility(View.GONE);
            frontCards[index].setVisibility(View.VISIBLE);
        }
        frontAnim.start();
        backAnim.start();

        isFrontVisible[index] = !isFrontVisible[index]; // Toggle visibility state

        // Add the card to the list of flipped cards only if it’s not already matched
        if (!isMatched[index]) {
            flippedCards.add(index);
        }

        // Check for a match if two cards are flipped
        if (flippedCards.size() == 2) {
            checkForMatch();
        }
        recordCardFlip(index);
    }

    // Start 30-second countdown timer
    private void startTimer() {
        countDownTimer = new CountDownTimer(100000, 1000) { // 30 seconds with 1-second intervals

            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText("Time: " + millisUntilFinished / 1000); // Update timer display
            }

            @Override
            public void onFinish() {
                endGame(); // End game when timer finishes
            }
        };
        countDownTimer.start();
    }

    // End game logic (timer expired)
    private void endGame() {
        Toast.makeText(EasyMode.this, "Time's up! Game Over.", Toast.LENGTH_LONG).show();
        // Optionally, navigate to a result screen or reset the game
        finish(); // Close the activity or reset the game
    }

    // Call this method when game is won to stop the timer
    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel(); // Stop the timer if game is completed early
        }
    }

    // Check if the flipped cards match
    private void checkForMatch() {
        int card1 = flippedCards.get(0);
        int card2 = flippedCards.get(1);

        if (randomNumbers[card1] == randomNumbers[card2]) {
            // Lock matched cards
            isMatched[card1] = true;
            isMatched[card2] = true;

            // Clear flipped cards list for the next pair
            flippedCards.clear();

            // Check if all cards are matched
            if (allCardsMatched()) {
                stopTimer(); // Stop the timer as the game is completed
                Toast.makeText(EasyMode.this, "Congratulations! You've completed the game!", Toast.LENGTH_LONG).show();
            }
        } else {
            // Flip back the cards after a short delay
            backCards[card1].postDelayed(() -> {
                flipBack(card1);
                flipBack(card2);
                flippedCards.clear(); // Clear flipped cards list after flipping back
            }, 500); // Delay in milliseconds to show the card briefly before flipping back
        }
    }

    // Helper function to check if all cards are matched
    private boolean allCardsMatched() {
        for (boolean matched : isMatched) {
            if (!matched) return false;
        }
        return true;
    }

    // Flip the card back to its original position if it’s not matched
    private void flipBack(int index) {
        if (isMatched[index]) {
            return; // Don't flip back if the card is already matched
        }

        AnimatorSet frontAnim = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.front_flip);
        AnimatorSet backAnim = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.back_flip);

        if (!isFrontVisible[index]) {
            frontAnim.setTarget(backCards[index]);
            backAnim.setTarget(frontCards[index]);
            backCards[index].setVisibility(View.GONE);
            frontCards[index].setVisibility(View.VISIBLE);
        }
        frontAnim.start();
        backAnim.start();
        isFrontVisible[index] = true; // Set to front visibility
    }

    private void startNewGameSession() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(GameDatabaseHelper.COLUMN_MODE, "Easy");
        values.put(GameDatabaseHelper.COLUMN_DATE, new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        values.put(GameDatabaseHelper.COLUMN_TIME, new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()));
        currentSessionId = db.insert(GameDatabaseHelper.TABLE_NAME, null, values); // Store the session ID
        db.close();
    }


    private void recordCardFlip(int cardNumber) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Ensure the card index is valid and matches the database schema
        if (cardNumber < 0 || cardNumber >= GameDatabaseHelper.CLICK_COLUMNS.length) {
            throw new IllegalArgumentException("Invalid card number: " + cardNumber);
        }

        // Get the column corresponding to the card's flip count
        String column = GameDatabaseHelper.CLICK_COLUMNS[cardNumber];

        // Update the flip count for the current session
        String query = "UPDATE " + GameDatabaseHelper.TABLE_NAME +
                " SET " + column + " = " + column + " + 1 WHERE " +
                GameDatabaseHelper.COLUMN_SESSION_ID + " = ?";
        db.execSQL(query, new String[]{String.valueOf(currentSessionId)});

        db.close(); // Close the database connection
    }
}
