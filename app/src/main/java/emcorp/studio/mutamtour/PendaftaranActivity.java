package emcorp.studio.mutamtour;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import emcorp.studio.mutamtour.Library.Constant;
import emcorp.studio.mutamtour.Library.CustomTypefaceSpan;
import emcorp.studio.mutamtour.Library.SharedFunction;
import emcorp.studio.mutamtour.Library.SharedPrefManager;
import emcorp.studio.mutamtour.Library.TypefaceUtil;

public class PendaftaranActivity extends AppCompatActivity {
    List<String> listkdprov = new ArrayList<String>();
    List<String> listnmprov = new ArrayList<String>();
    List<String> listkdkabkota = new ArrayList<String>();
    List<String> listnmkabkota = new ArrayList<String>();
    List<String> listkdkecamatan = new ArrayList<String>();
    List<String> listnmkecamatan = new ArrayList<String>();
    List<String> listiddesa = new ArrayList<String>();
    List<String> listnmdesa = new ArrayList<String>();
    private ProgressDialog progressDialog;
    EditText edtName,edtNoHp,edtEmail,edtAlamat,edtTglLahir;
    Spinner spinProvinsi,spinKabupaten,spinKecamatan,spinDesa;
    Button btnDafter;
    private int mYear, mMonth, mDay;
    String tanggalLahir = "";
    RadioButton radLaki, radPerempuan;
    SpannableStringBuilder SS;
    boolean editsession = false;
    Bundle extras;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendaftaran);
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/barclays.ttf");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/barclays.ttf");
//        setTitle("Pendaftaran");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(this, R.color.background_toolbar)))));
        actionBar.setTitle(Html.fromHtml("<font color='"+String.format("#%06x", ContextCompat.getColor(this, R.color.text_toolbar) & 0xffffff)+"'>Pendaftaran</font>"));
        SS = new SpannableStringBuilder(Html.fromHtml("<font color='"+String.format("#%06x", ContextCompat.getColor(this, R.color.text_toolbar) & 0xffffff)+"'>Pendaftaran</font>"));
        SS.setSpan (new CustomTypefaceSpan("", type), 0, SS.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        actionBar.setTitle(SS);
        Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.icon_toolbar), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        spinProvinsi = (Spinner)findViewById(R.id.spinProvinsi);
        spinKabupaten = (Spinner)findViewById(R.id.spinKabupaten);
        spinKecamatan = (Spinner)findViewById(R.id.spinKecamatan);
        spinDesa = (Spinner)findViewById(R.id.spinDesa);
        edtName = (EditText)findViewById(R.id.edtName);
        edtNoHp = (EditText)findViewById(R.id.edtNoHp);
        edtEmail = (EditText)findViewById(R.id.edtEmail);
        edtAlamat = (EditText)findViewById(R.id.edtAlamat);
        edtTglLahir = (EditText)findViewById(R.id.edtTglLahir);
        btnDafter = (Button)findViewById(R.id.btnDafter);
        radLaki = (RadioButton) findViewById(R.id.radLaki);
        radPerempuan = (RadioButton) findViewById(R.id.radPerempuan);

        edtName.setTypeface(type);
        edtNoHp.setTypeface(type);
        edtEmail.setTypeface(type);
        edtAlamat.setTypeface(type);
        edtTglLahir.setTypeface(type);
        btnDafter.setTypeface(type);
        radLaki.setTypeface(type);
        radPerempuan.setTypeface(type);
        btnDafter.setText("DAFTAR");
        extras = getIntent().getExtras();
        if (extras != null) {
            if(extras.getString("edit").equals("edit")){
                editsession = true;
                edtName.setText(extras.getString("nama"));
                edtNoHp.setText(extras.getString("hp"));
                edtEmail.setText(extras.getString("email"));
                edtTglLahir.setText(extras.getString("tgl_lahir"));
                String tgl = extras.getString("tgl_lahir");
                tanggalLahir = tgl.substring(6,10)+"-"+tgl.substring(3,5)+"-"+tgl.substring(0,2);

                if(extras.getString("jk").equals("1")){
                    radLaki.setChecked(true);
                    radPerempuan.setChecked(false);
                }else{
                    radLaki.setChecked(false);
                    radPerempuan.setChecked(true);
                }
                edtAlamat.setText(extras.getString("alamat"));
                btnDafter.setText("UDPATE");
            }
        }

        if(SharedFunction.getInstance(getApplicationContext()).isNetworkConnected()){
            LoadProv();
        }else{
            Toast.makeText(getApplicationContext(),R.string.internet_error, Toast.LENGTH_LONG).show();
        }

        edtTglLahir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(PendaftaranActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                edtTglLahir.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                tanggalLahir = String.valueOf(year)+"-"+String.valueOf(monthOfYear + 1)+"-"+String.valueOf(dayOfMonth);
//                                edtAkhir.setEnabled(true);
//                                edtAkhir.setText("");

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
                datePickerDialog.show();
//                }
            }
        });

        spinProvinsi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                LoadKab(listkdprov.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinKabupaten.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                LoadKec(listkdprov.get(spinProvinsi.getSelectedItemPosition()),listkdkabkota.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinKecamatan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                LoadDesa(listkdprov.get(spinProvinsi.getSelectedItemPosition()),listkdkabkota.get(spinKabupaten.getSelectedItemPosition()),listkdkecamatan.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnDafter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SharedFunction.getInstance(PendaftaranActivity.this).isNetworkConnected()){
                    if(edtName.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(),"Nama belum diisi", Toast.LENGTH_SHORT).show();
                        edtName.requestFocus();
                    }else{
                        if(edtNoHp.getText().toString().equals("")){
                            Toast.makeText(getApplicationContext(),"No HP belum diisi", Toast.LENGTH_SHORT).show();
                            edtNoHp.requestFocus();
                        }else{
                            if(edtTglLahir.getText().toString().equals("")){
                                Toast.makeText(getApplicationContext(),"Tanggal lahir belum diisi!", Toast.LENGTH_SHORT).show();
                                edtTglLahir.requestFocus();
                            }else{
                                if(edtAlamat.getText().toString().equals("")){
                                    Toast.makeText(getApplicationContext(),"Alamat belum diisi!", Toast.LENGTH_SHORT).show();
                                    edtAlamat.requestFocus();
                                }else{
                                    if(radLaki.isChecked()||radPerempuan.isChecked()){
                                        if(spinProvinsi.getSelectedItemPosition()>=0){
                                            if(spinKabupaten.getSelectedItemPosition()>=0){
                                                if(spinKecamatan.getSelectedItemPosition()>=0){
                                                    if(spinDesa.getSelectedItemPosition()>=0){
                                                        Toast.makeText(getApplicationContext(),tanggalLahir,Toast.LENGTH_SHORT).show();
                                                        RegisterProcess();
                                                    }else{
                                                        Toast.makeText(getApplicationContext(),"Desa belum dipilih!",Toast.LENGTH_SHORT).show();
                                                    }
                                                }else{
                                                    Toast.makeText(getApplicationContext(),"Kecamatan belum dipilih!",Toast.LENGTH_SHORT).show();
                                                }
                                            }else{
                                                Toast.makeText(getApplicationContext(),"Kabupaten belum dipilih!",Toast.LENGTH_SHORT).show();
                                            }
                                        }else{
                                            Toast.makeText(getApplicationContext(),"Provinsi belum dipilih!",Toast.LENGTH_SHORT).show();
                                        }
                                    }else{
                                        Toast.makeText(getApplicationContext(),"Jenis kelamin belum dipilih!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Internet tidak tersedia, periksa koneksi anda !", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public Integer findPosition(String kode, List<String> listkode){
        Integer pos = 0;
        for(int i=0;i<listkode.size();i++){
            if(listkode.get(i).equals(kode)){
                pos = i;
                break;
            }
        }
        return pos;
    }

    public void LoadProv(){
        progressDialog = new ProgressDialog(PendaftaranActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constant.ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("CETAK",response);
                        progressDialog.dismiss();
                        listkdprov.clear();
                        listnmprov.clear();
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray jsonArray = obj.getJSONArray("hasil");
                            if(jsonArray.length()==0){
                                Toast.makeText(PendaftaranActivity.this,"Tidak ada data", Toast.LENGTH_SHORT).show();
                            }else{
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject isiArray = jsonArray.getJSONObject(i);
                                    String kdprov = isiArray.getString("kdprov");
                                    String nmprov = isiArray.getString("nmprov");
                                    listkdprov.add(kdprov);
                                    listnmprov.add(nmprov);
                                }
                                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(PendaftaranActivity.this, android.R.layout.simple_spinner_item, listnmprov);
                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinProvinsi.setAdapter(dataAdapter);
                                if(editsession){
                                    spinProvinsi.setSelection(findPosition(extras.getString("provinsi"),listkdprov));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(
                                PendaftaranActivity.this,
                                error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("function", Constant.FUNCTION_LISTPROV);
                params.put("key", Constant.KEY);
                return params;
            }
        };
        DefaultRetryPolicy policy = new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void LoadKab(final String kdprov){
        progressDialog = new ProgressDialog(PendaftaranActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constant.ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("CETAK",response);
                        progressDialog.dismiss();
                        listkdkabkota.clear();
                        listnmkabkota.clear();
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray jsonArray = obj.getJSONArray("hasil");
                            if(jsonArray.length()==0){
                                Toast.makeText(PendaftaranActivity.this,"Tidak ada data", Toast.LENGTH_SHORT).show();
                            }else{
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject isiArray = jsonArray.getJSONObject(i);
                                    String kdkabkota = isiArray.getString("kdkabkota");
                                    String nmkabkota = isiArray.getString("nmkabkota");
                                    listkdkabkota.add(kdkabkota);
                                    listnmkabkota.add(nmkabkota);
                                }
                                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(PendaftaranActivity.this, android.R.layout.simple_spinner_item, listnmkabkota);
                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinKabupaten.setAdapter(dataAdapter);
                                if(editsession){
                                    spinKabupaten.setSelection(findPosition(extras.getString("kabupaten"),listkdkabkota));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(
                                PendaftaranActivity.this,
                                error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("function", Constant.FUNCTION_LISTKAB);
                params.put("key", Constant.KEY);
                params.put("kdprov", kdprov);
                return params;
            }
        };
        DefaultRetryPolicy policy = new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


    public void LoadKec(final String kdprov, final String kdkabkota){
        progressDialog = new ProgressDialog(PendaftaranActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constant.ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("CETAK",response);
                        progressDialog.dismiss();
                        listkdkecamatan.clear();
                        listnmkecamatan.clear();
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray jsonArray = obj.getJSONArray("hasil");
                            if(jsonArray.length()==0){
                                Toast.makeText(PendaftaranActivity.this,"Tidak ada data", Toast.LENGTH_SHORT).show();
                            }else{
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject isiArray = jsonArray.getJSONObject(i);
                                    String kdkecamatan = isiArray.getString("kdkecamatan");
                                    String nmkecamatan = isiArray.getString("nmkecamatan");
                                    listkdkecamatan.add(kdkecamatan);
                                    listnmkecamatan.add(nmkecamatan);
                                }
                                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(PendaftaranActivity.this, android.R.layout.simple_spinner_item, listnmkecamatan);
                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinKecamatan.setAdapter(dataAdapter);
                                if(editsession){
                                    spinKecamatan.setSelection(findPosition(extras.getString("kecamatan"),listkdkecamatan));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(
                                PendaftaranActivity.this,
                                error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("function", Constant.FUNCTION_LISTKEC);
                params.put("key", Constant.KEY);
                params.put("kdprov", kdprov);
                params.put("kdkabkota", kdkabkota);
                return params;
            }
        };
        DefaultRetryPolicy policy = new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void LoadDesa(final String kdprov, final String kdkabkota, final String kdkecamatan){
        progressDialog = new ProgressDialog(PendaftaranActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constant.ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("CETAK",response);
                        progressDialog.dismiss();
                        listiddesa.clear();
                        listnmdesa.clear();
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray jsonArray = obj.getJSONArray("hasil");
                            if(jsonArray.length()==0){
                                Toast.makeText(PendaftaranActivity.this,"Tidak ada data", Toast.LENGTH_SHORT).show();
                            }else{
                                for (int i=0; i<jsonArray.length(); i++) {
                                    JSONObject isiArray = jsonArray.getJSONObject(i);
                                    String kddesa = isiArray.getString("kddesa");
                                    String nmdesa = isiArray.getString("nmdesa");
                                    listiddesa.add(kddesa);
                                    listnmdesa.add(nmdesa);
                                }
                                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(PendaftaranActivity.this, android.R.layout.simple_spinner_item, listnmdesa);
                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinDesa.setAdapter(dataAdapter);
                                if(editsession){
                                    spinDesa.setSelection(findPosition(extras.getString("desa"),listiddesa));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(
                                PendaftaranActivity.this,
                                error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("function", Constant.FUNCTION_LISTDESA);
                params.put("key", Constant.KEY);
                params.put("kdprov", kdprov);
                params.put("kdkabkota", kdkabkota);
                params.put("kdkecamatan", kdkecamatan);
                return params;
            }
        };
        DefaultRetryPolicy policy = new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


    public void RegisterProcess(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constant.ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.d("CETAK",Constant.ROOT_URL+" "+response);
//                        Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONObject userDetails = obj.getJSONObject("hasil");
                            String message = userDetails.getString("message");
                            String success = userDetails.getString("success");
                            if(success.equals("1")){
                                Intent i = new Intent(PendaftaranActivity.this,MainActivity.class);
                                i.putExtra("MENU","MORE");
                                startActivity(i);
                                finish();
                            }
                            Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(
                                getApplicationContext(),
                                error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                if(editsession){
                    params.put("function", Constant.FUNCTION_UPDATEPENDAFTARAN);
                    params.put("recid", extras.getString("recid"));
                }else{
                    params.put("function", Constant.FUNCTION_PENDAFTARAN);
                }
                params.put("key", Constant.KEY);
                params.put("id", SharedPrefManager.getInstance(getApplicationContext()).getID());
                params.put("nama", edtName.getText().toString());
                params.put("tgl_lahir", tanggalLahir);
                if(radLaki.isChecked()){
                    params.put("jk", "1");
                }else{
                    params.put("jk", "0");
                }
                params.put("alamat", edtAlamat.getText().toString());
                params.put("provinsi", listkdprov.get(spinProvinsi.getSelectedItemPosition()));
                params.put("kabupaten", listkdkabkota.get(spinKabupaten.getSelectedItemPosition()));
                params.put("kecamatan", listkdkecamatan.get(spinKecamatan.getSelectedItemPosition()));
                params.put("desa", listiddesa.get(spinDesa.getSelectedItemPosition()));
                params.put("hp", edtNoHp.getText().toString());
                params.put("email", edtEmail.getText().toString());
                return params;
            }
        };
        DefaultRetryPolicy policy = new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PendaftaranActivity.this);
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(PendaftaranActivity.this,MainActivity.class);
                i.putExtra("MENU","MORE");
                startActivity(i);
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(PendaftaranActivity.this,MainActivity.class);
        i.putExtra("MENU","MORE");
        startActivity(i);
        finish();
    }
}
