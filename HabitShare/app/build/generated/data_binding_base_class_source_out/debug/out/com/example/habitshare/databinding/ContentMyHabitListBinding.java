// Generated by view binder compiler. Do not edit!
package com.example.habitshare.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.habitshare.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ContentMyHabitListBinding implements ViewBinding {
  @NonNull
  private final CardView rootView;

  @NonNull
  public final TextView habitDate;

  @NonNull
  public final TextView habitFrequency;

  @NonNull
  public final TextView habitName;

  @NonNull
  public final TextView habitStatus;

  private ContentMyHabitListBinding(@NonNull CardView rootView, @NonNull TextView habitDate,
      @NonNull TextView habitFrequency, @NonNull TextView habitName,
      @NonNull TextView habitStatus) {
    this.rootView = rootView;
    this.habitDate = habitDate;
    this.habitFrequency = habitFrequency;
    this.habitName = habitName;
    this.habitStatus = habitStatus;
  }

  @Override
  @NonNull
  public CardView getRoot() {
    return rootView;
  }

  @NonNull
  public static ContentMyHabitListBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ContentMyHabitListBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.content_my_habit_list, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ContentMyHabitListBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.habit_date;
      TextView habitDate = ViewBindings.findChildViewById(rootView, id);
      if (habitDate == null) {
        break missingId;
      }

      id = R.id.habit_frequency;
      TextView habitFrequency = ViewBindings.findChildViewById(rootView, id);
      if (habitFrequency == null) {
        break missingId;
      }

      id = R.id.habit_name;
      TextView habitName = ViewBindings.findChildViewById(rootView, id);
      if (habitName == null) {
        break missingId;
      }

      id = R.id.habit_status;
      TextView habitStatus = ViewBindings.findChildViewById(rootView, id);
      if (habitStatus == null) {
        break missingId;
      }

      return new ContentMyHabitListBinding((CardView) rootView, habitDate, habitFrequency,
          habitName, habitStatus);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
