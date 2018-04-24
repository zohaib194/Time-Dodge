package no.xillez.kentwh.mobilelab3;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
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
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HighscoreFragment extends Fragment{

    private DatabaseReference root;
    private ListView highscoreListView;
    private Button button1;
    private Button button2;
    private Button button3;
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
        this.highscoreListView = view.findViewById(R.id.LW1);
        this.button1 = view.findViewById(R.id.B1);
        this.button2 = view.findViewById(R.id.B2);
        this.button3 = view.findViewById(R.id.B3);

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

                t1.setText(adapterUser.get(position));
                t2.setText(adapterScore.get(position).toString());

                return view;
            }
        };
        this.highscoreListView.setAdapter(adapter);

        onDBUpdate();

        // TODO : Sorting the arrays
        // Top 5 button on click listener
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scoreList.size() - 1 > 5) {
                    Long[] scoreArray = new Long[5];
                    String[] userArray = new String[5];
                    userArray = userList.subList(0, 5).toArray(userArray);
                    scoreArray = scoreList.subList(0, 5).toArray(scoreArray);

                    adapterScore.clear();
                    adapterUser.clear();

                    for(int i = 0; i < userArray.length; i++){
                        adapterUser.add(userArray[i]);
                        adapterScore.add(scoreArray[i]);
                    }

                    adapter.notifyDataSetChanged();
                }
            }
        });

        // TODO : fix button 2 and 3 to show correct amount of items.

        // Top 10 button on click listener
        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (scoreList.size() - 1 > 10) {

                    Long[] scoreArray = new Long[10];
                    String[] userArray = new String[10];
                    userArray = userList.subList(0, 10).toArray(userArray);
                    scoreArray = scoreList.subList(0, 10).toArray(scoreArray);

                    adapterScore.clear();
                    adapterUser.clear();

                    for(int i = 0; i < userArray.length; i++){
                        adapterUser.add(userArray[i]);
                        adapterScore.add(scoreArray[i]);
                    }

                    adapter.notifyDataSetChanged();
                }
            }
        });

        // Overall button on click listener
        button3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                adapterScore.clear();
                adapterUser.clear();
                for(int i = 0; i < userList.size(); i++){
                    adapterUser.add(userList.get(i));
                    adapterScore.add(scoreList.get(i));
                }
                adapter.notifyDataSetChanged();
            }
        });
        return view;
    }

    public void debugfunction(){
        scoreList.clear();
        for(long i = 0; i < 20; i++){
            scoreList.add(i);

            Map<String, Object> map = new HashMap<>();
            map.put("u", "zohaib" + i);
            map.put("s", i);
            root.child("score").push().updateChildren(map);
        }
        adapter.notifyDataSetChanged();
    }

    private void onDBUpdate(){
        root.child("score").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    if(dataSnapshot1.getKey().equals("u")){
                        userList.add((String) dataSnapshot1.getValue());
                        adapterUser.add((String) dataSnapshot1.getValue());

                    } else {
                        scoreList.add((Long) dataSnapshot1.getValue());
                        adapterScore.add((Long) dataSnapshot1.getValue());

                    }
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


    // sort the lists
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
        if (context instanceof MenuNavigationFragment.OnFragmentInteractionListener) {
            mListener = (HighscoreFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


}
