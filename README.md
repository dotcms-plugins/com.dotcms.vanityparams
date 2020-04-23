# README

This OSGI plugin adds a WebInterceptor that resolves a vanityURL for a request - with the additional benifit of adding the query string specified in the vanityUrl to the request.  This functionality will be the default as of dotCMS 5.3+

Important note.  This plugin adds a filter runs before the defualt vanity url filter (it does not replace the default filter). This it is possible to cause two matches of a vanity url, e.g. if you have a vanity url of `/pages/test-spanish --> /paginas/en-espanol?language_id=2`  and a vanity url for `/paginas/en-espanol --> /pages/somewhere-else` then both will be matched and the defualt vanity url filter, which runs after this plugin,  will end up stripping out the expected params 



## How to build this example

To install all you need to do is build the JAR. to do this run
`./gradlew jar`

This will build two jars in the `build/libs` directory: a bundle fragment (in order to expose needed 3rd party libraries from dotCMS) and the plugin jar 

* **To install this bundle:**

    Copy the bundle jar files inside the Felix OSGI container (*dotCMS/felix/load*).
        
    OR
        
    Upload the bundle jars files using the dotCMS UI (*CMS Admin->Dynamic Plugins->Upload Plugin*).

* **To uninstall this bundle:**
    
    Remove the bundle jars files from the Felix OSGI container (*dotCMS/felix/load*).

    OR

    Undeploy the bundle jars using the dotCMS UI (*CMS Admin->Dynamic Plugins->Undeploy*).

