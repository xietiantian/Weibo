package bupt.tiantian.weibo.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import bupt.tiantian.weibo.R;

public class SettingActivity extends AppCompatActivity {
    public static final String KEY_PREF_LOAD_PICTURE = "check_load_picture";
    public static final String KEY_PREF_PIC_SIZE = "list_picmode";
    public static final String PIC_SIZE_NETWORK_DEPENDS = "0";
    public static final String PIC_SIZE_LARGE = "1";
    public static final String PIC_SIZE_MIDDLE = "2";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
