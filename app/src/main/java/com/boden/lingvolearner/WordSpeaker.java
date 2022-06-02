package com.boden.lingvolearner;

import static com.boden.lingvolearner.services.ContextHolder.getSettingsHolder;

import java.io.File;
import java.io.IOException;

import com.boden.lingvolearner.services.ContextHolder;

import android.media.MediaPlayer;

public class WordSpeaker {


    public void speakText(String message) {
        System.out.println("=======speaking : "+message);
        String pathToSoundFiles = getSettingsHolder().getPathToSoundFiles();
        String wordFilePath = pathToSoundFiles + message.charAt(0) + "/"
                + message + ".wav";
        boolean useFilesToSay = getSettingsHolder().isUseFilesToSay();
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
        boolean useTtsToSay = getSettingsHolder().isUseTtsToSay();
        if (useTtsToSay && !filePlayed) {
            ContextHolder.getWordPlayer().playWord(message);
        }
    }

    public void updateLanguageSelection(String language) {
    	for(WordPlayer player : ContextHolder.getAllWordPlayer()) {
    		player.updateLanguageSelection(language);
    	}
    }

    public void destroy() {
    	for(WordPlayer player : ContextHolder.getAllWordPlayer()) {
    		if (player != null) {
    			player.destroy();
    		}
    	}
    }
}
