package com.boden.lingvist;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

import java.io.File;
import java.io.IOException;

import static com.boden.lingvist.Constants.USE_TTS_TO_SAY;

public class WordSpeaker {

    private final SharedPreferences settings;

    private final WordPlayer mTts;

    public WordSpeaker(Context context, SharedPreferences settings){
        this.settings = settings;
        mTts = new WordPlayerTTS(context, settings);
    }

    public void speakText(String message) {
        System.out.println("=======speaking : "+message);
        String pathToSoundFiles = settings.getString(Constants.PATH_TO_SOUND_FILES, "");
        String wordFilePath = pathToSoundFiles + message.charAt(0) + "/"
                + message + ".wav";
        boolean useFilesToSay = settings.getBoolean(Constants.USE_FILES_TO_SAY, true);
        if (useFilesToSay) {
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(wordFilePath);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        boolean filePlayed = useFilesToSay && (new File(wordFilePath)).exists();
        boolean useTtsToSay = settings.getBoolean(USE_TTS_TO_SAY, true);
        if (useTtsToSay && !filePlayed) {
            mTts.playWord(message);
        }
    }

    public void updateLanguageSelection(String language) {
        mTts.updateLanguageSelection(language);
    }

    public void destroy() {
        if (mTts != null) {
            mTts.destroy();
        }
    }
}
