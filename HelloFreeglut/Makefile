LINUXBREW_HOME = /home/linuxbrew/.linuxbrew
ANT  =  /usr/bin/ant
JENV =	$(LINUXBREW_HOME)/bin/jenv

PACKAGE = com.example.hellofreeglut
MAINACTIVITY = android.app.NativeActivity

status:
	$(ANT) -version
	$(JENV) version

build:
	$(ANT) debug
install:
	adb logcat -c
	$(ANT) installd
uninstall:
	adb uninstall $(PACKAGE)
start:
	adb shell am start -n $(PACKAGE)/$(MAINACTIVITY)
stop:
	adb shell am force-stop $(PACKAGE)
log:
	adb logcat | grep $(PACKAGE)

clean:
	$(ANT) clean

