package com.vmr.model;

/*
 * Created by abhijit on 12/5/16.
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RecordDetails {

//    "fileNoderef": "workspace://SpacesStore/cba12e9d-b975-4a92-b5fa-e5c521f12ae8",
    public static final String FILE_NODEREF = "fileNoderef";
//    "status": "null",
    public static final String STATUS = "status";
//    "DoctypeName": "Medical/Health Care",
    public static final String DOCTYPE_NAME = "DoctypeName";
//    "name": "image_apple_100kb.jpg",
    public static final String NAME = "name";
//    "owner": "674",
    public static final String OWNER = "owner";
//    "DoctypeID": "vmrind_healthcare",
    public static final String DOCTYPE_ID = "DoctypeID";
    /**
        "Properties": [
             {
             "Name": "vmr_quickref",
             "Description": "04. Quick Reference",
             "Value": "duck"
             },
             {
             "Name": "vmr_expirydate",
             "Description": "05. Expiry Date",
             "Value": "Mon Sep 13 00:00:00 UTC 2021"
             },
             {
             "Name": "vmr_doclifespan",
             "Description": "06. Lifespan",
             "Value": "5"
             },
             {
             "Name": "vmr_geotag",
             "Description": "07. Geotag",
             "Value": "dubbin"
             },
             {
             "Name": "vmr_remarks",
             "Description": "08. Remarks",
             "Value": "dybbuk"
             },
             {
             "Name": "vmr_category",
             "Description": "09. Category",
             "Value": "CONF"
             },
             {
             "Name": "vmr_reminderdate",
             "Description": "12. Next Best Action",
             "Value": "Fri Oct 06 00:23:25 UTC 2017"
             },
             {
             "Name": "vmr_remindermessage",
             "Description": "13. Action Message",
             "Value": "duckbill"
             },
             {
             "Name": "vmr_commercialtype",
             "Description": "Commercial Type",
             "Value": "prepaid"
             },
             {
             "Name": "vmr_displayname",
             "Description": "Display Name of folder",
             "Value": "testing"
             },
             {
             "Name": "vmr_domain",
             "Description": "Domain Name",
             "Value": "individual"
             },
             {
             "Name": "vmr_foldercategory",
             "Description": "Folder Category",
             "Value": "IndUser"
             },
             {
             "Name": "vmr_membertype",
             "Description": "Member Type",
             "Value": "IND"
             },
             {
             "Name": "vmr_membername",
             "Description": "No Description",
             "Value": "Fname Lname"
             },
             {
             "Name": "vmr_memberid",
             "Description": "No Description",
             "Value": "workspace://SpacesStore/3f3fb831-e148-4d77-a907-7ccde58155cb"
             },
             {
             "Name": "vmr_pages",
             "Description": "No of pages",
             "Value": "0"
             },
             {
             "Name": "vmr_templateid",
             "Description": "Template Id",
             "Value": "vmrINDStandard"
             }
         ]
     */
    public static final String QUICKREF = "vmr_quickref";
    public static final String EXPIRYDATE = "vmr_expirydate";
    public static final String DOCLIFESPAN = "vmr_doclifespan";
    public static final String GEOTAG = "vmr_geotag";
    public static final String REMARKS = "vmr_remarks";
    public static final String CATEGORY = "vmr_category";
    public static final String REMINDERDATE = "vmr_reminderdate";
    public static final String REMINDERMESSAGE = "vmr_remindermessage";
    public static final String COMMERCIALTYPE = "vmr_commercialtype";
    public static final String DISPLAYNAME = "vmr_displayname";
    public static final String DOMAIN = "vmr_domain";
    public static final String FOLDERCATEGORY = "vmr_foldercategory";
    public static final String MEMBERTYPE = "vmr_membertype";
    public static final String MEMBERNAME = "vmr_membername";
    public static final String MEMBERID = "vmr_memberid";
    public static final String PAGES = "vmr_pages";
    public static final String TEMPLATEID = "vmr_templateid";

    private String nodeRef;
    private String status;
    private String docType;
    private String recordName;
    private String owner;
    private String docTypeId;

    private Map<String, String> properties;

    public static RecordDetails parseDetails(JSONObject jsonObject) throws JSONException{
        RecordDetails recordDetails = new RecordDetails();
        if(jsonObject.has(FILE_NODEREF)) recordDetails.setNodeRef(jsonObject.getString(FILE_NODEREF));
        if(jsonObject.has(STATUS)) recordDetails.setStatus(jsonObject.getString(STATUS));
        if(jsonObject.has(DOCTYPE_NAME)) recordDetails.setDocType(jsonObject.getString(DOCTYPE_NAME));
        if(jsonObject.has(NAME)) recordDetails.setRecordName(jsonObject.getString(NAME));
        if(jsonObject.has(OWNER)) recordDetails.setOwner(jsonObject.getString(OWNER));
        if(jsonObject.has(DOCTYPE_ID)) recordDetails.setDocTypeId(jsonObject.getString(DOCTYPE_ID));

        if(jsonObject.has("Properties")) {
            JSONArray jsonArray = jsonObject.getJSONArray("Properties");
            recordDetails.setProperties(getPropertiesMap(jsonArray));
        }

        return recordDetails;
    }

    private static Map<String, String> getPropertiesMap(JSONArray properties) throws JSONException {
        Map<String, String> propertiesMap = new HashMap<>();
        JSONObject jsonObject;
        for (int i = 0; i < properties.length() - 1; i++) {
            jsonObject = properties.getJSONObject(i);
            propertiesMap.put(jsonObject.getString("propertyName"), jsonObject.getString("propertyValue"));
        }

        return propertiesMap;
    }

    public static String getFileNoderef() {
        return FILE_NODEREF;
    }

    public static String getSTATUS() {
        return STATUS;
    }

    public static String getDoctypeName() {
        return DOCTYPE_NAME;
    }

    public static String getNAME() {
        return NAME;
    }

    public static String getOWNER() {
        return OWNER;
    }

    public static String getDoctypeId() {
        return DOCTYPE_ID;
    }

    public static String getQUICKREF() {
        return QUICKREF;
    }

    public static String getEXPIRYDATE() {
        return EXPIRYDATE;
    }

    public static String getDOCLIFESPAN() {
        return DOCLIFESPAN;
    }

    public static String getGEOTAG() {
        return GEOTAG;
    }

    public static String getREMARKS() {
        return REMARKS;
    }

    public static String getCATEGORY() {
        return CATEGORY;
    }

    public static String getREMINDERDATE() {
        return REMINDERDATE;
    }

    public static String getREMINDERMESSAGE() {
        return REMINDERMESSAGE;
    }

    public static String getCOMMERCIALTYPE() {
        return COMMERCIALTYPE;
    }

    public static String getDISPLAYNAME() {
        return DISPLAYNAME;
    }

    public static String getDOMAIN() {
        return DOMAIN;
    }

    public static String getFOLDERCATEGORY() {
        return FOLDERCATEGORY;
    }

    public static String getMEMBERTYPE() {
        return MEMBERTYPE;
    }

    public static String getMEMBERNAME() {
        return MEMBERNAME;
    }

    public static String getMEMBERID() {
        return MEMBERID;
    }

    public static String getPAGES() {
        return PAGES;
    }

    public static String getTEMPLATEID() {
        return TEMPLATEID;
    }

    public String getNodeRef() {
        return nodeRef;
    }

    public void setNodeRef(String nodeRef) {
        this.nodeRef = nodeRef;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getRecordName() {
        return recordName;
    }

    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDocTypeId() {
        return docTypeId;
    }

    public void setDocTypeId(String docTypeId) {
        this.docTypeId = docTypeId;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
