Implementation of the [Belly Mobile Location Application Coding Challenge](https://tech.bellycard.com/join/#mobile-location-application):

Build a simple application that consumes a public location based rest service (Foursquare, Yelp, etc.) to query a list of businesses near you. Your application should display and sort those businesses based on distance from the device’s current location. Use the provided [Photoshop document](https://tech.bellycard.com/challenges/iPhone-List-View.psd) when building the user interface for displaying those businesses.

Additionally the application should use a caching mechanism to store your queried data so it can still be displayed if the device cannot access the internet.

As a bonus objective, we would love to see you display queried locations on a mapview, and a some integration/unit tests around your code.

Feel free to use any open source project dependencies you are comfortable with. Your code should be hosted on GitHub.

### Yelp API Key is required.

In order for the app to function properly, an API key for Yelp must be included with the build.

[Generate an API key](https://www.yelp.com/developers/manage_api_keys), and include the key, token, and secrets for the build by adding the following lines to [USER_HOME]/.gradle/gradle.properties

    ConsumerKey="???"
    ConsumerSecret="???"
    Token="???"
    TokenSecret="???"`

### Google Maps API Key is required.

In order for the app to function properly, an API key for Google Maps must be included with the build.

[Generate an API key](https://developers.google.com/maps/documentation/android/start#get-key), and include the key for the build by adding the following line to [USER_HOME]/.gradle/gradle.properties

    MapsKey="<UNIQUE_API_KEY>"