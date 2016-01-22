package at.fhooe.mc.fahrtenbuch;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.SaveCallback;

import org.w3c.dom.Text;

import at.fhooe.mc.fahrtenbuch.database.parse.Trip;

public class FeedbackActivity extends Activity implements View.OnClickListener {
    private int selected = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

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
        b.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        ImageButton b = null;

        switch (v.getId()) {

            case R.id.button_smiley1: {
                b = (ImageButton) findViewById(R.id.button_smiley1);

                b.setBackground(getDrawable(R.drawable.smiley2));


                selected = 1;
                break;
            }
            case R.id.button_smiley2: {
                b = (ImageButton) findViewById(R.id.button_smiley1);
                b.setSelected(true);
                selected = 2;
                break;
            }
            case R.id.button_smiley3: {
                b = (ImageButton) findViewById(R.id.button_smiley1);
                b.setSelected(true);
                selected = 3;
                break;
            }
            case R.id.button_smiley4: {
                b = (ImageButton) findViewById(R.id.button_smiley1);
                b.setSelected(true);
                selected = 4;
                break;
            }
            case R.id.button_smiley5: {
                b = (ImageButton) findViewById(R.id.button_smiley1);
                b.setSelected(true);
                selected = 5;
                break;
            }
            case R.id.button_saveTrip: {
                Trip trip = App.trip;
                trip.setFeedback(selected);

                TextView description = (TextView)findViewById(R.id.textView_description);
                trip.setDescription(String.valueOf(description.getText()));

                trip.saveEventually(new SaveCallback() {

                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(FeedbackActivity.this, "Trip saved", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(FeedbackActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                break;
            }
        }
    }
}
