package sg.edu.rp.c347.p07_smsretriever;


import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFirst extends Fragment {

    EditText etSMS1;
    Button btnRetrieve1;
    TextView tvRetrieved1;

    public FragmentFirst() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        etSMS1 = view.findViewById(R.id.etSMS1);
        btnRetrieve1 = view.findViewById(R.id.btnRetrieve1);
        tvRetrieved1 = view.findViewById(R.id.tvRetrieved1);

        btnRetrieve1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = PermissionChecker.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS);
                if (permissionCheck != PermissionChecker.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_SMS},0);
                    return;
                }
                String inputText1 = etSMS1.getText().toString();
                Uri uri = Uri.parse("content://sms");
                String[] reqCols = new String[]{"date", "address", "body", "type"};
                ContentResolver cr = getActivity().getContentResolver();
                //ContentResolver cr = getContentResolver();
                String filter = "address LIKE ?";
                String[] filterArgs = {inputText1};
                Cursor cursor = cr.query(uri, reqCols, filter,filterArgs,null);

                String smsBody = "";
                if (cursor.moveToFirst()){
                    do{
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat
                                .format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        if (type.equalsIgnoreCase("1")){
                            type = "Inbox";
                        } else {
                            type = "Sent:";
                        }
                        smsBody += type + " " + address + "\n at " + date
                                + "\n\"" + body + "\"\n\n";
                    } while (cursor.moveToNext());
                }
                tvRetrieved1.setText(smsBody);
                Log.d("msg","Managed to retrieve");
            }
        });

        return view;
    }
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch (requestCode){
            case 0:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    btnRetrieve1.performClick();
                }else{
                    Toast.makeText(getActivity(),"Permission not granted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    protected void sendEmail() {
        Log.i("Send email", "");

    }
}
