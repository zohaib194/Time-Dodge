package no.xillez.kentwh.mobilelab3;

import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
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

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class GameOverFragment extends Fragment{

    private SharedPreferences sharedPreferences;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    private boolean shouldShareScore;

    private String userName = "";
    private Long bestScore = 0L;
    private Long newScore = 0L;
    private Long item = 0L;
    private Long bonus = 0L;
    private Long total;
    private TextView textViewBestScore;
    private TextView textViewItems;
    private TextView textViewBonus;
    private TextView textViewNewScore;
    private TextView textViewTotalScore;
    private TextView textViewNewBest;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get shared settings file in private mode.
        this.sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        this.shouldShareScore = sharedPref.getBoolean("pref_ShareScore", true);


        // Get the data from shared preferences.
        if(this.bestScore != 0L){
            this.bestScore = this.sharedPreferences.getLong(getString(R.string.preference_bestscore), 0L);
        }

        this.root = FirebaseDatabase.getInstance().getReference().getRoot();

        updateTotal();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        // Inflate the view.
        View view = inflater.inflate(R.layout.fragment_gameover, container, false);

        // UI setup
        this.textViewBestScore = view.findViewById(R.id.frag_gameover_textview02);
        this.textViewItems = view.findViewById(R.id.frag_gameover_textview03);
        this.textViewBonus = view.findViewById(R.id.frag_gameover_textview04);
        this.textViewNewScore = view.findViewById(R.id.frag_gameover_textview05);
        this.textViewTotalScore = view.findViewById(R.id.frag_gameover_textview06);
        this.textViewNewBest = view.findViewById(R.id.frag_gameover_textview07);

        Button buttonMenu = view.findViewById(R.id.frag_gameover_button01);
        Button buttonRestart = view.findViewById(R.id.frag_gameover_button02);

        // Event Listeners on buttons
        buttonMenu.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MenuActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        buttonRestart.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), GameActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(getString(R.string.preference_username), userName);
            intent.putExtra(getString(R.string.preference_bestscore), bestScore);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // "Animating" score, amount of items collected, bonus, and total numbers.
        this.animateScoreScreen();
    }

    /**
     * Animate all the text view fields.
     */
    public void animateScoreScreen() {

        animateTextView(this.textViewBestScore.getText().subSequence(0, 11), this.bestScore, "0", this.textViewBestScore);
        animateTextView(this.textViewItems.getText().subSequence(0, 6), this.item, "2", this.textViewItems);
        animateTextView(this.textViewBonus.getText().subSequence(0, 6), this.bonus, "5", this.textViewBonus);
        animateTextView(this.textViewNewScore.getText().subSequence(0, 10), this.newScore, "0", this.textViewNewScore);
        animateTextView(this.textViewTotalScore.getText().subSequence(0, 6), this.total, "0", this.textViewTotalScore);

    }

    /**
     * Animate numbers in text view.
     * @param title title of fields.
     * @param value a number to reach while incrementing.
     * @param multiplier a number to multiply with score.
     * @param t text view to set the text.
     */
    private void animateTextView(final CharSequence title, final long value, final CharSequence multiplier, final TextView t){
        final Long[] temp = {0L};
        final Long[] elapsedTime = {0L};
        final float[] floatItem = {0.0f};
        CountDownTimer countDownTimer = new CountDownTimer(3000, 5) {
            @Override
            public void onTick(long millisUntilFinished) {

                // If value is 0.
                if (value == 0L) {
                    t.setText("");  // Clear the text field.
                    if (multiplier == "0") {    // if multiplier is 0.
                        t.setText(MessageFormat.format("{0} {1}", title, value)); // Set text without multiplier.
                    } else {
                        t.setText(MessageFormat.format("{0} {1} X{2}", title, value, multiplier)); // Show with multiplier.
                    }
                    return;
                }

                // Math for keep incrementing numbers til desired value for 3 secs.
                floatItem[0] += ((float) value / 3000.0f) * ((3000 - millisUntilFinished) - elapsedTime[0]);
                if (floatItem[0] >= 1) {
                    temp[0] += (long) Math.floor(floatItem[0]);
                    floatItem[0] -= (long) Math.floor(floatItem[0]);

                    t.setText("");
                    if (multiplier == "0") {
                        t.setText(MessageFormat.format("{0} {1}", title, temp[0]));
                    } else {
                        t.setText(MessageFormat.format("{0} {1} X{2}", title, temp[0], multiplier));
                    }
                }

                elapsedTime[0] = 3000 - millisUntilFinished;

            }

            @Override
            public void onFinish() {
                temp[0] = 0L;
                floatItem[0] = 0.0f;
                t.setText("");
                if (multiplier == "0") {
                    t.setText(MessageFormat.format("{0} {1}", title, value));
                } else {
                    t.setText(MessageFormat.format("{0} {1} X{2}", title, value, multiplier));
                }

                if (total > bestScore) {
                    Animation anim = new AlphaAnimation(0.0f, 1.0f);
                    anim.setDuration(100);
                    anim.setStartOffset(20);
                    anim.setRepeatMode(Animation.REVERSE);
                    anim.setRepeatCount(Animation.INFINITE);
                    textViewNewBest.startAnimation(anim);
                }
            }
        }.start();
    }

    /**
     * Save the score to FireBase.
     */
    private void saveScoreToFireBase(){
        if(this.userName != null && this.total > this.bestScore && this.shouldShareScore) {
            Map<String, Object> map = new HashMap<>();
            map.put("s", this.total);

            this.root.child("score").child(this.userName).updateChildren(map);
        }
    }

    /**
     * Removes current score from highscore.
     */
    public void removeCurrentScoreFromHighscore(){

        if (this.bestScore < this.total){
            this.bestScore = this.total;
            this.sharedPreferences.edit().putLong(getString(R.string.preference_bestscore), this.total).apply();
        }

        // If Share score is turn off and user name is not provided then return.
        if(!this.shouldShareScore || this.userName == null) {
            return;
        }

        // Event listener to find the user in FireBase.
        this.root.child("score").child(this.userName).addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long scoreFromFireBase = (Long)dataSnapshot.child("s").getValue();
                // if the user exist.
                if(dataSnapshot.exists()){
                    // Check if the current value in FireBase is less the our current total.
                    if(scoreFromFireBase != null && scoreFromFireBase < total) {
                        // Remove the value from FireBase.
                        dataSnapshot.getRef().removeValue();
                    }else{
                        return;
                    }
                }
                // The current value from FireBase is remove and can add current score to FireBase.
                saveScoreToFireBase();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    /**
     * Set the bonus and update Total.
     * @param bonus is new bonus to be set.
     */
    public void setBonus(Long bonus) {
        this.bonus = bonus;
        updateTotal();
    }

    /**
     * Set the best score.
     * @param bestScore new best score to be set.
     */
    public void setBestScore(long bestScore) {
        this.bestScore = bestScore;
    }

    /**
     * Calculate the total score and update.
     */
    private void updateTotal(){
        this.total = this.newScore + (this.item * 2L) + (this.bonus * 5L);
    }

    /**
     * Set the new score and update the total.
     * @param score is new score to be set.
     */
    public void setNewScore(Long score)
    {
        this.newScore = score;
        updateTotal();
    }

    /**
     * Set the number of item collected in game and update the total.
     * @param item is the number of items.
     */
    public void setItem(Long item)
    {
        this.item = item;
        updateTotal();
    }

    /**
     * Set the user name.
     * @param userName is name to be set.
     */
    public void setUserName(String userName) {
        this.userName = userName;
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

}