package com.andreas.rockpaperscissors.model;

public interface RoundObserver {
    void draw();
    void victory(int roundScore, int totalScore);
    void loss();
}
