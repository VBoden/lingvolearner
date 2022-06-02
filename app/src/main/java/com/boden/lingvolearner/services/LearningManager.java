package com.boden.lingvolearner.services;

import static com.boden.lingvolearner.services.ContextHolder.getLearningManager;
import static com.boden.lingvolearner.services.ContextHolder.getSettingsHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import com.boden.lingvolearner.MainFormActivity;
import com.boden.lingvolearner.WordSpeaker;
import com.boden.lingvolearner.pojo.WordCard;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class LearningManager {

	private static final int WORDS_IN_CYCLE = 10;
	private Stage currentStage;
//	private AbstractStrategy currentStrategy;
	private Map<Stage, AbstractStrategy> stagesStrategies;
	private WordSpeaker speaker;

	private List<WordCard> allWordCards;
	private int currentCartdNum;

	private int rozmir_mas, krok_povtory, k_zal_sliv;
	private int repeatCount;
	private int kilk[] = new int[10];
	private String[] spisok = new String[WORDS_IN_CYCLE];
	private Random rnd = new Random();

	public LearningManager(List<WordCard> allWordCards) {
		this.allWordCards = allWordCards;
		this.speaker = ContextHolder.getWordSpeaker();
		rozmir_mas = allWordCards.size();
		createStagesMap();
		currentStage = Stage.FOREIGN_TO_NATIVE;
		repeatCount = getSettingsHolder().getRepeatCount();

//		currentStrategy = getCurrentStrategy();
	}

	private void createStagesMap() {
		stagesStrategies = new HashMap<>();
		stagesStrategies.put(Stage.FOREIGN_TO_NATIVE, new ForeignToNativeStrategy());
		stagesStrategies.put(Stage.NATIVE_TO_FOREIGN, new NativeToForeignStrategy());
		stagesStrategies.put(Stage.WRITING_WORDS, new WritingWordsStrategy());
	}

	public AbstractStrategy getCurrentStrategy() {
		return stagesStrategies.get(currentStage);
	}

	public int getTotalWordsCount() {
		return allWordCards.size();
	}

	public void playOnClick() {
		if (getCurrentStrategy().needPlayOnClick()) {
			speaker.speakText(getCurrentCard().getWord());
		}
	}

	public WordCard getCurrentCard() {
		return allWordCards.get(currentCartdNum);
	}

	public void startLearning() {
		currentStage = Stage.getFirst();
		startNewStage();
		ContextHolder.getUiUpdator(currentStage).startLearningUpdateUI();
	}

	public void startNewStage() {
		krok_povtory = repeatCount;
		resetCycle();
		changeCurrentCardNum();
		updateWordChoices();
		if (Objects.nonNull(ContextHolder.getUiUpdator(currentStage))) {
			ContextHolder.getUiUpdator(currentStage).updateOnStageStart();
		}
	}

	public boolean startNextStage() {
		boolean startedFromBegining = false;
		if (currentStage.isLast()) {
			int startFrom = ContextHolder.getSettingsHolder().getStartFromNumber();
			if (startFrom == rozmir_mas - WORDS_IN_CYCLE) {
				startFrom = 0;
				startedFromBegining = true;
			} else {
				startFrom = Math.min(startFrom + WORDS_IN_CYCLE, rozmir_mas - WORDS_IN_CYCLE);
			}
			ContextHolder.getSettingsHolder().updateStartNumber(startFrom);
		}
		if (currentStage.getNext().isLast()) {
			ContextHolder.getUiUpdator(currentStage).createNewActivity();
		}
		currentStage = currentStage.getNext();
		startNewStage();
		return startedFromBegining;
	}

	public void startPreviousStage() {
		if (currentStage.isFirst()) {
			int startFrom = ContextHolder.getSettingsHolder().getStartFromNumber();
			startFrom = Math.max(startFrom - WORDS_IN_CYCLE, 0);
			ContextHolder.getSettingsHolder().updateStartNumber(startFrom);
		}
		if (currentStage.isFirst()) {
			ContextHolder.getUiUpdator(currentStage).createNewActivity();
		}
		currentStage = currentStage.getPrevious();
		startNewStage();
	}

	public boolean checkAnswer(String answer) {
		int startFrom = ContextHolder.getSettingsHolder().getStartFromNumber();
		if (answer.equals(getCurrentStrategy().getWordToCheck(getCurrentCard()))) {
			k_zal_sliv--;
			kilk[currentCartdNum - startFrom]++;
			if (k_zal_sliv == 0) {
				krok_povtory--;
				resetCycle();
			}
			if (krok_povtory > 0) {
//				setupNextWord();
				changeCurrentCardNum();// !!!!!!!!!!
				// here need update word on ui
				ContextHolder.getUiUpdator(currentStage).updateWord();
			} else {
				if (currentStage.getNext().isLast()) {
					ContextHolder.getUiUpdator(currentStage).createNewActivity();
				}
				startNextStage();
				// jButton13.setEnabled(true);
//				Functions2_1();
				// need update full ui or start new activity for writing
			}
		} else {
			k_zal_sliv++;
			kilk[currentCartdNum - startFrom]--;
			// showDialog(0);
			return false;
		}
		return true;
	}

	private void changeCurrentCardNum() {
		int startFromNumber = getStartFromNumber();
		do {
			currentCartdNum = startFromNumber + rnd.nextInt(100) % 10;
		} while (kilk[currentCartdNum - startFromNumber] >= 1);
	}

	private int getStartFromNumber() {
		return getSettingsHolder().getStartFromNumber();
	}

	private void resetCycle() {
		k_zal_sliv = WORDS_IN_CYCLE;
		for (int i = 0; i < WORDS_IN_CYCLE; i++) {
			kilk[i] = 0;
		}
	}

	public void updateWordChoices() {
		for (int i = 0; i < WORDS_IN_CYCLE; i++) {
			spisok[i] = getCurrentStrategy().getWordToCheck(allWordCards.get(getStartFromNumber() + i));
		}
	}

	public String getWordToDisplay() {
		return getCurrentStrategy().getWordToDisplay(getCurrentCard());
	}

	public String getWordTranscription() {
		return getCurrentStrategy().getWordTranscription(getCurrentCard());
	}

	public String getWordAnswer() {
		return getCurrentStrategy().getWordToCheck(getCurrentCard());
	}

	public String[] getWordChoices() {
		return spisok;
	}

	public Stage getCurrentStage() {
		return currentStage;
	}

	public boolean hasPreviousStep() {
		return !(ContextHolder.getSettingsHolder().getStartFromNumber() == 0 && currentStage.isFirst());
	}
}
