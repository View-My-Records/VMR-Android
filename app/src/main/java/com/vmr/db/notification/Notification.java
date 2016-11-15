package com.vmr.db.notification;

import com.vmr.app.Vmr;
import com.vmr.model.NotificationItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * Created by abhijit on 10/4/16.
 */

public class Notification {

    private long masterId;
    private String masterOwner;

    private String  inboxId;
    private int     type;
    private String  toUserId;
    private String  subject;
    private boolean hasBody;
    private boolean isRead;
    private String  body;
    private Date    createdOn;
    private Date    updatedOn;
    private String  senderId;
    private String  senderFirstName;
    private String  senderLastName;
    private String  referenceId;
    private String documentId;

    public static List<Notification> getNotificationList(List<NotificationItem> notificationItemList){
        List<Notification> dbNotificationList = new ArrayList<>();
        Notification notificationItem;

        for (NotificationItem i : notificationItemList) {
            notificationItem = new Notification();
            notificationItem.setMasterId(0);
            notificationItem.setMasterOwner(Vmr.getLoggedInUserInfo().getLoggedinUserId());
            notificationItem.setInboxId(i.getInboxId());
            notificationItem.setType(i.getMailType());
            notificationItem.setToUserId(i.getToUserId());
            notificationItem.setSubject(i.getMailSubject());
            notificationItem.setHasBody(false);
            notificationItem.setBody(null);
            notificationItem.setRead(false);
            notificationItem.setCreatedOn(i.getCreatedOn());
            notificationItem.setUpdatedOn(new Date(System.currentTimeMillis()));
            notificationItem.setSenderId(i.getUserdetails().getSenderId());
            if(i.getUserdetails().getFirstName()!=null) {
                notificationItem.setSenderFirstName(i.getUserdetails().getFirstName());
                notificationItem.setSenderLastName(i.getUserdetails().getLastName());
            } else {
                notificationItem.setSenderLastName(i.getUserdetails().getCorporateName());
            }
            notificationItem.setReferenceId(i.getReferenceId());
            if(i.getDocumentAccessDetail() != null)
            notificationItem.setDocumentId(i.getDocumentAccessDetail().getDocId());
            dbNotificationList.add(notificationItem);
        }

        return dbNotificationList;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public long getMasterId() {
        return masterId;
    }

    public void setMasterId(long masterId) {
        this.masterId = masterId;
    }

    public String getMasterOwner() {
        return masterOwner;
    }

    public void setMasterOwner(String masterOwner) {
        this.masterOwner = masterOwner;
    }

    public String getInboxId() {
        return inboxId;
    }

    public void setInboxId(String inboxId) {
        this.inboxId = inboxId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean hasBody() {
        return hasBody;
    }

    public void setHasBody(boolean hasBody) {
        this.hasBody = hasBody;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Date getCreatedDate() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getUpdatedDate() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getSenderFirstName() {
        return senderFirstName;
    }

    public void setSenderFirstName(String senderFirstName) {
        this.senderFirstName = senderFirstName;
    }

    public String getSenderLastName() {
        return senderLastName;
    }

    public void setSenderLastName(String senderLastName) {
        this.senderLastName = senderLastName;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
