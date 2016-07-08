package co.allza.mararewards.interfaces;

import co.allza.mararewards.adapter.SegurosPagerAdapter;

/**
 * Created by Tavo on 07/07/2016.
 */
public interface VolleyCallback{
    void onSuccess(SegurosPagerAdapter result);
    void onFailure(String error);
    void onTokenReceived(String token);
}