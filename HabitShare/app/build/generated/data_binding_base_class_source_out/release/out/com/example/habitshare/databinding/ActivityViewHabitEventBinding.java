// Generated by view binder compiler. Do not edit!
package com.example.habitshare.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.habitshare.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityViewHabitEventBinding implements ViewBinding {
  @NonNull
  private final FrameLayout rootView;

  @NonNull
  public final Button cancelHabitEvent;

  @NonNull
  public final Button deleteHabitEvent;

  @NonNull
  public final Button editHabitEvent;

  @NonNull
  public final TextView textView15;

  @NonNull
  public final TextView viewHabitEventComment;

  @NonNull
  public final TextView viewHabitEventDate;

  @NonNull
  public final ImageView viewHabitEventImage;

  @NonNull
  public final TextView viewHabitEventLocation;

  @NonNull
  public final TextView viewHabitEventTitle;

  private ActivityViewHabitEventBinding(@NonNull FrameLayout rootView,
      @NonNull Button cancelHabitEvent, @NonNull Button deleteHabitEvent,
      @NonNull Button editHabitEvent, @NonNull TextView textView15,
      @NonNull TextView viewHabitEventComment, @NonNull TextView viewHabitEventDate,
      @NonNull ImageView viewHabitEventImage, @NonNull TextView viewHabitEventLocation,
      @NonNull TextView viewHabitEventTitle) {
    this.rootView = rootView;
    this.cancelHabitEvent = cancelHabitEvent;
    this.deleteHabitEvent = deleteHabitEvent;
    this.editHabitEvent = editHabitEvent;
    this.textView15 = textView15;
    this.viewHabitEventComment = viewHabitEventComment;
    this.viewHabitEventDate = viewHabitEventDate;
    this.viewHabitEventImage = viewHabitEventImage;
    this.viewHabitEventLocation = viewHabitEventLocation;
    this.viewHabitEventTitle = viewHabitEventTitle;
  }

  @Override
  @NonNull
  public FrameLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityViewHabitEventBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityViewHabitEventBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_view_habit_event, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityViewHabitEventBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.cancel_habit_event;
      Button cancelHabitEvent = ViewBindings.findChildViewById(rootView, id);
      if (cancelHabitEvent == null) {
        break missingId;
      }

      id = R.id.delete_habit_event;
      Button deleteHabitEvent = ViewBindings.findChildViewById(rootView, id);
      if (deleteHabitEvent == null) {
        break missingId;
      }

      id = R.id.edit_habit_event;
      Button editHabitEvent = ViewBindings.findChildViewById(rootView, id);
      if (editHabitEvent == null) {
        break missingId;
      }

      id = R.id.textView15;
      TextView textView15 = ViewBindings.findChildViewById(rootView, id);
      if (textView15 == null) {
        break missingId;
      }

      id = R.id.view_habit_event_comment;
      TextView viewHabitEventComment = ViewBindings.findChildViewById(rootView, id);
      if (viewHabitEventComment == null) {
        break missingId;
      }

      id = R.id.view_habit_event_date;
      TextView viewHabitEventDate = ViewBindings.findChildViewById(rootView, id);
      if (viewHabitEventDate == null) {
        break missingId;
      }

      id = R.id.view_habit_event_image;
      ImageView viewHabitEventImage = ViewBindings.findChildViewById(rootView, id);
      if (viewHabitEventImage == null) {
        break missingId;
      }

      id = R.id.view_habit_event_location;
      TextView viewHabitEventLocation = ViewBindings.findChildViewById(rootView, id);
      if (viewHabitEventLocation == null) {
        break missingId;
      }

      id = R.id.view_habit_event_title;
      TextView viewHabitEventTitle = ViewBindings.findChildViewById(rootView, id);
      if (viewHabitEventTitle == null) {
        break missingId;
      }

      return new ActivityViewHabitEventBinding((FrameLayout) rootView, cancelHabitEvent,
          deleteHabitEvent, editHabitEvent, textView15, viewHabitEventComment, viewHabitEventDate,
          viewHabitEventImage, viewHabitEventLocation, viewHabitEventTitle);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
