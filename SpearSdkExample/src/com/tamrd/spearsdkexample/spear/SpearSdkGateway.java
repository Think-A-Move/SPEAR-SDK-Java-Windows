package com.tamrd.spearsdkexample.spear;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import java.io.FileNotFoundException;

//import com.tamrd.spearsdkexample.view.WakeUpController;
import com.thinkamove.spearnative.recognizer.ProcessMode;
import com.thinkamove.spearnative.spear.api.SpearSdkApi;
import com.thinkamove.spearnative.spear.api.SpearWakeUp;
import com.thinkamove.spearnative.spear.api.SpearWakeUpListener;
import com.thinkamove.spearnative.spear.api.RecognitionListener;
import com.thinkamove.spearnative.spear.api.SpearInitListener;
import com.thinkamove.spearnative.spear.api.VadListener;
import com.thinkamove.spearnative.spearConfig.SpearConfigUpdateListener;
import com.thinkamove.spearnative.speechType.commands.api.CommandList;
import com.thinkamove.spearnative.spear.api.SpeechRecognizer;
import com.thinkamove.spearnative.spear.api.UnInitializeException;
import com.thinkamove.spearnative.helper.ResourceUtils;
import com.thinkamove.spearnative.helper.TempDir;
import com.thinkamove.spearnative.helper.os.OSSpecific;

public class SpearSdkGateway {

	private static final Logger LOGGER = Logger.getLogger(SpearSdkGateway.class.getSimpleName());

	/**
     * The minimum length a prefix for a file has to have according to {@link File#createTempFile(String, String)}}.
     */
    private static final int MIN_PREFIX_LENGTH = 3;

	private SpearSdkApi spearSdkApi;
	private SpeechRecognizer speechRecognizer;
	private SpearWakeUp spearWakeUp;
	private static TempDir tempDataFolderDir;

	public static final String DEFAULT_DATA_DIR = "SPEAR-DATA-EN";
	public static final String DEFAULT_FST_DIR = "Fsts";

	public static String workingDataDir;

	public SpearSdkGateway() {
        spearSdkApi = SpearSdkApi.getInstance();
    }

	public void subscribeSpearInitEvent(int uniqueId, SpearInitListener spearInitListener) {
        spearSdkApi.subscribeSpearInitEvent(uniqueId, spearInitListener);
    }

	public void initializeSpearSdk() {
    	spearSdkApi.initialize("C:\\Program Files\\Think-A-Move, Ltd\\SpearSDK\\resources");
    }

	public boolean isSpearInitialized() {
        return spearSdkApi.isSpearInitialized();
    }

	public void createSpearRecognizer() {
		try {
			speechRecognizer = spearSdkApi.createSpeechRecognizer();
		} catch (UnInitializeException e) {
			LOGGER.log(Level.SEVERE, "UnInitialized SpearSdkApi error", e);
		}
	}

	public boolean isSpeechRecognizerCreated() {
		if (speechRecognizer != null) {
			return true;
		}
		return false;
	}

	public void createSpearWakeUp() {
		try {
			spearWakeUp = spearSdkApi.createSpearWakeUp();
		} catch (UnInitializeException e) {
			LOGGER.log(Level.SEVERE, "UnInitialized SpearSdkApi error", e);
		}
	}

	public boolean isSpearWakeUpCreated() {
		if (spearWakeUp != null) {
			return true;
		}
		return false;
	}

	public void subscribeRecognitionListener(int uniqueId, RecognitionListener recognitionListener) {
		speechRecognizer.subscribeRecognitionListener(uniqueId, recognitionListener);
	}

	public void subscribeVadListener(int uniqueId, VadListener vadListener) {
		speechRecognizer.subscribeVadListener(uniqueId, vadListener);
	}

	public void subscribeSpearWakeUpListener(int uniqueId, SpearWakeUpListener spearWakeUpListener) {
		spearWakeUp.subscribeSpearWakeUpListener(uniqueId, spearWakeUpListener);
	}

