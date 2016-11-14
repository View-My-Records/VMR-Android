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

    private String inboxId;             //":12363,
    private String mailSubject;         //":"A Record(s) has been  Shared with you - Another test share",
    private String mailBody;
    private int mailType;
    private String  toUserId;
    private DocumentAccessDetail documentAccessDetail; //:{ }
    private UserDetails userdetails;    //":{  },
    private String referenceId;         //:48683046
    private String createdBy;           //:690
    private Date createdOn;             //":"Oct 3, 2016 7:05:28 PM",
    private Date updatedOn;             //":"Oct 3, 2016 7:05:28 PM",

    public NotificationItem(JSONObject inboxJson) {
        DateFormat df = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss a", Locale.getDefault());

        try {
            this.setInboxId(inboxJson.getString("inboxId"));
            this.setMailSubject(inboxJson.getString("mailSubject"));
//            this.setMailBody(inboxJson.getString("mailBody"));
            this.setMailBody("");
            this.setMailType(inboxJson.getInt("mailType"));
            this.setToUserId(inboxJson.getString("toUserId"));
            this.setDocumentAccessDetail(new DocumentAccessDetail(inboxJson.getJSONObject("documentAccessReqId")));
            this.setUserdetails(new UserDetails(inboxJson.getJSONObject("userdetails")));
            this.setReferenceId(inboxJson.getString("referenceId"));
            this.setCreatedBy(inboxJson.getString("createdBy"));
            this.setCreatedOn(df.parse(inboxJson.getString("createdOn")));
            this.setUpdatedOn(df.parse(inboxJson.getString("lastUpdatedOn")));
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

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public DocumentAccessDetail getDocumentAccessDetail() {
        return documentAccessDetail;
    }

    public void setDocumentAccessDetail(DocumentAccessDetail documentAccessDetail) {
        this.documentAccessDetail = documentAccessDetail;
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


    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public class UserDetails {

        private String senderId;  // 760
        private String firstName; //":"Abhijit",
        private String corporateName; //":"Fname",
        private String lastName; //":"Parate",
        private String emailId; //":"abhijitpparate@gmail.com",

        UserDetails(JSONObject userDetailJson) {
            try {
                this.setSenderId(userDetailJson.getString("slNo"));
                this.setFirstName(userDetailJson.getString("firstName"));
                if(userDetailJson.has("corporateName"))
                    this.setCorporateName(userDetailJson.getString("corporateName"));
                this.setLastName(userDetailJson.getString("lastName"));
                this.setEmailId(userDetailJson.getString("emailId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public String getSenderId() {
            return senderId;
        }

        public void setSenderId(String senderId) {
            this.senderId = senderId;
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

    public class DocumentAccessDetail {

        private String  docAccessId;         //": 11422,
        private String  docId;               //": "workspace://SpacesStore/a96bf127-c073-4538-9496-c6e2bac8d0da",
        private String  docApproveDuration;  //": 2,
        private String  docStatus;           //": 1,
        private String  docRequesterId;      //": 690,
        private Date    docRequestedDate;    //": "Oct 3, 2016 7:05:28 PM",
        private String  createdBy;           //": 690,
        private String  lastUpdatedBy;       //": 690,
        private Date    createdOn;           //": "Oct 3, 2016 7:05:28 PM",
        private Date    lastUpdatedOn;       //": "Oct 3, 2016 7:05:28 PM"

        DocumentAccessDetail(JSONObject documentAccessJSON){

            try {
                this.setDocAccessId(documentAccessJSON.getString("docAccessId"));
                this.setDocId(documentAccessJSON.getString("docId"));
                this.setDocApproveDuration(documentAccessJSON.getString("docId"));
                this.setDocStatus(documentAccessJSON.getString("docStatus"));
                this.setDocRequesterId(documentAccessJSON.getString("docRequesterId"));
                this.setDocRequestedDate(documentAccessJSON.getString("docRequestedDate"));
                this.setCreatedBy(documentAccessJSON.getString("createdBy"));
                this.setLastUpdatedBy(documentAccessJSON.getString("lastUpdatedBy"));
                this.setCreatedOn(documentAccessJSON.getString("createdOn"));
                this.setLastUpdatedOn(documentAccessJSON.getString("lastUpdatedOn"));
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }

        public String getDocAccessId() {
            return docAccessId;
        }

        public void setDocAccessId(String docAccessId) {
            this.docAccessId = docAccessId;
        }

        public String getDocId() {
            return docId;
        }

        public void setDocId(String docId) {
            this.docId = docId;
        }

        public String getDocApproveDuration() {
            return docApproveDuration;
        }

        public void setDocApproveDuration(String docApproveDuration) {
            this.docApproveDuration = docApproveDuration;
        }

        public String getDocStatus() {
            return docStatus;
        }

        public void setDocStatus(String docStatus) {
            this.docStatus = docStatus;
        }

        public String getDocRequesterId() {
            return docRequesterId;
        }

        public void setDocRequesterId(String docRequesterId) {
            this.docRequesterId = docRequesterId;
        }

        public Date getDocRequestedDate() {
            return docRequestedDate;
        }

        public void setDocRequestedDate(String docRequestedDate) throws ParseException{
            DateFormat df = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss a", Locale.getDefault());
            this.docRequestedDate = df.parse(docRequestedDate);
        }

        public String getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }

        public String getLastUpdatedBy() {
            return lastUpdatedBy;
        }

        public void setLastUpdatedBy(String lastUpdatedBy) {
            this.lastUpdatedBy = lastUpdatedBy;
        }

        public Date getCreatedOn() {
            return createdOn;
        }

        public void setCreatedOn(String createdOn) throws ParseException {
            DateFormat df = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss a", Locale.getDefault());
            this.createdOn = df.parse(createdOn);
        }

        public Date getLastUpdatedOn() {
            return lastUpdatedOn;
        }

        public void setLastUpdatedOn(String lastUpdatedOn) throws ParseException {
            DateFormat df = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss a", Locale.getDefault());
            this.lastUpdatedOn = df.parse(lastUpdatedOn);
        }
    }
}
