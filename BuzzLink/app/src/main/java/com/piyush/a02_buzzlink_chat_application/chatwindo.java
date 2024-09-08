package com.piyush.a02_buzzlink_chat_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatwindo extends AppCompatActivity {
    private String receiverImg, receiverUid, receiverName, senderUID;
    private CircleImageView profile;
    private TextView receiverNName;
    private FirebaseDatabase database;
    private FirebaseAuth firebaseAuth;
    public static String senderImg;
    public static String receiverIImg;
    private CardView sendbtn;
    private EditText textmsg;
    private ImageView backIcon, menuIcon;

    private ArrayList<msgModelclass> messagessArrayList;
    private String senderRoom, receiverRoom;
    private RecyclerView messageRecyclerView;
    private messagesAdpter messagesAdpter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatwindo);

        // Initialize the back icon
        backIcon = findViewById(R.id.backIcon);
        menuIcon = findViewById(R.id.menuIcon); // Initialize the menu icon

        // Handle back icon click
        backIcon.setOnClickListener(v -> finish());

        // Handle menu icon click to show popup menu
        menuIcon.setOnClickListener(this::showPopupMenu);

        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        sendbtn = findViewById(R.id.sendbtnn);
        textmsg = findViewById(R.id.textmsg);
        receiverNName = findViewById(R.id.recivername);
        profile = findViewById(R.id.profileimgg);
        messageRecyclerView = findViewById(R.id.msgadpter);

        // Retrieve receiver details from intent
        receiverName = getIntent().getStringExtra("nameeee");
        receiverImg = getIntent().getStringExtra("reciverImg");
        receiverUid = getIntent().getStringExtra("uid");

        // Initialize message list and adapter
        messagessArrayList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageRecyclerView.setLayoutManager(linearLayoutManager);
        messagesAdpter = new messagesAdpter(this, messagessArrayList);
        messageRecyclerView.setAdapter(messagesAdpter);

        // Load receiver's profile image and name
        Picasso.get().load(receiverImg).into(profile);
        receiverNName.setText(receiverName);

        // Get sender's UID and set up chat rooms
        senderUID = firebaseAuth.getUid();
        senderRoom = senderUID + receiverUid;
        receiverRoom = receiverUid + senderUID;

        // Load sender's profile image
        DatabaseReference userRef = database.getReference().child("user").child(senderUID);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderImg = snapshot.child("profilepic").getValue(String.class);
                receiverIImg = receiverImg;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        // Load messages from the chat room
        DatabaseReference chatRef = database.getReference().child("chats").child(senderRoom).child("messages");
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagessArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    msgModelclass message = dataSnapshot.getValue(msgModelclass.class);
                    if (message != null) {
                        messagessArrayList.add(message);
                    }
                }
                messagesAdpter.notifyDataSetChanged();
                messageRecyclerView.scrollToPosition(messagessArrayList.size() - 1); // Scroll to the latest message
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        // Send message on button click
        sendbtn.setOnClickListener(view -> {
            String message = textmsg.getText().toString().trim();
            if (message.isEmpty()) {
                Toast.makeText(chatwindo.this, "Enter the Message First", Toast.LENGTH_SHORT).show();
                return;
            }
            textmsg.setText("");
            long timestamp = new Date().getTime();
            msgModelclass msg = new msgModelclass(senderUID, message, timestamp);

            // Save message to both sender's and receiver's chat rooms
            database.getReference().child("chats").child(senderRoom).child("messages").push()
                    .setValue(msg).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            database.getReference().child("chats").child(receiverRoom).child("messages").push()
                                    .setValue(msg);
                        }
                    });
        });
    }

    // Method to show the popup menu when the three dots are clicked
    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.chat_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(this::onMenuItemClick);
        popup.show();
    }

    // Handle menu item clicks
    private boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.add_to_favourite) {
            addToFavourite();
            return true;
        } else if (item.getItemId() == R.id.delete_chat) {
            deleteChat();
            return true;
        } else {
            return false;
        }
    }


    // Placeholder method for adding chat to favourites
    private void addToFavourite() {
        // Code to add chat to favourites
        Toast.makeText(this, "Added to Favourite", Toast.LENGTH_SHORT).show();
    }

    // Method to delete chat only from the sender's side
    private void deleteChat() {
        // Reference to the sender's chat room
        DatabaseReference senderChatRef = database.getReference().child("chats").child(senderRoom);

        // Delete chat from sender's chat room
        senderChatRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Clear the chat messages from the local list and notify the adapter
                messagessArrayList.clear();
                messagesAdpter.notifyDataSetChanged();
                Toast.makeText(chatwindo.this, "Chat Deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(chatwindo.this, "Failed to delete chat", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
