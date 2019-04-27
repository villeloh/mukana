# Mukana
### a Bird Observation List App

---

## Features

Mukana is a simple list view CRUD app (although without the U and the D) that I made for the company CGI 
as a pre-task in April 2019. The spec required that I implement the following:

* a scrollable list view of bird observation items, containing:

  * the name of the bird species

  * its rarity (common, rare, or extremely rare)

  * some optional notes about the bird

  * a timestamp ( by which the list items are sorted)

  * optionally, an image and/or geolocation
  
  * optionally, different ways of sorting the list items

Apart from the alternative sorting option, all of these were implemented. There is no detail view or deleting of items, but they'd be easy enough to add.

Adding new list items is possible through a simple form, and the app state is persisted through MvRx ViewModels and a local Room database.

---

## Installation

### Android Studio

For running from Android Studio, just make sure you have the latest version (3.3.x), download or clone the repo, and hit 'run' in AS.

### Plain APK

For running directly on the phone, 

1. First allow your favorite file-loading app ('My Files' on my Samsung Galaxy S7) to handle unknown app files (from Settings; 
it can be under different categories based on the model of the phone).

2. Then download the APK file (either via the web or through a usb cable from a copy of this repo that you downloaded on your desktop computer).
The path to the APK file in the downloaded repo is the following:  
mukana-master/app/src/main/java/com/example/mukana/app-release.apk

3. Go to My Files (or your own phone's file management app), view the downloads folder and click open/allow/etc on the finished download, 
as many times as is needed. That should begin the installation, and you can then directly run the app once its finished.

This link may be helpful in case of any problems:

<https://www.wikihow.tech/Install-APK-Files-on-Android>
