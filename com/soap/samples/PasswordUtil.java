/*
 * The PasswordUtil is a utility class to set passwords of
 * Salesforce users via the Partner SOAP API (partner.wsdl)
 * 
 * Use Case:
 * Users are created via data loader import in user table.
 * The data loader success logfile contain the new user id for each new user.
 * The Salesforce administrator can reset password using the Setup menu,
 * but then the user will receive a Salesforce standard email with instructions
 * how to login and reset the password.
 * 
 * This utility might be useful in situations where this system-generated
 * email is not desired but instead the new users should have a temporary
 * password assigned without being notified by a Salesforce generated email.
 * 
 * The password util reads from the data loader success logfile the new user ids
 * and calls the SOAP API setPassword() for each user and assigns a static password.
 * The administrator can send out a custom email to the new users with
 * organization specific instructions and the temporary password set.
 * 
 * This utility uses resources from https://sourceforge.net/projects/javacsv/
 * to read from the data loader success csv
 * and the Force.com Web Service Connector (WSC) from https://github.com/forcedotcom/wsc
 * to easily invoke the SOAP API of Force.com.
 * 
 * Thomas Egeling
 * https://github.com/tegeling/soap_samples
 * 
 *   
 */
package com.soap.samples;

import java.io.IOException;

import com.csvreader.CsvReader;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.SetPasswordResult;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class PasswordUtil {

   PartnerConnection connection;
   String authEndPoint = "https://login.salesforce.com/services/Soap/u/36.0";

   public static void main(String[] args) throws IOException {
      if (args.length != 3) {
         System.out.println("Usage: com.soap.samples."
               + "PasswordUtil <AdminUsername> <AdminPassword> <SuccessCSVFilename>");
         System.exit(-1);
      }
      PasswordUtil sample = new PasswordUtil();
      sample.run(args[0], args[1], args[2]);
   }

   public void run(String username, String password, String csvfile) throws IOException {
      // Make a login call
      if (login(username, password)) {
    	  CsvReader myreader = new CsvReader(csvfile);
    	  myreader.readHeaders();

    	  while (myreader.readRecord())
    	  {
    		  String userID = myreader.get("ID");
    		  String userName = myreader.get("USERNAME");
			
    		  // Call Util API
    		  //doSetPassword(userID, "Salesforce1");
    		  System.out.println("New password set for " + userID + ":" + userName);
    	  }

    	  myreader.close();
    	  // Log out
    	  logout();
      }
   }

   private boolean login(String username, String password) {
      boolean success = false;

      try {
         ConnectorConfig config = new ConnectorConfig();
         config.setUsername(username);
         config.setPassword(password);
         config.setAuthEndpoint(authEndPoint);
         connection = new PartnerConnection(config);
         success = true;
      } catch (ConnectionException ce) {
         ce.printStackTrace();
      } 

      return success;
   }

   private void doSetPassword(String userId, String newPasswd) {
	   try {
	      SetPasswordResult result = connection.setPassword(userId, newPasswd);
	      System.out.println("The password for user ID " + userId + " changed to "
	            + newPasswd);
	   } catch (ConnectionException ce) {
	      ce.printStackTrace();
	   }
	}
   
   private void logout() {
      try {
         connection.logout();
         System.out.println("Logged out.");
      } catch (ConnectionException ce) {
         ce.printStackTrace();
      }
   }
}