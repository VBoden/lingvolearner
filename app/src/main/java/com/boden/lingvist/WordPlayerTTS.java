package com.boden.lingvist;

import android.content.Context;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class WordPlayerTTS implements WordPlayer, TextToSpeech.OnInitListener {

    private final Context context;
    private final SharedPreferences settings;

    private TextToSpeech mTts;

    public WordPlayerTTS(Context context, SharedPreferences settings) {
        this.context = context;
        this.settings = settings;
        init();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
           // init();
        } else {
            // Log.e(TAG, "Could not initialize TextToSpeech.");
        }
        // status =mTts.setEngineByPackageName("edu.cmu.cs.speech.tts.flite");
    }

    @Override
    public void init() {
        int usedTts = settings.getInt(Constants.USED_TTS, 1);
        if (usedTts == Constants.USE_GOOGLE_TTS) {
            mTts = new TextToSpeech(context, this);
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                mTts = new TextToSpeech(context, this,
                        "edu.cmu.cs.speech.tts.flite");
            }
        }
        String language = settings.getString(Constants.LANGUAGE, Locale.US.getLanguage());
        updateLanguageSelection(language);
    }

    @Override
    public void updateLanguageSelection(String language) {
        //System.out.println("=====LLocale.forLanguageTag(language): "+Locale.forLanguageTag(language));
        //System.out.println("=======Language is not : "+language);
        //System.out.println("=======Languages : "+mTts.getAvailableLanguages());
        int result = mTts.setLanguage(Locale.forLanguageTag(language));
        if (result == TextToSpeech.LANG_MISSING_DATA
                || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            System.out.println("=-=-Language is not available: "+language);
            // Log.e(TAG, "Language is not available.");
        } else {
            // soundButton.setEnabled(true);
        }
    }

    @Override
    public void playWord(String word) {
        mTts.speak(word, TextToSpeech.QUEUE_FLUSH, null,null);
    }

    @Override
    public void destroy() {
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
    }
}
