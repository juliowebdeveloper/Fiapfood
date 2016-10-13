package julio.com.br.fiapfoodv2.fragment;


import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

import butterknife.Bind;
import butterknife.ButterKnife;
import julio.com.br.fiapfoodv2.R;

/**
 * Created by Shido on 05/02/2016.
 */
public class SobreDialogFragment extends DialogFragment {


    @Bind(R.id.ivSobreLogo)
    ImageView ivSobreLogo;


    public SobreDialogFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View v = inflater.inflate(R.layout.fragment_sobre, container);
        getDialog().setTitle(R.string.menu_sobre);
        ButterKnife.bind(this, v);

        try{
            InputStream is = getActivity().getAssets().open("logo.png");
            Drawable d = Drawable.createFromStream(is, null);
            ivSobreLogo.setImageDrawable(d);
        }catch (IOException e){
            e.printStackTrace();
        }



        return v;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }




}
