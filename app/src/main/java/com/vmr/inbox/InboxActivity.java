package com.vmr.inbox;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.vmr.R;
import com.vmr.app.Vmr;
import com.vmr.db.DbManager;
import com.vmr.db.notification.Notification;
import com.vmr.debug.VmrDebug;
import com.vmr.home.controller.MessageController;
import com.vmr.inbox.adapter.InboxAdapter;
import com.vmr.inbox.controller.AcceptController;
import com.vmr.inbox.controller.InboxController;
import com.vmr.inbox.controller.RejectController;
import com.vmr.inbox.controller.ViewController;
import com.vmr.model.NotificationItem;
import com.vmr.network.VmrRequestQueue;
import com.vmr.utils.Constants;
import com.vmr.utils.ErrorMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InboxActivity extends AppCompatActivity
        implements
        InboxAdapter.OnNotificationClickListener,
        InboxController.OnFetchInboxListener
{

    // Views
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private TextView mTextView;

    // Controllers
    private InboxController inboxController;
    private DbManager dbManager;

    // Variables
    private List<com.vmr.db.notification.Notification> notifications = new ArrayList<>();
    private InboxAdapter inboxAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        dbManager = Vmr.getDbManager();
        inboxAdapter = new InboxAdapter(notifications, this);
        inboxController = new InboxController(this);

        setupRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        inboxController.fetchNotifications();
        notifications = dbManager.getAllNotifications();
        inboxAdapter.updateDataset(notifications);
    }

    private void setupRecyclerView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mTextView = (TextView) findViewById(R.id.tvEmpty);
        mRecyclerView = (RecyclerView) findViewById(R.id.rvInbox);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                VmrRequestQueue.getInstance().cancelPendingRequest(Constants.Request.FolderNavigation.ListUnIndexed.TAG);
                inboxController.fetchNotifications();
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(inboxAdapter);
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onNotificationClick(final com.vmr.db.notification.Notification notification) {
        MessageController messageController = new MessageController(new MessageController.OnFetchMessageListener() {
            @Override
            public void onFetchMessageSuccess(JSONObject jsonObject) {
                VmrDebug.printLogI(InboxActivity.this.getClass(), "Message received");
                try {
                    if(jsonObject.has("mailBody")) {
                        dbManager.updateNotification(notification.getInboxId(), jsonObject.getString("mailBody"));

                        final AlertDialog.Builder alert = new AlertDialog.Builder(InboxActivity.this);
                        alert.setTitle("Message Details");

                        WebView wv = new WebView(InboxActivity.this);
                        wv.loadData(jsonObject.getString("mailBody"), "text/html", "utf-8");

                        alert.setView(wv);

                        if (notification.getType() == 4) {
                            alert.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                            alert.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialogInterface, int i) {
                                    VmrDebug.printLogI(InboxActivity.this.getClass(), "Accept clicked");
                                    AcceptController acceptController = new AcceptController(new AcceptController.OnAcceptListener() {
                                        @Override
                                        public void onAcceptSuccess(JSONObject response) {
                                            VmrDebug.printLogI(InboxActivity.this.getClass(), "Accept success");
                                            dialogInterface.dismiss();
                                        }

                                        @Override
                                        public void onAcceptFailure(VolleyError error) {
                                            VmrDebug.printLogI(InboxActivity.this.getClass(), "Accept failed");
                                            Toast.makeText(InboxActivity.this, "Accept failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    acceptController.accept(notification.getInboxId(), notification.getSenderId(), notification.getInboxId(), notification.getReferenceId());
                                }
                            });
                            alert.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialogInterface, int i) {
                                    VmrDebug.printLogI(InboxActivity.this.getClass(), "Reject clicked");
                                    AlertDialog.Builder commentDialog = new AlertDialog.Builder(InboxActivity.this);
                                    commentDialog.setTitle("Title");

                                    // Set up the input
                                    final EditText input = new EditText(InboxActivity.this);
                                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                                    commentDialog.setView(input);

                                    // Set up the buttons
                                    commentDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(final DialogInterface dialog, int which) {
                                            String comment = input.getText().toString();
                                            RejectController rejectController = new RejectController(new RejectController.OnRejectListener() {
                                                @Override
                                                public void onRejectSuccess(JSONObject response) {
                                                    VmrDebug.printLogI(InboxActivity.this.getClass(), "Reject success");
                                                    dialog.dismiss();
                                                    dialogInterface.dismiss();
                                                }

                                                @Override
                                                public void onRejectFailure(VolleyError error) {
                                                    VmrDebug.printLogI(InboxActivity.this.getClass(), "Reject failed");
                                                    Toast.makeText(InboxActivity.this, "Reject failed", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            rejectController.reject(notification.getInboxId(), comment, notification.getReferenceId());
                                        }
                                    });
                                    commentDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });

                                    commentDialog.show();
                                }
                            });
                        } else if (notification.getType() == 5 || notification.getType() == 6) {
                            alert.setPositiveButton("View", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialogInterface, int i) {
                                    VmrDebug.printLogI(InboxActivity.this.getClass(), "View clicked");
                                    ViewController viewController = new ViewController(new ViewController.OnViewListener() {
                                        @Override
                                        public void onViewSuccess(JSONObject response) {
                                            VmrDebug.printLogI(InboxActivity.this.getClass(), "View Success" + response.toString());
                                            if (response.has("result")) {
                                                try {
                                                    if (response.getString("result").equals("Expired")) {
                                                        new AlertDialog.Builder(InboxActivity.this)
                                                                .setTitle("Alert")
                                                                .setMessage("This File Has Expired")
                                                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                                        dialogInterface.dismiss();
                                                                    }
                                                                })
                                                                .show();
                                                    } else if (response.getString("result").equals("Revoked")) {
                                                        new AlertDialog.Builder(InboxActivity.this)
                                                                .setTitle("Alert")
                                                                .setMessage("One or more files has been Revoked by the owner of the file, rest are available in Shared With Me folder.")
                                                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                                        dialogInterface.dismiss();
                                                                    }
                                                                })
                                                                .show();
                                                    } else if (response.getString("result").equals("Success")) {
                                                        new AlertDialog.Builder(InboxActivity.this)
                                                                .setTitle("Alert")
                                                                .setMessage("Shared files are available in Shared With Me folder.")
                                                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                                        dialogInterface.dismiss();
                                                                        inboxController.fetchNotifications();
                                                                    }
                                                                })
                                                                .show();
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onViewFailure(VolleyError error) {
                                            VmrDebug.printLogI(InboxActivity.this.getClass(), "View Failed");
                                            Toast.makeText(InboxActivity.this, "View failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    viewController.viewMessage(notification.getDocumentId(), notification.getSenderId(), notification.getToUserId(), notification.getReferenceId());
                                }
                            });
                            alert.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                        } else {
                            alert.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                        }
                        alert.show();
                    } else {
                        dbManager.updateNotificationReadFlag(notification.getInboxId());
                    }
                    notifications = dbManager.getAllNotifications();
                    inboxAdapter.updateDataset(notifications);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFetchMessageFailure(VolleyError error) {
                VmrDebug.printLogI(InboxActivity.this.getClass(), "Message fetch failed");
            }
        });

        messageController.fetchMessage(notification);

        VmrDebug.printLogI(this.getClass(), "Mailtype ->" + notification.getType());
    }

    @Override
    public void onFetchNotificationsSuccess(List<NotificationItem> notificationItemList) {
        if(notificationItemList != null && notificationItemList.size() != 0) {
            VmrDebug.printLogI( this.getClass() ,notificationItemList.size() + " Notifications fetched.");
            List<Notification> notifications = Notification.getNotificationList(notificationItemList);
            dbManager.removeAllNotifications(notifications);
            dbManager.updateAllNotifications(notifications);
        } else {
            dbManager.removeAllNotifications();
        }

        notifications = dbManager.getAllNotifications();
        inboxAdapter.updateDataset(notifications);

        mSwipeRefreshLayout.setRefreshing(false);

        if(notifications.isEmpty()){
            mRecyclerView.setVisibility(View.GONE);
            mTextView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFetchNotificationsFailure(VolleyError error) {
        mSwipeRefreshLayout.setRefreshing(false);
        Toast.makeText(Vmr.getVMRContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
    }
}