	public void setFstGrammar(String fstNamePath, boolean shouldLoadFstExternally) {
		try {
			speechRecognizer.setFstGrammar(fstNamePath, shouldLoadFstExternally);
		} catch (UnInitializeException e) {
			LOGGER.log(Level.SEVERE, "UnInitialized SpearSdkApi error", e);
		}
	}

	public void changeFstGrammar(String fstNamePath, boolean shouldLoadFstExternally) {
		try {
			speechRecognizer.changeFstGrammar(fstNamePath, shouldLoadFstExternally);
		} catch (UnInitializeException e) {
			LOGGER.log(Level.SEVERE, "Failed to change grammar.", e);
		}
	}

	public void setCommandListGrammar(CommandList commandList) {
		try {
			speechRecognizer.setCommandListGrammar(commandList);
		} catch (UnInitializeException e) {
			LOGGER.log(Level.SEVERE, "UnInitialized SpeechRecognizer error", e);
		}
	}

	public void changeCommandListGrammar(CommandList commandList) {
		try {
			speechRecognizer.changeCommandListGrammar(commandList);
		} catch (UnInitializeException e) {
			LOGGER.log(Level.SEVERE, "Failed to change grammar.", e);
		}
	}

	public void initWakeUpWithFst(String fstNamePath) {
		try {
			spearWakeUp.initWithFst(fstNamePath);
		} catch (UnInitializeException e) {
			LOGGER.log(Level.SEVERE, "UnInitialized SpearSdkApi error", e);
		}
	}

	public void initWakeUpWithWakeWord(String wakeWord) {
		try {
			spearWakeUp.initWithWakeWord(wakeWord);
		} catch (UnInitializeException e) {
			LOGGER.log(Level.SEVERE, "UnInitialized SpearSdkApi error", e);
		}
	}

	public void startWakeUpListening(boolean isMicrophoneInput) {
		spearWakeUp.startListening(isMicrophoneInput);
	}

	public void startListening(boolean isMicrophoneInput) {
		speechRecognizer.startListening(isMicrophoneInput);
	}

	public void stopListening() {
		speechRecognizer.stopListening();
	}

	public void stopWakeUpListening() {
		spearWakeUp.stopListening();
	}

	public ProcessMode getSpeechRecognizerStatus() {
        return speechRecognizer.getSpeechRecognizerState();
    }

	public ProcessMode getSpearWakeUpStatus() {
        return spearWakeUp.getSpeechRecognizerState();
    }

	public void updateSpearConfig(String[] commands, SpearConfigUpdateListener spearConfigUpdateListener) {
		spearSdkApi.updateSpearConfig(commands, spearConfigUpdateListener);
	}

