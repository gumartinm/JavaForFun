No way of easily sharing a build.gradle script (uploading it to some artifactory repository) 
 
It seems like the best option is creating a gradle pluin. 
 
This gradle plugin is a BOM (build of materials) 
 
See: http://stackoverflow.com/questions/9539986/how-to-share-a-common-build-gradle-via-a-repository  
  
  
You can install this plugin in your computer by means of this command: 

`gradle clean build install`  The install gradle task is provided by the `maven` plugin
