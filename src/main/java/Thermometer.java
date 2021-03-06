import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.swing.*;


public class Thermometer {
    private static final String APPLICATION_NAME = "Charity Fundraiser Thermometer";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = SheetsQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }


    public static void main(String... args) throws IOException, GeneralSecurityException, InterruptedException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String spreadsheetId = "18LjVf3c0rtBpviBCl7usTyEtUD6RqiHHE5dKsVlFYsY";
        final String range = "Sheet1!A2:B";
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        ValueRange response;
        List<List<Object>> values;

        // Above code utilized from Google Sheets API Quickstart file
        // Below code modified from above file for thermometer use

        double totalRaised = 0;
        int rowsTraversed = 0;
        int i;

        thermGraphics window = new thermGraphics();

        do {
            response = service.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            values = response.getValues();
            i = 0; // iteration count

            for (List row : values){
                i++;
                if(i > rowsTraversed && !(row.get(0).equals("EXIT"))) { // read row if row previously unread and if column 0 does not contain "EXIT"
                    rowsTraversed++; //track rows read
                    window.addDonor((String) row.get(0), Double.valueOf((String) row.get(1)));
                    totalRaised += Double.valueOf((String) row.get(1)); // track total raised for use with graphical thermometer
                    System.out.println(row.get(0) + " donated $" + row.get(1) + " || total = " + totalRaised); // temporary back-end debug line
                    //update graphics
                }else if(row.get(0).equals("EXIT")) break; // break for loop if column 0 contains "EXIT" (To deny access to rows that may not be completed yet)
                TimeUnit.SECONDS.sleep(1); // Time out between row reads
            }

        TimeUnit.SECONDS.sleep(15); // Time out between sheet retrievals

        } while (true); //determine how to exit cleanly - infinite loop used for development stage




        /*if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            System.out.println("Name, Donation");
            for (List row : values) {
                // Print columns A and E, which correspond to indices 0 and 4.
                System.out.printf("%s, %s\n", row.get(0), row.get(1));
            }
        }*/
    }

}
