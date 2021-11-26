# Build Instructions for SpearSdkExample Desktop Application

SpearSdkExample desktop application needs to be built using the JavaFX widget toolkit, e(fx)clipse IDE.
This instructions were validated on Windows 10.

## Install Java:

The SpearSdkExample application requires Java 8.

### Install OpenJDK 8:

1. Go to Java SE download site https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
2. Make sure you are in section Java SE Development Kit 8* and select Windows
3. Download JDK 8 (jdk-8u301-windows-x64.exe) for Windows x64(You need to register an Oracle account to download it)
4. Open downloaded JDK setup file.
	- Click Next
	- Keep default path and click Next
	- Click Next on Destination Folder screen
	- Click Close

### Set JAVA_HOME Environment variable:

1. Add JAVA_HOME to the system path
	- Right click on This PC/My Computer
	- Select the properties
	- On left side, Click on the Advanced System Settings
	- Go to the Advanced tab and select the Environment variables
	- Click New under the user variable for <USER NAME> and add
		- Variable name: JAVA_HOME
		- Variable value: C:\Program Files\Java\jdk<version>\bin
	- Click OK
	- Click OK -> Apply -> OK and close all windows
2. Verify JAVA installation
	- Open Command Prompt
	- Enter `java -version`. Expected results:

	```bash
		> java -version
		java version "1.8.0_301"
		Java(TM) SE Runtime Environment (build 1.8.0_301-b09)
		Java HotSpot(TM) 64-Bit Server VM (build 25.301-b09, mixed mode)

	```

## Install e(fx)clipse:

To build, you'll need to install e(fx)clipse, a variant of Eclipse with plugins specialized for working with JavaFX applications.

1. Download the "all-in-one" solution from http://efxclipse.bestsolution.at/install.html#all-in-one
2. "Install" e(fx)clipse by simply unzipping the downloaded file.
3. Run the `eclipse` executable to open eclipse


## Install WiX Toolset:

Wix toolset builds windows installer package and is required when packaging a deployable bundle.

1. Download WiX tool ".exe" from https://github.com/wixtoolset/wix3/releases/tag/wix3112rtm
2. Enable ".NET Framework 3.5.1"
	- Search for "Turn Windows features on or off" in Windows search ("Type here for search" inside the taskbar)
	- In "Turn Windows Features on or off" window: enable ".NET Framework 3.5 (includes .NET 2.0 and 3.0)"
	- Click OK
	- Select "Let Windows Update download the files for you"
	- Click Close
3. Double click the downloaded ".exe" file
4. Click Install
5. Click Yes

### Add Wix Toolset to System path:
1. Open "Edit the system environment variables" in Control Panel
2. In the Advanced tab, click on "Environment Variables..."
3. Select Path under "System variables"
4. Click Edit
5. Click New and add "C:\Program Files (x86)\WiX Toolset v3.11\bin"
6. Click OK, OK, OK
7. Restart Eclipse if open

## Build in Eclipse:

1. Start Eclipse
2. Add SpearSdkExample into workspace
    - Click `File`(Eclipse menu item)
    - Click `Open Projects from File System...`
    - Click `Directory` button
    - Find and select SpearSdkExample folder
    - Click `OK` button
    - Click `Finish` button
3. Add jdk
    - Click `Window` (Eclipse menu item)
    - Click `Preference`
    - Click  `Java`
	- Click `Installed JREs`
    - Click `Add` button
    - Select Standard VM and click `Next` button
    - Click `Directory` button
    - Browse to your JDK version (not JRE) of your installed Java (e.g., C:\Program Files\Java\jdk1.8.0_301)
    - Click `Finish` button
	- Click `OK` button
4. If Package Explorer window is not displayed in the view,
	- Click `Window` (Eclipse menu item)
	- Click `Show View`
	- Click `Project Explorer` 
5. Open `build.fxbuild` inside the Package Explorer
	- Click `>` next to SpearSdkExample
	- Double click `build.fxbuild`
6. Change `Packaging Format:` to `msi`
7. Click `Generate ant build.xml only`
    - Followed the prompt(if there is any) to add jdk(added in step 3) with the project
	- It will create a `build.xml` file under the build directory
8. Update `run configuration" for packaging scripts variables
	- Click `Run` (Eclipse menu item)
	- Click `External Tools`
	- Click `External Tools Configuration...`
	- Select `JFX Build - SpearSdkExample`  item under `Ant Build` (Note: This build is pointing to the `build.xml` file created by `build.fxbuild` file.)
	- Click `Environment` tab
		- Click the `New` button and add the following items
			- Name: SDKLibDir
			- Value: ${project_loc}\lib
		- Click `OK` button
    - Click `New` Button and add following items
			- Name: SDKResourceDir
			- Value: ${project_loc}\resources
		- Click `OK` button
		- Click `New` Button and add following items
			- Name: WxSDirectory
			- Value: ${project_loc}\package\windows
		- Click `OK` button
	- Click `Classpath` Tab
		- Click `User Entries`
		- Click `Add Folders...` button
			- Select `SpearSdkExample`
			- Click `OK` button
	- Click `Apply` button
	- Click `Run` button
4. The `SpearSdkExample-*.msi` will be created under `build/deploy/bundles`

## Install Package:

1. Double click on `SpearSdkExample-*.msi` file
2. Click Yes on the dialog `User Account Control` in windows to allow installation
3. This will create a desktop icon for the application. The application will be installed at `C:\Program Files\Think-A-Move, Ltd\`. It will create SpearSdk folder under it and automatically add it to the Path Environment Variable. 

## Run Installed Package:

1. Double click on the `SpearSdkExample` desktop shortcut 

OR

2. Go to C:\Program Files\Think-A-Move, Ltd\SpearSdkExample folder
	- Double click `SpearSdkExample.exe`

OR

3. Search for the `SpearSdkExample.exe` in windows search and open the application