package com.boden.lingvolearner;

import java.io.IOException;

import com.boden.lingvolearner.services.MediaFilesPlayer;

import android.media.MediaPlayer;

public class AndroidMediaFilesPlayer implements MediaFilesPlayer {
	
	private MediaPlayer mediaPlayer = new MediaPlayer();

	@Override
	public void play(String filePath) {
		try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
