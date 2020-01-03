package github.subha;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class DasCalendar extends LinearLayout {
    Context context;
    private View view;
    TextView monthName;
   // ImageView btPrevMonth, btNextMonth;
    Calendar calendar;
    Locale locale;
    LinearLayout[] fields;
    HashMap<Integer, LinearLayout> link;
    private static final String FIELD_ = "field_";
    private static final String DAY_ = "day_";
    LinearLayout lastRow;
    //private int currentMonthIndex = 0;

    private CalendarClickListener calendarClickListener;
    //--------------------------------------------------------------
    public DasCalendar(Context context) {
        this(context, null);
    }

    public DasCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            if (isInEditMode())
                return;
        }
        link = new HashMap<>();
        fields = new LinearLayout[42];
        initializeCalendarView();
    }

    private void initializeCalendarView() {
        LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflate.inflate(R.layout.calender_view, this, true);
        /*btPrevMonth = (ImageView) view.findViewById(R.id.btPrevMonth);
        btNextMonth = (ImageView) view.findViewById(R.id.btNextMonth);*/
        lastRow = view.findViewById(R.id.lastRow);
        lastRow.setVisibility(GONE);
       //SetButtonClickListener();
        GetAllField();
    }

    private void GetAllField() {
        for (int i = 0; i < 42; i++) {
            fields[i] = view.findViewWithTag(FIELD_ + (i + 1));
        }
        initializeCurrentMonth();
    }

    private void initializeCurrentMonth() {
        // Initialize calendar for current month
        locale = context.getResources().getConfiguration().locale;
        calendar = Calendar.getInstance(locale);
        LoadTitleView();
    }

    private void LoadTitleView() {
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        String sMonth = new DateFormatSymbols(locale).getShortMonths()[calendar.get(Calendar.MONTH)];
        sMonth = sMonth.substring(0, 1).toUpperCase() + sMonth.subSequence(1, sMonth.length());
        monthName = view.findViewById(R.id.monthName);
        monthName.setText(sMonth + " " + calendar.get(Calendar.YEAR));
        initializeDates();
    }

    public void ChangeCalendar(int year, int month) {
        calendar.set(year, (month - 1), 1);
        String sMonth = new DateFormatSymbols(locale).getShortMonths()[calendar.get(Calendar.MONTH)];
        sMonth = sMonth.substring(0, 1).toUpperCase() + sMonth.subSequence(1, sMonth.length());

        monthName = view.findViewById(R.id.monthName);
        monthName.setText(sMonth + " " + calendar.get(Calendar.YEAR));

        initializeDates();
    }

    private void initializeDates() {
        ClearDateView();
        link.clear();
        lastRow.setVisibility(GONE);
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        int totalDays = calendar.getActualMaximum(calendar.DAY_OF_MONTH);
        int fieldNo = calendar.get(Calendar.DAY_OF_WEEK);
        for (int i = 1; i <= totalDays; i++) {
            if (fieldNo == 36) {//If field no reach 36 then visible last row other wise not
                lastRow.setVisibility(VISIBLE);
            }
            LinearLayout field = fields[fieldNo - 1];
            TextView textView = new TextView(context);
            textView.setText("" + i);
            textView.setTextSize(18f);
            textView.setGravity(Gravity.CENTER);
            field.addView(textView);
            link.put(i, field);
            final int finalI = i;
            field.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (calendarClickListener != null) {
                        calendarClickListener.onDateClicked(calendar.get(Calendar.YEAR),(calendar.get(Calendar.MONTH)+1), finalI);
                    }
                }
            });

            fieldNo++;
        }
        SetCurrentDateColor();
    }

    private void SetCurrentDateColor() {
        //Change Text color of current Date
        Calendar cal = Calendar.getInstance();
        if (calendar.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == cal.get(Calendar.MONTH)) {
            Date date = cal.getTime();
            DateFormat dateFormat = new SimpleDateFormat("d");
            String formattedDate = dateFormat.format(date);
            View v = link.get(Integer.valueOf(formattedDate)).getChildAt(0);
            if (v instanceof TextView) {
                ((TextView) v).setTextColor(getResources().getColor(R.color.lightBlue));
                ((TextView) v).setTypeface(((TextView) v).getTypeface(), Typeface.BOLD);
            }
        }
    }

    private void ClearDateView() {
        for (LinearLayout ll : fields) {
            ll.removeAllViews();
            ll.setBackgroundResource(R.drawable.border1);
        }
    }

    //--------------------------------------------------------------
    public void changeTitleBackground(int color) {
        ((TextView) view.findViewById(R.id.monthName)).setBackgroundColor(color);
    }

    public void changeTitleTextColor(int color) {
        ((TextView) view.findViewById(R.id.monthName)).setTextColor(color);
    }

    //--------------------------------------------------------------
    /*public void SetTitleButtonVisibility(int action) {
        btPrevMonth.setVisibility(action);
        btNextMonth.setVisibility(action);
    }

    public void SetTitleButtonColor(int color) {
        btPrevMonth.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        btNextMonth.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    private void SetButtonClickListener() {
        btPrevMonth.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMonthIndex--;
                calendar.add(Calendar.MONTH, currentMonthIndex);
                currentMonthIndex = 0;
                LoadTitleView();
            }
        });
        btNextMonth.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMonthIndex++;
                calendar.add(Calendar.MONTH, currentMonthIndex);
                currentMonthIndex = 0;
                LoadTitleView();
            }
        });
    }
*/
    //--------------------------------------------------------------
    public void changeWeekHeaderBackground(int color) {
        ((LinearLayout) view.findViewById(R.id.weekHeader)).setBackgroundColor(color);
    }

    public void changeWeekHeaderTextColor(int color) {
        LinearLayout weekHeader = view.findViewById(R.id.weekHeader);
        for (int i = 0; i < weekHeader.getChildCount(); i++) {
            View v = weekHeader.getChildAt(i);
            if (v instanceof TextView) {
                ((TextView) v).setTextColor(color);
            }
        }
    }

    //--------------------------------------------------------------
    //Change Background color of a particular day
    public void changeDayBackground(int day, int color) {
        ((TextView) view.findViewWithTag(DAY_ + day)).setBackgroundColor(color);
        for (int i = day; i <= 42; i = i + 7) {
            view.findViewWithTag(FIELD_ + (i)).setBackgroundColor(color);
        }
    }

    public void changeDayTextColor(int day, int color) {
        ((TextView) view.findViewWithTag(DAY_ + day)).setTextColor(color);
        for (int i = day; i <= 42; i = i + 7) {
            LinearLayout dateView = view.findViewWithTag(FIELD_ + (i));
            for (int j = 0; j < dateView.getChildCount(); j++) {
                View v = dateView.getChildAt(j);
                if (v instanceof TextView) {
                    ((TextView) v).setTextColor(color);
                }
            }
        }
    }

    //--------------------------------------------------------------
    public void changeAllDateBackground(int color) {
        ((LinearLayout) view.findViewById(R.id.datesView)).setBackgroundColor(color);
    }

    public void changeDateBackground(int date, int color) {
        if (date <= link.size()) {
            link.get(date).setBackgroundColor(color);
        } else {
            Toast.makeText(context, "You entered a wrong date.", Toast.LENGTH_LONG).show();
        }
    }

    //--------------------------------------------------------------
    public void changeAllDateTextColor(int color) {
        for (int i = 1; i <= link.size(); i++) {
            LinearLayout v = link.get((i));
            for (int j = 0; j < v.getChildCount(); j++) {
                View v_ = v.getChildAt(j);
                if (v_ instanceof TextView) {
                    ((TextView) v_).setTextColor(color);
                }
            }
        }
        SetCurrentDateColor();
    }

    public void changeDateTextColor(int date, int color) {
        if (date <= link.size()) {
            for (int i = 0; i < link.get(date).getChildCount(); i++) {
                View v = link.get(date).getChildAt(i);
                if (v instanceof TextView) {
                    ((TextView) v).setTextColor(color);
                }
            }
        } else {
            Toast.makeText(context, "You entered a wrong date.", Toast.LENGTH_LONG).show();
        }
        SetCurrentDateColor();
    }

    //--------------------------------------------------------------
    public void addTextToDate(int date, String text) {
        if (date <= link.size()) {
            TextView textView = new TextView(context);
            textView.setText(text);
            textView.setTextSize(8f);
            textView.setGravity(Gravity.CENTER);
            link.get(date).addView(textView);
        } else {
            Toast.makeText(context, "You entered a wrong date.", Toast.LENGTH_LONG).show();
        }
    }

    public void addDrawableToDate(int date, Drawable drawable) {
        if (date <= link.size()) {
            ImageView imageView = new ImageView(context);
            imageView.setImageDrawable(drawable);
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 30);
            imageView.setLayoutParams(layoutParams);
            link.get(date).addView(imageView);
        } else {
            Toast.makeText(context, "You entered a wrong date.", Toast.LENGTH_LONG).show();
        }
    }

    //--------------------------------------------------------------
    /*public LinearLayout setDate(int date) {
        return link.get(date);
    }*/
    //--------------------------------------------------------------
    public void setCalendarClickListener(CalendarClickListener calendarClickListener) {
        this.calendarClickListener = calendarClickListener;
    }

}