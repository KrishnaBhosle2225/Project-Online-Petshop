package com.krishna.petshop;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PetCare extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<GetDoctorPojo.Doctorlist> doctorlists;
    ProgressDialog progressDialog;
    Preferences preferences;

    DoctorAdapter doctorAdapter;

    static final Integer CALL = 001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_care);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("PetCare");

        progressDialog = new ProgressDialog(PetCare.this);
        preferences = new Preferences(PetCare.this);

        doctorlists = new ArrayList<GetDoctorPojo.Doctorlist>();


        recyclerView = findViewById(R.id.recyclerPetCare);
        recyclerView.setHasFixedSize(true);


        LinearLayoutManager courseLayoutManager = new LinearLayoutManager(PetCare.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(courseLayoutManager);

        getDoctorList();
    }

    public void getDoctorList() {

        progressDialog.setMessage("Please wait....");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Retrofit retrofit = null;

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.weburl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        ApiInterface apiInterface;
        apiInterface = retrofit.create(ApiInterface.class);
        Map<String, String> params = new HashMap<>();


        Call<GetDoctorPojo> call = apiInterface.getDoctor(params);
        call.enqueue(new Callback<GetDoctorPojo>() {
            @Override
            public void onResponse(Call<GetDoctorPojo> call, Response<GetDoctorPojo> response) {

                progressDialog.dismiss();


                if (response.body().getSuccess() == 1) {

                    if (doctorlists.size() > 0) {
                        doctorlists.clear();
                        recyclerView.removeAllViewsInLayout();
                    }

                    /*ArrayList<GetCoursePojo.Courselist>*/
                    doctorlists = new ArrayList<>();


                    doctorlists.addAll(response.body().getDoctorlist());

                    // Initialize a new instance of RecyclerView Adapter instance
                    /*RecyclerView.Adapter*/
                    doctorAdapter = new DoctorAdapter(PetCare.this, doctorlists);

                    // Set the adapter for RecyclerView
                    recyclerView.setAdapter(doctorAdapter);

                    //   clear();
                    doctorAdapter.notifyDataSetChanged();


                } else if (response.body().getSuccess() == 0) {

                    doctorlists.clear();
                    recyclerView.removeAllViewsInLayout();

                    Toast.makeText(PetCare.this, "No doctor found", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<GetDoctorPojo> call, Throwable t) {

                progressDialog.dismiss();

                Log.e(PetCare.class.getSimpleName(), t.toString());
                Toast.makeText(PetCare.this, t.toString(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.ViewHolder> {
        private ArrayList<GetDoctorPojo.Doctorlist> doctorlists;
        private Context context;
        ProgressDialog progressDialog;
        String str = "";

        public DoctorAdapter(Context context, ArrayList<GetDoctorPojo.Doctorlist> doctorlists) {
            this.doctorlists = doctorlists;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_petcare, null);
            ViewHolder vh = new ViewHolder(view);

            progressDialog = new ProgressDialog(context);
            return vh;

        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            Glide.with(context).load(Constants.weburl + doctorlists.get(position).getDoctorphoto()).into(holder.imgCourse);
            holder.txtDoctorName.setText(doctorlists.get(position).getName());
            holder.txtMobileNo.setText(doctorlists.get(position).getMobile());
            holder.txtAddress.setText(doctorlists.get(position).getAddress());

            holder.txtMobileNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (checkPermission(Manifest.permission.CALL_PHONE)) {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:" + doctorlists.get(position).getMobile()));
                        startActivity(callIntent);

                    } else {
                        askForPermission(Manifest.permission.CALL_PHONE, CALL);
                    }
                }
            });


        }

        @Override
        public int getItemCount() {
            return doctorlists.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView txtDoctorName, txtMobileNo, txtAddress;
            public ImageView imgCourse;

            public ViewHolder(View itemView) {
                super(itemView);
                txtDoctorName = itemView.findViewById(R.id.txtDoctorName);
                txtMobileNo = itemView.findViewById(R.id.txtMobileNo);
                txtAddress = itemView.findViewById(R.id.txtAddressDoc);
                imgCourse = itemView.findViewById(R.id.profile_image);
            }
        }
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            }
        } else {
            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermission(String permission){
        if (Build.VERSION.SDK_INT >= 23) {
            int result = ContextCompat.checkSelfPermission(this, permission);
            if (result == PackageManager.PERMISSION_GRANTED){
                return true;
            } else {
                return false;
            }
        } else {
            return true;



        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
            switch (requestCode) {

                //Call
                case 001:
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + "9975375723"));
                    startActivity(callIntent);

                    break;

            }

            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}
