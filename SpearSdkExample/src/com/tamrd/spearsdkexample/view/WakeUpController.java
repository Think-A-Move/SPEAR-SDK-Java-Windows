package com.tamrd.spearsdkexample.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Button;
import javafx.fxml.Initializable;

import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.scene.control.TextField;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.ArrayList;

import com.tamrd.spearsdkexample.SpearDemoMain;
import com.tamrd.spearsdkexample.commandList.CommandListFactory;
import com.tamrd.spearsdkexample.commandList.DemoCommandList;
import com.tamrd.spearsdkexample.commandList.LabelCommandList;
import com.tamrd.spearsdkexample.spear.SpearSdkGateway;
import com.thinkamove.spearnative.RegistrationMode;
import com.thinkamove.spearnative.SpearWakeUpResult;
import com.thinkamove.spearnative.TamTranscriptionPair;
import com.thinkamove.spearnative.TamVadPair;
import com.thinkamove.spearnative.recognizer.ProcessMode;
import com.thinkamove.spearnative.spear.api.RecognitionListener;
import com.thinkamove.spearnative.spear.api.SpearInitListener;
import com.thinkamove.spearnative.spear.api.SpearWakeUpListener;
import com.thinkamove.spearnative.spear.api.UnInitializeException;
import com.thinkamove.spearnative.spear.api.VadListener;
import com.thinkamove.spearnative.spearConfig.SpearConfigUpdateListener;
import com.thinkamove.spearnative.speechType.commands.api.CommandList;

public class WakeUpController implements Initializable, SpearInitListener, VadListener, RecognitionListener, SpearWakeUpListener, SpearConfigUpdateListener {

	private static final Logger LOGGER = Logger.getLogger(WakeUpController.class.getSimpleName());

	// Reference to the main application.
    private SpearDemoMain mainApp;

    @FXML
    private Label spearStatus;

    @FXML
    private Label prompt;

    @FXML
    private Label AsrResultText;

    @FXML
    private HBox ignore_symbol_section;

    @FXML
    private ChoiceBox<String> ignore_symbols_choices;

    @FXML
    private HBox case_preference_section;

    @FXML
    private ChoiceBox<String> case_preference_choices;

    @FXML
    private Label update_config_warning;

    private SpearSdkGateway spearSdkGateway;

    private CommandList commandList;
    private CommandList demoCommandList;
    private CommandList labelCommandList;

    private String aviationFstLocation = "C:\\Program Files\\Think-A-Move, Ltd\\SpearSDK\\resources\\SPEAR-ASR\\models\\aviation_JL16k-NA_v4.fst";
    private String heySpearFstLocation = "C:\\Program Files\\Think-A-Move, Ltd\\SpearSDK\\resources\\SPEAR-WakeUp\\models\\heyspear.fst";

    String prompt_demo_command_list = "Please say the following commands:\n"
    		+ "\t1. Say \"STOP SPEAR\" to put back SPEAR in wake-up grammar OR\n"
    		+ "\t2. Say \"SWITCH GRAMMAR\" to switch to pre-compiled aviation commands grammar OR\n"
    		+ "\t3. Say \"SWITCH LABEL GRAMMAR\" to switch to label comands grammar OR\n"
    		+ "\t4. [ALPHA, BRAVO, CHARLIE, DELTA, ECHO, FOXTROT, GOLF, HOTEL, INDIA, JULIET,\n KILO, LIMA, MIKE, NOVEMBER, OSCAR, PAPA, QUEBEC,"
    		+ "ROMEO, SIERRA, TANGO,\n UNIFORM, VICTOR, WHISKEY, XRAY, YANKEE, ZULU, KWA BEK, KEI BEK,\n SWITCH GRAMMAR, STOP SPEAR, SWITCH"
    		+ " LABEL GRAMMAR]";
    String prompt_label_command_list = "Please say the following commands:\n"
    		+ "\t1. Say \"STOP SPEAR\" to put back SPEAR in wake-up grammar OR\n"
    		+ "\t2. I have a [dog, cat, rabbit, bird],\n"
    		+ "([turn on, turn off] light)|[volume up, volume off],\n"
    		+ "My [dog, cat, rabbit, bird] weight 24.5 lb,\n"
    		+ "Her [bicycle, ship, car, plane] values [any integer number] dollars,\n"
    		+ "CLE stands for cleveland,\n"
    		+ "STOP SPEAR";
    String prompt_pre_compiled_command_list = "Please say the following commands:\n"
    		+ "\t1. Say \"STOP SPEAR\" to put back SPEAR in wake-up grammar OR\n"
    		+ "\t2. Say any of [SET HEADING, SET COURSE, TUNE COM, SET COM, SELECT COM,\n"
    		+ "TUNE CHANNEL, SET CHANNEL, TUNE V H F, SELECT V H F, SET ALTITUDE\n"
    		+ "FLIGHT LEVEL, PROCEED DIRECT, SELECT DIRECT, CENTER MAP, SET RANGE\n"
    		+ "RANGE CLICK, MAP ZOOM IN, MAP ZOOM OUT, STOP SPEAR]";

