package com.vmr.model;

import org.json.JSONException;
import org.json.JSONObject;

/*
 * Created by abhijit on 11/29/16.
 */

public class Account {

    /*
        Sample JSON
        {
           "slNo":674,
           "corpOrFamily_Id":"ind",
           "firstName":"Fname",
           "lastName":"Lname",
           "emailId":"abhijit159@gmail.com",
           "userId":"induser",
           "domainType":1,
           "exist":1,
           "languageType":"en",
           "password":"f1b953ea11facc79763a4199b2d373b8418d72b43cc2fcfc9d1cae40ec056dc15c6f3ffb91ce2b75bd1829b982214a6ff0f1e703d1eb7bc2c35f1967c8f1ab0b", (SHA512)
           "packages":"1",
           "status":1,
           "urlType":"STANDARD",
           "url":"null",
           "logoPath":[  ],
           "securityQuestion":"Which is your favourite country?",
           "answerForSecurityQuestion":"270f758f6208f934582f579219963f05dd986e32a2c327b9312617d208ec192e299dbf0be1b07ee0092657785903025fc81c23e33104b5895d21042daf7d84bd", (SHA512)
           "membershipType":"IND",
           "lastAccessed":"Nov 29, 2016 8:39:50 PM",
           "accountApproved":"Aug 8, 2016 2:05:21 AM",
           "logoutTime":"Oct 15, 2016 12:42:46 AM",
           "lastLoginTime":"Nov 29, 2016 8:31:20 PM",
           "scanStatus":2,
           "AccountApprovedby":1,
           "pswdSetDate":"Aug 8, 2016",
           "userType":1,
           "commercialType":"prepaid",
           "encType":[  ],
           "encAlgorithm":[  ],
           "encKeyStrength":[  ],
           "enc1":[  ],
           "enc2":[  ],
           "enc3":[  ],
           "dec1":[  ],
           "dec2":[  ],
           "dec3":[  ],
           "createdBy":674,
           "lastUpdatedBy":674,
           "createdOn":"Aug 8, 2016 2:05:21 AM",
           "lastUpdatedOn":"Oct 15, 2016 12:42:46 AM"
        }
     */
    public static Account parseJson(JSONObject jsonObject) throws JSONException{
        return new Account();
    }
}
