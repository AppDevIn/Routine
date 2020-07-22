package com.mad.p03.np2020.routine.Task.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.Task.ViewHolder.TeamViewHolder;
import com.mad.p03.np2020.routine.Task.model.TeamDataListener;
import com.mad.p03.np2020.routine.models.Section;
import com.mad.p03.np2020.routine.models.Team;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
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

    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {

        //Excute the runner to get profile details
        AsyncTaskRunner asyncTaskRunner = new AsyncTaskRunner(holder.txtName, mTeam.getEmail().get(position));
        asyncTaskRunner.execute();

        holder.txtEmail.setText(mTeam.getEmail().get(position));
        holder.txtEmail.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d(TAG, "onLongClick: " + mTeam.getEmail().get(position) + " is being removed from " + mTeam.getSectionID());

                deleteEmail(position);

                return false;
            }
        });
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

        //Add to the list
        mTeam.addEmail(email);
        synchronized(this){
            this.notify();
        }

        //Send to the firebase
        mTeam.excuteFirebaseUpload(email);

    }

    private void deleteEmail(int position){

        //Delete from the firebase
        mTeam.excuteEmailDeleteFirebase(position);

        //Delete from the list
        mTeam.getEmail().remove(position);


        notifyItemRemoved(position);

    }



    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        String email;
        TextView mTextView;
        public AsyncTaskRunner(TextView view, String email) {
            this.email = email;
            mTextView = view;
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

                        //TODO: Get the ICON





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

