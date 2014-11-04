package trial.calender.custom.customcalender;

import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import trial.calender.custom.com.roomorama.caldroid.CaldroidFragment;
import trial.calender.custom.com.roomorama.caldroid.CaldroidListener;


public class MainActivity extends FragmentActivity {

  private CaldroidFragment dialogCaldroidFragment;
  private Bundle state;
  final String dialogTag = "CALDROID_DIALOG_FRAGMENT";


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.custom_calendar);
    state = savedInstanceState;
    CaldroidFragment.disabledBackgroundDrawable = R.color.caldroid_white;
    CaldroidFragment.disabledTextColor = Color.GRAY;
    dialogCaldroidFragment = new CaldroidFragment();
    Calendar calendar = Calendar.getInstance();
    dialogCaldroidFragment.setMinDate(calendar.getTime());
    calendar.add(Calendar.DATE, 40);
    dialogCaldroidFragment.setMaxDate(calendar.getTime());

  }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

  private Date outboundDate;
  private Date inboundDate;

  private CaldroidListener getCaldroidListener(final String tripType) {
    return new CaldroidListener() {

      @Override
      public void onSelectDate(Date date, View view) {
          dialogCaldroidFragment.getDialog().setTitle("You have selected : " + date.toString());
          if(tripType.equalsIgnoreCase("one_way")) {
            if (outboundDate != null) {
              dialogCaldroidFragment.setBackgroundResourceForDate(R.color.caldroid_white, outboundDate);
            }
            outboundDate = date;
            dialogCaldroidFragment.setBackgroundResourceForDate(R.color.caldroid_sky_blue, outboundDate);
            dialogCaldroidFragment.refreshView();
            //enable apply now button
          } else if(tripType.equalsIgnoreCase("round_trip")) {
            if(inboundDate != null && outboundDate != null) {
              outboundDate = date;
              inboundDate = null;
              dialogCaldroidFragment.setBackgroundResourceForDates(new HashMap<Date, Integer>(){{put(outboundDate, R.color.caldroid_sky_blue);}});
              dialogCaldroidFragment.refreshView();
            }
            else {
              if(outboundDate == null) {
                outboundDate = date;
                dialogCaldroidFragment.setBackgroundResourceForDate(R.color.caldroid_sky_blue, outboundDate);
                dialogCaldroidFragment.refreshView();
              }
              else {
                if(date.before(outboundDate)) {
                  outboundDate = date;
                  inboundDate = null;
                  dialogCaldroidFragment.setBackgroundResourceForDates(new HashMap<Date, Integer>(){{put(outboundDate, R.color.caldroid_sky_blue);}});
                  dialogCaldroidFragment.refreshView();
                  return;
                }
                ArrayList<Date> dates = new ArrayList<Date>();
                inboundDate = date;
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(outboundDate);
                incrementDay(calendar);

                while (calendar.getTime().before(inboundDate)) {
                  dates.add(calendar.getTime());
                  incrementDay(calendar);
                }

                HashMap<Date, Integer> dateIntegerHashMap = new HashMap<Date, Integer>();
                dateIntegerHashMap.put(outboundDate, R.color.caldroid_sky_blue);
                dateIntegerHashMap.put(inboundDate, R.color.caldroid_sky_blue);
                for(Date middleDate : dates) {
                  dateIntegerHashMap.put(middleDate, R.color.caldroid_darker_gray);

                }
                dialogCaldroidFragment.setBackgroundResourceForDates(dateIntegerHashMap);
                dialogCaldroidFragment.refreshView();
              }
            }
          }
      }

      @Override
      public void onChangeMonth(int month, int year) {
        String text = "month: " + month + " year: " + year;
        Toast.makeText(getApplicationContext(), text,
            Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onLongClickDate(Date date, View view) {

      }

      @Override
      public void onCaldroidViewCreated() {
        int titleDividerId = getResources().getIdentifier("titleDivider", "id", "android");

        int alertTitleId = getResources().getIdentifier("title", "id", "android");
        TextView alertTitle = (TextView) dialogCaldroidFragment.getDialog().getWindow().getDecorView().findViewById(alertTitleId);
        alertTitle.setGravity(Gravity.CENTER);

        View titleDivider = dialogCaldroidFragment.getDialog().getWindow().getDecorView().findViewById(titleDividerId);
        titleDivider.setBackgroundColor(Color.BLACK);
        dialogCaldroidFragment.getLeftArrowButton().setBackgroundResource(R.drawable.left_arrow);
      }

    };
  }

  private void incrementDay(Calendar calendar) {
    calendar.add(Calendar.DATE, 1);
  }


  public void launchCalenderForOneWay(View view) {
    dialogCaldroidFragment.setCaldroidListener(getCaldroidListener("one_way"));

    if (state != null) {
      dialogCaldroidFragment.restoreDialogStatesFromKey(
          getSupportFragmentManager(), state,
          "DIALOG_CALDROID_SAVED_STATE", dialogTag);
      Bundle args = dialogCaldroidFragment.getArguments();
      if (args == null) {
        args = new Bundle();
        dialogCaldroidFragment.setArguments(args);
      }
    } else {
      // Setup arguments
      Bundle bundle = new Bundle();
      // Setup dialogTitle
      bundle.putString(CaldroidFragment.DIALOG_TITLE, "Select a date");
      dialogCaldroidFragment.setArguments(bundle);
    }
    dialogCaldroidFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialogTheme);
    dialogCaldroidFragment.show(getSupportFragmentManager(),
        dialogTag);

  }

  public void launchCalenderForRoundTrip(View view) {

    dialogCaldroidFragment.setCaldroidListener(getCaldroidListener("round_trip"));
    Bundle bundle = new Bundle();
    // Setup dialogTitle
    bundle.putString(CaldroidFragment.DIALOG_TITLE, "Select a date");
    dialogCaldroidFragment.setArguments(bundle);
    dialogCaldroidFragment.show(getSupportFragmentManager(),
        dialogTag);

  }
}
