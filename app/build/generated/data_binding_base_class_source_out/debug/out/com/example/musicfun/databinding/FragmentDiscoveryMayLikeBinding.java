// Generated by view binder compiler. Do not edit!
package com.example.musicfun.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.musicfun.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentDiscoveryMayLikeBinding implements ViewBinding {
  @NonNull
  private final FrameLayout rootView;

  @NonNull
  public final ListView lvdiscovery;

  private FragmentDiscoveryMayLikeBinding(@NonNull FrameLayout rootView,
      @NonNull ListView lvdiscovery) {
    this.rootView = rootView;
    this.lvdiscovery = lvdiscovery;
  }

  @Override
  @NonNull
  public FrameLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentDiscoveryMayLikeBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentDiscoveryMayLikeBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_discovery_may_like, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentDiscoveryMayLikeBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.lvdiscovery;
      ListView lvdiscovery = ViewBindings.findChildViewById(rootView, id);
      if (lvdiscovery == null) {
        break missingId;
      }

      return new FragmentDiscoveryMayLikeBinding((FrameLayout) rootView, lvdiscovery);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
