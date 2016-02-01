package at.fhooe.mc.fahrtenbuch;

import android.app.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.SaveCallback;


import at.fhooe.mc.fahrtenbuch.database.parse.Trip;

/**
 * Activity to get feedback and description for a trip
 */
public class FeedbackActivity extends Activity implements View.OnClickListener {

    /**
     * number of the selected feedback from 1-5, -1 nothing selected
     */
    private int selectedFeedback = -1;

    /**
     * the trip to save and to add the feedback
     */
    Trip mTrip = App.trip;

    /**
     * Sets the title in the actionbar, sets the onclicklistener for the feedback imagebuttons
     * and sets the text for the mileage textView.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        setTitle(R.string.feedback);

        ImageButton b = null;

        b = (ImageButton) findViewById(R.id.button_smiley1);
        b.setOnClickListener(this);
        b = (ImageButton) findViewById(R.id.button_smiley2);
        b.setOnClickListener(this);
        b = (ImageButton) findViewById(R.id.button_smiley3);
        b.setOnClickListener(this);
        b = (ImageButton) findViewById(R.id.button_smiley4);
        b.setOnClickListener(this);
        b = (ImageButton) findViewById(R.id.button_smiley5);
        b.setOnClickListener(this);

        Button q = null;
        q = (Button) findViewById(R.id.button_saveTrip);
        q.setOnClickListener(this);

        TextView textViewMileage = (TextView) findViewById(R.id.textView_mileageFeed);
        textViewMileage.setText(String.valueOf(App.car.getMileage() + mTrip.getDistance()));
    }

    /**
     * The back button is disabled to not get bac to the maps view.
     */
    @Override
    public void onBackPressed() {
        //disable back button
    }

    /**
     * Is called if a button is clicked.
     * Sets the selected images for every button, sets the selectedFeedback
     * and if the save button is clicked: checks if feedback and description is given
     * and saves the trip with that information.
     * @param v button that was clicked
     */
    @Override
    public void onClick(View v) {

        ImageButton b1 = null;
        ImageButton b2 = null;
        ImageButton b3 = null;
        ImageButton b4 = null;
        ImageButton b5 = null;

        b1 = (ImageButton) findViewById(R.id.button_smiley1);

        b1.setBackground(getResources().getDrawable(R.drawable.feedback_1_n));
        b2 = (ImageButton) findViewById(R.id.button_smiley2);
        b2.setBackground(getResources().getDrawable(R.drawable.feedback_2_n));
        b3 = (ImageButton) findViewById(R.id.button_smiley3);
        b3.setBackground(getResources().getDrawable(R.drawable.feedback_3_n));
        b4 = (ImageButton) findViewById(R.id.button_smiley4);
        b4.setBackground(getResources().getDrawable(R.drawable.feedback_4_n));
        b5 = (ImageButton) findViewById(R.id.button_smiley5);
        b5.setBackground(getResources().getDrawable(R.drawable.feedback_5_n));

        switch (v.getId()) {

            case R.id.button_smiley1: {
                b1.setBackground(getResources().getDrawable(R.drawable.feedback_1_y));

                selectedFeedback = 1;
                break;
            }
            case R.id.button_smiley2: {
                b2.setBackground(getResources().getDrawable(R.drawable.feedback_2_y));
                selectedFeedback = 2;
                break;
            }
            case R.id.button_smiley3: {
                b3.setBackground(getResources().getDrawable(R.drawable.feedback_3_y));
                selectedFeedback = 3;
                break;
            }
            case R.id.button_smiley4: {
                b4.setBackground(getResources().getDrawable(R.drawable.feedback_4_y));
                selectedFeedback = 4;
                break;
            }
            case R.id.button_smiley5: {
                b5.setBackground(getResources().getDrawable(R.drawable.feedback_5_y));

                selectedFeedback = 5;
                break;
            }
            case R.id.button_saveTrip: {

                mTrip.setFeedback(selectedFeedback);

                TextView description = (TextView)findViewById(R.id.textView_description);
                TextView mileageText = (TextView)findViewById(R.id.textView_mileageFeed);

                //check if everything is given
                if (description.getText().toString().equals("")) {
                    Toast.makeText(FeedbackActivity.this, getString(R.string.ins_description), Toast.LENGTH_SHORT).show();
                } else if (selectedFeedback == -1) {
                    Toast.makeText(FeedbackActivity.this, getString(R.string.cho_feedback), Toast.LENGTH_SHORT).show();
                } else if (App.car.getMileage() > Integer.valueOf(String.valueOf(mileageText.getText()))) {
                    Toast.makeText(FeedbackActivity.this, getString(R.string.mil_wrong), Toast.LENGTH_SHORT).show();
                } else {
                    App.car.setMileage(Integer.valueOf(String.valueOf(mileageText.getText())));

                    mTrip.setDescription(String.valueOf(description.getText()));

                    //save trip
                    mTrip.saveEventually(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(FeedbackActivity.this, getString(R.string.trip_sav), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(FeedbackActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    // save car
                    App.car.saveEventually(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("Fahrtenbuch", "Car saved!");
                            } else {
                                Toast.makeText(FeedbackActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    finish();
                }

                break;
            }
        }
    }
}
