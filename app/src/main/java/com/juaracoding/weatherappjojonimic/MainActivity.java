package com.juaracoding.weatherappjojonimic;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.icu.util.Calendar;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.juaracoding.weatherappjojonimic.model.ModelWeather;
import com.juaracoding.weatherappjojonimic.service.APIClient;
import com.juaracoding.weatherappjojonimic.service.APIInterfacesRest;
import com.robin.locationgetter.EasyLocation;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;

    TextView txtKota, txtTanggal, txtDerajat, txtCuaca, txtTekanan, txtKelembaban, txtAngin, txtMatahirTerbit, txtMatahariTerbenam;
    EditText txtLokasi;
    ImageButton btnSearch;
    ImageView gCuaca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtKota = findViewById(R.id.txtKota);
        txtTanggal = findViewById(R.id.txtTanggal);
        txtDerajat = findViewById(R.id.txtDerajat);
        txtCuaca = findViewById(R.id.txtCuaca);
        txtTekanan = findViewById(R.id.txtTekanan);
        txtKelembaban = findViewById(R.id.txtKelembaban);
        txtAngin = findViewById(R.id.txtAngin);
        txtMatahirTerbit = findViewById(R.id.txtMatahariTerbit);
        txtMatahariTerbenam = findViewById(R.id.txtMatahariTerbenam);
        txtLokasi = findViewById(R.id.txtLokasi);
        btnSearch = findViewById(R.id.btnSearch);
        gCuaca = findViewById(R.id.gCuaca);

        new EasyLocation(MainActivity.this, new EasyLocation.EasyLocationCallBack() {
            @Override
            public void permissionDenied() {

            }

            @Override
            public void locationSettingFailed() {

            }

            @Override
            public void getLocation(Location location) {

                callWeather(location.getLatitude(),location.getLongitude());


            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWeatherByCity(txtLokasi.getText().toString());
            }
        });

    }

    APIInterfacesRest apiInterface;
    ProgressDialog progressDialog;

    public void callWeather(Double lat, Double lon){

        apiInterface = APIClient.getClient().create(APIInterfacesRest.class);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.show();
        Call<ModelWeather> call3 = apiInterface.getWeather(lat,lon,"6c57819f3114a6213bf6a1a0290c4f2c");
        call3.enqueue(new Callback<ModelWeather>() {
            @Override
            public void onResponse(Call<ModelWeather> call, Response<ModelWeather> response) {
                progressDialog.dismiss();
                ModelWeather dataWeather = response.body();
                //Toast.makeText(LoginActivity.this,userList.getToken().toString(),Toast.LENGTH_LONG).show();
                if (dataWeather !=null) {
                    txtKota.setText(dataWeather.getName()+ ", " + dataWeather.getSys().getCountry());
                    txtAngin.setText(dataWeather.getWind().getSpeed().toString() + " m/s");
                    txtCuaca.setText(dataWeather.getWeather().get(0).getDescription());
////                    txtSunrise.setText(dataWeather.getName());

                    txtTanggal.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
                    txtTekanan.setText(dataWeather.getMain().getPressure() + " hpa");
                    txtKelembaban.setText(dataWeather.getMain().getHumidity() + " %");
                    txtMatahirTerbit.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(dataWeather.getSys().getSunset() * 1000 * (60 * 60 * 7 ) )));
                    txtMatahariTerbenam.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(dataWeather.getSys().getSunrise() * 1000 * (60 * 60 * 7))));
//                    txtGeoCoords.setText("[ " +dataWeather.getCoord().getLat().toString() + " , " + dataWeather.getCoord().getLon().toString() + " ]");
                    txtDerajat.setText(new DecimalFormat("##.##").format(dataWeather.getMain().getTemp()-273.15) + "°C");

                    String image = "http://openweathermap.org/img/wn/"+ dataWeather.getWeather().get(0).getIcon()+"@2x.png";
                    Picasso.get().load(image).into(gCuaca);


                }else{

                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(MainActivity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<ModelWeather> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Maaf koneksi bermasalah",Toast.LENGTH_LONG).show();
                call.cancel();
            }
        });




    }

//  search by city
public void getWeatherByCity(String keyword){

    apiInterface = APIClient.getClient().create(APIInterfacesRest.class);
    progressDialog = new ProgressDialog(MainActivity.this);
    progressDialog.setTitle("Loading");
    progressDialog.show();
    Call<ModelWeather> call3 = apiInterface.getWeatherByCity(keyword,"6c57819f3114a6213bf6a1a0290c4f2c");
    call3.enqueue(new Callback<ModelWeather>() {
        @Override
        public void onResponse(Call<ModelWeather> call, Response<ModelWeather> response) {
            progressDialog.dismiss();
            ModelWeather dataWeather = response.body();
            //Toast.makeText(LoginActivity.this,userList.getToken().toString(),Toast.LENGTH_LONG).show();
            if (dataWeather !=null) {
                txtKota.setText(dataWeather.getName()+ ", " + dataWeather.getSys().getCountry());
                txtAngin.setText(dataWeather.getWind().getSpeed().toString() + " m/s");
                txtCuaca.setText(dataWeather.getWeather().get(0).getDescription());
////                    txtSunrise.setText(dataWeather.getName());

                txtTanggal.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
                txtTekanan.setText(dataWeather.getMain().getPressure() + " hpa");
                txtKelembaban.setText(dataWeather.getMain().getHumidity() + " %");
                txtMatahirTerbit.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(dataWeather.getSys().getSunset() * 1000 * (60 * 60 * 7 ) )));
                txtMatahariTerbenam.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(dataWeather.getSys().getSunrise() * 1000 * (60 * 60 * 7))));
//                    txtGeoCoords.setText("[ " +dataWeather.getCoord().getLat().toString() + " , " + dataWeather.getCoord().getLon().toString() + " ]");
                txtDerajat.setText(new DecimalFormat("##.##").format(dataWeather.getMain().getTemp()-273.15) + "°C");

                String image = "http://openweathermap.org/img/wn/"+ dataWeather.getWeather().get(0).getIcon()+"@2x.png";
                Picasso.get().load(image).into(gCuaca);


            }else{

                try {
                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                    Toast.makeText(MainActivity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

        }

        @Override
        public void onFailure(Call<ModelWeather> call, Throwable t) {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(),"Maaf koneksi bermasalah",Toast.LENGTH_LONG).show();
            call.cancel();
        }
    });




}
}
