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
public class FragmentSecond extends Fragment {

    EditText etSMS2;
    Button btnRetrieve2;
    TextView tvRetrieved2;

    public FragmentSecond() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        etSMS2 = view.findViewById(R.id.etSMS2);
        btnRetrieve2 = view.findViewById(R.id.btnRetrieve2);
        tvRetrieved2 = view.findViewById(R.id.tvRetrieved2);

        btnRetrieve2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = PermissionChecker.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS);
                if (permissionCheck != PermissionChecker.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_SMS},0);
                    return;
                }
                String inputText2 = etSMS2.getText().toString();
                Uri uri = Uri.parse("content://sms");
                String[] reqCols = new String[]{"date", "address", "body", "type"};
                ContentResolver cr = getActivity().getContentResolver();

                /*String filter = "address LIKE ?";
                String[] filterArgs = {inputText2};
                Cursor cursor = cr.query(uri, reqCols, filter,filterArgs,null);*/

                String[] words = inputText2.split(" ");
                String word = "";
                String filter = "";
                String[] filterArgs = new String[words.length];
                for (int i=0; i<words.length;i++){
                    if (i == 0){
                        filter += " body LIKE ? ";
                    } else {
                        filter += " OR body LIKE ? ";
                    }
                    filterArgs[i] = "%" + words[i] + "%";
                }
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
                tvRetrieved2.setText(smsBody);
            }
        });

        return view;
    }
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch (requestCode){
            case 0:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    btnRetrieve2.performClick();
                }else{
                    Toast.makeText(getActivity(),"Permission not granted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
