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

public class Gameplay extends AppCompatActivity {

    private final FrameLayout[] frontCards = new FrameLayout[16];
    private final FrameLayout[] backCards = new FrameLayout[16];
    private final boolean[] isFrontVisible = new boolean[16];
    private final int[] randomNumbers = new int[8];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a4_easy_mode);

        // Initialize random numbers
        generateRandomNumbers();

        // Initialize all front and back cards
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

        // Assign random numbers to the TextViews in the back cards
        assignRandomNumbersToBackCards();

        // Set click listeners for each card
        for (int i = 0; i < 16; i++) {
            final int index = i; // Create a final variable for use in the listener
            for (FrameLayout frameLayout : Arrays.asList(frontCards[i], backCards[i])) {
                frameLayout.setOnClickListener(v -> flipCard(index));
            }
            isFrontVisible[i] = true; // Initialize to show the front card
        }
    }

    // Method to generate random numbers
    private void generateRandomNumbers() {
        Random random = new Random();
        Integer[] numbers = new Integer[8];

        for (int i = 0; i < 8; i++) {
            numbers[i] = random.nextInt(100); // Generate random numbers from 0 to 99
        }

        List<Integer> shuffledNumbers = Arrays.asList(numbers);
        Collections.shuffle(shuffledNumbers);

        for (int i = 0; i < 8; i++) {
            randomNumbers[i] = shuffledNumbers.get(i);
        }
    }

    // Method to assign random numbers to back cards
    private void assignRandomNumbersToBackCards() {
        Random random = new Random();
        int[] randomNumbers = new int[8];

        // Generate 8 random numbers
        for (int i = 0; i < 8; i++) {
            randomNumbers[i] = random.nextInt(100); // Random number between 0 and 99
        }

        // Assign the random numbers to backText for first 8 cards
        for (int i = 0; i < 8; i++) {
            TextView backCardText = backCards[i].findViewById(R.id.backText1 + i);  // Adjust accordingly
            backCardText.setText(String.valueOf(randomNumbers[i]));
        }

        // Assign the same random numbers to remaining cards for matching
        for (int i = 8; i < 16; i++) {
            TextView backCardText = backCards[i].findViewById(R.id.backText9 + i); // Adjust accordingly
            backCardText.setText(String.valueOf(randomNumbers[i - 8]));
        }
    }


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
