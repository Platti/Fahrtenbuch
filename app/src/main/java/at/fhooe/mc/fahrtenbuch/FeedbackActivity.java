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

public class FeedbackActivity extends Activity implements View.OnClickListener {
    private int selected = -1;
    Trip mTrip = App.trip;

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

    @Override
    public void onBackPressed() {
        //disable back button
    }

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

                selected = 1;
                break;
            }
            case R.id.button_smiley2: {
                b2.setBackground(getResources().getDrawable(R.drawable.feedback_2_y));
                selected = 2;
                break;
            }
            case R.id.button_smiley3: {
                b3.setBackground(getResources().getDrawable(R.drawable.feedback_3_y));
                selected = 3;
                break;
            }
            case R.id.button_smiley4: {
                b4.setBackground(getResources().getDrawable(R.drawable.feedback_4_y));
                selected = 4;
                break;
            }
            case R.id.button_smiley5: {
                b5.setBackground(getResources().getDrawable(R.drawable.feedback_5_y));

                selected = 5;
                break;
            }
            case R.id.button_saveTrip: {

                mTrip.setFeedback(selected);

                TextView description = (TextView)findViewById(R.id.textView_description);

                if (description.getText().toString().equals("")) {
                    Toast.makeText(FeedbackActivity.this, R.string.ins_description, Toast.LENGTH_SHORT).show();
                } else if (selected == -1) {
                    Toast.makeText(FeedbackActivity.this, R.string.cho_feedback, Toast.LENGTH_SHORT).show();
                } else {

                    TextView mileageText = (TextView)findViewById(R.id.textView_mileageFeed);

                    if (!(String.valueOf(App.car.getMileage() + mTrip.getDistance()).equals(mileageText.getText()))) {
                        App.car.setMileage(Integer.valueOf(String.valueOf(mileageText.getText())));
                    }

                    mTrip.setDescription(String.valueOf(description.getText()));

                    mTrip.saveEventually(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(FeedbackActivity.this, R.string.trip_sav, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(FeedbackActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

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

//                    Intent i = new Intent(FeedbackActivity.this, CarActivity.class);
//                    startActivity(i);
                    finish();
                }

                break;
            }
        }
    }
}
