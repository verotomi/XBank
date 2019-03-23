package tamas.verovszki.xbank;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class CustomDialogClass extends Dialog implements android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button yes, no;
    public Boolean answer;
    public TextView TextViewquestion;
    public TextView TextViewMessage;
    public String question;

    // Step 2 - This variable represents the listener passed in by the owning object
    // The listener must implement the events interface and passes messages up to the parent.
    private MyCustomObjectListener2 listener;

    public CustomDialogClass(Activity a, String question) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.question = question;
    }

    public CustomDialogClass(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.question = getContext().getResources().getString(R.string.really_exit);
    }

    // Assign the listener implementing events interface that will receive the events
    public void setCustomObjectListener2(MyCustomObjectListener2 listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_custom);
        TextViewquestion = (TextView) findViewById(R.id.TextViewTitle);
        TextViewMessage = (TextView) findViewById(R.id.TextViewMessage);
        yes = (Button) findViewById(R.id.buttonYes);
        no = (Button) findViewById(R.id.buttonNo);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        TextViewquestion.setText(question);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonYes:
                //c.finish();
                listener.onYes("Some text"); // <---- fire listener here
                this.answer = true;
                break;
            case R.id.buttonNo:
                listener.onNo("Some text"); // <---- fire listener here
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

    public interface MyCustomObjectListener2 {
        // These methods are the different events and
        // need to pass relevant arguments related to the event triggered
        public void onYes(String title);
        public void onNo(String title);
    }
}