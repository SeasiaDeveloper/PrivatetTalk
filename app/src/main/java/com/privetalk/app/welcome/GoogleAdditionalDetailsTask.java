package com.privetalk.app.welcome;

import android.os.AsyncTask;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.Person;

import java.io.IOException;

public class GoogleAdditionalDetailsTask extends AsyncTask<GoogleSignInAccount, Void, Person> {

    @Override
    protected Person doInBackground(GoogleSignInAccount... googleSignInAccounts) {
        Person person = new Person();
        try {
            HttpTransport httpTransport = new NetHttpTransport();
            JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            //Redirect URL for web based applications.
            // Can be empty too.
            String redirectUrl = "";

            // Exchange auth code for access token
            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    httpTransport,
                    jsonFactory,
                    WelcomeActivity.clientId,
                    WelcomeActivity.clientSecret,
                    googleSignInAccounts[0].getServerAuthCode(),
                    redirectUrl
            ).execute();

            GoogleCredential credential = new GoogleCredential.Builder()
                    .setClientSecrets(
                            WelcomeActivity.clientId,
                            WelcomeActivity.clientSecret
                    )
                    .setTransport(httpTransport)
                    .setJsonFactory(jsonFactory)
                    .build();

            credential.setFromTokenResponse(tokenResponse);

            PeopleService peopleService = new PeopleService.Builder(httpTransport, jsonFactory, credential)
                    .setApplicationName("PriveTalk")
                    .build();

            // Get the user's profile
            Person profile = peopleService.people().get("people/me").setRequestMaskIncludeField("person.names,person.genders,person.birthdays,person.photos").execute();
            person = profile;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return person;
    }

    @Override
    protected void onPostExecute(Person person) {
        super.onPostExecute(person);
        // iterate through the list of Genders to
        // get the gender value (male, female, other)
    }
}
