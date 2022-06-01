package com.boden.lingvist;

public interface WordPlayer {

    void init();

    void updateLanguageSelection(String language);

    void playWord(String word);

    void destroy();
}
