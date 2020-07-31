package com.mad.p03.np2020.routine.Task.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mad.p03.np2020.routine.DAL.UserDBHelper;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.Task.ViewHolder.TeamViewHolder;
import com.mad.p03.np2020.routine.Task.model.GestureDetectorTaskSettings;
import com.mad.p03.np2020.routine.Task.model.GestureDetectorTeamItem;
import com.mad.p03.np2020.routine.Task.model.TeamDataListener;
import com.mad.p03.np2020.routine.models.Section;
import com.mad.p03.np2020.routine.models.Team;
import com.mad.p03.np2020.routine.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

public class TeamAdapter extends RecyclerView.Adapter<TeamViewHolder> implements TeamDataListener {

    Team mTeam;
    Context mContext;
    Section mSection;

    final static String TAG = "TeamAdapter";

    public TeamAdapter(Team team, Context context, Section section) {
        mTeam = team;
        mTeam.setEmail(new ArrayList<>());
        mSection = section;

        //This will trigger the TeamDataListener
        mTeam.getTeamFirebase(this);
        mContext = context;
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_team_item, parent, false);

        return new TeamViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {

        //Excute the runner to get profile details
        AsyncTaskRunner asyncTaskRunner = new AsyncTaskRunner(holder, mTeam.getEmail().get(position));
        asyncTaskRunner.execute();



        holder.txtEmail.setText(mTeam.getEmail().get(position));

        //Create the detector
        GestureDetectorTeamItem detectorTeamItem = new GestureDetectorTeamItem(this, position, holder);

        //Set the detector in the compat
        GestureDetectorCompat gestureDetectorCompat = new GestureDetectorCompat(mContext, detectorTeamItem);




        if(!mTeam.getEmail().get(position).equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()) && mSection.isAdmin() ){
            holder.mConstraintLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    gestureDetectorCompat.onTouchEvent(motionEvent);
                    return true;
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mTeam.getEmail().size();
    }


    @Override
    public void onDataAdd(String email) {
        mTeam.addEmail(email);

        notifyItemInserted(mTeam.getEmail().size()-1);
    }

    public List<String> getEmailList() {
        return mTeam.getEmail();
    }



    public void addEmail(String email){

        if(!mTeam.getEmail().contains(email)){
            //Add to the list
            mTeam.addEmail(email);
            synchronized(this){
                this.notifyItemInserted(mTeam.getEmail().size());
            }

            //Send to the firebase
            mTeam.excuteFirebaseUpload(email);
        }


    }

    public void deleteEmail(int position){

        //Delete from the firebase
        mTeam.excuteEmailDeleteFirebase(position);

        //Delete from the list
        mTeam.getEmail().remove(position);


        notifyItemRemoved(position);

    }



    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        String email;
        TextView mTextView;
        TeamViewHolder mHolder;

        public AsyncTaskRunner(TeamViewHolder holder, String email) {
            this.email = email;
            mTextView = holder.txtName;
            mHolder = holder;
        }

        @Override
        protected String doInBackground(String... params) {
            final String[] name = {"No name"};

            DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("users");

            userDatabase.orderByChild("Email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot snap:
                            dataSnapshot.getChildren()) {

                        name[0] = snap.child("Name").getValue(String.class);

                        //if its the owner add the owner text
                        if(snap.getKey().equals(mSection.getUID())){
                            name[0] = name[0] + " (Owner)";

                        }

                        mTextView.setText(name[0]);

                        //Get the ICON
                        StorageReference storageProfilePicture = FirebaseStorage.getInstance().getReference().child("ProfilePicture").child(snap.getKey() + ".jpg");

                        storageProfilePicture.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get().load(uri).into(mHolder.mImgPP);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "onFailure: Image path doesn't exist");
                            }
                        });






                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            return name[0];



        }


        @Override
        protected void onPostExecute(String result) {

        }


        @Override
        protected void onPreExecute() {

        }


        @Override
        protected void onProgressUpdate(String... text) {


        }
    }
}


