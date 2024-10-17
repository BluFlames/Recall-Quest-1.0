package com.example.recallquest10;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class EasyMode extends AppCompatActivity {

    private final FrameLayout[] frontCards = new FrameLayout[16];
    private final FrameLayout[] backCards = new FrameLayout[16];
    private final boolean[] isFrontVisible = new boolean[16];
    private final int[] randomNumbers = new int[16]; // Expanded to 16 for shuffling

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a4_easy_mode);

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
        // Generate 8 random numbers
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

        // Assign random numbers to the first 8 cards
        for (int i = 0; i < 8; i++) {
            backTexts[i].setText(String.valueOf(randomNumbers[i]));
        }

        // Assign matching numbers to the next 8 cards
        for (int i = 8; i < 16; i++) {
            backTexts[i].setText(String.valueOf(randomNumbers[i - 8]));
        }
    }

    // Generate 8 random numbers and shuffle for 16 positions
    private void generateRandomNumbers() {
        Random random = new Random();
        Integer[] numbers = new Integer[8];

        // Generate 8 random numbers
        for (int i = 0; i < 8; i++) {
            numbers[i] = random.nextInt(100000); // Random numbers between 0 and 99
        }

        // Duplicate the numbers for matching
        for (int i = 0; i < 8; i++) {
            randomNumbers[i] = numbers[i];
            randomNumbers[i + 8] = numbers[i]; // Matching number
        }

        // Shuffle the randomNumbers array to randomize the card positions
        List<Integer> shuffledNumbers = Arrays.asList(numbers);
        Collections.shuffle(Collections.singletonList(randomNumbers));
    }

    // Set click listeners to handle card flips
    private void setCardClickListeners() {
        for (int i = 0; i < 16; i++) {
            final int index = i; // Create a final variable for use in the listener
            for (FrameLayout frameLayout : Arrays.asList(frontCards[i], backCards[i])) {
                frameLayout.setOnClickListener(v -> flipCard(index));
            }
            isFrontVisible[i] = true; // Initialize to show the front card
        }
    }

    // Flip card logic
    private void flipCard(int index) {
        AnimatorSet frontAnim = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.front_flip);
        AnimatorSet backAnim = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.back_flip);

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
    }
}
