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

    private String masterOwner;

    private String    id;
    private int     type;
    private String  subject;
    private boolean hasBody;
    private String  body;
    private Date    createdOn;
    private Date    updatedOn;
    private String  senderFirstName;
    private String  senderLastName;

    public static List<Notification> getNotificationList(List<NotificationItem> notificationItemList){
        List<Notification> dbNotificationList = new ArrayList<>();
        Notification notificationItem;

        for (NotificationItem i : notificationItemList) {
            notificationItem = new Notification();
            notificationItem.setMasterOwner(Vmr.getLoggedInUserInfo().getLoggedinUserId());
            notificationItem.setId(i.getInboxId());
            notificationItem.setType(i.getMailType());
            notificationItem.setSubject(i.getMailSubject());
            notificationItem.setHasBody(false);
            notificationItem.setBody(null);
            notificationItem.setCreatedOn(i.getCreatedOn());
            notificationItem.setUpdatedOn(new Date(System.currentTimeMillis()));
            notificationItem.setSenderFirstName(i.getUserdetails().getFirstName());
            notificationItem.setSenderLastName(i.getUserdetails().getLastName());
            dbNotificationList.add(notificationItem);
        }


        return dbNotificationList;
    }

    public String getMasterOwner() {
        return masterOwner;
    }

    public void setMasterOwner(String masterOwner) {
        this.masterOwner = masterOwner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public boolean isHasBody() {
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

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date    getUpdatedOn() {
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
}
