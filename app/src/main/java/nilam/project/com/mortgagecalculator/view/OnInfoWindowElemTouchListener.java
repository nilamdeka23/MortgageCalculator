package nilam.project.com.mortgagecalculator.view;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.google.android.gms.maps.model.Marker;

/**
 * To implement a A dialog with a Delete button to allow user to remove mortgage calculation and
 * an Edit button to bring the user to the Calculation view to make changes, when user tap on a specific marker.
 * Since this is not a default behaviour supported by google maps android v2 api, I have implemented a (hack)solution
 * posted on the below StackOverFlow thread,
 * http://stackoverflow.com/questions/14123243/google-maps-android-api-v2-interactive-infowindow-like-in-original-android-go/15040761#15040761
 */
public abstract class OnInfoWindowElemTouchListener implements OnTouchListener {
    private final View view;
    private final Handler handler = new Handler();

    private Marker marker;
    private boolean pressed = false;

    public OnInfoWindowElemTouchListener(View view) {
        this.view = view;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    @Override
    public boolean onTouch(View vv, MotionEvent event) {
        if (event.getX() >= 0 && event.getX() <= view.getWidth() &&
                event.getY() >= 0 && event.getY() <= view.getHeight()) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    startPress();
                    break;

                // We need to delay releasing of the view a little so it shows the pressed state on the screen
                case MotionEvent.ACTION_UP:
                    handler.postDelayed(confirmClickRunnable, 150);
                    break;

                case MotionEvent.ACTION_CANCEL:
                    endPress();
                    break;
                default:
                    break;
            }
        } else {
            // If the touch goes outside of the view's area
            // (like when moving finger out of the pressed button)
            // just release the press
            endPress();
        }
        return false;
    }

    private void startPress() {
        if (!pressed) {
            pressed = true;
            handler.removeCallbacks(confirmClickRunnable);
            if (marker != null)
                marker.showInfoWindow();
        }
    }

    private boolean endPress() {
        if (pressed) {
            pressed = false;
            handler.removeCallbacks(confirmClickRunnable);
            if (marker != null)
                marker.showInfoWindow();
            return true;
        } else
            return false;
    }

    private final Runnable confirmClickRunnable = new Runnable() {
        public void run() {
            if (endPress()) {
                onClickConfirmed(view, marker);
            }
        }
    };

    /**
     * This is called after a successful click
     */
    protected abstract void onClickConfirmed(View v, Marker marker);
}