jclouds Google Cloud Storage Provider
===========================================================

* Q. What is the identity for GCE?

A. the identity is the developer email which can be obtained from the admin GUI. Its usually something in the form: <my account id>@developer.gserviceaccount.com

* Q. What is the credential for GCE

A. the credential is a private key, in pem format. It can be extracted from the p12 keystore that is obtained when creating a "Service Account" (in the GUI: Google apis console > Api Access > Create another client ID > "Service Account"

* Q. How to convert a p12 keystore into a pem format jclouds Google Cloud Storage can handle:

A.

1. Convert the p12 file into pem format (it will ask for the keystore password, which is usually "notasecret"):
 openssl pkcs12 -in <my_keystore>.p12 -out <my_keystore>.pem -nodes

2. Extract only the pk and remove passphrase
 openssl rsa -in <my_keystore>.pem -out <my_key>.pem

The last file (<my_key>.pem) should contain the pk that needs to be passed to `ContextBuilder.credential()` for the provider `google-cloud-storage`.


Running the live tests:
---------------------------------------------------------------

1. Place the following in your ~/.m2/settings.xml in a profile enabled when live:
```
    <test.google-cloud-storage.identity>YOUR_ACCOUNT_NUMBER@developer.gserviceaccount.com</test.google-cloud-storage.identity>
    <test.google-cloud-storage.credential>-----BEGIN RSA PRIVATE KEY-----
MIICXgIBAAKBgQRRbRqVDtJLN1MO/xJoKqZuphDeBh5jIKueW3aNIiWs1XFcct+h
-- this text is literally from your <my_key>.pem
aH7xmpHSTbbXmQkuuv+z8EKijigprd/FoJpTX1f5/R+4wQ==
-----END RSA PRIVATE KEY-----</test.google-cloud-storage.credential>
 <test.google-cloud-storage.bucket>YOUR_BUCKET_NAME</test.google-cloud-storage.bucket>
  </properties>
```

2. mvn clean install -Plive 



