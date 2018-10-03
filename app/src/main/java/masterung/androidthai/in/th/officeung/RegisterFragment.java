package masterung.androidthai.in.th.officeung;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;

public class RegisterFragment extends Fragment {

    //    Explicit
    private Boolean aBoolean = true;
    private Uri uri;
    private ImageView imageView;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        Create Toolbar
        createToolbar();

//        Avata Controller
        avataController();

    }   // Main Method

    public class uploadListener implements FTPDataTransferListener {


        @Override
        public void started() {
            Toast.makeText(getActivity(), "Start Upload", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void transferred(int i) {
            Toast.makeText(getActivity(), "Continue Upload", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void completed() {
            Toast.makeText(getActivity(), "Completed Upload", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void aborted() {

        }

        @Override
        public void failed() {

        }
    }   // upload Class




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK) {

            aBoolean = false;
            uri = data.getData();

            try {

                Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
                Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, 800, 600, false);
                imageView.setImageBitmap(bitmap1);

            } catch (Exception e) {
                Log.d("2OctV1", "e ==> " + e.toString());
            }


        } else {
            MyAlert myAlert = new MyAlert(getActivity());
            myAlert.normalDialog(getString(R.string.title_avata),
                    getString(R.string.message_avata));
        }

    }

    private void avataController() {
        imageView = getView().findViewById(R.id.imvAvata);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Please Choose App"), 1);

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.itmeUpload) {

            checkAvataAnContent();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkAvataAnContent() {

        MyAlert myAlert = new MyAlert(getActivity());

        EditText nameEditText = getView().findViewById(R.id.edtName);
        EditText emailEditText = getView().findViewById(R.id.edtEmail);
        EditText passwordEditText = getView().findViewById(R.id.edtPassword);

        String nameString = nameEditText.getText().toString().trim();
        String emailString = emailEditText.getText().toString().trim();
        String passwordString = passwordEditText.getText().toString().trim();


        if (aBoolean) {
//            Not Choose Avata
            myAlert.normalDialog(getString(R.string.title_avata),
                    getString(R.string.message_avata));
        } else if (nameString.isEmpty() || emailString.isEmpty() || passwordString.isEmpty()) {
//            Have Space
            myAlert.normalDialog(getString(R.string.title_space),
                    getString(R.string.message_space));
        } else {
//            No Space

//            Find Path of Choosed Image
            String pathImageString = null;
            String[] strings = new String[]{MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(uri, strings,
                    null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                pathImageString = cursor.getString(index);
            } else {
                pathImageString = uri.getPath();
            }
            Log.d("2OctV1", "path ==> " + pathImageString);

//            Find Name of Choosed Image
            String nameImageString = pathImageString.substring(pathImageString.lastIndexOf("/"));
            Log.d("2OctV1", "NameImage ==> " + nameImageString);

//            Upload Image to Server
            StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy
                    .Builder().permitAll().build();
            StrictMode.setThreadPolicy(threadPolicy);

            File file = new File(pathImageString);
            MyConstant myConstant = new MyConstant();
            FTPClient ftpClient = new FTPClient();

            try {

                ftpClient.connect(myConstant.getHostString(), myConstant.getPortAnInt());
                ftpClient.login(myConstant.getUserFTPString(), myConstant.getPasswordString());
                ftpClient.setType(FTPClient.TYPE_BINARY);
                ftpClient.changeDirectory("MasterUNG");
                ftpClient.upload(file, new uploadListener());

//                Add Value to mySQL
                AddNewUser addNewUser = new AddNewUser(getActivity());
                addNewUser.execute(nameString, emailString, passwordString,
                        myConstant.getPrefixString() + nameImageString,
                        myConstant.getUrlAddUserString());
                String result = addNewUser.get();
                Log.d("2OctV1", "result ==> " + result);

                if (Boolean.parseBoolean(result)) {
                    getActivity().getSupportFragmentManager().popBackStack();
                } else {
                    myAlert.normalDialog("Cannot Upload",
                            "Please Try Agains");
                }



            } catch (Exception e) {

                try {
                    ftpClient.disconnect(true);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }   // try2

            }   // try1




        }   // if


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_register, menu);
    }

    private void createToolbar() {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbarRegister);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.regis));
        ((MainActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity()
                        .getSupportFragmentManager()
                        .popBackStack();

            }
        });
        setHasOptionsMenu(true);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        return view;
    }
}
