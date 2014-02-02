package io.github.importre.android.chromeadb;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class OssLicenseFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_oss_license, container, false);
        setContents(rootView);
        return rootView;
    }

    private void setContents(View rootView) {
        String str;
        StringBuilder buf = new StringBuilder("<pre>");

        try {
            InputStream is = getActivity().getAssets().open("LICENSE-2.0.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            while ((str = in.readLine()) != null) {
                buf.append(str + "<br />");
            }
            in.close();
        } catch (IOException e) {
            return;
        }

        WebView webView = (WebView) rootView.findViewById(R.id.webview);
        buf.append("</pre>");

        String encoding = "utf-8";
        WebSettings settings = webView.getSettings();
        settings.setDefaultTextEncodingName(encoding);
        webView.loadDataWithBaseURL(null, buf.toString(), "text/html", encoding, null);
    }
}
