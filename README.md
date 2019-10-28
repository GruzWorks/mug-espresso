# mug-espresso
Ah shit, here we go again.

## Building from source

### Requirements

* Android Studio 3.5.1
* Android SDK level 29 (Android 10.0 Q)

### Setting up the IDE

0. Clone the repository
1. Start Android Studio and open the repo's working directory as a project
1. Wait for gradle to finish sync
1. Run/Debug configuration should be created automatically, if not create an Android App configuration for module `app`

### Running the app

1. Select device to run
	1. If using a physical device, turn on USB debugging and connect it via USB cable
	1. If using an emulator, make sure it's been created
1. Run by pressing the Run button (play icon)

### Building an apk

1. Build the project by clicking on the Make project button (hammer icon)

Built apk can be found there: `app/build/outputs/apk/debug/app-debug.apk`
