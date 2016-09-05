package com.vmr.db;

/*
 * Created by abhijit on 9/3/16.
 */

import com.vmr.model.VmrFolder;

import java.util.Date;

public class vmrItem {

    private int ITEM_ID ;                // integer NOT NULL CONSTRAINT ITEMS_pk PRIMARY KEY AUTOINCREMENT,
    private String ITEM_NODE_REF;           //  varchar(60) NOT NULL,
    private String ITEM_PARENT_NODE_REF ;   // varchar(60) NOT NULL,
    private String ITEM_NAME;               //  varchar(100) NOT NULL,
    private String ITEM_DOC_TYPE ;          // integer NOT NULL,
    private String FOLDER_CATEGORY;         //  varchar(100) NOT NULL,
    private String FILE_CATEGORY ;          // varchar(20) NOT NULL,
    private int FILE_SIZE;                  //  integer NOT NULL,
    private String FILE_MIME_;              // TYPE varchar(100) NOT NULL,
    private boolean IS_FOLDER;               //  boolean NOT NULL,
    private boolean IS_SHARED ;              // boolean NOT NULL,
    private int WRITE ;                  // integer NOT NULL,
    private int DELETE ;                 // integer NOT NULL,
    private String ITEM_OWNER ;             // varchar(100) NOT NULL,
    private Date CREATED_BY ;             // integer NOT NULL,
    private String CREATED_DATE ;           // integer NOT NULL,
    private Date UPDATED_BY;              //  integer NOT NULL,
    private String UPDATED_DATE ;           // integer NOT NULL

    public vmrItem(VmrFolder vmrFolder) {

    }

    public String getITEM_PARENT_NODE_REF() {
        return ITEM_PARENT_NODE_REF;
    }

    public void setITEM_PARENT_NODE_REF(String ITEM_PARENT_NODE_REF) {
        this.ITEM_PARENT_NODE_REF = ITEM_PARENT_NODE_REF;
    }

    public int getITEM_ID() {
        return ITEM_ID;
    }

    public void setITEM_ID(int ITEM_ID) {
        this.ITEM_ID = ITEM_ID;
    }

    public String getITEM_NODE_REF() {
        return ITEM_NODE_REF;
    }

    public void setITEM_NODE_REF(String ITEM_NODE_REF) {
        this.ITEM_NODE_REF = ITEM_NODE_REF;
    }

    public String getITEM_NAME() {
        return ITEM_NAME;
    }

    public void setITEM_NAME(String ITEM_NAME) {
        this.ITEM_NAME = ITEM_NAME;
    }

    public String getITEM_DOC_TYPE() {
        return ITEM_DOC_TYPE;
    }

    public void setITEM_DOC_TYPE(String ITEM_DOC_TYPE) {
        this.ITEM_DOC_TYPE = ITEM_DOC_TYPE;
    }

    public String getFOLDER_CATEGORY() {
        return FOLDER_CATEGORY;
    }

    public void setFOLDER_CATEGORY(String FOLDER_CATEGORY) {
        this.FOLDER_CATEGORY = FOLDER_CATEGORY;
    }

    public String getFILE_CATEGORY() {
        return FILE_CATEGORY;
    }

    public void setFILE_CATEGORY(String FILE_CATEGORY) {
        this.FILE_CATEGORY = FILE_CATEGORY;
    }

    public int getFILE_SIZE() {
        return FILE_SIZE;
    }

    public void setFILE_SIZE(int FILE_SIZE) {
        this.FILE_SIZE = FILE_SIZE;
    }

    public String getFILE_MIME_() {
        return FILE_MIME_;
    }

    public void setFILE_MIME_(String FILE_MIME_) {
        this.FILE_MIME_ = FILE_MIME_;
    }

    public boolean IS_FOLDER() {
        return IS_FOLDER;
    }

    public void setIS_FOLDER(boolean IS_FOLDER) {
        this.IS_FOLDER = IS_FOLDER;
    }

    public boolean IS_SHARED() {
        return IS_SHARED;
    }

    public void setIS_SHARED(boolean IS_SHARED) {
        this.IS_SHARED = IS_SHARED;
    }

    public int getWRITE() {
        return WRITE;
    }

    public void setWRITE(int WRITE) {
        this.WRITE = WRITE;
    }

    public int getDELETE() {
        return DELETE;
    }

    public void setDELETE(int DELETE) {
        this.DELETE = DELETE;
    }

    public String getITEM_OWNER() {
        return ITEM_OWNER;
    }

    public void setITEM_OWNER(String ITEM_OWNER) {
        this.ITEM_OWNER = ITEM_OWNER;
    }

    public Date getCREATED_BY() {
        return CREATED_BY;
    }

    public void setCREATED_BY(Date CREATED_BY) {
        this.CREATED_BY = CREATED_BY;
    }

    public String getCREATED_DATE() {
        return CREATED_DATE;
    }

    public void setCREATED_DATE(String CREATED_DATE) {
        this.CREATED_DATE = CREATED_DATE;
    }

    public Date getUPDATED_BY() {
        return UPDATED_BY;
    }

    public void setUPDATED_BY(Date UPDATED_BY) {
        this.UPDATED_BY = UPDATED_BY;
    }

    public String getUPDATED_DATE() {
        return UPDATED_DATE;
    }

    public void setUPDATED_DATE(String UPDATED_DATE) {
        this.UPDATED_DATE = UPDATED_DATE;
    }
}
