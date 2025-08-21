LINUXBREW_HOME = /home/linuxbrew/.linuxbrew
ANT  =  /usr/bin/ant
JENV =	$(LINUXBREW_HOME)/bin/jenv

PACKAGE = `xmlstarlet select -T -t -v "//manifest/@package" -n AndroidManifest.xml`
MAINACTIVITY = `xmlstarlet select -T -t -v "//manifest/@package" -n AndroidManifest.xml`.`xmlstarlet select -T -t -v "//application/activity/@android:name" -n AndroidManifest.xml`

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
	adb shell am start -n $(PACKAGE)/$(MAINACTIVITY)
clean:
	$(ANT) clean

