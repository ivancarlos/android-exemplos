LINUXBREW_HOME = /home/linuxbrew/.linuxbrew
ANT  =  /usr/bin/ant
JENV =	$(LINUXBREW_HOME)/bin/jenv

PACKAGE      = org.gg.android.commons.gallery
MAINACTIVITY = .SampleActivity
#MAINACTIVITY = .GalleryActivity


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
stop:
	adb shell am force-stop $(PACKAGE)
clean:
	ant clean

update:
	android update project \
	--name SampleActivity \
	--target android-16 \
	--path .
