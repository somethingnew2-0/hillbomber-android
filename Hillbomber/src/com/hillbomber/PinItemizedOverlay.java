package com.hillbomber;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class PinItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	private OverlayItem overlay;
	private Context context;
	public PinItemizedOverlay(int difficulty, Context context) {
		super(boundCenterBottom(difficultyToDrawable(difficulty, context.getResources())));
		this.context = context;
	}
	
	public void addOverlay(OverlayItem overlay) {
	    this.overlay = overlay;
	    populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		 return overlay;
	}

	@Override
	public int size() {
		return 1;
	}
	
	@Override
	protected boolean onTap(int index) {
	  AlertDialog.Builder dialog = new AlertDialog.Builder(context);
	  dialog.setTitle(overlay.getTitle());
	  dialog.setMessage(overlay.getSnippet());
	  dialog.show();
	  return true;
	}
	
	private static Drawable difficultyToDrawable(int difficulty, Resources resources) {
		switch (difficulty) {
		case 1:
			return resources.getDrawable(R.drawable.greenpin);
		case 2:
			return resources.getDrawable(R.drawable.bluepin);
		case 3:
			return resources.getDrawable(R.drawable.blackpin);
		case 4:
			return resources.getDrawable(R.drawable.dblblackpin);
		default:
			return resources.getDrawable(R.drawable.greenpin);
		}
	}
}
