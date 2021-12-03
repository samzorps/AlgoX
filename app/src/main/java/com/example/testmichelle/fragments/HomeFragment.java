package com.example.testmichelle.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.testmichelle.R;
import com.example.testmichelle.model.UserMoney;
import com.example.testmichelle.model.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.ta4j.core.TimeSeries;
import org.ta4j.core.Trade;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.num.Num;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    TextView text_name;
    TextView text_balance;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        text_name = (TextView) view.findViewById(R.id.text_username);
        text_name.setVisibility(View.INVISIBLE);
        /*
        text_balance = (TextView) view.findViewById(R.id.text_balance);
        text_balance.setVisibility(View.INVISIBLE);
*/

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users");
        databaseReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserProfile name = snapshot.getValue(UserProfile.class);
                text_name.setText("Hello," +" "+ name.getName());
                text_name.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        text_balance = (TextView) view.findViewById(R.id.text_balance);
        text_balance.setVisibility(View.INVISIBLE);
        databaseReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserMoney money = snapshot.getValue(UserMoney.class);
                text_balance.setText("Your Balance " + "\n" + "$"+ money.getCurrentBalance());
                text_balance.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return view;
    }



    public static ArrayList<Double> createListOfAlgorithmValues(TimeSeries series, TradingRecord tradingRecord, double startingValue) {

        ArrayList<Double> resultingList = new ArrayList<Double>();
        for (int i = 0; i < series.getBarCount(); i++) {
            resultingList.add(1.);
        }

        int numberOfProfitable = 0;
        for (Trade trade : tradingRecord.getTrades()) {
            int entryIndex = trade.getEntry().getIndex();
            int exitIndex = trade.getExit().getIndex();

            double result;
            if (trade.getEntry().isBuy()) {
                // buy-then-sell trade
                result = series.getBar(exitIndex).getClosePrice().dividedBy(series.getBar(entryIndex).getClosePrice()).doubleValue();
            } else {
                // sell-then-buy trade
                result = series.getBar(entryIndex).getClosePrice().dividedBy(series.getBar(exitIndex).getClosePrice()).doubleValue();
            }

            resultingList.set(exitIndex, result);
        }

        ArrayList<Double> finalList = new ArrayList<Double>();

        for (int i = 0; i < resultingList.size(); i++) {
            startingValue *= resultingList.get(i);
            finalList.add(startingValue);
        }
        return finalList;
    }
}