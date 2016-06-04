# soap_samples
Sample use cases for Salesforce SOAP API calls. Mainly based on the Force.com Web Service Connector (WSC) available at https://github.com/forcedotcom/wsc

## PasswordUtil
The [PasswordUtil](https://github.com/tegeling/soap_samples/blob/master/com/soap/samples/PasswordUtil.java) is a utility class to set temporary passwords of Salesforce users via the Partner SOAP API (partner.wsdl)

### Use Case:
Users are created via data loader import in user table.
The data loader success logfile contain the new user id for each new user. The Salesforce administrator can reset password using the Setup menu, but then the user will receive a Salesforce standard email with instructions how to login and reset the password.

This utility might be useful in situations where this system-generated email is not desired but instead the new users should have a temporary password assigned without being notified by a Salesforce generated email.

The password util reads from the data loader success logfile the new user ids and calls the SOAP API setPassword() for each user and assigns a static password. The administrator can send out a custom email to the new users with organization specific instructions and the temporary password set.
