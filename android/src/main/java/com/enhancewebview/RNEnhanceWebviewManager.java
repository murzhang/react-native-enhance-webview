
package com.enhancewebview;

import android.content.Context;
import android.graphics.Path;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.views.webview.ReactWebViewManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class RNEnhanceWebviewManager extends ReactWebViewManager {

  private Context context;

  @Override
  public String getName() {
    return "RNEnhanceWebview";
  }

  @Override
  protected WebView createViewInstance(ThemedReactContext reactContext) {
    context = reactContext;
    WebView root = super.createViewInstance(reactContext);
    return root;
  }

  @ReactProp(name = "allowFileAccessFromFileURLs")
  public void setAllowFileAccessFromFileURLs(WebView root, boolean allows) {
    root.getSettings().setAllowFileAccessFromFileURLs(allows);

    root.setWebViewClient(new ReactWebViewClient(){
      @Nullable
      @Override
      public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        String key="?local=";
        int findIndex=url.indexOf(key);
        if(url.indexOf("/resources/")!=-1 && findIndex!=-1){
          String localUrl=url.substring(findIndex+key.length()+1);
          String mime=url.indexOf("?local=1")!=-1?"image/jpeg":(url.indexOf("?local=2")!=-1?"audio/x-mpeg":"video/mp4");
          return this.getWebResourceResponse(localUrl,mime);
        }
        else {
          return super.shouldInterceptRequest(view, url);
        }
      }

      @Nullable
      @Override
      public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        return super.shouldInterceptRequest(view, request);
      }

      private WebResourceResponse getWebResourceResponse(String url, String mime) {
        WebResourceResponse response = null;
        try {
          response = new WebResourceResponse(mime, "UTF-8", new FileInputStream(new File(url)));
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        }
        return response;
      }
    });
  }

  @ReactProp(name = "autoFocus")
  public void setAutoFocus(WebView root, boolean autoFocus) {
    if (autoFocus) {
      root.requestFocus();
      InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
  }
}