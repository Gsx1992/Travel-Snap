# Travel-Snap
TravelSnap  is  a  location  based  service  that  allows  users  to  easily  publish  their  photos to  an online map, powered by Google Maps. When a user takes a photo, the GPS coordinates of the device are automatically obtained and attached to any photo they take, this allows users to use the  application  without  any  internet  access  and  later  upload  the  photos.  The  image  is  then plotted on the map where the user took the image and not where it was uploaded from.


## Built With

* [Parse](http://www.parse.com) - Parse was used for the backend to store photos, user data etc..
* [Google Maps](https://developers.google.com/maps/documentation/android-api/) - Google maps was used to display user photos on the website and in the application.
* [Marker Clustering](https://developers.google.com/maps/documentation/javascript/marker-clustering) - Marker Clusterer was used to cluster photos for the website.
* [Facebook](https://developers.facebook.com/docs/android/) - The Facebook SDK was used to share user maps to Facebook.


## Documents

[Implementation Document](https://dl.dropboxusercontent.com/u/40807068/glaceyfypreport.docx) This document explains the implementation of the project in further detail.

## Website

A website was also created as part of the project to allow users to share their photos to others. [Example Map](https://travel-snap.herokuapp.com/map/2CVN7vpEoF) includes an example of photos for a specific user(The clustering library used in this project is not updated, while it still works various graphics are broken. To view a cluster simply click the number "15" on the map.
