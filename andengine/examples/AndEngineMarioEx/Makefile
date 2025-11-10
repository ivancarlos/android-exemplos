LINUXBREW_HOME = /home/linuxbrew/.linuxbrew
ANT  =  /usr/bin/ant
JENV =	$(LINUXBREW_HOME)/bin/jenv

PACKAGE = com.sizeofint.games.marioex
MAINACTIVITY = .Game

status:
	$(ANT) -version
	$(JENV) version

build:
	$(ANT) debug
install:
	$(ANT) installd
uninstall:
	adb uninstall $(PACKAGE)
start:
	adb logcat -c
	adb shell am start -n $(PACKAGE)/$(MAINACTIVITY)
stop:
	adb shell am force-stop $(PACKAGE)

log:
	adb logcat > crash.txt
	# adb logcat | grep -i marioex
clean:
	$(ANT) clean

