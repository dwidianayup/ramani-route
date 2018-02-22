package com.ujuizi.ramani.routingbuilder;

import android.app.Activity;
import android.util.Log;

import com.ujuizi.ramani.feedback.Feedback;

/**
 * Created by ujuizi on 8/16/17.
 */

public class FeedbackHelper implements Feedback.FeedbackListener {

    private Feedback feedback;
    private String feedbackPictureDir;
    private Activity activity;

    public FeedbackHelper(Activity activity) {
        this.activity = activity;
        feedbackPictureDir = "/RamaniRouting";
        feedback = new Feedback(feedbackPictureDir, this);

    }

    public void feedback(String name) {


//set your own title, the default is feedback
        feedback.setPictureName(name);


//the function to start the feedback and get a ScreenCapture (SS)
        feedback.start(activity);


//and when you take the screencapture programmatically, not with holding the power and volume up button.
//the dialog will not get captured
//so you need to add the dialog view when you start the feedback

//        AlertDialog dialog2 = AlertDialog.class.cast(dialog);
//        feedback.start(activity, dialog2);

    }

    @Override
    public void onFeedbackSend(String type, String comment, String SSPath) {
        Log.e("onFeedbackSend","type : "+type);
        Log.e("onFeedbackSend","comment : "+comment);
        Log.e("onFeedbackSend","SSPath : "+SSPath);
    }
}
