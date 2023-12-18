package com.boden.lingvolearner.services;

public interface WordPlayer {

    void updateLanguageSelection(String language);

    void playWord(String word);

    void destroy();
}
