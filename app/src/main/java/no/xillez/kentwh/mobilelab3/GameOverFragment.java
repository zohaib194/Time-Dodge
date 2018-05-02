package no.xillez.kentwh.mobilelab3;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class GameOverFragment extends Fragment{

    private OnFragmentInteractionListener mListener;
    private SharedPreferences sharedPreferences;
    private CountDownTimer countDownTimer[];
    private boolean isAnimateDone = false;
    private DatabaseReference root;
    private ValueEventListener valueEventListener;

    public String userName = "";
    private Long bestScore;
    private Long newScore = 0L;
    private Long item;
    private Long bonus = 0L;
    private Long total;
    private TextView t1;
    private TextView t2;
    private TextView t3;
    private TextView t4;
    private TextView t5;
    private TextView t6;
    private TextView t7;
    private Button b1;
    private Button b2;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get shared preferences file in private mode.
        this.sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        // Get the data from shared preferences.
        this.bestScore = this.sharedPreferences.getLong(getString(R.string.preference_bestscore), 0L);
        this.item = this.sharedPreferences.getLong(getString(R.string.preference_item), 0L);
      //  this.bonus = this.sharedPreferences.getLong(getString(R.string.preference_bonus), 0L);
        this.total = (long)(newScore + (item * 0.5) + (bonus * 0.2));
        this.root = FirebaseDatabase.getInstance().getReference().getRoot();

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        // Inflate the view.
        View view = inflater.inflate(R.layout.fragment_gameover, container, false);

        // UI setup
        this.t1 = view.findViewById(R.id.frag_gameover_textview01);
        this.t2 = view.findViewById(R.id.frag_gameover_textview02);
        this.t3 = view.findViewById(R.id.frag_gameover_textview03);
        this.t4 = view.findViewById(R.id.frag_gameover_textview04);
        this.t5 = view.findViewById(R.id.frag_gameover_textview05);
        this.t6 = view.findViewById(R.id.frag_gameover_textview06);
        this.t7 = view.findViewById(R.id.frag_gameover_textview07);

        this.b1 = view.findViewById(R.id.frag_gameover_button01);
        this.b2 = view.findViewById(R.id.frag_gameover_button02);

        // Event Listeners on buttons
        this.b1.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MenuActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        this.b2.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), GameActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(getString(R.string.preference_username), userName);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Count down timer to increment numbers in textView.
        this.countDownTimer = new CountDownTimer[5];

        // "Animating" score, amount of items collected, bonus, and total numbers.
        animateTextView(countDownTimer[0], t2.getText().subSequence(0, 11), bestScore, "0", t2);
        animateTextView(countDownTimer[1], t3.getText().subSequence(0, 6), item, "0.5", t3);
        animateTextView(countDownTimer[2], t4.getText().subSequence(0, 6), bonus, "0.2", t4);
        animateTextView(countDownTimer[3], t5.getText().subSequence(0, 10), newScore, "0", t5);
        animateTextView(countDownTimer[4], t6.getText().subSequence(0, 6), total, "0", t6);

        removeCurrentScoreFromHighscore();

        if (this.newScore > this.bestScore)
            this.sharedPreferences.edit().putLong(getString(R.string.preference_bestscore), newScore).apply();
    }

    /**
     * Animate numbers in text view.
     * @param countDownTimer timer that runs every 5 milissec for 3 secs.
     * @param title title of fields.
     * @param value a number to reach while incrementing.
     * @param multiplier a number to multiply with score.
     * @param t textview to set the text.
     */
    private void animateTextView(CountDownTimer countDownTimer, final CharSequence title, final long value, final CharSequence multiplier, final TextView t){
        final Long[] temp = {0l};
        final long[] elapsedTime = {0l};
        final float[] floatItem = {0.0f};
        countDownTimer = new CountDownTimer(3000, 5) {
            @Override
            public void onTick(long millisUntilFinished) {

                //
                if (value == 0L)
                {
                    t.setText("");
                    if(multiplier == "0"){
                        t.setText(title + " " + value);
                    }else {
                        t.setText(title + " " + value + "X " + multiplier);
                    }
                    return;
                }

                // Items
                floatItem[0] += ((float)value / 3000.0f)*((3000 - millisUntilFinished) - elapsedTime[0]);
                if (floatItem[0] >= 1) {
                    temp[0] += (long)Math.floor(floatItem[0]);
                    floatItem[0] -= (long)Math.floor(floatItem[0]);

                    t.setText("");
                    if(multiplier == "0"){
                        t.setText(title + " " + temp[0]);
                    }else {
                        t.setText(title + " " + temp[0] + "X " + multiplier);
                    }
                }

                elapsedTime[0] = 3000 - millisUntilFinished;

            }

            @Override
            public void onFinish() {
                temp[0] = 0L;
                floatItem[0] = 0.0f;
                t.setText("");
                if(multiplier == "0") {
                    t.setText(title + " " + value);
                } else {
                    t.setText(title + " " + value + "X " + multiplier);
                }

                if(total > bestScore){
                    Animation anim = new AlphaAnimation(0.0f, 1.0f);
                    anim.setDuration(100);
                    anim.setStartOffset(20);
                    anim.setRepeatMode(Animation.REVERSE);
                    anim.setRepeatCount(Animation.INFINITE);
                    t7.startAnimation(anim);
                }
            }
        }.start();
    }

    private void saveScoreToFirebase(){
        if(userName != null && total > bestScore) {
            Map<String, Object> map = new HashMap<>();
            map.put("s", total);
            map.put("u", userName);
            root.child("score").push().updateChildren(map);
        }
    }


    private void removeCurrentScoreFromHighscore(){
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot entry : dataSnapshot.getChildren()) {
                    if (( entry.child("s").getValue()) != null && entry.child("u").getValue() != null) {
                        if(((long) entry.child("s").getValue()) < total && entry.child("u").getValue().equals(userName)) {
                            entry.getRef().setValue(null);
                        }
                    }
                }
                saveScoreToFirebase();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        root.child("score").addListenerForSingleValueEvent(valueEventListener);
    }

    public void setBonus(Long bonus) {
        this.bonus = bonus;
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
        if (context instanceof GameOverFragment.OnFragmentInteractionListener) {
            mListener = (GameOverFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public Long getNewScore()
    {
        return newScore;
    }

    public void setNewScore(Long score)
    {
        this.newScore = score;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
