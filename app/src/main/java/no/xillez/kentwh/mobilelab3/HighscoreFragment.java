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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HighscoreFragment extends Fragment{

    private DatabaseReference root;
    private ListView highscoreListView;
    private Button button1;
    private Button button2;
    private Button button3;
    private ArrayList<String> scoreList;
    private ArrayList<String> userList;
    private ArrayAdapter adapter;
    private OnFragmentInteractionListener mListener;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onDBUpdate();

        button1.
    }

    public void debugfunction(){
        for(int i = 0; i < 10; i++){
            scoreList.add("yolo : " + i);

            Map<String, Object> map = new HashMap<>();
            map.put("u", "YOLO");
            map.put("m", scoreList.get(i));
            root.child("score").push().updateChildren(map);
        }
    }

    private void onDBUpdate(){
        this.root = FirebaseDatabase.getInstance().getReference().getRoot();

        root.child("score").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    scoreList.add(dataSnapshot1.getKey() + " : " + dataSnapshot1.getValue());

                }
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

    private void onClick(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_highscore, container, false);

        this.highscoreListView = view.findViewById(R.id.LW1);
        this.button1 = view.findViewById(R.id.B1);
        this.button2 = view.findViewById(R.id.B2);
        this.button3 = view.findViewById(R.id.B3);

        this.scoreList = new ArrayList<>();
        this.userList = new ArrayList<>();
        this.adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, scoreList){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView t1 = view.findViewById(android.R.id.text1);

                t1.setText(scoreList.get(position));

                return view;
            }
        };

        this.highscoreListView.setAdapter(adapter);

        debugfunction();

        adapter.notifyDataSetChanged();

        return view;
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