	public void copyAssetFromJar() {
		try {
			tempDataFolderDir = new TempDir("spear");

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		workingDataDir = tempDataFolderDir.getTempDir().getAbsolutePath();

		try {
			copyResourceDirectoryFromJar(DEFAULT_DATA_DIR, tempDataFolderDir.getTempDir());
			copyResourceDirectoryFromJar(DEFAULT_FST_DIR, tempDataFolderDir.getTempDir());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
     * Copy directory of resources from inside a JAR file to a temporary destination directory.  All resources under
     * the destination directory are deleted upon JVM exit (whether this function placed them there, or not).
     *
     * Based on these solutions:
     * @see https://stackoverflow.com/a/20073154
     * @see https://javarevisited.blogspot.com/2015/03/how-to-delete-directory-in-java-with-files.html
     *
     * @param resourceDirectoryPath The path of file inside JAR as absolute path (beginning with '/'), e.g. /package/File.ext
     * @param destination The destination directory
     * @throws IOException
     */
    public static void copyResourceDirectoryFromJar(String resourceDirectoryPath, File destination) throws IOException {
    	if (!destination.isDirectory()) {
    		throw new IOException("Destination is not directory.");
    	}

    	final File jarFile = new File(SpearSdkGateway.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		LOGGER.log(Level.INFO, "Abstract File pointing to this JAR created successfully.");

		// Run with JAR file and close it when we exit the try block
		try (JarFile jar = new JarFile(jarFile)) {
			LOGGER.log(Level.INFO, "JarFile reference created.");
		    // All entries in JAR
		    final Enumeration<JarEntry> entries = jar.entries();
		    while(entries.hasMoreElements()) {
		        final String resourcePath = entries.nextElement().getName();
		        // Filter out non resource entries
		        if (resourcePath.startsWith(resourceDirectoryPath + File.separator)) {
		            File tempFile = new File(destination, resourcePath);

		        	try (InputStream is = ResourceUtils.class.getResourceAsStream(File.separator + resourcePath)) {
		                // Create parent directory structure
		        		if (!resourcePath.endsWith("/")) {
		        			tempFile.getParentFile().mkdirs();
			        		// Copy file
			        		Files.copy(is, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		        		}
		            } catch (IOException e) {
		                tempFile.delete();
		                throw e;
		            } catch (NullPointerException e) {
		                tempFile.delete();
		                throw new FileNotFoundException("File " + resourcePath + " was not found inside JAR.");
		            }
		        }
		    }
		} catch (IOException e) {
			throw new IOException("Failed to create JarFile reference, possibly because this class is being used outside the JAR in which it resides.");
		}

		// Mark all resources inside destination for deletion upon JVM exit.
		for (File file : destination.listFiles()) {
			deleteDirectoryOnExit(file);
		}
    }

    /**
     * Register a non-empty directory for deletion on JVM exit.
     *
     * @param directoryToBeDeleted The directory to be deleted.
     */
    private static void deleteDirectoryOnExit(File directoryToBeDeleted) {
    	// File.deleteOnExit() deletes files or directories in the reverse order
    	// that they are registered so we register them before recursing into
    	// child directories.
    	LOGGER.log(Level.INFO, "Registering " + directoryToBeDeleted.getName() + " for deletion on JVM exit.");
    	directoryToBeDeleted.deleteOnExit();

    	if (directoryToBeDeleted.isDirectory()) {
    		File[] children = directoryToBeDeleted.listFiles();
    		for (int i = 0; i < children.length; i++) {
    			deleteDirectoryOnExit(children[i]);
    		}
    	}
    }

    /**
     * Copies a single resource from inside a JAR file to the temporary directory.
     *
     * The file from JAR is copied into system temporary directory. The temporary file is deleted after exiting.
     * Method uses String as filename because the pathname is "abstract", not system-dependent.
     *
     * @param resourcePath The path of file inside JAR as absolute path (beginning with '/'), e.g. /package/File.ext
     * @param destination The abstract File representation of the system dependent temp directory
     * @throws IOException If temporary file creation or read/write operation fails
     * @throws IllegalArgumentException If source file (param path) does not exist
     * @throws IllegalArgumentException If the path is not absolute or if the filename is shorter than three characters
     * (restriction of {@link File#createTempFile(java.lang.String, java.lang.String)}).
     * @throws FileNotFoundException If the file could not be found inside the JAR.
     */
    public static void copyResourceFromJar(String resourcePath, File destination) throws IOException {
        // Obtain filename from path
        String[] parts = resourcePath.split(File.separator);
        String filename = (parts.length > 1) ? parts[parts.length - 1] : null;

        // Check if the filename is okay
        if (filename == null || filename.length() < MIN_PREFIX_LENGTH) {
            throw new IllegalArgumentException("The filename has to be at least 3 characters long.");
        }

        File temp = new File(destination, filename);

        try (InputStream is = ResourceUtils.class.getResourceAsStream(File.separator + resourcePath)) {
            Files.copy(is, temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            temp.delete();
            throw e;
        } catch (NullPointerException e) {
            temp.delete();
            throw new FileNotFoundException("File " + resourcePath + " was not found inside JAR.");
        }

        // Delete when JVM exits.
        temp.deleteOnExit();
    }

	public void destroy() {
        spearSdkApi.destroy();
    }
}
