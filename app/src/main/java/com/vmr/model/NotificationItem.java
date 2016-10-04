package com.vmr.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/*
 * Created by abhijit on 10/3/16.
 */

public class NotificationItem {

    private String inboxId;           //":12363,
    private String mailSubject;     //":"A Record(s) has been  Shared with you - Another test share",
    private String mailBody;
    private int mailType;
    private UserDetails userdetails;//":{  },
    private Date createdOn;         //":"Oct 3, 2016 7:05:28 PM",
    public NotificationItem(JSONObject inboxJson) {
        Date result ;
        DateFormat df = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss a", Locale.ENGLISH);

        try {
            this.setInboxId(inboxJson.getString("inboxId"));
            this.setMailSubject(inboxJson.getString("mailSubject"));
            this.setMailBody(inboxJson.getString("mailBody"));
            this.setMailType(inboxJson.getInt("mailType"));
            this.setUserdetails(new UserDetails(inboxJson.getJSONObject("userdetails")));
            result = df.parse(inboxJson.getString("createdOn"));
            this.setCreatedOn(result);
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    public static List<NotificationItem> getInboxList(JSONArray inboxArray){
        List<NotificationItem> notificationItemList = new ArrayList<>();
        if(inboxArray!= null && inboxArray.length() > 0) {
            NotificationItem notificationItem;
            try {
                for (int i = 0; i < inboxArray.length(); i++) {
                     notificationItem = new NotificationItem(inboxArray.getJSONObject(i));
                    notificationItemList.add(notificationItem);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return notificationItemList;
    }

    public String getInboxId() {
        return inboxId;
    }

    public void setInboxId(String inboxId) {
        this.inboxId = inboxId;
    }

    public String getMailSubject() {
        return mailSubject;
    }

    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }

    public String getMailBody() {
        return mailBody;
    }

    public void setMailBody(String mailBody) {
        this.mailBody = mailBody;
    }

    public int getMailType() {
        return mailType;
    }

    public void setMailType(int mailType) {
        this.mailType = mailType;
    }

    public UserDetails getUserdetails() {
        return userdetails;
    }

    public void setUserdetails(UserDetails userdetails) {
        this.userdetails = userdetails;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public class UserDetails{

        private String firstName; //":"Abhijit",
        private String corporateName; //":"Fname",
        private String lastName; //":"Parate",
        private String emailId; //":"abhijitpparate@gmail.com",

        UserDetails(JSONObject userDetailJson) {
            Date result ;
            DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);

            try {
                this.setFirstName(userDetailJson.getString("firstName"));
                if(userDetailJson.has("corporateName"))
                    this.setCorporateName(userDetailJson.getString("corporateName"));
                this.setLastName(userDetailJson.getString("lastName"));
                this.setEmailId(userDetailJson.getString("emailId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getCorporateName() {
            return corporateName;
        }

        public void setCorporateName(String corporatename) {
            this.corporateName = corporatename;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmailId() {
            return emailId;
        }

        public void setEmailId(String emailId) {
            this.emailId = emailId;
        }
    }
}
