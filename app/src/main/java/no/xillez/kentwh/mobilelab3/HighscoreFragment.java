package no.xillez.kentwh.mobilelab3;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class HighscoreFragment extends Fragment{

    // FireBase
    private DatabaseReference root;

    // Lists
    private ArrayList<Long> scoreList;
    private ArrayList<Long> adapterScore;
    private ArrayList<String> userList;
    private ArrayList<String> adapterUser;
    private ArrayAdapter adapter;

    private OnFragmentInteractionListener mListener;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        // Inflate the view.
        View view = inflater.inflate(R.layout.fragment_highscore, container, false);

        // DB connection.
        this.root = FirebaseDatabase.getInstance().getReference().getRoot();

        // UI setup.
        ListView listViewHighScore = view.findViewById(R.id.frag_highscore_listview01);
        Button buttonTopFive = view.findViewById(R.id.frag_highscore_button01);
        Button buttonTopTen = view.findViewById(R.id.frag_highscore_button02);
        Button buttonOverAll = view.findViewById(R.id.frag_highscore_button03);

        // Array lists and setup array list adapter
        this.scoreList = new ArrayList<>();
        this.userList = new ArrayList<>();
        this.adapterScore = new ArrayList<>();
        this.adapterUser = new ArrayList<>();

        this.adapter = new ArrayAdapter<String>(getActivity(), R.layout.highscore_layout, R.id.text1, adapterUser){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView t1 = view.findViewById(R.id.text1);
                TextView t2 = view.findViewById(R.id.text2);

                t1.setText(MessageFormat.format("{0}. {1}", position + 1, adapterUser.get(position)));
                t2.setText(MessageFormat.format("{0}", adapterScore.get(position)));

                return view;
            }
        };
        listViewHighScore.setAdapter(this.adapter);

        onDBUpdate();

        // Top 5 button on click listener
        buttonTopFive.setOnClickListener(v -> {
            if (this.scoreList.size() - 1 > 5) {
                Long[] scoreArray = new Long[5];
                String[] userArray = new String[5];
                userArray = this.userList.subList(0, 5).toArray(userArray);
                scoreArray = this.scoreList.subList(0, 5).toArray(scoreArray);

                this. adapterScore.clear();
                this.adapterUser.clear();

                for(int i = 0; i < userArray.length; i++){
                    this.adapterUser.add(userArray[i]);
                    this.adapterScore.add(scoreArray[i]);
                }

                this.adapter.notifyDataSetChanged();
            }
        });

        // Top 10 button on click listener
        buttonTopTen.setOnClickListener(v -> {
            if (this.scoreList.size() - 1 > 10) {

                Long[] scoreArray = new Long[10];
                String[] userArray = new String[10];
                userArray = this.userList.subList(0, 10).toArray(userArray);
                scoreArray = this.scoreList.subList(0, 10).toArray(scoreArray);

                this.adapterScore.clear();
                this.adapterUser.clear();

                for(int i = 0; i < userArray.length; i++){
                    this.adapterUser.add(userArray[i]);
                    this.adapterScore.add(scoreArray[i]);
                }

                this.adapter.notifyDataSetChanged();
            }
        });

        // Overall button on click listener
        buttonOverAll.setOnClickListener(v -> {
            this.adapterScore.clear();
            this.adapterUser.clear();
            for(int i = 0; i < userList.size(); i++){
                this.adapterUser.add(userList.get(i));
                this.adapterScore.add(scoreList.get(i));
            }
            this.adapter.notifyDataSetChanged();
        });
        return view;
    }

    /**
     * Function update the user and score list once there is new entry in FireBase.
     */
    private void onDBUpdate(){
        this.root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot user : dataSnapshot.getChildren()){

                    userList.add( user.getKey() );
                    adapterUser.add( user.getKey() );

                    scoreList.add((Long) user.child("s").getValue());
                    adapterScore.add((Long) user.child("s").getValue());
                }
                sort(scoreList, userList, 0, scoreList.size() - 1);
                sort(adapterScore, adapterUser, 0, adapterScore.size() - 1);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Sort the list.
     * @param sList score list to be sorted.
     * @param uList user list to be sorted.
     * @param low is the lowest index in the sublist.
     * @param high is the highest index in the sublist.
     */
    private void sort(List<Long> sList, List<String> uList, int low, int high){
        int i = low, j = high;

        Long pivot = sList.get(low + (high-low)/2);

        while (i <= j) {

            while (sList.get(i) > pivot) {
                i++;
            }
            while (sList.get(j) < pivot) {
                j--;
            }

            if (i <= j) {
                // swap
                Long temp = sList.get(i);
                sList.set(i, sList.get(j));
                sList.set(j, temp);

                String temp1 = uList.get(i);
                uList.set(i, uList.get(j));
                uList.set(j, temp1);

                i++;
                j--;
            }
        }

        if (low < j)
            sort(sList, uList, low, j);
        if (i < high)
            sort(sList, uList, i, high);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            this.mListener = (HighscoreFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

}
