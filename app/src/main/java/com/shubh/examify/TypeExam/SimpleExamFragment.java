package com.shubh.examify.TypeExam;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.shubh.examify.DashBoardActivity;
import com.shubh.examify.databinding.FragmentSimpleExamBinding;

import java.util.Locale;


public class SimpleExamFragment extends Fragment {


    public SimpleExamFragment() {
        // Required empty public constructor
    }

    FragmentSimpleExamBinding binding;
    private int inMin , inHour , finMin , finHour , date=0;
    private String Date;

    FirebaseDatabase mDatabase;
    FirebaseStorage mStorage;
    Uri imageuri = null;
    Uri pdf ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSimpleExamBinding.inflate(inflater, container, false);

        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();


        binding.inTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        inHour = selectedHour;
                        inMin = selectedMinute;
                        binding.inTime.setText(String.format(Locale.getDefault() , "%02d:%02d" , inHour , inMin));

                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext() , onTimeSetListener ,inHour , inMin,false);
                timePickerDialog.setTitle("Pick stating time of exam");
                timePickerDialog.show();
            }
        });

        binding.finTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        finHour = selectedHour;
                        finMin = selectedMinute;
                        binding.finTime.setText(String.format(Locale.getDefault() , "%02d:%02d" , finHour , finMin));

                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext() , onTimeSetListener ,finHour , finMin,false);
                timePickerDialog.setTitle("Pick Final time of exam");
                timePickerDialog.show();
            }
        });

        binding.dateSimpleExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialoue();
            }



            private void showDatePickerDialoue() {

                MaterialDatePicker datePicker = MaterialDatePicker.Builder
                        .datePicker()
                        .setTitleText("Select Date")
                        .build();

                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        date =1;
                        Date = datePicker.getHeaderText();
                        binding.dateSimpleExam.setText(datePicker.getHeaderText());
                    }
                });


                datePicker.show(getChildFragmentManager() , "Tag");


            }
        });

        binding.SimpleSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(binding.subject2.getText().toString().isEmpty())
                {
                    binding.subject2.setError("Empty");
                }
                else if (binding.title2.getText().toString().isEmpty())
                {
                    binding.title2.setError("Empty");
                }
                else if (binding.chapter2.getText().toString().isEmpty())
                {
                    binding.chapter2.setError("Empty");
                }
                else if(binding.maxSimpleMarks2.getText().toString().isEmpty())
                {
                    binding.maxSimpleMarks2.setError("Empty");

                }
                else if(inHour== 0 && inMin ==0  )
                {
                    Toast.makeText(getContext(), "Pick Time and Date", Toast.LENGTH_SHORT).show();
                }
                else if(finHour == 0 && finMin==0 )
                {
                    Toast.makeText(getContext(), "Pick Time and Date", Toast.LENGTH_SHORT).show();

                }
                else if (binding.instructionsSimpleExam2.getText().toString().isEmpty())
                {
                    binding.instructionsSimpleExam2.setError("Empty");
                }
                else
                {
                    if(imageuri != null)
                    {
                        // Here we are initialising the progress dialog box
                        dialog = new ProgressDialog(getContext());
                        dialog.setMessage("Uploading");


                        // this will show message uploading
                        // while pdf is uploading
                        dialog.show();

                        final String timestamp = "" + System.currentTimeMillis();
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                        final String messagePushID = timestamp;

                        Toast.makeText(getContext(), imageuri.toString(), Toast.LENGTH_SHORT).show();


                        //   Here we are uploading the pdf in firebase storage with the name of current time
                        final StorageReference filepath = storageReference.child(messagePushID + "." + "pdf");
                        Toast.makeText(getContext(), filepath.getName(), Toast.LENGTH_SHORT).show();
                        filepath.putFile(imageuri).continueWithTask(new Continuation() {
                            @Override
                            public Object then(@NonNull Task task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                return filepath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    // After uploading is done it progress
                                    // dialog box will be dismissed

                                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                                pdf = uri;


                                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                                            String ID = sharedPreferences.getString("ID" , "false");
                                            String Name = sharedPreferences.getString("Name" , "false");
                                            String Img = sharedPreferences.getString("IMG" , "false");



                                            DatabaseReference databaseReference = mDatabase.getReference().child("Teachers").child(ID).child("Exams").push();
//                                            databaseReference.child("URL").setValue(messagePushID + "." + "pdf");
                                            databaseReference.child("inHour").setValue(inHour);
                                            databaseReference.child("inMin").setValue(inMin);
                                            databaseReference.child("finHour").setValue(finHour);
                                            databaseReference.child("finMin").setValue(finMin);
                                            databaseReference.child("Name").setValue(Name);
                                            databaseReference.child("PDFurl").setValue(pdf.toString());
                                            databaseReference.child("Chapter").setValue(binding.chapter2.getText().toString());
                                            databaseReference.child("img").setValue(Img);
                                            databaseReference.child("Subject").setValue(binding.subject2.getText().toString());
                                            databaseReference.child("Title").setValue(binding.title2.getText().toString());
                                            databaseReference.child("Date").setValue(Date);
                                            databaseReference.child("isActive").setValue("Active");
                                            databaseReference.child("examinarID").setValue(ID);
                                            databaseReference.child("MaxMarks").setValue(binding.maxSimpleMarks2.getText().toString());
                                            databaseReference.child("Instructions").setValue(binding.instructionsSimpleExam2.getText().toString());

                                            DatabaseReference databaseReference2 = mDatabase.getReference().child("AllExam").child("Exams").child(databaseReference.getKey());
//                                            databaseReference2.child("URL").setValue(messagePushID + "." + "pdf");
                                            databaseReference2.child("Name").setValue(Name);
                                            databaseReference2.child("img").setValue(Img);
                                            databaseReference2.child("Chapter").setValue(binding.chapter2.getText().toString());
                                            databaseReference2.child("PDFurl").setValue(pdf.toString());
                                            databaseReference.child("isActive").setValue("Active");
                                            databaseReference2.child("inHour").setValue(inHour);
                                            databaseReference2.child("inMin").setValue(inMin);
                                            databaseReference2.child("finHour").setValue(finHour);
                                            databaseReference2.child("finMin").setValue(finMin);
                                            databaseReference2.child("Subject").setValue(binding.subject2.getText().toString());
                                            databaseReference2.child("Title").setValue(binding.title2.getText().toString());
                                            databaseReference2.child("Date").setValue(Date);
                                            databaseReference2.child("Instructions").setValue(binding.instructionsSimpleExam2.getText().toString());
                                            databaseReference2.child("MaxMarks").setValue(binding.maxSimpleMarks2.getText().toString());
                                            databaseReference2.child("isActive").setValue("Active");
                                            databaseReference2.child("examinarID").setValue(ID);


                                        }
                                    });



                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(getContext(), "UploadedFailed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {

                                Intent intent = new Intent(getContext() , DashBoardActivity.class);
                                startActivity(intent);
                                dialog.dismiss();

                            }
                        });




                    }
                    else
                    {
                        Toast.makeText(getContext(), "Upload Exam paper PDF", Toast.LENGTH_SHORT).show();
                    }

                }






            }
        });



        // After Clicking on this we will be
        // redirected to choose pdf
        binding.AddPdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                // We will be redirected to choose pdf
                galleryIntent.setType("application/pdf");
                startActivityForResult(galleryIntent, 1);
            }
        });



        return binding.getRoot();

//        MaterialTimePicker picker  = new MaterialTimePicker.Builder().set();

    }


    ProgressDialog dialog;

    @SuppressWarnings("deprecation")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {


            imageuri = data.getData();
            binding.AddPdfBtn.setText("Added Succesfully");

        }
    }
}


