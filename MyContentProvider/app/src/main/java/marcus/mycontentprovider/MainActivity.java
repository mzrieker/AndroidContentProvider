package marcus.mycontentprovider;

//import android.content.ContentValues;
//import android.database.Cursor;
//import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
import android.view.View;
import android.widget.Button;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.TextView;
import marcus.mycontentprovider.db.mydb;
//import marcus.mycontentprovider.db.mySQLiteHelper;

public class MainActivity extends AppCompatActivity {
    Button resetBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resetBtn = (Button) findViewById(R.id.reset_button);
        registerListeners();


    }

    private void registerListeners() {

        resetBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mydb db = new mydb(getApplicationContext());
                db.open();
                //db.shinny();
                db.close();
            }
        });
    }
}