    String mode = "spear-wakeup";
    String registrationStat = "Unregistered";

    @Override
	public void initialize(URL location, ResourceBundle resources) {
    	initializeChoiceBoxes();
    	hideUpdateConfigView();
		initializeSpearSdk();
		updateUI();
	}

    final File jarFile = new File(SpearSdkGateway.class.getProtectionDomain().getCodeSource().getLocation().getPath());

    private void initializeSpearSdk() {
    	spearSdkGateway = new SpearSdkGateway();
    	spearSdkGateway.subscribeSpearInitEvent(this.hashCode(), this);
		spearSdkGateway.initializeSpearSdk();
    	aviationFstLocation = "C:\\Program Files\\Think-A-Move, Ltd\\SpearSDK\\resources\\SPEAR-ASR\\models\\aviation_JL16k-NA_v4.fst";
    	heySpearFstLocation = "C:\\Program Files\\Think-A-Move, Ltd\\SpearSDK\\resources\\SPEAR-WakeUp\\models\\heyspear.fst";
	}

    private void initializeSpearRecognizer() {
    	if (spearSdkGateway.isSpearInitialized()) {
			spearSdkGateway.createSpearRecognizer();
			spearSdkGateway.subscribeRecognitionListener(this.hashCode(), this);
			spearSdkGateway.subscribeVadListener(this.hashCode(), this);
			demoCommandList = new DemoCommandList();
			labelCommandList = new LabelCommandList();
			spearSdkGateway.setCommandListGrammar(demoCommandList);
			spearSdkGateway.setCommandListGrammar(labelCommandList);
			spearSdkGateway.setFstGrammar(aviationFstLocation, true);
			updateConfig();
		}
    }

    private void initializeSpearWakeUp() {
    	if (spearSdkGateway.isSpearInitialized()) {
			spearSdkGateway.createSpearWakeUp();
			spearSdkGateway.subscribeSpearWakeUpListener(this.hashCode(), this);
			spearSdkGateway.initWakeUpWithFst(heySpearFstLocation);
			spearSdkGateway.startWakeUpListening(true);
		}
    	LOGGER.log(Level.INFO, "InitializeSpearWakeUp complete.");
    }

    private void updateUI() {
        updateSpeechStatus();
	}

    private void updateSpeechStatus() {
    	ProcessMode processMode = ProcessMode.UNINITIALIZED;

    	if (mode.equals("spear-wakeup") && spearSdkGateway.isSpearWakeUpCreated()) {
    		processMode = spearSdkGateway.getSpearWakeUpStatus();
        } else if (mode.equals("spear-asr") && spearSdkGateway.isSpeechRecognizerCreated()) {
        	processMode = spearSdkGateway.getSpeechRecognizerStatus();
        }

        switch (processMode) {
            case UNINITIALIZED:
                spearStatus.setText(registrationStat+ ", Loading...");
                break;
            case IDLE:
                spearStatus.setText(registrationStat+ ", Ready");
                break;
            case LISTENING:
                spearStatus.setText(registrationStat+ ", Listening...");
                break;
            case PROCESSING:
                spearStatus.setText(registrationStat+ ", Processing... please wait");
                break;
            case TERMINATED :
                spearStatus.setText(registrationStat+ ", Terminated!");
                break;
        }
    }

    public void shutdown() {
		shutdownSpeech();
	}

	private void shutdownSpeech() {
		spearSdkGateway.destroy();
	}

	private void initializeChoiceBoxes() {
		ignore_symbols_choices.getItems().add(" ");
		ignore_symbols_choices.getItems().add("SET");
		ignore_symbols_choices.getItems().add("ZOOM");
		ignore_symbols_choices.getItems().add("DELTA");
		case_preference_choices.getItems().add("upper");
		case_preference_choices.getItems().add("lower");
		case_preference_choices.getItems().add("raw");
	}

	private void hideUpdateConfigView() {
		update_config_warning.setText("");
		ignore_symbol_section.setVisible(false);
		case_preference_section.setVisible(false);
    }

	private void showUpdateConfigView() {
		ignore_symbol_section.setVisible(true);
		case_preference_section.setVisible(true);
    }

