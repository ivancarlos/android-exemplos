LINUXBREW_HOME = /home/linuxbrew/.linuxbrew
ANT  =  /usr/bin/ant
JENV =	$(LINUXBREW_HOME)/bin/jenv

PACKAGE = eu.nazgee.sunflower
MAINACTIVITY = .GameActivity

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
	adb logcat | grep -i $(PACKAGE)
clean:
	$(ANT) clean

