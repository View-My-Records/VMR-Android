package com.vmr.login.controller.request;


import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vmr.network.PreLoginRequest;
import com.vmr.utils.Constants;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/*
 * Created by abhijit on 8/17/16.
 */

public class AlfrescoTicketRequest extends PreLoginRequest<String> {

    public AlfrescoTicketRequest( Response.Listener<String> successListener, Response.ErrorListener errorListener) {
        super(Method.GET, Constants.Url.ALFRESCO_TICKET, successListener, errorListener);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        byte[] responseBytes = response.data;
        String ticket;
        try {
            DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(responseBytes));
            ticket = parse.getFirstChild().getTextContent();
        } catch (Exception e) {
            // handle IOException
            return Response.error(new VolleyError("Failed to retrieve ticket"));
        }

        return Response.success(ticket, getCacheEntry());
    }

}