	public void updateConfig() {
		ArrayList<String> commadList = new ArrayList<String>();
        commadList.add("--ignored-symbols=<UNK>,<NOISE>," + ignore_symbols_choices.getValue());
        commadList.add("--case-preference=" + case_preference_choices.getValue());
        if (!commadList.isEmpty()) {
            String[] commands = new String[commadList.size()];
            for (int j = 0; j < commadList.size(); j++) {
                commands[j] = commadList.get(j);
            }
            spearSdkGateway.updateSpearConfig(commands, this);
            update_config_warning.setText("");
        } else {
        	update_config_warning.setText("SpearSdkExample: No config parameter passed to update config.");
        }
	}

	public void onUpdateConfigClicked() {
		updateConfig();
    }

	/**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(SpearDemoMain mainApp) {
        this.mainApp = mainApp;
    }

	@Override
	public void onSpearInitComplete(RegistrationMode registrationMode) {
		LOGGER.log(Level.INFO, "onSpearInitComplete");

		if (registrationMode == RegistrationMode.REGISTERED) {
            registrationStat = "Registered";
        } else if (registrationMode == RegistrationMode.UNREGISTERED){
            registrationStat = "Unregistered";
        } else if (registrationMode == RegistrationMode.EXPIRED){
            registrationStat = "Expired";
        }

		initializeSpearWakeUp();
		initializeSpearRecognizer();
		LOGGER.log(Level.INFO, "registrationStat: " + registrationStat);
	}

	@Override
	public void onSpearInitError(Throwable throwable) {
		LOGGER.log(Level.INFO, "onSpearInitError: ", throwable);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				prompt.setText(throwable.getMessage());
			}
		});
	}

	@Override
	public void onCommitResult(TamTranscriptionPair[] tamTranscriptionPairs) {
		Platform.runLater(new Runnable() {
            @Override
            public void run() {
                LOGGER.log(Level.INFO, "CommitResult:" + tamTranscriptionPairs[0]);

                String result = tamTranscriptionPairs[0].transcription;

                AsrResultText.setText(result);

                switch (result.toUpperCase(Locale.US)) {
                	case DemoCommandList.SWITCH_GRAMMAR:
                		AsrResultText.setText("");
                		spearSdkGateway.changeFstGrammar(aviationFstLocation, true);
                		prompt.setText(prompt_pre_compiled_command_list);
                		break;
                	case DemoCommandList.SWITCH_LABEL_GRAMMAR:
                		AsrResultText.setText("");
                		spearSdkGateway.changeCommandListGrammar(labelCommandList);
                		prompt.setText(prompt_label_command_list);
                		break;
                	case DemoCommandList.STOP_SPEAR:
                		AsrResultText.setText("");
                		mode = "spear-wakeup";
                		spearSdkGateway.stopListening();
                		hideUpdateConfigView();
                		spearSdkGateway.startWakeUpListening(true);
                		break;
                }
                updateUI();
            }
        });
	}

	@Override
	public void onIntermediateResult(TamTranscriptionPair[] arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartListening() {
		LOGGER.log(Level.INFO, "onStartListening");
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				updateUI();
			}
		});
	}

	@Override
	public void onStopListening() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				updateUI();
			}
		});
	}

	@Override
	public void onVadResult(TamVadPair arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCommitResult(SpearWakeUpResult spearWakeUpResult) {
		if (spearWakeUpResult.getRetval() == 1) {
			spearSdkGateway.stopWakeUpListening();
			mode = "spear-asr";
			spearSdkGateway.changeCommandListGrammar(demoCommandList);
			spearSdkGateway.startListening(true);

			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					LOGGER.log(Level.INFO, "spearWakeUpResult:" + spearWakeUpResult.getRetval());
	                prompt.setText(prompt_demo_command_list);
	                showUpdateConfigView();
	                updateUI();
				}
			});
		}
	}

	@Override
	public void onStartWakeUpListening() {
		LOGGER.log(Level.INFO, "onStartWakeUpListening");
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				prompt.setText("Please say \"HEY SPEAR\" to change grammar to Demo Commands.");
				updateUI();
			}
		});

	}

	@Override
	public void onStopWakeUpListening() {
		LOGGER.log(Level.INFO, "onStopWakeUpListening");
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				updateUI();
			}
		});

	}

	@Override
	public void onRecognitionError(Throwable throwable) {
		LOGGER.log(Level.INFO, "onRecognitionError");
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (throwable.getMessage().contains("Trial limit is reached")) {
		        	prompt.setText("Trial time is used up! Please restart the Demo application.");
		        } else {
		        	prompt.setText("Recognition engine is terminated unexpectedly, please contact developer for help.");
		        }
				AsrResultText.setText("");
				hideUpdateConfigView();
				updateUI();
			}
		});
        LOGGER.log(Level.INFO, "Error in Recognizing audio:", throwable);
	}

	@Override
	public void updateSpearConfigStatus(String status) {
		// TODO Auto-generated method stub
		update_config_warning.setText(status);

	}
}

