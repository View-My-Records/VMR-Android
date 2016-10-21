package com.vmr.model;

/*
 * Created by abhijit on 10/20/16.
 */

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

public class SearchResult {
    private String shared;     //: false;
    private String category;   //: "HCONF";
    private Date created;      //: "19-Oct-2016 06:33:01";
    private String name;       //: "20161018_233439.jpg";
    private String owner;      //: "674";
    private String path;       //: "My Records";
    private String lastname;   //: "Lname";
    private String noderef;    //: "9e32fbc0-44fb-4ed5-9c4a-24ea4e787bfd";
    private String doctypeid;  //: "vmrind_bill";
    private String firstname;  //: "Fname Lname";
    private String createdby;  //: "674";
    private String doctype;    //: "Bills and Warranty Cards";
    private String mimeType;   //: "application/octet-stream";

    public SearchResult(JSONObject resultJson) throws JSONException {
        this.setShared(resultJson.getString("shared"));
        this.setCategory(resultJson.getString("category"));
        this.setName(resultJson.getString("name"));
        this.setOwner(resultJson.getString("owner"));
        this.setPath(resultJson.getString("path"));
        this.setLastname(resultJson.getString("lastname"));
        this.setFirstname(resultJson.getString("firstname"));
        this.setNoderef(resultJson.getString("noderef"));
        this.setDoctype(resultJson.getString("doctype"));
        this.setDoctypeid(resultJson.getString("doctypeid"));
        this.setMimeType(resultJson.getString("mimeType"));

        String dateString = resultJson.getString("created");
        DateFormat df = new SimpleDateFormat("dd-MMM-yyyy kk:mm:ss", Locale.getDefault());
        Date result = null;
        try {
            result = df.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.setCreated(result);
        this.setCreatedby(resultJson.getString("createdby"));
    }

    public static List<SearchResult> getResultsList(JSONObject resultJson){
        List<SearchResult> results = new ArrayList<>();
        if(resultJson.has("result")) {
            try {
                Object o = resultJson.get("result");
                if(o instanceof JSONArray) {
                    JSONArray resultsArray = resultJson.getJSONArray("result");
                    SearchResult searchResult;
                    for (int i = 0; i < resultsArray.length(); i++) {
                        searchResult = new SearchResult(resultsArray.getJSONObject(i));
                        results.add(searchResult);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    public String getShared() {
        return shared;
    }

    public void setShared(String shared) {
        this.shared = shared;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getRecordName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getNodeRef() {
        return noderef;
    }

    public void setNoderef(String noderef) {
        this.noderef = noderef;
    }

    public String getDoctypeid() {
        return doctypeid;
    }

    public void setDoctypeid(String doctypeid) {
        this.doctypeid = doctypeid;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getCreatedby() {
        return createdby;
    }

    public void setCreatedby(String createdby) {
        this.createdby = createdby;
    }

    public String getDoctype() {
        return doctype;
    }

    public void setDoctype(String doctype) {
        this.doctype = doctype;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
